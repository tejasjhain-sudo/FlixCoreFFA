package club.aspvp.ffa.internal;

import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class y5p8 implements Listener {
   private final u2q5 bQ;
   private final d bR;
   private final r bS;

   public y5p8(u2q5 var1, d var2, r var3) {
      this.bQ = var1;
      this.bR = var2;
      this.bS = var3;
   }

   @EventHandler
   public final void onInteract(PlayerInteractEvent var1) {
      Player var2 = var1.getPlayer();
      d var10000 = this.bR;
      ItemStack var4 = var1.getItem();
      d var3 = var10000;
      ItemMeta var9;
      if (var4 != null && !var4.getType().isAir() && var4.hasItemMeta()
         ? (var9 = var4.getItemMeta()) != null && var9.getPersistentDataContainer().has(var3.eQ, PersistentDataType.STRING)
         : false) {
         var1.setCancelled(true);
         if (!var2.hasPermission("ffa.arena.edit")) {
            this.bS.h1s7(var2, "no-permission");
         } else {
            Block var7;
            if ((var7 = var1.getClickedBlock()) == null) {
               this.bS.h1s7(var2, "region-click-block");
            } else {
               byte var11 = switch (var1.getAction()) {
                  case LEFT_CLICK_BLOCK -> 1;
                  case RIGHT_CLICK_BLOCK -> 2;
                  default -> 0;
               };
               byte var6 = var11;
               if (var11 != 0) {
                  Location var8 = var7.getLocation();
                  String var10 = this.bQ.s6h0(var2).orElse("new arena");
                  u2q5.ab2c var5 = this.bQ.ab2c(var2, var6, var8);
                  switch (var5) {
                     case cC:
                        this.bS.h1s7(var2, "region-no-selection");
                        return;
                     case cD:
                        this.bS.ab2c(var2, "region-wand-pos1", ab2c(var10, var8));
                        return;
                     case cE:
                        this.bS.ab2c(var2, "region-wand-pos2", ab2c(var10, var8));
                        return;
                     case cF:
                        this.bS.h1s7(var2, "region-world-mismatch");
                        return;
                     case cG:
                        this.bS.ab2c(var2, "region-wand-pos" + var6, ab2c(var10, var8));
                        this.bS.h1s7(var2, "region-selection-ready");
                        return;
                     case cH:
                        this.bS.ab2c(var2, "region-wand-pos" + var6, ab2c(var10, var8));
                        this.bS.ab2c(var2, "region-set", Map.of("arena", var10));
                  }
               }
            }
         }
      }
   }

   private static Map<String, String> ab2c(String var0, Location var1) {
      return Map.of("arena", var0, "x", String.valueOf(var1.getBlockX()), "y", String.valueOf(var1.getBlockY()), "z", String.valueOf(var1.getBlockZ()));
   }
}
