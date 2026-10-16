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
    private final Map<String, IncomingAirdrop> incomingAirdrops = new ConcurrentHashMap<>();
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
        if (activeAirdrops.containsKey(finalArenaId.toLowerCase(Locale.ROOT)) || incomingAirdrops.containsKey(finalArenaId.toLowerCase(Locale.ROOT))) {
            if (sender != null) sender.sendMessage("§cAn active or incoming supply crate already exists in arena '" + finalArenaId + "'.");
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
        final boolean fallingCapsuleVisual = config.getBoolean("airdrop.effects.falling-capsule-visual", true);
        final boolean groundRadarEnabled = config.getBoolean("airdrop.effects.ground-radar-target", true);

        if (warningSeconds <= 0) {
            landAirdrop(finalArenaId, dropLocation);
        } else {
            final int totalTicks = warningSeconds * 20;

            ArmorStand capsuleStand = null;
            if (fallingCapsuleVisual && world != null) {
                Location skySpawn = dropLocation.clone().add(0.5, beamHeight, 0.5);
                capsuleStand = (ArmorStand) world.spawnEntity(skySpawn, EntityType.ARMOR_STAND);
                capsuleStand.setGravity(false);
                capsuleStand.setVisible(false);
                capsuleStand.setMarker(true);
                capsuleStand.setInvulnerable(true);
                capsuleStand.setCustomName(color("&6&l★ INCOMING SUPPLY CAPSULE ★"));
                capsuleStand.setCustomNameVisible(true);
                if (capsuleStand.getEquipment() != null) {
                    capsuleStand.getEquipment().setHelmet(new ItemStack(Material.CHEST));
                }
            }

            final ArmorStand finalCapsule = capsuleStand;
            final IncomingAirdrop incoming = new IncomingAirdrop(finalArenaId, dropLocation, finalCapsule);
            incomingAirdrops.put(finalArenaId.toLowerCase(Locale.ROOT), incoming);

            incoming.task = new BukkitRunnable() {
                int elapsedTicks = 0;

                @Override
                public void run() {
                    elapsedTicks += 3;
                    int remainingSeconds = Math.max(0, (totalTicks - elapsedTicks) / 20);

                    if (elapsedTicks >= totalTicks) {
                        incomingAirdrops.remove(finalArenaId.toLowerCase(Locale.ROOT));
                        if (finalCapsule != null && finalCapsule.isValid()) {
                            finalCapsule.remove();
                        }
                        landAirdrop(finalArenaId, dropLocation);
                        cancel();
                        return;
                    }

                    double progress = Math.min(1.0, (double) elapsedTicks / totalTicks);
                    double currentCapsuleY = dropLocation.getY() + (beamHeight * (1.0 - progress));

                    // 1. Move descending cargo capsule ArmorStand & render 3D parachute canopy + cords
                    if (finalCapsule != null && finalCapsule.isValid() && world != null) {
                        Location standLoc = new Location(world, dropLocation.getX() + 0.5, currentCapsuleY - 1.25, dropLocation.getZ() + 0.5);
                        finalCapsule.teleport(standLoc);
                        renderParachuteCanopy(world, dropLocation.getX() + 0.5, currentCapsuleY, dropLocation.getZ() + 0.5, elapsedTicks);
                    }

                    // 2. Quantum Double-Helix Plasma Vortex Beam (connecting sky and cargo)
                    if (beamEnabled) {
                        renderQuantumDoubleHelix(dropLocation, currentCapsuleY, elapsedTicks, beamHeight);
                    }

                    // 3. Ground Sci-Fi Targeting Radar Reticle
                    if (groundRadarEnabled) {
                        renderGroundRadar(dropLocation, elapsedTicks, remainingSeconds);
                    }

                    // 4. Audible radar / beacon ping on every second
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

        // Touchdown kinetic shockwave blast
        if (config.getBoolean("airdrop.effects.landing-shockwave", true)) {
            triggerLandingShockwave(loc);
        }

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

        // Keep the beacon beam and protective landed visuals while crate is waiting to be claimed
        final boolean beamEnabled = config.getBoolean("airdrop.effects.beam-enabled", true);
        final int beamHeight = config.getInt("airdrop.effects.beam-height", 70);
        final boolean flaresEnabled = config.getBoolean("airdrop.effects.ground-smoke-flares", true);
        final boolean shieldEnabled = config.getBoolean("airdrop.effects.landing-shield-dome", true);

        if (beamEnabled || flaresEnabled || shieldEnabled) {
            airdrop.beamTask = new BukkitRunnable() {
                int tick = 0;
                @Override
                public void run() {
                    if (!activeAirdrops.containsKey(arenaId.toLowerCase(Locale.ROOT))) {
                        cancel();
                        return;
                    }
                    tick += 3;
                    if (beamEnabled) {
                        renderBeaconBeam(loc, tick, beamHeight);
                    }
                    if (shieldEnabled) {
                        renderLandedShieldDome(loc, tick);
                    }
                    // 4 Emergency signal flares burn for initial 15 seconds (300 ticks)
                    if (flaresEnabled && tick <= 300) {
                        renderEmergencyFlares(loc);
                    }
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
        IncomingAirdrop incoming = incomingAirdrops.remove(arenaId.toLowerCase(Locale.ROOT));
        if (incoming != null) {
            incoming.cancel();
        }

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
        for (IncomingAirdrop inc : incomingAirdrops.values()) {
            inc.cancel();
        }
        incomingAirdrops.clear();
        for (String arenaId : new ArrayList<>(activeAirdrops.keySet())) {
            despawnAirdrop(arenaId, false);
        }
        activeAirdrops.clear();
    }

    public Map<String, ActiveAirdrop> getActiveAirdrops() {
        return activeAirdrops;
    }

    public Map<String, IncomingAirdrop> getIncomingAirdrops() {
        return incomingAirdrops;
    }

    public void renderBeaconBeam(Location loc, int tick, int height) {
        renderQuantumDoubleHelix(loc, loc.getY(), tick, height);
    }

    public void renderQuantumDoubleHelix(Location loc, double currentCapsuleY, int tick, int beamHeight) {
        World world = loc.getWorld();
        if (world == null) return;

        double cx = loc.getBlockX() + 0.5;
        double cz = loc.getBlockZ() + 0.5;
        double minY = loc.getY();
        double maxY = Math.min(world.getMaxHeight() - 2, minY + beamHeight);

        Particle.DustOptions cyanHelix = new Particle.DustOptions(Color.fromRGB(0, 230, 255), 1.3f);
        Particle.DustOptions goldHelix = new Particle.DustOptions(Color.fromRGB(255, 195, 0), 1.3f);

        // Core central beam
        for (double y = minY; y <= maxY; y += 1.3) {
            world.spawnParticle(Particle.END_ROD, cx, y, cz, 1, 0.0, 0.0, 0.0, 0.0, null, true);
        }

        // Double-Helix counter-rotating plasma spirals
        double baseAngle = (tick * 0.22);
        for (double y = minY; y <= maxY; y += 1.8) {
            // Strand 1 (Cyan clockwise)
            double angle1 = baseAngle + (y * 0.38);
            double h1x = cx + 0.65 * Math.cos(angle1);
            double h1z = cz + 0.65 * Math.sin(angle1);
            world.spawnParticle(Particle.DUST, h1x, y, h1z, 1, 0, 0, 0, 0, cyanHelix, true);

            // Strand 2 (Amber Gold counter-clockwise)
            double angle2 = -baseAngle + (y * 0.38) + Math.PI;
            double h2x = cx + 0.65 * Math.cos(angle2);
            double h2z = cz + 0.65 * Math.sin(angle2);
            world.spawnParticle(Particle.DUST, h2x, y, h2z, 1, 0, 0, 0, 0, goldHelix, true);
        }

        // Periodic plasma rings climbing upward
        double ringY = minY + ((tick * 1.6) % Math.max(1, (maxY - minY)));
        int ringPts = 8;
        for (int i = 0; i < ringPts; i++) {
            double ra = (2 * Math.PI / ringPts) * i;
            world.spawnParticle(Particle.FIREWORK, cx + 0.85 * Math.cos(ra), ringY, cz + 0.85 * Math.sin(ra), 1, 0, 0, 0, 0, null, true);
        }
    }

    private void renderParachuteCanopy(World world, double cx, double cy, double cz, int tick) {
        if (world == null) return;

        double sway = Math.sin(tick * 0.1) * 0.22;
        double canopyApexY = cy + 3.2;
        double canopyRimY = cy + 2.3;
        double radius = 1.7;

        Particle.DustOptions canopyWhite = new Particle.DustOptions(Color.fromRGB(250, 250, 255), 1.3f);
        Particle.DustOptions canopyOrange = new Particle.DustOptions(Color.fromRGB(255, 130, 0), 1.3f);
        Particle.DustOptions cordDust = new Particle.DustOptions(Color.fromRGB(210, 210, 220), 0.9f);

        // Canopy circular rim (12 points)
        int rimPts = 12;
        for (int i = 0; i < rimPts; i++) {
            double angle = (2 * Math.PI / rimPts) * i + sway;
            double rx = cx + radius * Math.cos(angle);
            double rz = cz + radius * Math.sin(angle);
            Particle.DustOptions d = (i % 2 == 0) ? canopyWhite : canopyOrange;
            world.spawnParticle(Particle.DUST, rx, canopyRimY, rz, 1, 0, 0, 0, 0, d, true);
        }

        // Canopy apex dome point
        world.spawnParticle(Particle.DUST, cx + (sway * 0.5), canopyApexY, cz + (sway * 0.5), 2, 0.1, 0.05, 0.1, 0, canopyOrange, true);

        // 4 Paracord suspension lines from canopy rim corners down to chest capsule
        double[][] rimCorners = {
            {radius * 0.7, radius * 0.7},
            {-radius * 0.7, radius * 0.7},
            {radius * 0.7, -radius * 0.7},
            {-radius * 0.7, -radius * 0.7}
        };

        double chestTopY = cy - 0.2;
        for (double[] corner : rimCorners) {
            double startX = cx + corner[0] + sway;
            double startY = canopyRimY;
            double startZ = cz + corner[1] + sway;

            for (double t = 0.2; t <= 0.8; t += 0.25) {
                double px = startX + (cx - startX) * t;
                double py = startY + (chestTopY - startY) * t;
                double pz = startZ + (cz - startZ) * t;
                world.spawnParticle(Particle.DUST, px, py, pz, 1, 0, 0, 0, 0, cordDust, true);
            }
        }

        // Stabilization thruster jet puff underneath chest
        world.spawnParticle(Particle.SMALL_FLAME, cx, cy - 1.1, cz, 1, 0.02, 0.06, 0.02, 0.01, null, true);
    }

    private void renderGroundRadar(Location loc, int tick, int remainingSeconds) {
        World world = loc.getWorld();
        if (world == null) return;

        double cx = loc.getBlockX() + 0.5;
        double cy = loc.getY() + 0.12;
        double cz = loc.getBlockZ() + 0.5;

        boolean isAlert = (remainingSeconds <= 5 && remainingSeconds > 0);
        Color mainColor = isAlert
                ? (tick % 6 < 3 ? Color.fromRGB(255, 30, 30) : Color.fromRGB(255, 140, 50))
                : Color.fromRGB(0, 220, 255);
        Color accentColor = isAlert ? Color.fromRGB(255, 0, 0) : Color.fromRGB(255, 200, 0);

        Particle.DustOptions mainDust = new Particle.DustOptions(mainColor, 1.3f);
        Particle.DustOptions accentDust = new Particle.DustOptions(accentColor, 1.1f);

        // 1. Outer target ring (radius = 3.0m)
        int ringPts = 16;
        for (int i = 0; i < ringPts; i++) {
            double angle = (2 * Math.PI / ringPts) * i;
            double rx = cx + 3.0 * Math.cos(angle);
            double rz = cz + 3.0 * Math.sin(angle);
            world.spawnParticle(Particle.DUST, rx, cy, rz, 1, 0, 0, 0, 0, mainDust, true);
        }

        // 2. Cardinal tick marks projecting outward at 0, 90, 180, 270 deg
        double[][] cardinalDirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (double[] dir : cardinalDirs) {
            for (double d = 3.0; d <= 3.65; d += 0.3) {
                world.spawnParticle(Particle.DUST, cx + dir[0] * d, cy, cz + dir[1] * d, 1, 0, 0, 0, 0, accentDust, true);
            }
        }

        // 3. Sweeping 360 radar beam
        double sweepAngle = (tick * 0.16) % (2 * Math.PI);
        for (double r = 0.4; r <= 2.9; r += 0.5) {
            double sx = cx + r * Math.cos(sweepAngle);
            double sz = cz + r * Math.sin(sweepAngle);
            world.spawnParticle(Particle.DUST, sx, cy, sz, 1, 0, 0, 0, 0, mainDust, true);
        }

        // 4. Expanding sonar ping wave
        double waveProgress = ((tick * 2) % 30) / 30.0;
        double waveR = 0.5 + (waveProgress * 2.4);
        int wavePts = 10;
        for (int i = 0; i < wavePts; i++) {
            double wa = (2 * Math.PI / wavePts) * i;
            world.spawnParticle(Particle.DUST, cx + waveR * Math.cos(wa), cy, cz + waveR * Math.sin(wa), 1, 0, 0, 0, 0, accentDust, true);
        }

        // 5. Center crosshair
        world.spawnParticle(Particle.END_ROD, cx, cy + 0.15, cz, 1, 0, 0, 0, 0, null, true);

        // If in alert state, spark warning electric flashes
        if (isAlert && tick % 4 == 0) {
            world.spawnParticle(Particle.ELECTRIC_SPARK, cx, cy + 0.25, cz, 2, 0.4, 0.1, 0.4, 0.05, null, true);
        }
    }

    private void triggerLandingShockwave(Location loc) {
        World world = loc.getWorld();
        if (world == null) return;

        double cx = loc.getBlockX() + 0.5;
        double cy = loc.getY() + 0.2;
        double cz = loc.getBlockZ() + 0.5;

        // Visual flash & explosion
        world.spawnParticle(Particle.FLASH, cx, cy + 0.5, cz, 3, 0, 0, 0, 0, null, true);
        world.spawnParticle(Particle.EXPLOSION, cx, cy + 0.6, cz, 4, 0.4, 0.2, 0.4, 0.0, null, true);

        // Expanding ground kinetic shockwave (rapid expanding rings)
        Particle.DustOptions blastDust = new Particle.DustOptions(Color.fromRGB(255, 170, 0), 1.6f);
        new BukkitRunnable() {
            int waveStep = 1;

            @Override
            public void run() {
                if (waveStep > 4) {
                    cancel();
                    return;
                }
                double radius = waveStep * 1.25;
                int points = 16 + (waveStep * 4);
                for (int i = 0; i < points; i++) {
                    double angle = (2 * Math.PI / points) * i;
                    double x = cx + radius * Math.cos(angle);
                    double z = cz + radius * Math.sin(angle);
                    world.spawnParticle(Particle.DUST, x, cy, z, 1, 0, 0, 0, 0, blastDust, true);
                    if (waveStep <= 2) {
                        world.spawnParticle(Particle.FIREWORK, x, cy + 0.05, z, 1, 0, 0.02, 0, 0.01, null, true);
                    }
                }
                waveStep++;
            }
        }.runTaskTimer(plugin, 1L, 2L);
    }

    private void renderEmergencyFlares(Location loc) {
        World world = loc.getWorld();
        if (world == null) return;
        double cy = loc.getY() + 0.1;
        double[][] flareOffsets = {
            {1.7, 1.7}, {-1.7, 1.7}, {1.7, -1.7}, {-1.7, -1.7}
        };
        for (double[] offset : flareOffsets) {
            double fx = loc.getBlockX() + 0.5 + offset[0];
            double fz = loc.getBlockZ() + 0.5 + offset[1];
            world.spawnParticle(Particle.FLAME, fx, cy, fz, 1, 0.03, 0.05, 0.03, 0.01, null, true);
            world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, fx, cy + 0.1, fz, 1, 0.04, 0.08, 0.04, 0.008, null, true);
        }
    }

    private void renderLandedShieldDome(Location loc, int tick) {
        World world = loc.getWorld();
        if (world == null) return;
        double cx = loc.getBlockX() + 0.5;
        double cy = loc.getY();
        double cz = loc.getBlockZ() + 0.5;

        Particle.DustOptions shieldCyan = new Particle.DustOptions(Color.fromRGB(0, 240, 255), 1.2f);
        Particle.DustOptions shieldGold = new Particle.DustOptions(Color.fromRGB(255, 215, 0), 1.0f);

        // 1. Forcefield latitude rings (hemisphere over the chest)
        double[][] rings = {
            {0.2, 2.2, 14},
            {1.1, 1.8, 10},
            {1.8, 1.1, 6}
        };
        for (double[] ring : rings) {
            double y = cy + ring[0];
            double r = ring[1];
            int pts = (int) ring[2];
            for (int i = 0; i < pts; i++) {
                double a = (2 * Math.PI / pts) * i + (tick * 0.02);
                world.spawnParticle(Particle.DUST, cx + r * Math.cos(a), y, cz + r * Math.sin(a), 1, 0, 0, 0, 0, shieldCyan, true);
            }
        }

        // 2. Tilted energetic orbiting spark ring
        double orbAngle = (tick * 0.22) % (2 * Math.PI);
        double ox = 1.35 * Math.cos(orbAngle);
        double oz = 1.35 * Math.sin(orbAngle);
        double oy = 0.75 + 0.45 * Math.sin(orbAngle * 2);
        world.spawnParticle(Particle.END_ROD, cx + ox, cy + oy, cz + oz, 1, 0, 0, 0, 0, null, true);
        world.spawnParticle(Particle.DUST, cx - ox, cy + oy, cz - oz, 1, 0, 0, 0, 0, shieldGold, true);
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

    public static class IncomingAirdrop {
        public final String arenaId;
        public final Location dropLocation;
        public final ArmorStand capsuleStand;
        public BukkitTask task;

        public IncomingAirdrop(String arenaId, Location dropLocation, ArmorStand capsuleStand) {
            this.arenaId = arenaId;
            this.dropLocation = dropLocation;
            this.capsuleStand = capsuleStand;
        }

        public void cancel() {
            if (task != null) {
                task.cancel();
                task = null;
            }
            if (capsuleStand != null && capsuleStand.isValid()) {
                capsuleStand.remove();
            }
        }
    }
}
