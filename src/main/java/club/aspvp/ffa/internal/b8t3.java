package club.aspvp.ffa.internal;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public final class b8t3 implements Listener {
   private final i ca;

   public b8t3(i var1) {
      this.ca = var1;
   }

   @EventHandler
   public final void onClose(InventoryCloseEvent var1) {
      HumanEntity var3;
      if ((var3 = var1.getPlayer()) instanceof Player) {
         Player var2 = (Player)var3;
         InventoryHolder var6;
         if ((var6 = var1.getInventory().getHolder()) instanceof p6d2) {
            p6d2 var4 = (p6d2)var6;
            if (var4.aW == h1s7.bd) {
               i var5 = this.ca;
               this.ca.fN.remove(var2.getUniqueId());
            }

            return;
         }
      }
   }
}
