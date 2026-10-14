package club.aspvp.ffa.internal;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

public final class s6h0 implements Listener {
   private final g bY;
   private final Set<UUID> bZ = ConcurrentHashMap.newKeySet();

   public s6h0(g var1) {
      this.bY = var1;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = false
   )
   public final void onInteract(PlayerInteractEvent var1) {
      Action var2;
      if ((var2 = var1.getAction()) == Action.LEFT_CLICK_BLOCK || var2 == Action.RIGHT_CLICK_BLOCK) {
         g var10000 = this.bY;
         Player var10001 = var1.getPlayer();
         boolean var10002 = var2 == Action.LEFT_CLICK_BLOCK;
         Block var6 = var1.getClickedBlock();
         boolean var5 = var10002;
         Player var4 = var10001;
         g var3 = var10000;
         boolean var11;
         if (!var10000.k2w6(var4, var4.getInventory().getItemInMainHand())) {
            var11 = false;
         } else if (var6 == null) {
            var3.fg.h1s7(var4, "spawnpvp-click-block-required");
            var11 = true;
         } else {
            var3.ab2c(var4, var5 ? 1 : 2, var6);
            var11 = true;
         }

         if (var11) {
            var1.setCancelled(true);
            return;
         }
      }

      if (var2 == Action.RIGHT_CLICK_BLOCK) {
         g var12 = this.bY;
         Block var9 = var1.getClickedBlock();
         Player var8 = var1.getPlayer();
         g var7 = var12;
         Integer var10;
         boolean var13;
         if ((var10 = var12.fi.remove(var8.getUniqueId())) == null) {
            var13 = false;
         } else if (var9 == null) {
            var7.fg.h1s7(var8, "spawnpvp-click-block-required");
            var13 = true;
         } else {
            var7.ab2c(var8, var10, var9);
            var13 = true;
         }

         if (var13) {
            var1.setCancelled(true);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = false
   )
   public final void onDamage(EntityDamageByEntityEvent var1) {
      if (this.bY.G()) {
         g var4 = this.bY;
         Entity var3;
         if (this.bY.J().getBoolean("protection-bypass", var4.J().getBoolean("worldguard-bypass", true)) && (var3 = var1.getEntity()) instanceof Player) {
            Player var2 = (Player)var3;
            Player var7;
            Projectile var9;
            ProjectileSource var10;
            Player var11;
            Player var12;
            if ((
                  var12 = (var3 = var1.getDamager()) instanceof Player
                     ? (var7 = (Player)var3)
                     : (
                        (var3 = var1.getDamager()) instanceof Projectile
                           ? ((var10 = (var9 = (Projectile)var3).getShooter()) instanceof Player ? (var11 = (Player)var10) : null)
                           : null
                     )
               )
               == null) {
               return;
            }

            if (this.bY.x7q9(var2.getLocation()) && this.bY.x7q9(var12.getLocation())) {
               var1.setCancelled(false);
               var4 = this.bY;
               if (this.bY.J().getBoolean("pvp.knockback-only", true)) {
                  var1.setDamage(0.0);
                  Vector var13;
                  (var13 = var2.getLocation().toVector().subtract(var12.getLocation().toVector())).setY(0);
                  if (var13.lengthSquared() < 1.0E-4) {
                     (var13 = var12.getLocation().getDirection().multiply(-1)).setY(0);
                  }

                  if (var13.lengthSquared() < 1.0E-4) {
                     var13 = new Vector(0, 0, 1);
                  }

                  var13.normalize();
                  Vector var14;
                  (var14 = var13.multiply(0.72)).setY(0.36);
                  var2.setVelocity(var14);
               }
            }

            return;
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public final void onMove(PlayerMoveEvent var1) {
      if (var1.getTo() != null
         && (
            !var1.getFrom().getWorld().equals(var1.getTo().getWorld())
               || var1.getFrom().getBlockX() != var1.getTo().getBlockX()
               || var1.getFrom().getBlockY() != var1.getTo().getBlockY()
               || var1.getFrom().getBlockZ() != var1.getTo().getBlockZ()
         )) {
         Player var2 = var1.getPlayer();
         boolean var18 = this.bY.x7q9(var1.getTo());
         boolean var3 = this.bZ.contains(var2.getUniqueId());
         if (var18 && !var3) {
            this.bZ.add(var2.getUniqueId());
            g var20 = this.bY;
            ConfigurationSection var21 = this.bY.u2q5("spawnpvp-enter");
            FileConfiguration var4 = var20.J();
            if (var21 == null ? var4.getBoolean("enter-title.enabled", true) : var21.getBoolean("enabled", true)) {
               String var5 = var21 == null ? var4.getString("enter-title.title", "&#2EB6FF&lSpawnPvP") : var21.getString("title", "&#2EB6FF&lSpawnPvP");
               String var6 = var21 == null
                  ? var4.getString("enter-title.subtitle", "&fPvP is enabled in this area.")
                  : var21.getString("subtitle", "&fPvP is enabled in this area.");
               long var12 = var21 == null ? var4.getLong("enter-title.fade-in", 5L) : var21.getLong("fade-in", 5L);
               long var14 = var21 == null ? var4.getLong("enter-title.stay", 35L) : var21.getLong("stay", 35L);
               long var16 = var21 == null ? var4.getLong("enter-title.fade-out", 10L) : var21.getLong("fade-out", 10L);
               u var22 = var20.fh;
               Component var10001 = var20.fh.m4z1(var2, var5, true);
               u var23 = var20.fh;
               var2.showTitle(
                  Title.title(
                     var10001,
                     var20.fh.m4z1(var2, var6, true),
                     Times.times(Duration.ofMillis(var12 * 50L), Duration.ofMillis(var14 * 50L), Duration.ofMillis(var16 * 50L))
                  )
               );
            }
         } else {
            if (!var18 && var3) {
               this.bZ.remove(var2.getUniqueId());
            }
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public final void onDrop(PlayerDropItemEvent var1) {
      if (!var1.getPlayer().hasPermission("ffa.admin") && this.bY.x7q9(var1.getPlayer().getLocation())) {
         var1.setCancelled(true);
      }
   }

   @EventHandler
   public final void onDeath(PlayerDeathEvent var1) {
      if (this.bY.x7q9(var1.getEntity().getLocation())) {
         var1.getDrops().clear();
         var1.setDroppedExp(0);
      }

      this.bZ.remove(var1.getEntity().getUniqueId());
   }
}
