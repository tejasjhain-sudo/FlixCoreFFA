package club.aspvp.ffa.internal;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class r5n0 implements CommandExecutor {
   private final q1d9 aI;
   private final r aJ;

   public r5n0(q1d9 var1, r var2) {
      this.aI = var1;
      this.aJ = var2;
   }

   public final boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (!var5.hasPermission("ffa.settings.menu") && !var5.hasPermission("ffa.admin")) {
            this.aJ.h1s7(var5, "no-permission");
            return true;
         } else {
            this.aI.b(var5);
            return true;
         }
      } else {
         this.aJ.h1s7(var1, "only-players");
         return true;
      }
   }
}
