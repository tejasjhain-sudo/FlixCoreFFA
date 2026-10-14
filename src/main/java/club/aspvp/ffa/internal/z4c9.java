package club.aspvp.ffa.internal;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class z4c9 implements Listener {
   private final g1v7 bi;

   public z4c9(g1v7 var1) {
      this.bi = var1;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = false
   )
   public final void onInteract(PlayerInteractEvent var1) {
      Action var2;
      if ((var2 = var1.getAction()) == Action.LEFT_CLICK_BLOCK || var2 == Action.RIGHT_CLICK_BLOCK) {
         if (var1.getClickedBlock() != null) {
            if (this.bi.ab2c(var1.getPlayer(), var2 == Action.LEFT_CLICK_BLOCK, var1.getClickedBlock().getLocation())) {
               var1.setCancelled(true);
            }
         }
      }
   }
}
