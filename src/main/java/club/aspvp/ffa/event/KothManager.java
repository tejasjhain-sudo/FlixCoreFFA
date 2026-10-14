package club.aspvp.ffa.event;

import club.aspvp.ffa.TwoCubeFFA;
import club.aspvp.ffa.api.ArenaRegionSnapshot;
import club.aspvp.ffa.api.ArenaSnapshot;
import club.aspvp.ffa.api.TwoCubeFFAAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class KothManager {

    private final TwoCubeFFA plugin;
    private final EventManager eventManager;
    private final Map<String, ActiveKoth> activeGames = new ConcurrentHashMap<>();
    private BukkitTask autoTask;

    public KothManager(TwoCubeFFA plugin, EventManager eventManager) {
        this.plugin = plugin;
        this.eventManager = eventManager;
    }

    public void startAutoScheduler() {
        stopAutoScheduler();
        FileConfiguration config = eventManager.getConfig();
        if (!config.getBoolean("koth.enabled", true)) return;

        long intervalMinutes = config.getLong("koth.interval-minutes", 45);
        if (intervalMinutes <= 0) return;

        long ticks = intervalMinutes * 60L * 20L;
        this.autoTask = new BukkitRunnable() {
            @Override
            public void run() {
                startKoth(null, null);
            }
        }.runTaskTimer(plugin, ticks, ticks);
    }

    public void stopAutoScheduler() {
        if (this.autoTask != null) {
            this.autoTask.cancel();
            this.autoTask = null;
        }
    }

    public boolean startKoth(String arenaId, CommandSender sender) {
        FileConfiguration config = eventManager.getConfig();
        if (!config.getBoolean("koth.enabled", true) && sender == null) {
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
        if (activeGames.containsKey(finalArenaId.toLowerCase(Locale.ROOT))) {
            if (sender != null) sender.sendMessage("§cA KOTH event is already active in arena '" + finalArenaId + "'.");
            return false;
        }

        KothZone zone = getOrCreateZone(finalArenaId, targetArena);
        if (zone == null || zone.getWorld() == null) {
            if (sender != null) sender.sendMessage("§cCould not establish a KOTH capture zone for arena '" + finalArenaId + "'.");
            return false;
        }

        int captureSeconds = config.getInt("koth.capture-seconds", 60);
        int maxDurationMinutes = config.getInt("koth.max-duration-minutes", 15);

        String initialTitle = color(config.getString("koth.bossbar.uncontested-title", "&6&lKOTH &8» &e%arena% &8| &7Stand in zone to capture! &8(&e%time_left%s&8)")
                .replace("%arena%", finalArenaId)
                .replace("%time_left%", String.valueOf(captureSeconds)));

        BossBar bossBar = Bukkit.createBossBar(initialTitle, BarColor.YELLOW, BarStyle.SEGMENTED_10);
        bossBar.setProgress(0.0);
        bossBar.setVisible(true);

        ActiveKoth koth = new ActiveKoth(finalArenaId, zone, bossBar, captureSeconds, maxDurationMinutes * 60);
        activeGames.put(finalArenaId.toLowerCase(Locale.ROOT), koth);

        // Visual boundary particles task (every 10 ticks = 0.5s)
        if (config.getBoolean("koth.boundary-particles.enabled", true)) {
            String particleName = config.getString("koth.boundary-particles.particle", "FLAME");
            Particle particle;
            try {
                particle = Particle.valueOf(particleName.toUpperCase(Locale.ROOT));
            } catch (Exception ex) {
                particle = Particle.FLAME;
            }
            final Particle finalParticle = particle;
            koth.particleTask = new BukkitRunnable() {
                @Override
                public void run() {
                    drawZoneBoundary(zone, finalParticle);
                }
            }.runTaskTimer(plugin, 0L, 10L);
        }

        // 1-second capture tick loop
        koth.tickTask = new BukkitRunnable() {
            @Override
            public void run() {
                tickKoth(koth);
            }
        }.runTaskTimer(plugin, 20L, 20L);

        String startMsg = color(config.getString("koth.broadcast.started", "&8[&6&lFlix&eFFA&8] &6👑 King of the Hill (KOTH) &ehas begun in arena &b%arena%&e! Control the hill for &c%capture_time%s &eto win!")
                .replace("%arena%", finalArenaId)
                .replace("%capture_time%", String.valueOf(captureSeconds)));
        Bukkit.broadcastMessage(startMsg);

        if (sender != null) {
            sender.sendMessage("§aKOTH event started in arena §e" + finalArenaId + "§a!");
        }
        return true;
    }

    private void tickKoth(ActiveKoth koth) {
        FileConfiguration config = eventManager.getConfig();
        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();

        // Check overall timeout
        koth.totalSecondsElapsed++;
        if (koth.totalSecondsElapsed >= koth.maxSeconds) {
            String timeoutMsg = color(config.getString("koth.broadcast.timeout", "&8[&6&lFlix&eFFA&8] &c👑 KOTH in arena &b%arena% &eended with no winner.")
                    .replace("%arena%", koth.arenaId));
            Bukkit.broadcastMessage(timeoutMsg);
            stopKoth(koth.arenaId, false);
            return;
        }

        // Collect players in arena
        List<Player> arenaPlayers = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            Optional<String> playerArena = api.arenaOf(p);
            if (playerArena.isPresent() && playerArena.get().equalsIgnoreCase(koth.arenaId)) {
                arenaPlayers.add(p);
                if (!koth.bossBar.getPlayers().contains(p)) {
                    koth.bossBar.addPlayer(p);
                }
            } else {
                if (koth.bossBar.getPlayers().contains(p)) {
                    koth.bossBar.removePlayer(p);
                }
            }
        }

        // Find players standing inside the KOTH zone
        List<Player> zonePlayers = new ArrayList<>();
        for (Player p : arenaPlayers) {
            if (p.getGameMode() == GameMode.SURVIVAL && !p.isDead() && koth.zone.contains(p.getLocation())) {
                zonePlayers.add(p);
            }
        }

        if (zonePlayers.isEmpty()) {
            // Uncontested
            koth.bossBar.setColor(BarColor.YELLOW);
            int timeLeft = koth.requiredSeconds - koth.cappingSeconds;
            String title = color(config.getString("koth.bossbar.uncontested-title", "&6&lKOTH &8» &e%arena% &8| &7Stand in zone to capture! &8(&e%time_left%s&8)")
                    .replace("%arena%", koth.arenaId)
                    .replace("%time_left%", String.valueOf(timeLeft)));
            koth.bossBar.setTitle(title);
            double progress = (double) koth.cappingSeconds / koth.requiredSeconds;
            koth.bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        } else if (zonePlayers.size() == 1) {
            // Single capper
            Player capper = zonePlayers.get(0);
            if (capper.getUniqueId().equals(koth.currentCapperUUID)) {
                koth.cappingSeconds++;
            } else {
                koth.currentCapperUUID = capper.getUniqueId();
                koth.currentCapperName = capper.getName();
                koth.cappingSeconds = 1;
            }

            int timeLeft = Math.max(0, koth.requiredSeconds - koth.cappingSeconds);
            double progress = (double) koth.cappingSeconds / koth.requiredSeconds;

            koth.bossBar.setColor(BarColor.GREEN);
            String title = color(config.getString("koth.bossbar.capping-title", "&6&lKOTH &8» &e%arena% &8| &a%player% &8(&b%time_left%s left&8)")
                    .replace("%arena%", koth.arenaId)
                    .replace("%player%", capper.getName())
                    .replace("%time_left%", String.valueOf(timeLeft)));
            koth.bossBar.setTitle(title);
            koth.bossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));

            // Action bar progress
            String progressBar = buildProgressBar(koth.cappingSeconds, koth.requiredSeconds);
            String abMsg = color(config.getString("koth.actionbar.capping", "&6&lKOTH: &a%player% &7is capturing! &e%time_left%s remaining &8[%progress_bar%&8]")
                    .replace("%player%", capper.getName())
                    .replace("%time_left%", String.valueOf(timeLeft))
                    .replace("%progress_bar%", progressBar));
            for (Player p : arenaPlayers) {
                p.sendActionBar(abMsg);
            }

            // Check Win Condition!
            if (koth.cappingSeconds >= koth.requiredSeconds) {
                onWin(capper, koth);
            }
        } else {
            // Contested!
            koth.bossBar.setColor(BarColor.RED);
            String title = color(config.getString("koth.bossbar.contested-title", "&6&lKOTH &8» &e%arena% &8| &c&lCONTESTED! &8(&f%players% players&8)")
                    .replace("%arena%", koth.arenaId)
                    .replace("%players%", String.valueOf(zonePlayers.size())));
            koth.bossBar.setTitle(title);

            String contMsg = color(config.getString("koth.actionbar.contested", "&c&lKOTH IS CONTESTED! &7Fight for control of the hill!"));
            for (Player p : arenaPlayers) {
                p.sendActionBar(contMsg);
            }
        }
    }

    private void onWin(Player winner, ActiveKoth koth) {
        FileConfiguration config = eventManager.getConfig();
        String winMsg = color(config.getString("koth.broadcast.captured", "&8[&6&lFlix&eFFA&8] &6👑 &a%player% &ehas conquered the &b%arena% &eKOTH and claimed the crown!")
                .replace("%player%", winner.getName())
                .replace("%arena%", koth.arenaId));
        Bukkit.broadcastMessage(winMsg);

        // Reward tokens
        int tokens = config.getInt("koth.rewards.tokens", 750);
        if (tokens > 0) {
            TwoCubeFFAAPI.get().addTokens(winner, tokens);
            winner.sendMessage("§8[§6👑§8] §eYou were awarded §6" + tokens + " tokens §efor capturing the Hill!");
        }

        // Title
        String mainTitle = color(config.getString("koth.rewards.title.title", "&6&lVICTORY!"));
        String subTitle = color(config.getString("koth.rewards.title.subtitle", "&eYou captured the %arena% KOTH!").replace("%arena%", koth.arenaId));
        int fadeIn = config.getInt("koth.rewards.title.fadeIn", 10);
        int stay = config.getInt("koth.rewards.title.stay", 70);
        int fadeOut = config.getInt("koth.rewards.title.fadeOut", 20);
        winner.sendTitle(mainTitle, subTitle, fadeIn, stay, fadeOut);

        // Sound
        winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0f, 1.0f);

        // Fireworks
        int fwCount = config.getInt("koth.rewards.fireworks", 3);
        Location loc = winner.getLocation().add(0, 1, 0);
        for (int i = 0; i < fwCount; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (winner.isOnline()) {
                    Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
                    FireworkMeta fwm = fw.getFireworkMeta();
                    fwm.addEffect(FireworkEffect.builder().withColor(Color.YELLOW, Color.ORANGE).withFade(Color.RED).with(FireworkEffect.Type.BALL_LARGE).build());
                    fwm.setPower(1);
                    fw.setFireworkMeta(fwm);
                }
            }, i * 15L);
        }

        // Custom console commands
        List<String> commands = config.getStringList("koth.rewards.commands");
        for (String cmd : commands) {
            String formattedCmd = cmd.replace("%player%", winner.getName()).replace("%arena%", koth.arenaId);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCmd);
        }

        stopKoth(koth.arenaId, false);
    }

    public void stopKoth(String arenaId, boolean broadcast) {
        ActiveKoth koth = activeGames.remove(arenaId.toLowerCase(Locale.ROOT));
        if (koth == null) return;

        if (koth.tickTask != null) koth.tickTask.cancel();
        if (koth.particleTask != null) koth.particleTask.cancel();
        koth.bossBar.removeAll();

        if (broadcast) {
            Bukkit.broadcastMessage("§8[§6&lFlix&eFFA§8] §c👑 KOTH in arena §b" + arenaId + " §ehas been stopped.");
        }
    }

    public void cleanup() {
        stopAutoScheduler();
        for (String arenaId : new ArrayList<>(activeGames.keySet())) {
            stopKoth(arenaId, false);
        }
        activeGames.clear();
    }

    public boolean setZone(String arenaId, Location min, Location max) {
        KothZone zone = KothZone.fromCorners(min, max);
        FileConfiguration config = eventManager.getConfig();
        ConfigurationSection sec = config.getConfigurationSection("koth.zones." + arenaId.toLowerCase(Locale.ROOT));
        if (sec == null) {
            sec = config.createSection("koth.zones." + arenaId.toLowerCase(Locale.ROOT));
        }
        zone.saveToConfig(sec);
        eventManager.saveConfig();
        return true;
    }

    private KothZone getOrCreateZone(String arenaId, ArenaSnapshot arena) {
        FileConfiguration config = eventManager.getConfig();
        ConfigurationSection sec = config.getConfigurationSection("koth.zones." + arenaId.toLowerCase(Locale.ROOT));
        if (sec != null) {
            KothZone z = KothZone.fromConfig(sec);
            if (z != null) return z;
        }

        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
        Optional<ArenaRegionSnapshot> regOpt = api.arenaRegion(arenaId);
        World world = arena.spawn() != null ? arena.spawn().getWorld() : null;

        if (regOpt.isPresent()) {
            ArenaRegionSnapshot reg = regOpt.get();
            if (world == null && reg.world() != null) {
                world = Bukkit.getWorld(reg.world());
            }
            if (world != null) {
                double centerX = (reg.minX() + reg.maxX()) / 2.0;
                double centerZ = (reg.minZ() + reg.maxZ()) / 2.0;
                int minY = (int) Math.min(reg.minY(), reg.maxY());
                int maxY = (int) Math.max(reg.minY(), reg.maxY());

                // If region center is too close to spawn (< 20 blocks), shift it into the battlefield
                if (arena.spawn() != null) {
                    Location sp = arena.spawn();
                    double dx = centerX - sp.getX();
                    double dz = centerZ - sp.getZ();
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    if (dist < 20.0) {
                        if (dist < 1.0) {
                            centerX = sp.getX() + 25.0;
                            centerZ = sp.getZ() + 25.0;
                        } else {
                            centerX = sp.getX() + (dx / dist) * 28.0;
                            centerZ = sp.getZ() + (dz / dist) * 28.0;
                        }
                    }
                }

                // Find real solid floor at (centerX, centerZ) skipping all barriers
                int groundY = findRealGroundY(world, (int) centerX, (int) centerZ, maxY, minY);
                if (groundY != -1) {
                    KothZone zone = new KothZone(
                            world.getName(),
                            centerX - 2.5, groundY, centerZ - 2.5,
                            centerX + 2.5, groundY + 4.0, centerZ + 2.5
                    );
                    ConfigurationSection newSec = config.createSection("koth.zones." + arenaId.toLowerCase(Locale.ROOT));
                    zone.saveToConfig(newSec);
                    eventManager.saveConfig();
                    return zone;
                }
            }
        }

        // Fallback: 25 blocks away from spawn in combat direction
        if (arena.spawn() != null) {
            Location sp = arena.spawn();
            World spWorld = sp.getWorld();
            double targetX = sp.getX() + 25.0;
            double targetZ = sp.getZ() + 25.0;
            int groundY = findRealGroundY(spWorld, (int) targetX, (int) targetZ, sp.getBlockY() + 15, Math.max(spWorld.getMinHeight(), sp.getBlockY() - 25));
            if (groundY == -1) groundY = sp.getBlockY();

            KothZone defaultZone = new KothZone(
                    spWorld.getName(),
                    targetX - 2.5, groundY, targetZ - 2.5,
                    targetX + 2.5, groundY + 4.0, targetZ + 2.5
            );
            ConfigurationSection newSec = config.createSection("koth.zones." + arenaId.toLowerCase(Locale.ROOT));
            defaultZone.saveToConfig(newSec);
            eventManager.saveConfig();
            return defaultZone;
        }

        return null;
    }

    private int findRealGroundY(World world, int x, int z, int startY, int minY) {
        startY = Math.min(startY, world.getMaxHeight() - 2);
        minY = Math.max(minY, world.getMinHeight());
        for (int y = startY; y >= minY; y--) {
            Block b = world.getBlockAt(x, y, z);
            Material m = b.getType();
            if (m == Material.BARRIER || m == Material.STRUCTURE_VOID || m == Material.LIGHT || m.isAir()) {
                continue;
            }
            if (!m.isSolid()) {
                continue;
            }
            Block above1 = world.getBlockAt(x, y + 1, z);
            Block above2 = world.getBlockAt(x, y + 2, z);
            if (above1.getType() != Material.BARRIER && !above1.getType().isSolid()
                    && above2.getType() != Material.BARRIER && !above2.getType().isSolid()) {
                return y + 1;
            }
        }
        return -1;
    }

    private void drawZoneBoundary(KothZone zone, Particle particle) {
        World world = zone.getWorld();
        if (world == null) return;

        double y = zone.getMinY() + 0.15;
        double step = 0.5;

        // Perimeter line 1: (minX, minZ) -> (maxX, minZ)
        for (double x = zone.getMinX(); x <= zone.getMaxX(); x += step) {
            world.spawnParticle(particle, x, y, zone.getMinZ(), 1, 0, 0, 0, 0.0);
        }
        // Perimeter line 2: (maxX, minZ) -> (maxX, maxZ)
        for (double z = zone.getMinZ(); z <= zone.getMaxZ(); z += step) {
            world.spawnParticle(particle, zone.getMaxX(), y, z, 1, 0, 0, 0, 0.0);
        }
        // Perimeter line 3: (maxX, maxZ) -> (minX, maxZ)
        for (double x = zone.getMaxX(); x >= zone.getMinX(); x -= step) {
            world.spawnParticle(particle, x, y, zone.getMaxZ(), 1, 0, 0, 0, 0.0);
        }
        // Perimeter line 4: (minX, maxZ) -> (minX, minZ)
        for (double z = zone.getMaxZ(); z >= zone.getMinZ(); z -= step) {
            world.spawnParticle(particle, zone.getMinX(), y, z, 1, 0, 0, 0, 0.0);
        }
    }

    private String buildProgressBar(int current, int max) {
        int totalBars = 12;
        int completed = (int) Math.round(((double) current / max) * totalBars);
        completed = Math.max(0, Math.min(totalBars, completed));
        return "§a" + "|".repeat(completed) + "§7" + "|".repeat(totalBars - completed);
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public Map<String, ActiveKoth> getActiveGames() {
        return activeGames;
    }

    public static class ActiveKoth {
        public final String arenaId;
        public final KothZone zone;
        public final BossBar bossBar;
        public final int requiredSeconds;
        public final int maxSeconds;
        public int totalSecondsElapsed = 0;
        public UUID currentCapperUUID = null;
        public String currentCapperName = null;
        public int cappingSeconds = 0;
        public BukkitTask tickTask;
        public BukkitTask particleTask;

        public ActiveKoth(String arenaId, KothZone zone, BossBar bossBar, int requiredSeconds, int maxSeconds) {
            this.arenaId = arenaId;
            this.zone = zone;
            this.bossBar = bossBar;
            this.requiredSeconds = requiredSeconds;
            this.maxSeconds = maxSeconds;
        }
    }
}
