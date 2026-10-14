package club.aspvp.ffa.internal;

import java.util.Map;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class q8v3 implements CommandExecutor {
   private final r aB;

   public q8v3(r var1) {
      this.aB = var1;
   }

   public final boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (!var5.hasPermission("ffa.hat") && !var5.hasPermission("ffa.admin")) {
            this.aB.h1s7(var5, "no-permission");
            return true;
         } else {
            ItemStack var6;
            if ((var6 = var5.getInventory().getItemInMainHand()).getType().isAir()) {
               this.aB.h1s7(var5, "hat-empty");
               return true;
            } else {
               ItemStack var7 = var5.getInventory().getHelmet();
               var5.getInventory().setHelmet(var6.clone());
               var5.getInventory().setItemInMainHand(var7 == null ? new ItemStack(Material.AIR) : var7.clone());
               var5.updateInventory();
               this.aB.ab2c(var5, "hat-equipped", Map.of("item", var6.getType().name()));
               return true;
            }
         }
      } else {
         this.aB.h1s7(var1, "only-players");
         return true;
      }
   }
}
