package club.aspvp.ffa.event;

import club.aspvp.ffa.TwoCubeFFA;
import club.aspvp.ffa.api.ArenaRegionSnapshot;
import club.aspvp.ffa.api.ArenaSnapshot;
import club.aspvp.ffa.api.TwoCubeFFAAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class AirdropManager {

    private final TwoCubeFFA plugin;
    private final EventManager eventManager;
    private final Map<String, ActiveAirdrop> activeAirdrops = new ConcurrentHashMap<>();
    private BukkitTask autoTask;

    public AirdropManager(TwoCubeFFA plugin, EventManager eventManager) {
        this.plugin = plugin;
        this.eventManager = eventManager;
    }

    public void startAutoScheduler() {
        stopAutoScheduler();
        FileConfiguration config = eventManager.getConfig();
        if (!config.getBoolean("airdrop.enabled", true)) return;

        long intervalMinutes = config.getLong("airdrop.interval-minutes", 20);
        if (intervalMinutes <= 0) return;

        long ticks = intervalMinutes * 60L * 20L;
        this.autoTask = new BukkitRunnable() {
            @Override
            public void run() {
                spawnAirdrop(null, null);
            }
        }.runTaskTimer(plugin, ticks, ticks);
    }

    public void stopAutoScheduler() {
        if (this.autoTask != null) {
            this.autoTask.cancel();
            this.autoTask = null;
        }
    }

    public boolean spawnAirdrop(String arenaId, CommandSender sender) {
        FileConfiguration config = eventManager.getConfig();
        if (!config.getBoolean("airdrop.enabled", true) && sender == null) {
            return false;
        }

        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
        Collection<ArenaSnapshot> arenas = api.arenas();
        if (arenas.isEmpty()) {
            if (sender != null) sender.sendMessage("§cNo arenas are currently loaded.");
            return false;
        }

        ArenaSnapshot targetArena = null;
        if (arenaId != null && !arenaId.isBlank()) {
            targetArena = api.arena(arenaId).orElse(null);
            if (targetArena == null) {
                if (sender != null) sender.sendMessage("§cArena '" + arenaId + "' not found.");
                return false;
            }
        } else {
            List<ArenaSnapshot> enabledList = arenas.stream().filter(ArenaSnapshot::enabled).toList();
            if (enabledList.isEmpty()) {
                enabledList = new ArrayList<>(arenas);
            }
            targetArena = enabledList.get(ThreadLocalRandom.current().nextInt(enabledList.size()));
        }

        final String finalArenaId = targetArena.id();
        if (activeAirdrops.containsKey(finalArenaId.toLowerCase(Locale.ROOT))) {
            if (sender != null) sender.sendMessage("§cAn active supply crate already exists in arena '" + finalArenaId + "'.");
            return false;
        }

        Location dropLocation = calculateDropLocation(finalArenaId, targetArena);
        if (dropLocation == null || dropLocation.getWorld() == null) {
            if (sender != null) sender.sendMessage("§cCould not determine a valid drop location for arena '" + finalArenaId + "'.");
            return false;
        }

        int warningSeconds = config.getInt("airdrop.warning-countdown-seconds", 15);
        String spawnMsg = color(config.getString("airdrop.broadcast.spawning", "&8[&6&lFlix&eFFA&8] &6📦 &eAn &6Elite Supply Crate &eis incoming to arena &b%arena% &e(&f%x%, %y%, %z%&e) in &c%time%s&e!")
                .replace("%arena%", finalArenaId)
                .replace("%x%", String.valueOf(dropLocation.getBlockX()))
                .replace("%y%", String.valueOf(dropLocation.getBlockY()))
                .replace("%z%", String.valueOf(dropLocation.getBlockZ()))
                .replace("%time%", String.valueOf(warningSeconds)));
        Bukkit.broadcastMessage(spawnMsg);

        // Sound cue that the supply crate is incoming (Raid Horn + Beacon activation)
        World world = dropLocation.getWorld();
        String incomingSoundName = config.getString("airdrop.effects.incoming-sound", "EVENT_RAID_HORN");
        Sound incomingSound = parseSound(incomingSoundName, Sound.EVENT_RAID_HORN);

        if (world != null && incomingSound != null) {
            try {
                world.playSound(dropLocation, incomingSound, 12.0f, 1.0f);
                world.playSound(dropLocation, Sound.BLOCK_BEACON_ACTIVATE, 12.0f, 1.1f);
            } catch (Exception ignored) {}
        }

        // Play incoming alarm directly to players inside this arena
        for (Player p : Bukkit.getOnlinePlayers()) {
            Optional<String> playerArena = TwoCubeFFAAPI.get().arenaOf(p);
            if (playerArena.isPresent() && playerArena.get().equalsIgnoreCase(finalArenaId)) {
                if (incomingSound != null) {
                    try {
                        p.playSound(p.getLocation(), incomingSound, 1.0f, 1.0f);
                        p.playSound(p.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.8f, 1.1f);
                    } catch (Exception ignored) {}
                }
            }
        }

        final boolean beamEnabled = config.getBoolean("airdrop.effects.beam-enabled", true);
        final int beamHeight = config.getInt("airdrop.effects.beam-height", 70);

        if (warningSeconds <= 0) {
            landAirdrop(finalArenaId, dropLocation);
        } else {
            final int totalTicks = warningSeconds * 20;
            // Visual beacon beam descending and countdown alarm task
            new BukkitRunnable() {
                int elapsedTicks = 0;

                @Override
                public void run() {
                    elapsedTicks += 3;
                    int remainingSeconds = Math.max(0, (totalTicks - elapsedTicks) / 20);

                    if (elapsedTicks >= totalTicks) {
                        landAirdrop(finalArenaId, dropLocation);
                        cancel();
                        return;
                    }

                    // Render high-intensity beacon beam from ground all the way up to sky
                    if (beamEnabled) {
                        renderBeaconBeam(dropLocation, elapsedTicks, beamHeight);
                    }

                    // Audible radar / beacon ping on every second
                    if (elapsedTicks % 20 == 0) {
                        World w = dropLocation.getWorld();
                        if (w != null) {
                            if (remainingSeconds <= 5 && remainingSeconds > 0) {
                                try {
                                    w.playSound(dropLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 8.0f, 1.0f + (5 - remainingSeconds) * 0.2f);
                                } catch (Exception ignored) {}
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (TwoCubeFFAAPI.get().arenaOf(p).filter(a -> a.equalsIgnoreCase(finalArenaId)).isPresent()) {
                                        try {
                                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.8f, 1.5f);
                                        } catch (Exception ignored) {}
                                    }
                                }
                            } else {
                                try {
                                    w.playSound(dropLocation, Sound.BLOCK_BEACON_AMBIENT, 6.0f, 1.0f);
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }
            }.runTaskTimer(plugin, 0L, 3L);
        }

        if (sender != null) {
            sender.sendMessage("§aSupply crate event initiated for arena §e" + finalArenaId + " §aat " + dropLocation.getBlockX() + ", " + dropLocation.getBlockY() + ", " + dropLocation.getBlockZ());
        }
        return true;
    }

    private Location calculateDropLocation(String arenaId, ArenaSnapshot arena) {
        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
        Optional<ArenaRegionSnapshot> regOpt = api.arenaRegion(arenaId);
        World world = arena.spawn() != null ? arena.spawn().getWorld() : null;
        Location spawnLoc = arena.spawn();

        if (regOpt.isPresent()) {
            ArenaRegionSnapshot reg = regOpt.get();
            if (world == null && reg.world() != null) {
                world = Bukkit.getWorld(reg.world());
            }
            if (world != null) {
                double minX = Math.min(reg.minX(), reg.maxX()) + 6;
                double maxX = Math.max(reg.minX(), reg.maxX()) - 6;
                double minZ = Math.min(reg.minZ(), reg.maxZ()) + 6;
                double maxZ = Math.max(reg.minZ(), reg.maxZ()) - 6;
                int minY = (int) Math.min(reg.minY(), reg.maxY());
                int maxY = (int) Math.max(reg.minY(), reg.maxY());

                if (maxX > minX && maxZ > minZ) {
                    for (int attempt = 0; attempt < 30; attempt++) {
                        int randX = (int) ThreadLocalRandom.current().nextDouble(minX, maxX);
                        int randZ = (int) ThreadLocalRandom.current().nextDouble(minZ, maxZ);

                        // Keep airdrop at least 15 blocks away from spawn point
                        if (spawnLoc != null && spawnLoc.distanceSquared(new Location(world, randX, spawnLoc.getY(), randZ)) < 225) {
                            continue;
                        }

                        Location ground = findSafeGround(world, randX, randZ, maxY, minY);
                        if (ground != null) {
                            return ground;
                        }
                    }
                }
            }
        }

        // Fallback: search around spawn 18-35 blocks away (outside spawn protection)
        if (spawnLoc != null) {
            World spWorld = spawnLoc.getWorld();
            int spX = spawnLoc.getBlockX();
            int spY = spawnLoc.getBlockY();
            int spZ = spawnLoc.getBlockZ();

            for (int attempt = 0; attempt < 25; attempt++) {
                double angle = ThreadLocalRandom.current().nextDouble(0, 2 * Math.PI);
                double dist = ThreadLocalRandom.current().nextDouble(18, 35);
                int randX = (int) (spX + dist * Math.cos(angle));
                int randZ = (int) (spZ + dist * Math.sin(angle));

                Location ground = findSafeGround(spWorld, randX, randZ, spY + 15, Math.max(spWorld.getMinHeight(), spY - 25));
                if (ground != null) {
                    return ground;
                }
            }
        }

        return null;
    }

    private Location findSafeGround(World world, int x, int z, int startY, int minY) {
        startY = Math.min(startY, world.getMaxHeight() - 2);
        minY = Math.max(minY, world.getMinHeight());

        for (int y = startY; y >= minY; y--) {
            Block ground = world.getBlockAt(x, y, z);
            Material mat = ground.getType();

            // SKIP BARRIERS, AIR, VOID, LIGHT, LEAVES
            if (mat == Material.BARRIER || mat == Material.STRUCTURE_VOID || mat == Material.LIGHT || mat.isAir()) {
                continue;
            }

            // Must be solid walkable surface
            if (!mat.isSolid()) {
                continue;
            }

            // Check chest placement space and player headspace (both must NOT be solid or barrier)
            Block chestBlock = world.getBlockAt(x, y + 1, z);
            Block headBlock = world.getBlockAt(x, y + 2, z);

            if (isPassableAir(chestBlock) && isPassableAir(headBlock)) {
                return new Location(world, x, y + 1, z);
            }
        }
        return null;
    }

    private boolean isPassableAir(Block b) {
        Material m = b.getType();
        if (m == Material.BARRIER || m == Material.STRUCTURE_VOID || m.isSolid()) {
            return false;
        }
        return m.isAir() || m == Material.SHORT_GRASS || m == Material.TALL_GRASS || m == Material.FERN || m == Material.SNOW;
    }

    private void landAirdrop(String arenaId, Location loc) {
        World world = loc.getWorld();
        if (world == null) return;

        Block block = loc.getBlock();
        block.setType(Material.CHEST);

        FileConfiguration config = eventManager.getConfig();
        int despawnSeconds = config.getInt("airdrop.despawn-seconds", 300);

        List<ArmorStand> stands = spawnHolograms(loc, despawnSeconds);

        ActiveAirdrop airdrop = new ActiveAirdrop(arenaId, loc, block, stands, despawnSeconds);
        activeAirdrops.put(arenaId.toLowerCase(Locale.ROOT), airdrop);

        // Sound and fireworks
        if (config.getBoolean("airdrop.effects.firework-on-land", true)) {
            spawnFirework(loc.clone().add(0.5, 1.0, 0.5), Color.ORANGE, Color.YELLOW);
        }
        String soundName = config.getString("airdrop.effects.landing-sound", "ENTITY_ENDER_DRAGON_GROWL");
        try {
            Sound sound = parseSound(soundName, Sound.ENTITY_ENDER_DRAGON_GROWL);
            world.playSound(loc, sound, 4.0f, 1.0f);
            world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 3.0f, 0.8f);
            world.playSound(loc, Sound.BLOCK_BEACON_POWER_SELECT, 5.0f, 1.2f);
        } catch (Exception ignored) {}

        String landMsg = color(config.getString("airdrop.broadcast.landed", "&8[&6&lFlix&eFFA&8] &a📦 The Supply Crate has landed in arena &b%arena% &eat &f%x%, %y%, %z%&e! &eFight for it!")
                .replace("%arena%", arenaId)
                .replace("%x%", String.valueOf(loc.getBlockX()))
                .replace("%y%", String.valueOf(loc.getBlockY()))
                .replace("%z%", String.valueOf(loc.getBlockZ())));
        Bukkit.broadcastMessage(landMsg);

        // Keep the beacon beam shining into the sky while crate is waiting to be claimed
        final boolean beamEnabled = config.getBoolean("airdrop.effects.beam-enabled", true);
        final int beamHeight = config.getInt("airdrop.effects.beam-height", 70);
        if (beamEnabled) {
            airdrop.beamTask = new BukkitRunnable() {
                int tick = 0;
                @Override
                public void run() {
                    if (!activeAirdrops.containsKey(arenaId.toLowerCase(Locale.ROOT))) {
                        cancel();
                        return;
                    }
                    tick += 3;
                    renderBeaconBeam(loc, tick, beamHeight);
                    if (tick % 80 == 0) {
                        try {
                            loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_AMBIENT, 3.0f, 1.0f);
                        } catch (Exception ignored) {}
                    }
                }
            }.runTaskTimer(plugin, 0L, 3L);
        }

        // Despawn countdown task
        airdrop.task = new BukkitRunnable() {
            int remaining = despawnSeconds;
            @Override
            public void run() {
                remaining--;
                airdrop.secondsRemaining = remaining;
                if (remaining <= 0) {
                    despawnAirdrop(arenaId, true);
                    cancel();
                } else if (remaining % 5 == 0) {
                    updateHolograms(airdrop);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private List<ArmorStand> spawnHolograms(Location loc, int remainingSeconds) {
        List<ArmorStand> list = new ArrayList<>();
        FileConfiguration config = eventManager.getConfig();
        List<String> rawLines = config.getStringList("airdrop.hologram.lines");
        if (rawLines.isEmpty()) {
            rawLines = List.of("&6&l★ SUPPLY CRATE ★", "&eRight-click to claim!", "&7Expires in: &c%time_left%s");
        }

        World world = loc.getWorld();
        if (world == null) return list;

        double baseHeight = 1.3 + (rawLines.size() * 0.28);
        for (int i = 0; i < rawLines.size(); i++) {
            String text = color(rawLines.get(i).replace("%time_left%", String.valueOf(remainingSeconds)));
            Location lineLoc = loc.clone().add(0.5, baseHeight - (i * 0.28), 0.5);
            ArmorStand stand = (ArmorStand) world.spawnEntity(lineLoc, EntityType.ARMOR_STAND);
            stand.setGravity(false);
            stand.setVisible(false);
            stand.setMarker(true);
            stand.setCustomName(text);
            stand.setCustomNameVisible(true);
            stand.setInvulnerable(true);
            list.add(stand);
        }
        return list;
    }

    private void updateHolograms(ActiveAirdrop airdrop) {
        FileConfiguration config = eventManager.getConfig();
        List<String> rawLines = config.getStringList("airdrop.hologram.lines");
        if (rawLines.isEmpty()) {
            rawLines = List.of("&6&l★ SUPPLY CRATE ★", "&eRight-click to claim!", "&7Expires in: &c%time_left%s");
        }
        for (int i = 0; i < Math.min(rawLines.size(), airdrop.holograms.size()); i++) {
            String text = color(rawLines.get(i).replace("%time_left%", String.valueOf(airdrop.secondsRemaining)));
            airdrop.holograms.get(i).setCustomName(text);
        }
    }

    public boolean handleInteract(Player player, Block block) {
        if (block == null || block.getType() != Material.CHEST) return false;

        ActiveAirdrop match = null;
        for (ActiveAirdrop a : activeAirdrops.values()) {
            if (a.chestBlock.equals(block)) {
                match = a;
                break;
            }
        }
        if (match == null) return false;

        claimAirdrop(player, match);
        return true;
    }

    private void claimAirdrop(Player player, ActiveAirdrop airdrop) {
        FileConfiguration config = eventManager.getConfig();
        activeAirdrops.remove(airdrop.arenaId.toLowerCase(Locale.ROOT));

        if (airdrop.beamTask != null) airdrop.beamTask.cancel();
        if (airdrop.task != null) airdrop.task.cancel();
        airdrop.holograms.forEach(ArmorStand::remove);
        airdrop.chestBlock.setType(Material.AIR);

        // Give token rewards
        if (config.getBoolean("airdrop.loot.tokens.enabled", true)) {
            int minTokens = config.getInt("airdrop.loot.tokens.min", 150);
            int maxTokens = config.getInt("airdrop.loot.tokens.max", 500);
            int roll = ThreadLocalRandom.current().nextInt(minTokens, Math.max(minTokens + 1, maxTokens + 1));
            TwoCubeFFAAPI.get().addTokens(player, roll);
            player.sendMessage("§8[§6★§8] §eYou received §6" + roll + " tokens §efrom the Supply Crate!");
        }

        // Give items
        List<Map<?, ?>> items = config.getMapList("airdrop.loot.items");
        for (Map<?, ?> itemDef : items) {
            int chance = itemDef.get("chance") instanceof Number ? ((Number) itemDef.get("chance")).intValue() : 100;
            if (ThreadLocalRandom.current().nextInt(100) < chance) {
                ItemStack is = parseLootItem(itemDef);
                if (is != null) {
                    HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(is);
                    if (!leftover.isEmpty()) {
                        leftover.values().forEach(drop -> player.getWorld().dropItemNaturally(player.getLocation(), drop));
                    }
                }
            }
        }

        // Sounds and firework
        if (config.getBoolean("airdrop.effects.firework-on-claim", true)) {
            spawnFirework(player.getLocation().add(0, 1, 0), Color.LIME, Color.YELLOW);
        }
        String claimSound = config.getString("airdrop.effects.claim-sound", "UI_TOAST_CHALLENGE_COMPLETE");
        try {
            Sound sound = Sound.valueOf(claimSound.toUpperCase(Locale.ROOT));
            player.playSound(player.getLocation(), sound, 2.0f, 1.0f);
        } catch (Exception ignored) {}

        String claimMsg = color(config.getString("airdrop.broadcast.claimed", "&8[&6&lFlix&eFFA&8] &6🎉 &a%player% &ehas claimed the Supply Crate in &b%arena%&e!")
                .replace("%player%", player.getName())
                .replace("%arena%", airdrop.arenaId));
        Bukkit.broadcastMessage(claimMsg);
    }

    public void despawnAirdrop(String arenaId, boolean broadcast) {
        ActiveAirdrop airdrop = activeAirdrops.remove(arenaId.toLowerCase(Locale.ROOT));
        if (airdrop == null) return;

        if (airdrop.beamTask != null) airdrop.beamTask.cancel();
        if (airdrop.task != null) airdrop.task.cancel();
        airdrop.holograms.forEach(ArmorStand::remove);
        airdrop.chestBlock.setType(Material.AIR);

        if (broadcast) {
            FileConfiguration config = eventManager.getConfig();
            String msg = color(config.getString("airdrop.broadcast.despawned", "&8[&6&lFlix&eFFA&8] &c📦 The Supply Crate in arena &b%arena% &ehas expired.")
                    .replace("%arena%", arenaId));
            Bukkit.broadcastMessage(msg);
        }
    }

    public void cleanup() {
        stopAutoScheduler();
        for (String arenaId : new ArrayList<>(activeAirdrops.keySet())) {
            despawnAirdrop(arenaId, false);
        }
        activeAirdrops.clear();
    }

    public Map<String, ActiveAirdrop> getActiveAirdrops() {
        return activeAirdrops;
    }

    public void renderBeaconBeam(Location loc, int tick, int height) {
        World world = loc.getWorld();
        if (world == null) return;

        double x = loc.getBlockX() + 0.5;
        double z = loc.getBlockZ() + 0.5;
        double minY = loc.getY();
        double maxY = Math.min(world.getMaxHeight() - 2, minY + height);

        // Core central beam: intense END_ROD line with force=true so players 100+ blocks away see it clearly
        for (double y = minY; y <= maxY; y += 0.8) {
            world.spawnParticle(Particle.END_ROD, x, y, z, 1, 0.0, 0.0, 0.0, 0.0, null, true);
        }

        // Swirling energetic spiral around the beam column
        double baseAngle = (tick * 0.28) % (2 * Math.PI);
        for (double y = minY; y <= maxY; y += 2.5) {
            double angle = baseAngle + (y * 0.45);
            double ox = 0.42 * Math.cos(angle);
            double oz = 0.42 * Math.sin(angle);
            world.spawnParticle(Particle.FIREWORK, x + ox, y, z + oz, 1, 0.0, 0.0, 0.0, 0.0, null, true);
        }

        // Concentric beacon base rings on ground every 9 ticks (~0.45s)
        if (tick % 9 == 0) {
            for (int i = 0; i < 10; i++) {
                double ringAngle = (2 * Math.PI / 10) * i;
                double rx = 0.85 * Math.cos(ringAngle);
                double rz = 0.85 * Math.sin(ringAngle);
                world.spawnParticle(Particle.GLOW, x + rx, minY + 0.15, z + rz, 1, 0.0, 0.02, 0.0, 0.0, null, true);
            }
        }
    }

    private Sound parseSound(String name, Sound def) {
        if (name == null || name.isBlank()) return def;
        try {
            return Sound.valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            return def;
        }
    }

    private ItemStack parseLootItem(Map<?, ?> def) {
        String matName = String.valueOf(def.get("material"));
        Material mat = Material.matchMaterial(matName);
        if (mat == null) return null;

        int amount = def.get("amount") instanceof Number ? ((Number) def.get("amount")).intValue() : 1;
        ItemStack is = new ItemStack(mat, Math.max(1, amount));

        if (mat == Material.POTION || mat == Material.SPLASH_POTION) {
            String potTypeName = def.get("potion-type") != null ? String.valueOf(def.get("potion-type")) : "STRENGTH";
            int amplifier = def.get("amplifier") instanceof Number ? ((Number) def.get("amplifier")).intValue() : 1;
            int durSec = def.get("duration-seconds") instanceof Number ? ((Number) def.get("duration-seconds")).intValue() : 90;
            PotionMeta meta = (PotionMeta) is.getItemMeta();
            if (meta != null) {
                PotionEffectType effectType = PotionEffectType.getByName(potTypeName);
                if (effectType != null) {
                    meta.addCustomEffect(new PotionEffect(effectType, durSec * 20, amplifier), true);
                }
                if (def.get("name") != null) {
                    meta.setDisplayName(color(String.valueOf(def.get("name"))));
                }
                is.setItemMeta(meta);
            }
        } else if (def.get("name") != null) {
            ItemMeta meta = is.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(color(String.valueOf(def.get("name"))));
                is.setItemMeta(meta);
            }
        }
        return is;
    }

    private void spawnFirework(Location loc, Color c1, Color c2) {
        World world = loc.getWorld();
        if (world == null) return;
        Firework fw = (Firework) world.spawnEntity(loc, EntityType.FIREWORK_ROCKET);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.addEffect(FireworkEffect.builder().withColor(c1).withFade(c2).with(FireworkEffect.Type.BALL_LARGE).build());
        fwm.setPower(1);
        fw.setFireworkMeta(fwm);
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static class ActiveAirdrop {
        public final String arenaId;
        public final Location location;
        public final Block chestBlock;
        public final List<ArmorStand> holograms;
        public int secondsRemaining;
        public BukkitTask task;
        public BukkitTask beamTask;

        public ActiveAirdrop(String arenaId, Location location, Block chestBlock, List<ArmorStand> holograms, int secondsRemaining) {
            this.arenaId = arenaId;
            this.location = location;
            this.chestBlock = chestBlock;
            this.holograms = holograms;
            this.secondsRemaining = secondsRemaining;
        }
    }
}
