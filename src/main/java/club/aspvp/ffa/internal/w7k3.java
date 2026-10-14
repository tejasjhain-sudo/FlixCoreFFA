package club.aspvp.ffa.internal;

import club.aspvp.ffa.api.event.FfaArenaLeaveEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Player.Spigot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;

public final class w7k3 implements Listener {
   private final p bq;
   private final t br;
   private final u2q5 bs;
   private final b bt;
   private final h bu;
   private final q bv;
   private final f bw;
   private final e bx;
   private final a7r4 by;
   private final x6y3 bz;
   private final c bA;
   private final e7z1 bB;
   private final Set<UUID> bC = ConcurrentHashMap.newKeySet();
   private final Set<UUID> bD = ConcurrentHashMap.newKeySet();

   public w7k3(p var1, t var2, u2q5 var3, b var4, h var5, q var6, f var7, e var8, a7r4 var9, x6y3 var10, c var11, e7z1 var12) {
      this.bq = var1;
      this.br = var2;
      this.bs = var3;
      this.bt = var4;
      this.bu = var5;
      this.bv = var6;
      this.bw = var7;
      this.bx = var8;
      this.by = var9;
      this.bz = var10;
      this.bA = var11;
      this.bB = var12;
   }

   @EventHandler
   public final void onArenaLeave(FfaArenaLeaveEvent var1) {
      if (this.bt.i5k2(var1.getArena()) <= 0L) {
         this.bs.arena(var1.getArena()).ifPresent(this.bB::z4c9);
      }
   }

   @EventHandler
   public final void onJoin(PlayerJoinEvent var1) {
      this.bu.V(var1.getPlayer());
      this.bA.E(var1.getPlayer());
      this.bt.u(var1.getPlayer());
      this.br.ab2c(var1.getPlayer(), () -> {
         Player var3 = var1.getPlayer();
         this.bC.remove(var3.getUniqueId());
         if (this.bt.w(var3)) {
            this.bv.ae(var3);
            this.bt.ab2c(var3, null, false);
         }

         this.ab2c(var3, "join");
         this.br.ab2c(var3, () -> this.ab2c(var3, "join-retry"), 10L);
         y.w7k3("JOIN", var3.getName() + " was reset to lobby on server join.");
      }, 5L);
      this.br.ab2c(var1.getPlayer(), () -> this.f3y8(var1.getPlayer()), 40L);
   }

   @EventHandler
   public final void onQuit(PlayerQuitEvent var1) {
      this.bD.remove(var1.getPlayer().getUniqueId());
      this.bz.l(var1.getPlayer());
      if (this.bt.w(var1.getPlayer())) {
         this.bC.add(var1.getPlayer().getUniqueId());
         this.bv.ae(var1.getPlayer());
         this.bt.ab2c(var1.getPlayer(), null, false);
      }

      this.bu.ab(var1.getPlayer());
      c var10000 = this.bA;
      Player var3 = var1.getPlayer();
      var10000.eP.remove(var3.getUniqueId());
      this.bt.A(var1.getPlayer());
      this.bx.L(var1.getPlayer());
      var1.getPlayer();
   }

