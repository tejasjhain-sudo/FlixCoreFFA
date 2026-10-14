package club.aspvp.ffa.internal;

import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class f3y8 implements CommandExecutor {
   private final p aT;
   private final i aU;
   private final r aV;

   public f3y8(p var1, i var2, r var3) {
      this.aT = var1;
      this.aU = var2;
      this.aV = var3;
   }

   public final boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (!this.aT.x7q9("totempractice", true)) {
            this.aV.ab2c(var5, "feature-disabled", Map.of("feature", "totempractice"));
            return true;
         } else if (!var5.hasPermission("ffa.totempractice")) {
            this.aV.h1s7(var5, "no-permission");
            return true;
         } else {
            this.aU.ac(var5);
            return true;
         }
      } else {
         this.aV.h1s7(var1, "only-players");
         return true;
      }
   }
}
