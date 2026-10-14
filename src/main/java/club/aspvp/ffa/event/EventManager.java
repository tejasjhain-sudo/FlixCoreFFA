package club.aspvp.ffa.event;

import club.aspvp.ffa.TwoCubeFFA;
import club.aspvp.ffa.api.ArenaSnapshot;
import club.aspvp.ffa.api.TwoCubeFFAAPI;
import club.aspvp.ffa.internal.p;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class EventManager implements Listener {

    private final TwoCubeFFA plugin;
    private final p configManager;
    private final AirdropManager airdropManager;
    private final KothManager kothManager;

    public EventManager(TwoCubeFFA plugin, p configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.airdropManager = new AirdropManager(plugin, this);
        this.kothManager = new KothManager(plugin, this);
    }

    public void init() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        airdropManager.startAutoScheduler();
        kothManager.startAutoScheduler();
    }

    public void cleanup() {
        airdropManager.cleanup();
        kothManager.cleanup();
    }

    public FileConfiguration getConfig() {
        return configManager.h("events.yml");
    }

    public void saveConfig() {
        configManager.i("events.yml");
    }

    public AirdropManager getAirdropManager() {
        return airdropManager;
    }

    public KothManager getKothManager() {
        return kothManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.CHEST) return;

        if (airdropManager.handleInteract(event.getPlayer(), event.getClickedBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.CHEST) return;
        for (AirdropManager.ActiveAirdrop airdrop : airdropManager.getActiveAirdrops().values()) {
            if (airdrop.chestBlock.equals(event.getBlock())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cYou cannot break the Supply Crate! Right-click to claim the loot.");
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        for (AirdropManager.ActiveAirdrop airdrop : airdropManager.getActiveAirdrops().values()) {
            event.blockList().remove(airdrop.chestBlock);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        for (AirdropManager.ActiveAirdrop airdrop : airdropManager.getActiveAirdrops().values()) {
            event.blockList().remove(airdrop.chestBlock);
        }
    }

    public boolean handleAirdropCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("ffa.admin") && !sender.hasPermission("ffa.event.admin")) {
            sender.sendMessage("§cYou do not have permission to manage FFA live events.");
            return true;
        }

        // Subcommands: /ffa airdrop [start|stop|list] [arena]
        int offset = (args.length > 0 && args[0].equalsIgnoreCase("airdrop")) ? 1 : 0;
        String sub = (args.length > offset) ? args[offset].toLowerCase(Locale.ROOT) : "help";

        switch (sub) {
            case "start":
            case "spawn": {
                String arena = (args.length > offset + 1) ? args[offset + 1] : null;
                airdropManager.spawnAirdrop(arena, sender);
                return true;
            }
            case "stop":
            case "despawn": {
                if (args.length <= offset + 1) {
                    sender.sendMessage("§cUsage: /ffa airdrop stop <arena>");
                    return true;
                }
                String arena = args[offset + 1];
                airdropManager.despawnAirdrop(arena, true);
                sender.sendMessage("§aDespawned airdrop in arena §e" + arena + "§a.");
                return true;
            }
            case "list": {
                Map<String, AirdropManager.ActiveAirdrop> map = airdropManager.getActiveAirdrops();
                if (map.isEmpty()) {
                    sender.sendMessage("§eNo active supply crates at this moment.");
                } else {
                    sender.sendMessage("§6§lActive Supply Crates (" + map.size() + "):");
                    map.values().forEach(a -> sender.sendMessage("§8 - §b" + a.arenaId + " §7at §f" + a.location.getBlockX() + ", " + a.location.getBlockY() + ", " + a.location.getBlockZ() + " §8(§c" + a.secondsRemaining + "s left§8)"));
                }
                return true;
            }
            default:
                sender.sendMessage("§6§lFlixCore FFA - Airdrop Commands:");
                sender.sendMessage("§e/ffa airdrop start [arena] §7- Drop a supply crate");
                sender.sendMessage("§e/ffa airdrop stop <arena>  §7- Remove active supply crate");
                sender.sendMessage("§e/ffa airdrop list          §7- List active supply crates");
                return true;
        }
    }

    public boolean handleKothCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("ffa.admin") && !sender.hasPermission("ffa.event.admin")) {
            sender.sendMessage("§cYou do not have permission to manage FFA live events.");
            return true;
        }

        int offset = (args.length > 0 && args[0].equalsIgnoreCase("koth")) ? 1 : 0;
        String sub = (args.length > offset) ? args[offset].toLowerCase(Locale.ROOT) : "help";

        switch (sub) {
            case "start": {
                String arena = (args.length > offset + 1) ? args[offset + 1] : null;
                kothManager.startKoth(arena, sender);
                return true;
            }
            case "stop": {
                if (args.length <= offset + 1) {
                    sender.sendMessage("§cUsage: /ffa koth stop <arena>");
                    return true;
                }
                String arena = args[offset + 1];
                kothManager.stopKoth(arena, true);
                sender.sendMessage("§aStopped KOTH in arena §e" + arena + "§a.");
                return true;
            }
            case "setzone": {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cOnly in-game players can set KOTH zones.");
                    return true;
                }
                if (args.length <= offset + 1) {
                    sender.sendMessage("§cUsage: /ffa koth setzone <arena>");
                    return true;
                }
                String arena = args[offset + 1];
                Optional<ArenaSnapshot> arenaOpt = TwoCubeFFAAPI.get().arena(arena);
                if (arenaOpt.isEmpty()) {
                    sender.sendMessage("§cArena '" + arena + "' does not exist.");
                    return true;
                }
                Location pLoc = player.getLocation();
                Location min = pLoc.clone().add(-2.5, -1.0, -2.5);
                Location max = pLoc.clone().add(2.5, 3.0, 2.5);
                kothManager.setZone(arena, min, max);
                sender.sendMessage("§aSuccessfully defined 5x5 KOTH capture zone for arena §b" + arena + " §aat your location!");
                return true;
            }
            case "list": {
                Map<String, KothManager.ActiveKoth> map = kothManager.getActiveGames();
                if (map.isEmpty()) {
                    sender.sendMessage("§eNo active KOTH events at this moment.");
                } else {
                    sender.sendMessage("§6§lActive KOTH Events (" + map.size() + "):");
                    map.values().forEach(k -> {
                        String capper = k.currentCapperName != null ? k.currentCapperName : "None";
                        sender.sendMessage("§8 - §b" + k.arenaId + " §7| Capper: §a" + capper + " §8(§e" + k.cappingSeconds + "/" + k.requiredSeconds + "s§8)");
                    });
                }
                return true;
            }
            default:
                sender.sendMessage("§6§lFlixCore FFA - King of the Hill (KOTH) Commands:");
                sender.sendMessage("§e/ffa koth start [arena]    §7- Start KOTH in arena");
                sender.sendMessage("§e/ffa koth stop <arena>     §7- Stop active KOTH");
                sender.sendMessage("§e/ffa koth setzone <arena>  §7- Set 5x5 KOTH zone at your feet");
                sender.sendMessage("§e/ffa koth list             §7- List active KOTH events");
                return true;
        }
    }

    public boolean handleEventsCommand(CommandSender sender, String[] args) {
        sender.sendMessage("§8===============================================");
        sender.sendMessage("§6§l        FlixCore FFA - Dynamic Live Events");
        sender.sendMessage("§8===============================================");
        sender.sendMessage("§e★ §6Supply Crates (Airdrops): §f" + airdropManager.getActiveAirdrops().size() + " active");
        for (AirdropManager.ActiveAirdrop a : airdropManager.getActiveAirdrops().values()) {
            sender.sendMessage("   §8» §b" + a.arenaId + " §7at §f" + a.location.getBlockX() + ", " + a.location.getBlockY() + ", " + a.location.getBlockZ() + " §8(§c" + a.secondsRemaining + "s left§8)");
        }
        sender.sendMessage("§e★ §6King of the Hill (KOTH): §f" + kothManager.getActiveGames().size() + " active");
        for (KothManager.ActiveKoth k : kothManager.getActiveGames().values()) {
            String capper = k.currentCapperName != null ? k.currentCapperName : "None";
            sender.sendMessage("   §8» §b" + k.arenaId + " §7| Capper: §a" + capper + " §8(§e" + k.cappingSeconds + "/" + k.requiredSeconds + "s§8)");
        }
        sender.sendMessage("§8===============================================");
        return true;
    }

    public List<String> tabCompleteAirdrop(CommandSender sender, String[] args) {
        int offset = (args.length > 0 && args[0].equalsIgnoreCase("airdrop")) ? 1 : 0;
        int targetLen = offset + 1;
        if (args.length == targetLen) {
            return filterPrefix(List.of("start", "stop", "list"), args[args.length - 1]);
        }
        if (args.length == targetLen + 1 && (args[targetLen - 1].equalsIgnoreCase("start") || args[targetLen - 1].equalsIgnoreCase("stop"))) {
            return getArenaNames(args[args.length - 1]);
        }
        return Collections.emptyList();
    }

    public List<String> tabCompleteKoth(CommandSender sender, String[] args) {
        int offset = (args.length > 0 && args[0].equalsIgnoreCase("koth")) ? 1 : 0;
        int targetLen = offset + 1;
        if (args.length == targetLen) {
            return filterPrefix(List.of("start", "stop", "setzone", "list"), args[args.length - 1]);
        }
        if (args.length == targetLen + 1 && (args[targetLen - 1].equalsIgnoreCase("start") || args[targetLen - 1].equalsIgnoreCase("stop") || args[targetLen - 1].equalsIgnoreCase("setzone"))) {
            return getArenaNames(args[args.length - 1]);
        }
        return Collections.emptyList();
    }

    private List<String> getArenaNames(String prefix) {
        List<String> list = new ArrayList<>();
        TwoCubeFFAAPI.get().arenas().forEach(a -> list.add(a.id()));
        return filterPrefix(list, prefix);
    }

    private List<String> filterPrefix(List<String> source, String prefix) {
        String p = prefix.toLowerCase(Locale.ROOT);
        return source.stream().filter(s -> s.toLowerCase(Locale.ROOT).startsWith(p)).toList();
    }
}
