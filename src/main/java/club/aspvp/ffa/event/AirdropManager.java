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

        if (warningSeconds <= 0) {
            landAirdrop(finalArenaId, dropLocation);
        } else {
            // Visual warning beam task leading up to drop
            new BukkitRunnable() {
                int count = warningSeconds;
                @Override
                public void run() {
                    if (count <= 0) {
                        landAirdrop(finalArenaId, dropLocation);
                        cancel();
                        return;
                    }
                    // Visual beacon / beam particles descending
                    World world = dropLocation.getWorld();
                    if (world != null) {
                        for (double y = dropLocation.getY(); y <= dropLocation.getY() + 30; y += 2.0) {
                            world.spawnParticle(Particle.FIREWORK, dropLocation.getX() + 0.5, y, dropLocation.getZ() + 0.5, 1, 0.1, 0.1, 0.1, 0.0);
                        }
                    }
                    count--;
                }
            }.runTaskTimer(plugin, 0L, 20L);
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

        if (regOpt.isPresent()) {
            ArenaRegionSnapshot reg = regOpt.get();
            if (world == null && reg.world() != null) {
                world = Bukkit.getWorld(reg.world());
            }
            if (world != null) {
                double minX = Math.min(reg.minX(), reg.maxX()) + 3;
                double maxX = Math.max(reg.minX(), reg.maxX()) - 3;
                double minZ = Math.min(reg.minZ(), reg.maxZ()) + 3;
                double maxZ = Math.max(reg.minZ(), reg.maxZ()) - 3;
                if (maxX > minX && maxZ > minZ) {
                    double randX = ThreadLocalRandom.current().nextDouble(minX, maxX);
                    double randZ = ThreadLocalRandom.current().nextDouble(minZ, maxZ);
                    int y = world.getHighestBlockYAt((int) randX, (int) randZ);
                    return new Location(world, (int) randX, y + 1, (int) randZ);
                }
            }
        }

        if (arena.spawn() != null) {
            Location sp = arena.spawn().clone();
            double randX = sp.getBlockX() + ThreadLocalRandom.current().nextInt(-6, 7);
            double randZ = sp.getBlockZ() + ThreadLocalRandom.current().nextInt(-6, 7);
            int y = sp.getWorld().getHighestBlockYAt((int) randX, (int) randZ);
            return new Location(sp.getWorld(), (int) randX, y + 1, (int) randZ);
        }

        return null;
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
            Sound sound = Sound.valueOf(soundName.toUpperCase(Locale.ROOT));
            world.playSound(loc, sound, 3.0f, 1.0f);
        } catch (Exception ignored) {}

        String landMsg = color(config.getString("airdrop.broadcast.landed", "&8[&6&lFlix&eFFA&8] &a📦 The Supply Crate has landed in arena &b%arena% &eat &f%x%, %y%, %z%&e! &eFight for it!")
                .replace("%arena%", arenaId)
                .replace("%x%", String.valueOf(loc.getBlockX()))
                .replace("%y%", String.valueOf(loc.getBlockY()))
                .replace("%z%", String.valueOf(loc.getBlockZ())));
        Bukkit.broadcastMessage(landMsg);

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

        public ActiveAirdrop(String arenaId, Location location, Block chestBlock, List<ArmorStand> holograms, int secondsRemaining) {
            this.arenaId = arenaId;
            this.location = location;
            this.chestBlock = chestBlock;
            this.holograms = holograms;
            this.secondsRemaining = secondsRemaining;
        }
    }
}
