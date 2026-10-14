package club.aspvp.ffa.duel;

import club.aspvp.ffa.TwoCubeFFA;
import club.aspvp.ffa.api.ArenaSnapshot;
import club.aspvp.ffa.api.TwoCubeFFAAPI;
import club.aspvp.ffa.internal.p;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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

    private final TwoCubeFFA plugin;
    private final p configManager;
    private final Map<UUID, ActiveCageDuel> activeDuels = new ConcurrentHashMap<>();
    private final Map<UUID, Map<UUID, DuelInvite>> pendingInvites = new ConcurrentHashMap<>();

    public DuelManager(TwoCubeFFA plugin, p configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void init() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public FileConfiguration getConfig() {
        return configManager.h("duels.yml");
    }

    public void saveConfig() {
        configManager.i("duels.yml");
    }

    public boolean handleDuelCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly in-game players can use duel commands.");
            return true;
        }

        int offset = (args.length > 0 && (args[0].equalsIgnoreCase("duel") || args[0].equalsIgnoreCase("duels"))) ? 1 : 0;
        String sub = (args.length > offset) ? args[offset].toLowerCase(Locale.ROOT) : "help";

        switch (sub) {
            case "accept": {
                if (args.length <= offset + 1) {
                    player.sendMessage("§cUsage: /ffa duel accept <player>");
                    return true;
                }
                acceptInvite(player, args[offset + 1]);
                return true;
            }
            case "decline": {
                if (args.length <= offset + 1) {
                    player.sendMessage("§cUsage: /ffa duel decline <player>");
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
                    duel.restoreBlocks();
                    activeDuels.remove(player.getUniqueId());
                }
                return true;
            }
            case "setcage": {
                if (!player.hasPermission("ffa.admin") && !player.hasPermission("ffa.event.admin")) {
                    player.sendMessage("§cYou do not have permission to set cage locations.");
                    return true;
                }
                if (args.length <= offset + 1) {
                    player.sendMessage("§cUsage: /ffa duel setcage <arena>");
                    return true;
                }
                String arena = args[offset + 1];
                setCageLocation(arena, player.getLocation());
                player.sendMessage("§8[§6⚔§8] §aSuccessfully set permanent 21x21 Cage Duel arena for §b" + arena + " §aat your location!");
                return true;
            }
            default: {
                // /ffa duel <player> [kit]
                String targetName = args[offset];
                Player target = Bukkit.getPlayerExact(targetName);
                if (target == null) {
                    player.sendMessage("§cPlayer '" + targetName + "' is not online.");
                    return true;
                }
                String kit = (args.length > offset + 1) ? args[offset + 1].toLowerCase(Locale.ROOT) : getConfig().getString("default-kit", "nethpot");
                sendInvite(player, target, kit);
                return true;
            }
        }
    }

    public void sendInvite(Player sender, Player target, String kitName) {
        if (sender.getUniqueId().equals(target.getUniqueId())) {
            sender.sendMessage("§cYou cannot challenge yourself to a duel!");
            return;
        }

        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
        Optional<String> senderArena = api.arenaOf(sender);
        Optional<String> targetArena = api.arenaOf(target);

        if (senderArena.isEmpty()) {
            sender.sendMessage("§cYou must be inside an FFA arena to challenge players to a duel.");
            return;
        }
        if (targetArena.isEmpty() || !targetArena.get().equalsIgnoreCase(senderArena.get())) {
            sender.sendMessage("§c" + target.getName() + " is not playing in your arena!");
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
                senderArena.get(), kitName,
                System.currentTimeMillis()
        );

        pendingInvites.computeIfAbsent(target.getUniqueId(), k -> new ConcurrentHashMap<>()).put(sender.getUniqueId(), invite);

        sender.sendMessage(color(getConfig().getString("messages.invite-sent", "&8[&6⚔&8] &eDuel invite sent to &b%target% &e(&6Kit: &f%kit%&e). Expires in 30s.")
                .replace("%target%", target.getName())
                .replace("%kit%", kitName)));

        List<String> rawRecv = getConfig().getStringList("messages.invite-received");
        if (rawRecv.isEmpty()) {
            target.sendMessage("§8===================================================");
            target.sendMessage("§6§l             GLADIATOR CAGE DUEL");
            target.sendMessage("§e" + sender.getName() + " §7has challenged you to an In-Arena Cage Duel!");
            target.sendMessage("§7Kit: §b" + kitName + " §8| §7Arena: §e" + senderArena.get());
            target.sendMessage("§aType §e/ffa duel accept " + sender.getName() + " §ato accept!");
            target.sendMessage("§cType §e/ffa duel decline " + sender.getName() + " §cto decline.");
            target.sendMessage("§8===================================================");
        } else {
            for (String line : rawRecv) {
                target.sendMessage(color(line
                        .replace("%player%", sender.getName())
                        .replace("%kit%", kitName)
                        .replace("%arena%", senderArena.get())));
            }
        }
    }

    public void acceptInvite(Player target, String senderName) {
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

        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
        if (!api.isInArena(sender) || !api.isInArena(target)) {
            target.sendMessage("§cBoth players must be in the arena to begin the duel.");
            return;
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

    public void startDuel(Player p1, Player p2, String kitName, String arenaId) {
        int radiusXZ = getConfig().getInt("cage.radius-xz", 10);
        int heightY = getConfig().getInt("cage.height-y", 8);

        Location center = getOrCreateCageCenter(arenaId);
        if (center == null || center.getWorld() == null) {
            p1.sendMessage("§cCould not establish a Cage Duel arena.");
            p2.sendMessage("§cCould not establish a Cage Duel arena.");
            return;
        }

        ActiveCageDuel duel = new ActiveCageDuel(
                p1.getUniqueId(), p1.getName(),
                p2.getUniqueId(), p2.getName(),
                arenaId, kitName, center, radiusXZ, heightY
        );

        activeDuels.put(p1.getUniqueId(), duel);
        activeDuels.put(p2.getUniqueId(), duel);

        // Build Barrier Cage
        buildCage(duel);

        // Teleport players to opposite ends
        Location loc1 = center.clone().add(-radiusXZ + 3.0, 1.0, 0.5);
        Location loc2 = center.clone().add(radiusXZ - 3.0, 1.0, 0.5);
        loc1.setYaw(-90f);
        loc2.setYaw(90f);

        p1.teleport(loc1);
        p2.teleport(loc2);

        // Prepare player state & kits
        prepareFighter(p1, kitName);
        prepareFighter(p2, kitName);

        // Create BossBar
        String bbTitle = color("&6&lCAGE DUEL: &e" + p1.getName() + " &cvs &e" + p2.getName() + " &8[&b" + kitName + "&8]");
        duel.bossBar = Bukkit.createBossBar(bbTitle, BarColor.PURPLE, BarStyle.SOLID);
        duel.bossBar.addPlayer(p1);
        duel.bossBar.addPlayer(p2);
        duel.bossBar.setVisible(true);

        // Add arena spectators to BossBar
        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
        for (Player online : Bukkit.getOnlinePlayers()) {
            api.arenaOf(online).ifPresent(a -> {
                if (a.equalsIgnoreCase(arenaId) && !online.equals(p1) && !online.equals(p2)) {
                    duel.bossBar.addPlayer(online);
                }
            });
        }

        // Broadcast
        String startMsg = color(getConfig().getString("messages.duel-started", "&8[&6⚔&8] &6⚔ &eA Cage Duel has begun between &b%player1% &eand &b%player2% &8(&6Kit: &f%kit%&8)!")
                .replace("%player1%", p1.getName())
                .replace("%player2%", p2.getName())
                .replace("%kit%", kitName));
        Bukkit.broadcastMessage(startMsg);

        // Floor perimeter particles
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

        // 3-Second Countdown
        duel.frozen = true;
        new BukkitRunnable() {
            int countdown = 3;
            @Override
            public void run() {
                if (!duel.active) {
                    cancel();
                    return;
                }
                if (countdown > 0) {
                    String title = (countdown == 3) ? "§e§l3" : (countdown == 2) ? "§6§l2" : "§c§l1";
                    p1.sendTitle(title, "§7Prepare for battle!", 0, 25, 5);
                    p2.sendTitle(title, "§7Prepare for battle!", 0, 25, 5);
                    p1.playSound(p1.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
                    p2.playSound(p2.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
                    countdown--;
                } else {
                    duel.frozen = false;
                    p1.sendTitle("§a§lFIGHT!", "§eGladiator Cage Match", 5, 25, 10);
                    p2.sendTitle("§a§lFIGHT!", "§eGladiator Cage Match", 5, 25, 10);
                    p1.playSound(p1.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.5f, 1.2f);
                    p2.playSound(p2.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.5f, 1.2f);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void prepareFighter(Player player, String kitName) {
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setFireTicks(0);
        player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));
        player.getInventory().clear();

        if (kitName.equalsIgnoreCase("tank")) {
            equipTankKit(player);
        } else {
            boolean applied = TwoCubeFFAAPI.get().applyKit(player, kitName);
            if (!applied) {
                // Fallback to nethpot if kit not found
                if (!TwoCubeFFAAPI.get().applyKit(player, "nethpot")) {
                    equipTankKit(player);
                }
            }
        }
    }

    private void equipTankKit(Player player) {
        // Armor
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

        // Weapons
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

        // Potions
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

    private void buildCage(ActiveCageDuel duel) {
        World world = duel.cageCenter.getWorld();
        if (world == null) return;

        int cx = duel.cageCenter.getBlockX();
        int cy = duel.cageCenter.getBlockY();
        int cz = duel.cageCenter.getBlockZ();
        int r = duel.radiusXZ;
        int h = duel.heightY;

        // 1. Floor (Glass)
        for (int x = cx - r; x <= cx + r; x++) {
            for (int z = cz - r; z <= cz + r; z++) {
                saveAndSetBlock(duel, world.getBlockAt(x, cy, z), Material.GLASS);
            }
        }

        // 2. Barrier Walls
        for (int y = cy + 1; y <= cy + h; y++) {
            for (int x = cx - r; x <= cx + r; x++) {
                saveAndSetBlock(duel, world.getBlockAt(x, y, cz - r), Material.BARRIER);
                saveAndSetBlock(duel, world.getBlockAt(x, y, cz + r), Material.BARRIER);
            }
            for (int z = cz - r; z <= cz + r; z++) {
                saveAndSetBlock(duel, world.getBlockAt(cx - r, y, z), Material.BARRIER);
                saveAndSetBlock(duel, world.getBlockAt(cx + r, y, z), Material.BARRIER);
            }
        }

        // 3. Barrier Ceiling
        for (int x = cx - r; x <= cx + r; x++) {
            for (int z = cz - r; z <= cz + r; z++) {
                saveAndSetBlock(duel, world.getBlockAt(x, cy + h, z), Material.BARRIER);
            }
        }
    }

    private void saveAndSetBlock(ActiveCageDuel duel, Block block, Material newMat) {
        if (!duel.originalBlocks.containsKey(block)) {
            duel.originalBlocks.put(block, block.getBlockData().clone());
        }
        block.setType(newMat, false);
    }

    public void endDuel(Player winner, Player loser, boolean broadcastWin) {
        ActiveCageDuel duel = activeDuels.remove(winner.getUniqueId());
        if (duel != null) {
            activeDuels.remove(loser.getUniqueId());
            duel.active = false;

            if (duel.timerTask != null) duel.timerTask.cancel();
            if (duel.particleTask != null) duel.particleTask.cancel();
            if (duel.bossBar != null) duel.bossBar.removeAll();

            // Reward winner
            int tokens = getConfig().getInt("rewards.tokens", 250);
            TwoCubeFFAAPI.get().addTokens(winner, tokens);
            winner.sendMessage("§8[§6⚔§8] §eYou won the Cage Duel and earned §6" + tokens + " tokens§e!");

            // Winner celebration
            winner.sendTitle("§6§lVICTORY!", "§eYou conquered the Cage Duel!", 5, 60, 15);
            winner.playSound(winner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0f, 1.0f);
            spawnFireworks(winner.getLocation().add(0, 1, 0), 3);

            if (loser.isOnline()) {
                loser.sendTitle("§c§lDEFEAT!", "§7Better luck next time!", 5, 50, 15);
            }

            if (broadcastWin) {
                String winMsg = color(getConfig().getString("messages.duel-won", "&8[&6⚔&8] &6👑 &a&l%winner% &ehas DEFEATED &c&l%loser% &ein the Cage Duel!")
                        .replace("%winner%", winner.getName())
                        .replace("%loser%", loser.getName()));
                Bukkit.broadcastMessage(winMsg);
            }

            // Restore cage blocks cleanly
            duel.restoreBlocks();

            // Release winner back into arena after 5s
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (winner.isOnline() && TwoCubeFFAAPI.get().isInArena(winner)) {
                        ArenaSnapshot arena = TwoCubeFFAAPI.get().arena(duel.arenaId).orElse(null);
                        if (arena != null && arena.spawn() != null) {
                            winner.teleport(arena.spawn());
                        }
                        winner.setHealth(winner.getMaxHealth());
                        winner.setFoodLevel(20);
                        String primaryKit = (arena != null && !arena.kits().isEmpty()) ? arena.kits().get(0) : "nethpot";
                        TwoCubeFFAAPI.get().applyKit(winner, primaryKit);
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

    public void setCageLocation(String arenaId, Location loc) {
        FileConfiguration config = getConfig();
        String path = "arenas." + arenaId.toLowerCase(Locale.ROOT) + ".cage";
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getBlockX());
        config.set(path + ".y", loc.getBlockY());
        config.set(path + ".z", loc.getBlockZ());
        saveConfig();
    }

    private Location getOrCreateCageCenter(String arenaId) {
        FileConfiguration config = getConfig();
        String path = "arenas." + arenaId.toLowerCase(Locale.ROOT) + ".cage";
        if (config.contains(path + ".world")) {
            World w = Bukkit.getWorld(config.getString(path + ".world"));
            if (w != null) {
                return new Location(w, config.getInt(path + ".x"), config.getInt(path + ".y"), config.getInt(path + ".z"));
            }
        }

        // Auto-elevate above arena spawn (Y+25 in the sky overlooking arena!)
        TwoCubeFFAAPI api = TwoCubeFFAAPI.get();
        ArenaSnapshot arena = api.arena(arenaId).orElse(null);
        if (arena != null && arena.spawn() != null) {
            Location sp = arena.spawn();
            int elevatedY = Math.min(sp.getWorld().getMaxHeight() - 15, sp.getBlockY() + 25);
            return new Location(sp.getWorld(), sp.getBlockX(), elevatedY, sp.getBlockZ());
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

    public void cleanup() {
        for (ActiveCageDuel duel : new HashSet<>(activeDuels.values())) {
            duel.restoreBlocks();
            if (duel.timerTask != null) duel.timerTask.cancel();
            if (duel.particleTask != null) duel.particleTask.cancel();
            if (duel.bossBar != null) duel.bossBar.removeAll();
        }
        activeDuels.clear();
        pendingInvites.clear();
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
