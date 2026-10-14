package club.aspvp.ffa.duel;

import club.aspvp.ffa.TwoCubeFFA;
import club.aspvp.ffa.api.ArenaSnapshot;
import club.aspvp.ffa.api.KitSnapshot;
import club.aspvp.ffa.api.TwoCubeFFAAPI;
import club.aspvp.ffa.internal.p;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DuelManager implements Listener {

    public static class CageBound {
        public final String arena;
        public final String world;
        public final Location center;
        public final int radius;
        public final int height;
        public final int minX, maxX;
        public final int minY, maxY;
        public final int minZ, maxZ;

        public CageBound(String arena, Location center, int radius, int height) {
            this.arena = arena;
            this.world = center.getWorld() != null ? center.getWorld().getName() : "world";
            this.center = center;
            this.radius = radius;
            this.height = height;
            this.minX = center.getBlockX() - radius;
            this.maxX = center.getBlockX() + radius;
            this.minY = center.getBlockY();
            this.maxY = center.getBlockY() + height + 1;
            this.minZ = center.getBlockZ() - radius;
            this.maxZ = center.getBlockZ() + radius;
        }

        public boolean contains(Location loc) {
            if (loc == null || loc.getWorld() == null) return false;
            if (!loc.getWorld().getName().equalsIgnoreCase(world)) return false;
            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();
            return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
        }
    }

    public static class OpenArenaInvite {
        public final UUID senderId;
        public final String senderName;
        public final String arenaId;
        public final String kitName;
        public final long timestamp;

        public OpenArenaInvite(UUID senderId, String senderName, String arenaId, String kitName, long timestamp) {
            this.senderId = senderId;
            this.senderName = senderName;
            this.arenaId = arenaId;
            this.kitName = kitName;
            this.timestamp = timestamp;
        }

        public boolean isExpired(long timeoutSec) {
            return System.currentTimeMillis() - timestamp > timeoutSec * 1000L;
        }
    }

    private final TwoCubeFFA plugin;
    private final p configManager;
    private final Map<UUID, ActiveCageDuel> activeDuels = new ConcurrentHashMap<>();
    private final Map<UUID, Map<UUID, DuelInvite>> pendingInvites = new ConcurrentHashMap<>();
    private final Map<String, OpenArenaInvite> openArenaInvites = new ConcurrentHashMap<>();
    private final Map<String, CageBound> cageBounds = new ConcurrentHashMap<>();

    public DuelManager(TwoCubeFFA plugin, p configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void init() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        loadCageBounds();
    }

    public FileConfiguration getConfig() {
        return configManager.h("duels.yml");
    }

    public void saveConfig() {
        configManager.i("duels.yml");
    }

    public void loadCageBounds() {
        cageBounds.clear();
        ConfigurationSection sec = getConfig().getConfigurationSection("arenas");
        if (sec == null) return;
        for (String arenaKey : sec.getKeys(false)) {
            String path = "arenas." + arenaKey + ".cage";
            if (getConfig().contains(path + ".world")) {
                World w = Bukkit.getWorld(getConfig().getString(path + ".world", ""));
                if (w != null) {
                    Location loc = new Location(w, getConfig().getDouble(path + ".x"), getConfig().getDouble(path + ".y"), getConfig().getDouble(path + ".z"));
                    int r = getConfig().getInt(path + ".radius", getConfig().getInt("cage.radius-xz", 8));
                    int h = getConfig().getInt(path + ".height", getConfig().getInt("cage.height-y", 6));
                    cageBounds.put(arenaKey.toLowerCase(Locale.ROOT), new CageBound(arenaKey, loc, r, h));
                }
            }
        }
    }

    public boolean isInsideAnyCage(Location loc) {
        if (loc == null) return false;
        for (CageBound bound : cageBounds.values()) {
            if (bound.contains(loc)) return true;
        }
        return false;
    }

    // =========================================================================
    // Indestructible Cage Block Protection (Even in Build Arenas)
    // =========================================================================

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent event) {
        if (isInsideAnyCage(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot break blocks in the Cage Duel arena!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isInsideAnyCage(event.getBlockPlaced().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot place blocks on the Cage Duel arena!");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(b -> isInsideAnyCage(b.getLocation()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(b -> isInsideAnyCage(b.getLocation()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block b : event.getBlocks()) {
            if (isInsideAnyCage(b.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block b : event.getBlocks()) {
            if (isInsideAnyCage(b.getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBlockBurn(BlockBurnEvent event) {
        if (isInsideAnyCage(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    // =========================================================================
    // Command Handling: /duel, /duels, /ffa cage, /cage
    // =========================================================================

    public boolean handleDuelCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly in-game players can use duel commands.");
            return true;
        }

        int offset = 0;
        if (args.length > 0 && (args[0].equalsIgnoreCase("duel") || args[0].equalsIgnoreCase("duels") || args[0].equalsIgnoreCase("cage"))) {
            offset = 1;
        }

        int relativeLen = args.length - offset;
        String sub = relativeLen > 0 ? args[offset].toLowerCase(Locale.ROOT) : "";

        // Check if command is /ffa cage ... or /cage ...
        boolean isCageCmd = (args.length > 0 && args[0].equalsIgnoreCase("cage"))
                || (args.length > 1 && args[0].equalsIgnoreCase("ffa") && args[1].equalsIgnoreCase("cage"));

        if (isCageCmd || sub.equals("setcage") || sub.equals("addcage")) {
            return handleCageAdminCommand(player, args, offset);
        }

        // When a player runs /duels or /duel with no args:
        // Automatically check which arena they are in and broadcast the duel challenge with an accept button!
        if (relativeLen == 0) {
            TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
            Optional<String> arena = api.arenaOf(player);
            if (arena.isPresent()) {
                sendOpenArenaInvite(player, getConfig().getString("default-kit", "nethpot"));
                return true;
            } else {
                sendHelp(player);
                return true;
            }
        }

        switch (sub) {
            case "help": {
                sendHelp(player);
                return true;
            }
            case "accept": {
                String targetSender = relativeLen > 1 ? args[offset + 1] : null;
                acceptInvite(player, targetSender);
                return true;
            }
            case "decline": {
                if (relativeLen <= 1) {
                    player.sendMessage("§cUsage: /duel decline <player>");
                    return true;
                }
                declineInvite(player, args[offset + 1]);
                return true;
            }
            case "leave":
            case "forfeit": {
                ActiveCageDuel duel = activeDuels.get(player.getUniqueId());
                if (duel == null) {
                    player.sendMessage("§cYou are not currently in a cage duel.");
                    return true;
                }
                Player opp = duel.getOpponent(player);
                if (opp != null) {
                    endDuel(opp, player, false);
                } else {
                    activeDuels.remove(player.getUniqueId());
                }
                return true;
            }
            default: {
                // Check if the argument is a kit name for an open arena duel: /duels <kit>
                if (isKnownKit(sub) && Bukkit.getPlayerExact(args[offset]) == null) {
                    TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
                    Optional<String> arena = api.arenaOf(player);
                    if (arena.isPresent()) {
                        sendOpenArenaInvite(player, sub);
                        return true;
                    }
                }

                // Otherwise treat as direct player challenge: /duel <player> [kit]
                String targetName = args[offset];
                Player target = Bukkit.getPlayerExact(targetName);
                if (target == null) {
                    player.sendMessage("§cPlayer '" + targetName + "' is not online (or unknown duel kit).");
                    return true;
                }
                String kit = (relativeLen > 1) ? args[offset + 1].toLowerCase(Locale.ROOT) : getConfig().getString("default-kit", "nethpot");
                sendInvite(player, target, kit);
                return true;
            }
        }
    }

    private boolean handleCageAdminCommand(Player player, String[] args, int offset) {
        if (!player.hasPermission("ffa.admin") && !player.hasPermission("ffa.event.admin")) {
            player.sendMessage("§cYou do not have permission to manage duel cages.");
            return true;
        }

        int subOffset = offset;
        String action = "add";
        if (args.length > subOffset) {
            String val = args[subOffset].toLowerCase(Locale.ROOT);
            if (val.equals("cage") && args.length > subOffset + 1) {
                subOffset++;
                action = args[subOffset].toLowerCase(Locale.ROOT);
            } else if (!val.equals("cage")) {
                action = val;
            }
        }

        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();

        switch (action) {
            case "setcage":
            case "add":
            case "set":
            case "create": {
                String arenaId = null;
                if (args.length > subOffset + 1) {
                    arenaId = args[subOffset + 1];
                } else if (api.arenaOf(player).isPresent()) {
                    arenaId = api.arenaOf(player).get();
                } else {
                    for (ArenaSnapshot a : api.arenas()) {
                        if (a.enabled()) {
                            arenaId = a.id();
                            break;
                        }
                    }
                }

                if (arenaId == null) {
                    player.sendMessage("§cUsage: /ffa cage add <arena> (or stand inside an active arena)");
                    return true;
                }

                setupGlassCage(player, arenaId);
                return true;
            }
            case "remove":
            case "delete": {
                String arenaId = (args.length > subOffset + 1) ? args[subOffset + 1] : api.arenaOf(player).orElse(null);
                if (arenaId == null) {
                    player.sendMessage("§cUsage: /ffa cage remove <arena>");
                    return true;
                }
                removeGlassCage(player, arenaId);
                return true;
            }
            case "tp":
            case "teleport": {
                String arenaId = (args.length > subOffset + 1) ? args[subOffset + 1] : api.arenaOf(player).orElse(null);
                if (arenaId == null) {
                    player.sendMessage("§cUsage: /ffa cage tp <arena>");
                    return true;
                }
                CageBound bound = cageBounds.get(arenaId.toLowerCase(Locale.ROOT));
                if (bound == null || bound.center == null) {
                    player.sendMessage("§cNo cage has been set for arena '" + arenaId + "'.");
                    return true;
                }
                player.teleport(bound.center.clone().add(0, 1, 0));
                player.sendMessage("§aTeleported to the Cage Duel arena for §b" + arenaId + "§a!");
                return true;
            }
            default: {
                player.sendMessage("§8===================================================");
                player.sendMessage("§6§l           ⚔ CAGE ARENA MANAGEMENT ⚔");
                player.sendMessage("§e/ffa cage add [arena] §7- Create & protect glass cage at your location");
                player.sendMessage("§e/ffa cage tp <arena> §7- Teleport to the arena cage");
                player.sendMessage("§e/ffa cage remove <arena> §7- Remove arena cage");
                player.sendMessage("§8===================================================");
                return true;
            }
        }
    }

    public void setupGlassCage(Player admin, String arenaId) {
        Location center = admin.getLocation().getBlock().getLocation().add(0.5, 0.0, 0.5);
        int radiusXZ = getConfig().getInt("cage.radius-xz", 8);
        int heightY = getConfig().getInt("cage.height-y", 6);

        // 1. Build the physical Glass Cage right at the admin's location on the arena ground
        createGlassCage(center, radiusXZ, heightY);

        // 2. Save location to duels.yml
        setCageLocation(arenaId, center, radiusXZ, heightY);

        // 3. Register bounding box for block protection
        CageBound bound = new CageBound(arenaId, center, radiusXZ, heightY);
        cageBounds.put(arenaId.toLowerCase(Locale.ROOT), bound);

        admin.sendMessage("§8===================================================");
        admin.sendMessage("§6§l           ⚔ CAGE DUEL ARENA CREATED ⚔");
        admin.sendMessage("§aSuccessfully generated indestructible Glass Cage for §b" + arenaId + "§a!");
        admin.sendMessage("§7• Location: §eX: " + center.getBlockX() + ", Y: " + center.getBlockY() + ", Z: " + center.getBlockZ());
        admin.sendMessage("§7• Dimensions: §e" + (radiusXZ * 2 + 1) + "x" + (radiusXZ * 2 + 1) + " §7(Height: §e" + heightY + "§7)");
        admin.sendMessage("§7• Protection: §aActive (Indestructible in all arenas including build mode)");
        admin.sendMessage("§8===================================================");
        admin.playSound(admin.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    public void removeGlassCage(Player admin, String arenaId) {
        String key = arenaId.toLowerCase(Locale.ROOT);
        CageBound bound = cageBounds.remove(key);
        if (bound != null) {
            World w = Bukkit.getWorld(bound.world);
            if (w != null) {
                // Clear glass
                for (int y = bound.minY; y <= bound.maxY; y++) {
                    for (int x = bound.minX; x <= bound.maxX; x++) {
                        for (int z = bound.minZ; z <= bound.maxZ; z++) {
                            Block b = w.getBlockAt(x, y, z);
                            if (b.getType() == Material.GLASS || b.getType() == Material.BARRIER) {
                                b.setType(Material.AIR, false);
                            }
                        }
                    }
                }
            }
        }
        getConfig().set("arenas." + key, null);
        saveConfig();
        admin.sendMessage("§aRemoved Cage Duel arena for §b" + arenaId + "§a.");
    }

    public void createGlassCage(Location center, int radius, int height) {
        World world = center.getWorld();
        if (world == null) return;
        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        Material glassMat = Material.GLASS;

        // Floor
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                world.getBlockAt(x, cy, z).setType(glassMat, false);
            }
        }

        // Walls and Interior
        for (int y = cy + 1; y <= cy + height; y++) {
            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    boolean isWall = (x == cx - radius || x == cx + radius || z == cz - radius || z == cz + radius);
                    if (isWall) {
                        world.getBlockAt(x, y, z).setType(glassMat, false);
                    } else {
                        world.getBlockAt(x, y, z).setType(Material.AIR, false);
                    }
                }
            }
        }

        // Roof
        int roofY = cy + height + 1;
        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int z = cz - radius; z <= cz + radius; z++) {
                world.getBlockAt(x, roofY, z).setType(glassMat, false);
            }
        }
    }

    public void setCageLocation(String arenaId, Location loc, int radius, int height) {
        FileConfiguration config = getConfig();
        String path = "arenas." + arenaId.toLowerCase(Locale.ROOT) + ".cage";
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".radius", radius);
        config.set(path + ".height", height);
        saveConfig();
    }

    public void sendHelp(Player player) {
        player.sendMessage("§8===================================================");
        player.sendMessage("§6§l               FLIXCORE FFA DUELS");
        player.sendMessage("§e/duels §7- Send an open cage duel challenge to your arena");
        player.sendMessage("§e/duels <kit> §7- Send an open cage duel challenge with a kit");
        player.sendMessage("§e/duel <player> [kit] §7- Challenge a specific player");
        player.sendMessage("§e/duel accept [player] §7- Accept a pending duel challenge");
        player.sendMessage("§e/duel decline <player> §7- Decline a duel challenge");
        player.sendMessage("§e/duel forfeit §7- Surrender the active duel");
        if (player.hasPermission("ffa.admin") || player.hasPermission("ffa.event.admin")) {
            player.sendMessage("§e/ffa cage add [arena] §7- Set glass duel cage at your location");
        }
        player.sendMessage("§7Supported Kits: §bnethpot§7, §btank§7, or any arena kit");
        player.sendMessage("§8===================================================");
    }

    // =========================================================================
    // Open Arena Duel Challenge (/duels)
    // =========================================================================

    public void sendOpenArenaInvite(Player sender, String kitName) {
        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
        Optional<String> senderArena = api.arenaOf(sender);

        if (senderArena.isEmpty()) {
            sender.sendMessage("§cYou must be inside an FFA arena to challenge the arena to a duel! (Or type: /duel <player> [kit])");
            return;
        }

        String arenaId = senderArena.get();

        CageBound bound = cageBounds.get(arenaId.toLowerCase(Locale.ROOT));
        if (bound == null) {
            // Check if cage configured in duels.yml or fallback
            Location cageLoc = getOrCreateCageCenter(arenaId);
            if (cageLoc == null) {
                sender.sendMessage("§cThe Cage Duel arena for '" + arenaId + "' has not been set yet! An admin must run /ffa cage add " + arenaId + ".");
                return;
            }
        }

        if (activeDuels.containsKey(sender.getUniqueId())) {
            sender.sendMessage("§cYou are already inside an active cage duel!");
            return;
        }

        boolean cageOccupied = activeDuels.values().stream().anyMatch(d -> d.arenaId.equalsIgnoreCase(arenaId));
        if (cageOccupied) {
            sender.sendMessage("§cThe Cage Duel arena in §e" + arenaId + " §cis currently occupied! Please wait for the current duel to finish.");
            return;
        }

        long timeoutSec = getConfig().getLong("invite-timeout-seconds", 30);
        OpenArenaInvite existing = openArenaInvites.get(arenaId.toLowerCase(Locale.ROOT));
        if (existing != null && !existing.isExpired(timeoutSec) && existing.senderId.equals(sender.getUniqueId())) {
            sender.sendMessage("§cYou already have an active duel challenge in this arena! Waiting for an opponent to accept...");
            return;
        }

        OpenArenaInvite invite = new OpenArenaInvite(
                sender.getUniqueId(), sender.getName(),
                arenaId, kitName,
                System.currentTimeMillis()
        );
        openArenaInvites.put(arenaId.toLowerCase(Locale.ROOT), invite);

        // Interactive Clickable Button for Opponents
        TextComponent acceptBtn = new TextComponent("  §a§l▶ [✔ CLICK HERE TO ACCEPT] ◀");
        acceptBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + sender.getName()));
        acceptBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aClick to instantly accept " + sender.getName() + "'s duel challenge!").create()));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (api.arenaOf(p).isPresent() && api.arenaOf(p).get().equalsIgnoreCase(arenaId)) {
                p.sendMessage("§8===================================================");
                p.sendMessage("§6§l           ⚔ IN-ARENA CAGE DUEL CHALLENGE ⚔");
                p.sendMessage("§e" + sender.getName() + " §7has issued an open Cage Duel challenge!");
                p.sendMessage("§7Arena: §6" + arenaId + " §8| §7Kit: §b" + kitName);
                p.sendMessage("");
                if (!p.getUniqueId().equals(sender.getUniqueId())) {
                    p.spigot().sendMessage(acceptBtn);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
                } else {
                    p.sendMessage("§eChallenge broadcasted to §6" + arenaId + "§e! Waiting for an opponent...");
                }
                p.sendMessage("");
                p.sendMessage("§8===================================================");
            }
        }
    }

    // =========================================================================
    // Direct Player Duel Challenge (/duel <player> [kit])
    // =========================================================================

    public void sendInvite(Player sender, Player target, String kitName) {
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            sender.sendMessage("§cYou cannot challenge yourself to a duel!");
            return;
        }

        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
        Optional<String> senderArena = api.arenaOf(sender);
        Optional<String> targetArena = api.arenaOf(target);

        String arenaName = null;
        if (senderArena.isPresent()) {
            arenaName = senderArena.get();
        } else if (targetArena.isPresent()) {
            arenaName = targetArena.get();
        } else {
            for (ArenaSnapshot a : api.arenas()) {
                if (a.enabled()) {
                    arenaName = a.id();
                    break;
                }
            }
        }

        if (arenaName == null) {
            sender.sendMessage("§cNo active FFA arena is available for duels. Create or enable an arena first.");
            return;
        }

        if (activeDuels.containsKey(sender.getUniqueId())) {
            sender.sendMessage("§cYou are already inside an active cage duel!");
            return;
        }
        if (activeDuels.containsKey(target.getUniqueId())) {
            sender.sendMessage("§c" + target.getName() + " is already in a duel!");
            return;
        }

        DuelInvite invite = new DuelInvite(
                sender.getUniqueId(), sender.getName(),
                target.getUniqueId(), target.getName(),
                arenaName, kitName,
                System.currentTimeMillis()
        );

        pendingInvites.computeIfAbsent(target.getUniqueId(), k -> new ConcurrentHashMap<>()).put(sender.getUniqueId(), invite);

        sender.sendMessage(color(getConfig().getString("messages.invite-sent", "&8[&6⚔&8] &eDuel invite sent to &b%target% &e(&6Kit: &f%kit%&e). Expires in 30s.")
                .replace("%target%", target.getName())
                .replace("%kit%", kitName)));

        // Opponent receives interactive clickable ACCEPT and DECLINE buttons (No typing needed!)
        target.sendMessage("§8===================================================");
        target.sendMessage("§6§l           ⚔ IN-ARENA GLADIATOR DUEL ⚔");
        target.sendMessage("§e" + sender.getName() + " §7has challenged you to an In-Arena Cage Duel!");
        target.sendMessage("§7Kit: §b" + kitName + " §8| §7Arena: §e" + arenaName);
        target.sendMessage("");

        TextComponent acceptBtn = new TextComponent("  §a§l▶ [✔ CLICK HERE TO ACCEPT] ◀  ");
        acceptBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + sender.getName()));
        acceptBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aClick to instantly accept " + sender.getName() + "'s duel!").create()));

        TextComponent declineBtn = new TextComponent("§c§l[✖ DECLINE]");
        declineBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel decline " + sender.getName()));
        declineBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cClick to decline this duel challenge").create()));

        TextComponent actionRow = new TextComponent();
        actionRow.addExtra(acceptBtn);
        actionRow.addExtra(declineBtn);
        target.spigot().sendMessage(actionRow);

        target.sendMessage("");
        target.sendMessage("§8===================================================");
        target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
    }

    public void acceptInvite(Player target, String senderName) {
        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
        Optional<String> targetArena = api.arenaOf(target);

        // 1. Check if accepting an open arena invite
        if (targetArena.isPresent()) {
            OpenArenaInvite openInvite = openArenaInvites.get(targetArena.get().toLowerCase(Locale.ROOT));
            long timeoutSec = getConfig().getLong("invite-timeout-seconds", 30);
            if (openInvite != null && !openInvite.isExpired(timeoutSec)) {
                if (senderName == null || senderName.isEmpty() || openInvite.senderName.equalsIgnoreCase(senderName)) {
                    if (openInvite.senderId.equals(target.getUniqueId())) {
                        target.sendMessage("§cYou cannot accept your own duel challenge!");
                        return;
                    }
                    Player sender = Bukkit.getPlayer(openInvite.senderId);
                    if (sender == null || !sender.isOnline()) {
                        target.sendMessage("§cThe challenger is no longer online.");
                        openArenaInvites.remove(targetArena.get().toLowerCase(Locale.ROOT));
                        return;
                    }
                    if (activeDuels.containsKey(sender.getUniqueId()) || activeDuels.containsKey(target.getUniqueId())) {
                        target.sendMessage("§cOne of the players is already participating in an active duel.");
                        return;
                    }
                    openArenaInvites.remove(targetArena.get().toLowerCase(Locale.ROOT));
                    startDuel(sender, target, openInvite.kitName, openInvite.arenaId);
                    return;
                }
            }
        }

        // 2. Direct invite acceptance
        if (senderName == null || senderName.isEmpty()) {
            target.sendMessage("§cUsage: /duel accept <player>");
            return;
        }

        Player sender = Bukkit.getPlayerExact(senderName);
        if (sender == null) {
            target.sendMessage("§cPlayer '" + senderName + "' is no longer online.");
            return;
        }

        Map<UUID, DuelInvite> targetMap = pendingInvites.get(target.getUniqueId());
        if (targetMap == null || !targetMap.containsKey(sender.getUniqueId())) {
            target.sendMessage("§cYou do not have a pending duel invitation from " + sender.getName() + ".");
            return;
        }

        DuelInvite invite = targetMap.remove(sender.getUniqueId());
        long timeoutSec = getConfig().getLong("invite-timeout-seconds", 30);
        if (invite.isExpired(timeoutSec)) {
            target.sendMessage("§cThis duel invitation has expired.");
            return;
        }

        if (activeDuels.containsKey(sender.getUniqueId()) || activeDuels.containsKey(target.getUniqueId())) {
            target.sendMessage("§cOne of the players is already participating in an active duel.");
            return;
        }

        if (!api.isInArena(sender)) {
            api.join(sender, invite.arenaId());
        }
        if (!api.isInArena(target)) {
            api.join(target, invite.arenaId());
        }

        startDuel(sender, target, invite.kitName(), invite.arenaId());
    }

    public void declineInvite(Player target, String senderName) {
        Player sender = Bukkit.getPlayerExact(senderName);
        if (sender != null) {
            Map<UUID, DuelInvite> targetMap = pendingInvites.get(target.getUniqueId());
            if (targetMap != null) targetMap.remove(sender.getUniqueId());
            sender.sendMessage("§c" + target.getName() + " declined your duel invitation.");
        }
        target.sendMessage("§eDuel invitation declined.");
    }

    // =========================================================================
    // Duel Lifecycle & In-Arena Gladiator Cage Execution
    // =========================================================================

    public void startDuel(Player p1, Player p2, String kitName, String arenaId) {
        CageBound bound = cageBounds.get(arenaId.toLowerCase(Locale.ROOT));
        Location center;
        int radiusXZ;
        int heightY;

        if (bound != null && bound.center != null && bound.center.getWorld() != null) {
            center = bound.center;
            radiusXZ = bound.radius;
            heightY = bound.height;
        } else {
            // Find genuine safe ground inside arena (NEVER elevated to the sky!)
            center = getOrCreateCageCenter(arenaId);
            if (center == null || center.getWorld() == null) {
                p1.sendMessage("§cThe Cage Duel arena has not been set yet! An admin must run /ffa cage add " + arenaId + ".");
                p2.sendMessage("§cThe Cage Duel arena has not been set yet! An admin must run /ffa cage add " + arenaId + ".");
                return;
            }
            radiusXZ = getConfig().getInt("cage.radius-xz", 8);
            heightY = getConfig().getInt("cage.height-y", 6);
            createGlassCage(center, radiusXZ, heightY);
            bound = new CageBound(arenaId, center, radiusXZ, heightY);
            cageBounds.put(arenaId.toLowerCase(Locale.ROOT), bound);
        }

        ActiveCageDuel duel = new ActiveCageDuel(
                p1.getUniqueId(), p1.getName(),
                p2.getUniqueId(), p2.getName(),
                arenaId, kitName, center, radiusXZ, heightY
        );

        activeDuels.put(p1.getUniqueId(), duel);
        activeDuels.put(p2.getUniqueId(), duel);

        // Teleport fighters to opposite sides of the glass cage on the arena ground
        Location loc1 = center.clone().add(-radiusXZ + 2.5, 1.0, 0.5);
        Location loc2 = center.clone().add(radiusXZ - 2.5, 1.0, 0.5);
        loc1.setYaw(-90f);
        loc2.setYaw(90f);

        p1.teleport(loc1);
        p2.teleport(loc2);

        // Prepare fighters
        prepareFighter(p1, kitName);
        prepareFighter(p2, kitName);

        // Spectator BossBar
        String bbTitle = color("&6&lCAGE DUEL: &e" + p1.getName() + " &cvs &e" + p2.getName() + " &8[&b" + kitName + "&8]");
        duel.bossBar = Bukkit.createBossBar(bbTitle, BarColor.PURPLE, BarStyle.SOLID);
        duel.bossBar.addPlayer(p1);
        duel.bossBar.addPlayer(p2);
        duel.bossBar.setVisible(true);

        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
        for (Player online : Bukkit.getOnlinePlayers()) {
            api.arenaOf(online).ifPresent(a -> {
                if (a.equalsIgnoreCase(arenaId) && !online.equals(p1) && !online.equals(p2)) {
                    duel.bossBar.addPlayer(online);
                }
            });
        }

        // Broadcast start
        String startMsg = color(getConfig().getString("messages.duel-started", "&8[&6⚔&8] &6⚔ &eA Cage Duel has begun between &b%player1% &eand &b%player2% &8(&6Kit: &f%kit%&8)!")
                .replace("%player1%", p1.getName())
                .replace("%player2%", p2.getName())
                .replace("%kit%", kitName));
        Bukkit.broadcastMessage(startMsg);

        // Boundary particles
        String pName = getConfig().getString("cage.boundary-particles.particle", "END_ROD");
        Particle boundaryParticle;
        try {
            boundaryParticle = Particle.valueOf(pName.toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            boundaryParticle = Particle.END_ROD;
        }
        final Particle finalPart = boundaryParticle;
        duel.particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!duel.active) {
                    cancel();
                    return;
                }
                drawCagePerimeter(duel, finalPart);
            }
        }.runTaskTimer(plugin, 0L, 20L);

        // 3-Second Freeze Countdown
        duel.frozen = true;
        new BukkitRunnable() {
            int count = 3;

            @Override
            public void run() {
                if (!duel.active) {
                    cancel();
                    return;
                }
                if (count > 0) {
                    String title = (count == 3 ? "§c§l3" : (count == 2 ? "§6§l2" : "§e§l1"));
                    String sub = "§7Duel begins in §f" + count + "§7...";
                    sendDuelTitle(p1, title, sub, 0, 25, 5);
                    sendDuelTitle(p2, title, sub, 0, 25, 5);
                    p1.playSound(p1.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1f);
                    p2.playSound(p2.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1f);
                    count--;
                } else {
                    duel.frozen = false;
                    sendDuelTitle(p1, "§a§lFIGHT!", "§eGlory to the victor!", 0, 30, 10);
                    sendDuelTitle(p2, "§a§lFIGHT!", "§eGlory to the victor!", 0, 30, 10);
                    p1.playSound(p1.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1.2f);
                    p2.playSound(p2.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1.2f);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);

        // Maximum duration timer
        int maxSec = getConfig().getInt("max-duration-seconds", 300);
        duel.timerTask = new BukkitRunnable() {
            int remaining = maxSec;

            @Override
            public void run() {
                if (!duel.active) {
                    cancel();
                    return;
                }
                remaining--;
                if (duel.bossBar != null) {
                    double progress = Math.max(0.0, Math.min(1.0, (double) remaining / maxSec));
                    duel.bossBar.setProgress(progress);
                }
                if (remaining <= 0) {
                    p1.sendMessage("§cDuel timed out! It's a draw.");
                    p2.sendMessage("§cDuel timed out! It's a draw.");
                    endDuel(p1, p2, false);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void prepareFighter(Player player, String kitName) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setFireTicks(0);
        for (PotionEffect pe : player.getActivePotionEffects()) {
            player.removePotionEffect(pe.getType());
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        if (kitName.equalsIgnoreCase("tank")) {
            equipTankKit(player);
        } else {
            boolean applied = TwoCubeFFAAPI.get().applyKit(player, kitName);
            if (!applied) {
                if (!TwoCubeFFAAPI.get().applyKit(player, "nethpot")) {
                    equipTankKit(player);
                }
            }
        }
    }

    private void equipTankKit(Player player) {
        ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
        ItemStack chest = new ItemStack(Material.NETHERITE_CHESTPLATE);
        ItemStack legs = new ItemStack(Material.NETHERITE_LEGGINGS);
        ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);

        for (ItemStack is : new ItemStack[]{helmet, chest, legs, boots}) {
            ItemMeta m = is.getItemMeta();
            if (m != null) {
                m.addEnchant(Enchantment.PROTECTION, 4, true);
                m.addEnchant(Enchantment.UNBREAKING, 3, true);
                is.setItemMeta(m);
            }
        }
        ItemMeta bm = boots.getItemMeta();
        if (bm != null) {
            bm.addEnchant(Enchantment.FEATHER_FALLING, 4, true);
            boots.setItemMeta(bm);
        }

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chest);
        player.getInventory().setLeggings(legs);
        player.getInventory().setBoots(boots);

        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta sm = sword.getItemMeta();
        if (sm != null) {
            sm.addEnchant(Enchantment.SHARPNESS, 5, true);
            sm.addEnchant(Enchantment.UNBREAKING, 3, true);
            sword.setItemMeta(sm);
        }
        ItemStack axe = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta am = axe.getItemMeta();
        if (am != null) {
            am.addEnchant(Enchantment.SHARPNESS, 5, true);
            am.addEnchant(Enchantment.EFFICIENCY, 5, true);
            axe.setItemMeta(am);
        }
        ItemStack shield = new ItemStack(Material.SHIELD);

        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, axe);
        player.getInventory().setItemInOffHand(shield);
        player.getInventory().setItem(2, new ItemStack(Material.GOLDEN_APPLE, 16));
        player.getInventory().setItem(3, new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 2));
        player.getInventory().setItem(4, new ItemStack(Material.ENDER_PEARL, 16));
        player.getInventory().setItem(5, new ItemStack(Material.TOTEM_OF_UNDYING, 1));

        ItemStack turtlePot = createSplashPotion(PotionEffectType.RESISTANCE, 40, 2);
        ItemStack healPot = createSplashPotion(PotionEffectType.INSTANT_HEALTH, 1, 1);
        player.getInventory().setItem(6, turtlePot);
        player.getInventory().setItem(7, turtlePot);
        player.getInventory().setItem(8, healPot);
        for (int i = 9; i < 27; i++) {
            player.getInventory().setItem(i, healPot.clone());
        }
    }

    private ItemStack createSplashPotion(PotionEffectType type, int seconds, int amp) {
        ItemStack pot = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) pot.getItemMeta();
        if (meta != null) {
            meta.addCustomEffect(new PotionEffect(type, seconds * 20, amp), true);
            pot.setItemMeta(meta);
        }
        return pot;
    }

    public void endDuel(Player winner, Player loser, boolean byKill) {
        ActiveCageDuel duel = activeDuels.remove(winner.getUniqueId());
        if (duel == null && loser != null) duel = activeDuels.remove(loser.getUniqueId());
        if (duel == null) return;

        duel.active = false;
        activeDuels.remove(duel.player1UUID);
        activeDuels.remove(duel.player2UUID);

        if (duel.timerTask != null) duel.timerTask.cancel();
        if (duel.particleTask != null) duel.particleTask.cancel();
        if (duel.bossBar != null) {
            duel.bossBar.removeAll();
            duel.bossBar = null;
        }

        if (winner != null && winner.isOnline()) {
            sendDuelTitle(winner, "§6§lVICTORY!", "§eYou emerged triumphant!", 10, 60, 20);
            winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            spawnFireworks(winner.getLocation(), 4);

            int tokenReward = getConfig().getInt("rewards.tokens", 250);
            try {
                TwoCubeFFAAPI.get().addTokens(winner, tokenReward);
                winner.sendMessage("§8[§6⚔§8] §a+§e" + tokenReward + " §aTokens for winning the Cage Duel!");
            } catch (Exception ignored) {}

            if (loser != null) {
                String winMsg = color(getConfig().getString("messages.duel-won", "&8[&6⚔&8] &6👑 &a&l%winner% &ehas DEFEATED &c&l%loser% &ein the Cage Duel!")
                        .replace("%winner%", winner.getName())
                        .replace("%loser%", loser.getName()));
                Bukkit.broadcastMessage(winMsg);
            }

            // Release winner back into arena spawn after 5s
            final String duelArenaId = duel.arenaId;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (winner.isOnline()) {
                        ArenaSnapshot arena = TwoCubeFFAAPI.get().arena(duelArenaId).orElse(null);
                        if (arena != null && arena.spawn() != null) {
                            winner.teleport(arena.spawn());
                        } else {
                            Location lobby = TwoCubeFFAAPI.get().lobby();
                            if (lobby != null) winner.teleport(lobby);
                        }
                        winner.setHealth(winner.getMaxHealth());
                        winner.setFoodLevel(20);
                        if (TwoCubeFFAAPI.get().isInArena(winner)) {
                            String primaryKit = (arena != null && !arena.kits().isEmpty()) ? arena.kits().get(0) : "nethpot";
                            TwoCubeFFAAPI.get().applyKit(winner, primaryKit);
                        }
                        winner.sendMessage("§aReturned to the arena!");
                    }
                }
            }.runTaskLater(plugin, 100L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Player victim = (event.getEntity() instanceof Player p) ? p : null;
        Player damager = null;
        if (event.getDamager() instanceof Player p) {
            damager = p;
        } else if (event.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player p) {
            damager = p;
        }

        if (victim != null) {
            ActiveCageDuel vDuel = activeDuels.get(victim.getUniqueId());
            if (vDuel != null) {
                if (vDuel.frozen) {
                    event.setCancelled(true);
                    return;
                }
                // Only opponent can damage victim!
                if (damager == null || !damager.getUniqueId().equals(vDuel.getOpponentUUID(victim.getUniqueId()))) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (damager != null) {
            ActiveCageDuel dDuel = activeDuels.get(damager.getUniqueId());
            if (dDuel != null) {
                if (dDuel.frozen) {
                    event.setCancelled(true);
                    return;
                }
                // Damager in duel can ONLY damage their opponent!
                if (victim == null || !victim.getUniqueId().equals(dDuel.getOpponentUUID(damager.getUniqueId()))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        ActiveCageDuel duel = activeDuels.get(victim.getUniqueId());
        if (duel != null) {
            Player opp = duel.getOpponent(victim);
            if (opp != null) {
                endDuel(opp, victim, true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player quitter = event.getPlayer();
        ActiveCageDuel duel = activeDuels.get(quitter.getUniqueId());
        if (duel != null) {
            Player opp = duel.getOpponent(quitter);
            if (opp != null) {
                String forfeitMsg = color(getConfig().getString("messages.duel-forfeit", "&8[&6⚔&8] &c%loser% disconnected. &a%winner% &ewon by forfeit!")
                        .replace("%winner%", opp.getName())
                        .replace("%loser%", quitter.getName()));
                Bukkit.broadcastMessage(forfeitMsg);
                endDuel(opp, quitter, false);
            }
        }
        pendingInvites.remove(quitter.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ActiveCageDuel duel = activeDuels.get(player.getUniqueId());
        if (duel != null && duel.frozen) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (to != null && (from.getX() != to.getX() || from.getZ() != to.getZ())) {
                event.setTo(from);
            }
        }
    }

    private Location getOrCreateCageCenter(String arenaId) {
        FileConfiguration config = getConfig();
        String path = "arenas." + arenaId.toLowerCase(Locale.ROOT) + ".cage";
        if (config.contains(path + ".world")) {
            World w = Bukkit.getWorld(config.getString(path + ".world"));
            if (w != null) {
                return new Location(w, config.getDouble(path + ".x"), config.getDouble(path + ".y"), config.getDouble(path + ".z"));
            }
        }

        // Locate real ground inside the combat arena (NEVER in the sky!)
        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
        ArenaSnapshot arena = api.arena(arenaId).orElse(null);
        if (arena != null && arena.spawn() != null) {
            Location sp = arena.spawn();
            World w = sp.getWorld();
            int targetX = sp.getBlockX() + 15;
            int targetZ = sp.getBlockZ() + 15;
            Location safeGround = findSafeGround(w, targetX, targetZ, sp.getBlockY() + 5, w.getMinHeight());
            if (safeGround != null) {
                return safeGround;
            }
            return sp.clone().add(15, 0, 15);
        }
        return null;
    }

    private Location findSafeGround(World world, int x, int z, int startY, int minY) {
        startY = Math.min(startY, world.getMaxHeight() - 2);
        minY = Math.max(minY, world.getMinHeight());

        for (int y = startY; y >= minY; y--) {
            Block ground = world.getBlockAt(x, y, z);
            Material mat = ground.getType();
            if (mat == Material.BARRIER || mat == Material.STRUCTURE_VOID || mat == Material.LIGHT || mat.isAir()) {
                continue;
            }
            if (!mat.isSolid()) continue;

            Block b1 = world.getBlockAt(x, y + 1, z);
            Block b2 = world.getBlockAt(x, y + 2, z);
            if (!b1.getType().isSolid() && b1.getType() != Material.BARRIER && !b2.getType().isSolid() && b2.getType() != Material.BARRIER) {
                return new Location(world, x + 0.5, y + 1, z + 0.5);
            }
        }
        return null;
    }

    private void drawCagePerimeter(ActiveCageDuel duel, Particle particle) {
        World world = duel.cageCenter.getWorld();
        if (world == null) return;

        double y = duel.cageCenter.getY() + 1.1;
        double minX = duel.cageCenter.getX() - duel.radiusXZ;
        double maxX = duel.cageCenter.getX() + duel.radiusXZ;
        double minZ = duel.cageCenter.getZ() - duel.radiusXZ;
        double maxZ = duel.cageCenter.getZ() + duel.radiusXZ;

        for (double x = minX; x <= maxX; x += 1.0) {
            world.spawnParticle(particle, x, y, minZ, 1, 0, 0, 0, 0.0);
            world.spawnParticle(particle, x, y, maxZ, 1, 0, 0, 0, 0.0);
        }
        for (double z = minZ; z <= maxZ; z += 1.0) {
            world.spawnParticle(particle, minX, y, z, 1, 0, 0, 0, 0.0);
            world.spawnParticle(particle, maxX, y, z, 1, 0, 0, 0, 0.0);
        }
    }

    private void spawnFireworks(Location loc, int count) {
        World world = loc.getWorld();
        if (world == null) return;
        for (int i = 0; i < count; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Firework fw = (Firework) world.spawnEntity(loc, EntityType.FIREWORK_ROCKET);
                FireworkMeta fwm = fw.getFireworkMeta();
                fwm.addEffect(FireworkEffect.builder().withColor(Color.ORANGE, Color.YELLOW).withFade(Color.RED).with(FireworkEffect.Type.BALL_LARGE).build());
                fwm.setPower(1);
                fw.setFireworkMeta(fwm);
            }, i * 12L);
        }
    }

    private boolean isKnownKit(String kit) {
        if (kit == null) return false;
        String k = kit.toLowerCase(Locale.ROOT);
        if (k.equals("nethpot") || k.equals("tank")) return true;
        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
        for (KitSnapshot snap : api.kits()) {
            if (snap.id().equalsIgnoreCase(k)) return true;
        }
        return false;
    }

    public List<String> tabCompleteDuel(CommandSender sender, String[] args) {
        int offset = 0;
        if (args.length > 0 && (args[0].equalsIgnoreCase("duel") || args[0].equalsIgnoreCase("duels") || args[0].equalsIgnoreCase("cage"))) {
            offset = 1;
        }

        int relativeLen = args.length - offset;
        List<String> completions = new ArrayList<>();

        if (relativeLen == 1) {
            String current = args[args.length - 1].toLowerCase(Locale.ROOT);
            for (String sub : Arrays.asList("accept", "decline", "forfeit", "leave", "help")) {
                if (sub.startsWith(current)) completions.add(sub);
            }
            if (sender.hasPermission("ffa.admin")) {
                for (String sub : Arrays.asList("add", "remove", "tp", "info")) {
                    if (sub.startsWith(current)) completions.add(sub);
                }
            }
            // Kits
            for (String kit : Arrays.asList("nethpot", "tank")) {
                if (kit.startsWith(current)) completions.add(kit);
            }
            // Online players
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (sender instanceof Player && p.getUniqueId().equals(((Player) sender).getUniqueId())) continue;
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(current)) {
                    completions.add(p.getName());
                }
            }
        } else if (relativeLen == 2) {
            String first = args[offset].toLowerCase(Locale.ROOT);
            String current = args[args.length - 1].toLowerCase(Locale.ROOT);

            if (first.equals("add") || first.equals("remove") || first.equals("tp") || first.equals("info")) {
                TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
                for (ArenaSnapshot a : api.arenas()) {
                    if (a.id().toLowerCase(Locale.ROOT).startsWith(current)) {
                        completions.add(a.id());
                    }
                }
            } else if (first.equals("accept") || first.equals("decline")) {
                if (sender instanceof Player p) {
                    Map<UUID, DuelInvite> invites = pendingInvites.get(p.getUniqueId());
                    if (invites != null) {
                        for (DuelInvite inv : invites.values()) {
                            if (inv.senderName().toLowerCase(Locale.ROOT).startsWith(current)) {
                                completions.add(inv.senderName());
                            }
                        }
                    }
                }
            } else {
                for (String kit : Arrays.asList("nethpot", "tank")) {
                    if (kit.startsWith(current)) completions.add(kit);
                }
                TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
                for (KitSnapshot kitSnap : api.kits()) {
                    if (kitSnap.id().toLowerCase(Locale.ROOT).startsWith(current)) {
                        completions.add(kitSnap.id());
                    }
                }
            }
        }
        return completions;
    }

    private void sendDuelTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public void cleanup() {
        for (ActiveCageDuel duel : new HashSet<>(activeDuels.values())) {
            if (duel.timerTask != null) duel.timerTask.cancel();
            if (duel.particleTask != null) duel.particleTask.cancel();
            if (duel.bossBar != null) duel.bossBar.removeAll();
        }
        activeDuels.clear();
        pendingInvites.clear();
        openArenaInvites.clear();
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
