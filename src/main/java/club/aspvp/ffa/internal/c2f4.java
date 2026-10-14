package club.aspvp.ffa.internal;

import java.util.Iterator;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class c2f4 implements Listener {
   private final f bV;
   private final b bW;
   private final g bX;

   public c2f4(f var1, b var2, g var3) {
      this.bV = var1;
      this.bW = var2;
      this.bX = var3;
   }

   @EventHandler
   public final void onInteract(PlayerInteractEvent var1) {
      Action var2;
      if ((var2 = var1.getAction()) == Action.RIGHT_CLICK_AIR || var2 == Action.RIGHT_CLICK_BLOCK) {
         Player var10 = var1.getPlayer();
         if (!this.bW.w(var10)) {
            if (this.bX.G()) {
               g var3 = this.bX;
               if (this.bX.J().getBoolean("pvp.disable-spawn-item-actions", true) && this.bX.x7q9(var10.getLocation())) {
                  return;
               }
            }

            f var10000 = this.bV;
            int var10002 = var10.getInventory().getHeldItemSlot();
            ItemStack var5 = var1.getItem();
            int var4 = var10002;
            f var11 = var10000;
            boolean var18;
            if (var10000.fa.x7q9("spawn-items", true) && var11.fa.h("spawnitem.yml").getBoolean("enabled", true)) {
               label69: {
                  ItemStack var6 = var5;
                  int var17 = var4;
                  Player var15 = var10;
                  var11 = var11;
                  if (var5 != null) {
                     Iterator var7 = var11.A().iterator();

                     while (var7.hasNext()) {
                        f.ab2c var8;
                        if ((var8 = (f.ab2c)var7.next()).ab2c("enabled", true) && var8.ab2c("slot", -1) == var17 && var6.isSimilar(var11.ab2c(var15, var8))) {
                           var19 = var8;
                           break label69;
                        }
                     }
                  }

                  var19 = null;
               }

               f.ab2c var13 = var19;
               if (var19 == null) {
                  var18 = false;
               } else {
                  label85: {
                     String var16;
                     if ((var16 = var13.C()).isBlank() || var10.hasPermission(var16)) {
                        String var14;
                        if (f.x6y3(var14 = var13.B())) {
                           var18 = false;
                           break label85;
                        }

                        if (!var14.isBlank()) {
                           var10.performCommand(var14.startsWith("/") ? var14.substring(1) : var14);
                        }
                     }

                     var18 = true;
                  }
               }
            } else {
               var18 = false;
            }

            if (var18) {
               var1.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public final void onDrop(PlayerDropItemEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.bV.q8v3(var2, var1.getItemDrop().getItemStack())) {
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
         if (var1.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
            && var1.getClickedInventory() == var3.getInventory()
            && this.bV.q8v3(var3, var1.getCurrentItem())) {
            var1.setCancelled(true);
         } else {
            if (var1.getClickedInventory() != null && var1.getClickedInventory() != var3.getInventory() && this.bV.q8v3(var3, var1.getCursor())) {
               var1.setCancelled(true);
            }
         }
      }
   }
}