   @EventHandler
   public final void onDeath(PlayerDeathEvent var1) {
      Player var2;
      Player var3;
      if ((var3 = (var2 = var1.getEntity()).getKiller()) != null && this.ab2c(var2, var3)) {
         h var12 = this.bu;
         o var7 = this.bu.W(var3);
         p var10 = var12.fH;
         int var8 = var12.fH.gt.getConfig().getInt("settings.xp-per-kill", 3);
         var10 = var12.fH;
         int var9 = var12.fH.gt.getConfig().getInt("settings.level-xp-base", 100);
         int var5 = var12.Y(var3);
         int var24 = var7.u + 1;
         var7.u = var24;
         var7.f3y8(var7.w + 1);
         var7.p6d2(var7.y + var5);
         var24 = var7.z + var8;
         var7.z = var24;

         while (var7.z >= var7.A * var9) {
            var24 = var7.z - var7.A * var9;
            var7.z = var24;
            var7.h1s7(var7.A + 1);
         }

         var12.fI.x7q9(var7);
         o var15;
         o var10000 = var15 = var12.W(var2);
         var24 = var15.v + 1;
         var10000.v = var24;
         var15.f3y8(0);
         var12.fI.x7q9(var15);
         q var13 = this.bv;
         this.bv.b8t3(var3, "kill");
         var13.g1v7(var3, "kill");
         var13.ag(var3);
         var13.ah(var3);
         var10 = var13.gw;
         var13.x7q9(var13.gw.gt.getConfig().getString("kill-message", ""), var3, Map.of("victim", var2.getName(), "attacker", var3.getName()));
         var10 = var13.gw;
         List var16;
         FileConfiguration var18;
         if ((var18 = var13.gw.gt.getConfig()).getBoolean("auto-gg.enabled", true)
            && (!var18.getBoolean("auto-gg.require-permission", false) || var3.hasPermission(var18.getString("auto-gg.permission", "flixcoreffa.autogg.use")))
            && !(var16 = var18.getStringList("auto-gg.messages")).isEmpty()) {
            String var17 = (String)var16.get(ThreadLocalRandom.current().nextInt(var16.size()));
            var13.x7q9(var17, var3, Map.of("player", var3.getName()));
         }

         this.bv.x7q9(var2, var3);
      } else if (this.bt.w(var2)) {
         int var11 = this.bu.W(var2).v + 1;
         this.bu.W(var2).v = var11;
         this.bu.W(var2).f3y8(0);
         this.bu.aa(var2);
         q var4 = this.bv;
         this.bv.x7q9(var2, null);
      }

      if (this.bt.w(var2)) {
         var1.setKeepInventory(false);
         var1.getDrops().clear();
         var1.setDroppedExp(0);
         y.w7k3("DEATH", var2.getName() + " died inside arena '" + this.bt.v(var2).orElse("unknown") + "'.");
         this.bD.add(var2.getUniqueId());
         this.bv.ae(var2);
         this.bt.ab2c(var2, null, false);
         this.bx.L(var2);
         p var22 = this.bq;
         if (this.bq.gt.getConfig().getBoolean("auto-respawn.enabled", true)) {
            var22 = this.bq;
            long var14 = this.bq.gt.getConfig().getLong("auto-respawn.delay-ticks", 2L);
            y.w7k3("RESPAWN", "Scheduled auto-respawn for " + var2.getName() + " in " + var14 + " ticks.");
            this.br.ab2c(var2, () -> {
               if (var2.isDead() && this.bD.contains(var2.getUniqueId())) {
                  try {
                     var2.getClass().getMethod("respawn").invoke(var2);
                     return;
                  } catch (ReflectiveOperationException var3x) {
                     try {
                        Spigot var5x;
                        (var5x = var2.spigot()).getClass().getMethod("respawn").invoke(var5x);
                        return;
                     } catch (ReflectiveOperationException var2x) {
                     }
                  }
               }
            }, var14);
         }
      }
   }

   @EventHandler
   public final void onRespawn(PlayerRespawnEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.bD.remove(var2.getUniqueId())) {
         Location var3;
         if ((var3 = this.bs.b8t3()) != null && var3.getWorld() != null) {
            var1.setRespawnLocation(var3);
         }

         this.br.ab2c(var2, () -> this.ab2c(var2, "death"), 1L);
         this.br.ab2c(var2, () -> this.ab2c(var2, "death-retry"), 11L);
      } else {
         this.bt.v(var2).flatMap(this.bs::arena).ifPresent(var3x -> {
            Location var4;
            if ((var4 = var3x.fU == null ? null : var3x.fU.ac()) != null) {
               var1.setRespawnLocation(var4);
            }

            p var7 = this.bq;
            long var5 = this.bq.gt.getConfig().getLong("settings.respawn-delay-ticks", 2L);
            this.br.ab2c(var2, () -> {
               if (this.bt.w(var2)) {
                  var2.setGameMode(GameMode.SURVIVAL);
                  var2.setWalkSpeed(0.2F);
                  var2.setFlySpeed(0.1F);
                  var2.setAllowFlight(false);
                  var2.setFlying(false);
                  var2.setInvulnerable(false);
                  this.bt.z(var2);
               }
            }, var5);
            this.br.ab2c(var2, () -> {
               q var3xx = this.bv;
               this.bv.g1v7(var2, "respawn");
            }, var5);
            y.w7k3("RESPAWN", "Restoring " + var2.getName() + " at arena '" + var3x.O() + "' with kit '" + this.bt.x(var2) + "'.");
         });
      }
   }

   @EventHandler
   public final void onDamage(EntityDamageByEntityEvent var1) {
      Entity var3;
      if ((var3 = var1.getEntity()) instanceof Player) {
         Player var2 = (Player)var3;
         Entity var4;
         Player var5;
         Projectile var6;
         Player var7;
         Player var8;
         ProjectileSource var10;
         if ((
                  var8 = (var4 = var1.getDamager()) instanceof Player
                     ? (var5 = (Player)var4)
                     : (
                        (var4 = var1.getDamager()) instanceof Projectile
                           ? ((var10 = (var6 = (Projectile)var4).getShooter()) instanceof Player ? (var7 = (Player)var10) : null)
                           : null
                     )
               )
               != null) {
            club.aspvp.ffa.TwoCubeFFA plugin = club.aspvp.ffa.TwoCubeFFA.getPlugin(club.aspvp.ffa.TwoCubeFFA.class);
            if (plugin != null && plugin.getDuelManager() != null) {
               if (plugin.getDuelManager().isInDuel(var2) || plugin.getDuelManager().isInDuel(var8)) {
                  return; // In active cage duel: DuelManager handles all authorizations!
               }
            }
            if (this.bt.w(var2) || this.bt.w(var8)) {
               if (!this.ab2c(var2, var8)) {
                  var1.setCancelled(true);
               } else {
                  if (this.bv.af(var2) || this.bv.af(var8)) {
                     var1.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public final void onMove(PlayerMoveEvent var1) {
      Player var5 = var1.getPlayer();
      club.aspvp.ffa.TwoCubeFFA plugin = club.aspvp.ffa.TwoCubeFFA.getPlugin(club.aspvp.ffa.TwoCubeFFA.class);
      if (plugin != null && plugin.getDuelManager() != null && plugin.getDuelManager().isInDuel(var5)) {
         return; // Cage duel movement is handled exclusively by DuelManager!
      }
      p var2 = this.bq;
      if (this.bq.gt.getConfig().getBoolean("settings.prevent-arena-region-exit", true)) {
         if (var1.getTo() != null) {
            Location var10000 = var1.getFrom();
            Location var3 = var1.getTo();
            Location var4 = var10000;
            if (!var10000.getWorld().equals(var3.getWorld())
               || var4.getBlockX() != var3.getBlockX()
               || var4.getBlockY() != var3.getBlockY()
               || var4.getBlockZ() != var3.getBlockZ()) {
               this.bt.v(var5).flatMap(this.bs::arena).map(j::S).ifPresent(var3x -> {
                  if (!var3x.m4z1(var1.getTo())) {
                     var1.setCancelled(true);
                     this.bs.arena(this.bt.v(var5).orElse("")).ifPresent(var1xx -> {
                        Location var3xx;
                        if ((var3xx = var1xx.fU == null ? null : var1xx.fU.ac()) != null) {
                           var5.teleportAsync(var3xx);
                        }
                     });
                  }
               });
               return;
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = false
   )
   public final void onBreak(BlockBreakEvent var1) {
      if (this.bt.w(var1.getPlayer())) {
         boolean var2 = this.ab2c(var1.getPlayer(), var1.getBlock().getType());
         var1.setCancelled(!var2);
         if (!var2) {
            this.ab2c(var1.getPlayer(), "break", var1.getBlock().getType());
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = false
   )
   public final void onPlace(BlockPlaceEvent var1) {
      if (this.bt.w(var1.getPlayer())) {
         boolean var2 = this.x7q9(var1.getPlayer(), var1.getBlockPlaced().getType());
         var1.setCancelled(!var2);
         if (!var2) {
            this.ab2c(var1.getPlayer(), "place", var1.getBlockPlaced().getType());
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = false
   )
   public final void onBucketEmpty(PlayerBucketEmptyEvent var1) {
      if (this.bt.w(var1.getPlayer())) {
         Material var2 = var1.getBucket();

         Material var10000 = switch (var2) {
            case WATER_BUCKET -> Material.WATER;
            case LAVA_BUCKET -> Material.LAVA;
            default -> null;
         };
         var2 = var10000;
         boolean var3 = var10000 != null && this.x7q9(var1.getPlayer(), var2);
         var1.setCancelled(!var3);
         if (!var3 && var2 != null) {
            this.ab2c(var1.getPlayer(), "place", var2);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = false
   )
   public final void onBucketFill(PlayerBucketFillEvent var1) {
      if (this.bt.w(var1.getPlayer())) {
         Material var2 = var1.getBlock().getType();
         boolean var3 = this.ab2c(var1.getPlayer(), var2);
         var1.setCancelled(!var3);
         if (!var3) {
            this.ab2c(var1.getPlayer(), "break", var2);
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public final void onDrop(PlayerDropItemEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.bt.w(var2) || this.r5n0(var2) && !var2.hasPermission("ffa.admin")) {
         var1.setCancelled(true);
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public final void onInventoryClick(InventoryClickEvent var1) {
      HumanEntity var2;
      if ((var2 = var1.getWhoClicked()) instanceof Player) {
         Player var3 = (Player)var2;
         if (!this.bt.w(var3) && !var3.hasPermission("ffa.admin") && this.r5n0(var3) && var1.getClickedInventory() == var3.getInventory()) {
            var1.setCancelled(true);
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public final void onSwapHand(PlayerSwapHandItemsEvent var1) {
      Player var2 = var1.getPlayer();
      if (!this.bt.w(var2) && !var2.hasPermission("ffa.admin") && this.r5n0(var2)) {
         var1.setCancelled(true);
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public final void onTeleport(PlayerTeleportEvent var1) {
      club.aspvp.ffa.TwoCubeFFA plugin = club.aspvp.ffa.TwoCubeFFA.getPlugin(club.aspvp.ffa.TwoCubeFFA.class);
      if (plugin != null && plugin.getDuelManager() != null && plugin.getDuelManager().isInDuel(var1.getPlayer())) {
         return; // Cage duel teleports are handled exclusively by DuelManager!
      }
      p var2 = this.bq;
      if (this.bq.gt.getConfig().getBoolean("settings.prevent-arena-region-exit", true) && var1.getTo() != null && this.bt.w(var1.getPlayer())) {
         this.bt.v(var1.getPlayer()).flatMap(this.bs::arena).ifPresent(var2x -> {
            if (var2x.S() != null && !var2x.S().m4z1(var1.getTo())) {
               var1.setCancelled(true);
               Location var4;
               if ((var4 = var2x.fU == null ? null : var2x.fU.ac()) != null) {
                  this.br.ab2c(var1.getPlayer(), () -> var1.getPlayer().teleportAsync(var4), 1L);
               }
            }
         });
      }
   }

   private boolean k2w6(Player var1) {
      return this.bt.v(var1).flatMap(this.bs::arena).map(j::P).orElse(Boolean.FALSE);
   }

   private boolean ab2c(Player var1, Material var2) {
      if (this.k2w6(var1)) {
         return true;
      } else {
         return ab2c(var2) ? true : this.bt.v(var1).flatMap(this.bs::arena).map(var1x -> {
            String var2x = var2.name();
            return j.ab2c(var1x.fS, var2x);
         }).orElse(Boolean.FALSE);
      }
   }

   private boolean x7q9(Player var1, Material var2) {
      if (this.k2w6(var1)) {
         return true;
      } else {
         return ab2c(var2) ? true : this.bt.v(var1).flatMap(this.bs::arena).map(var1x -> {
            String var2x = var2.name();
            return j.ab2c(var1x.fT, var2x);
         }).orElse(Boolean.FALSE);
      }
   }

   private static boolean ab2c(Material var0) {
      return var0 == Material.WATER || var0 == Material.LAVA;
   }

   private void ab2c(Player var1, String var2, Material var3) {
      this.bt
         .v(var1)
         .flatMap(this.bs::arena)
         .ifPresent(
            var3x -> y.w7k3(
                  "BUILD",
                  "Blocked "
                     + var1.getName()
                     + " from "
                     + var2
                     + " "
                     + var3.name()
                     + " in arena '"
                     + var3x.O()
                     + "'; buildAllowed="
                     + var3x.P()
                     + ", breakList="
                     + List.<String>copyOf(var3x.fS)
                     + ", placeList="
                     + List.<String>copyOf(var3x.fT)
                     + "."
               )
         );
   }

   private boolean r5n0(Player var1) {
      Location var2;
      return (var2 = this.bs.b8t3()) != null && var2.getWorld() != null && var1.getWorld().getName().equals(var2.getWorld().getName());
   }

   private boolean ab2c(Player var1, Player var2) {
      return this.bt.v(var1).flatMap(var2x -> this.bt.v(var2).filter(var1xx -> var2x.equalsIgnoreCase(var1xx))).isPresent();
   }

   private static void t9b4(Player var0) {
      var0.setItemOnCursor(new ItemStack(Material.AIR));
      var0.getInventory().clear();
      var0.getInventory().setArmorContents(null);
      var0.getInventory().setItemInOffHand(new ItemStack(Material.AIR));

      for (PotionEffect var2 : var0.getActivePotionEffects()) {
         var0.removePotionEffect(var2.getType());
      }

      var0.setGameMode(GameMode.SURVIVAL);
      var0.updateInventory();
   }

   private void ab2c(Player var1, String var2) {
      if (var1.isOnline()) {
         if (!this.bt.w(var1)) {
            t9b4(var1);
            this.f3y8(var1);
            this.bw.N(var1);
            y.w7k3("LOBBY", var1.getName() + " restored to lobby spawn state after " + var2 + ".");
         }
      }
   }

   private void f3y8(Player var1) {
      Location var2;
      if ((var2 = this.bs.b8t3()) != null && var2.getWorld() != null && var1.isOnline()) {
         var1.teleportAsync(var2);
      }
   }
}
