package club.aspvp.ffa.internal;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class x7q9 implements CommandExecutor, TabCompleter {
   private final p aa;
   private final r ab;
   private final o3w8 ac;

   public x7q9(p var1, r var2, o3w8 var3) {
      this.aa = var1;
      this.ab = var2;
      this.ac = var3;
   }

   public final boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var8) {
         if (!this.aa.x7q9("chat-customization", true)) {
            this.ab.ab2c(var1, "feature-disabled", Map.of("feature", "chat-customization"));
            return true;
         } else {
            String var7 = var2.getName().toLowerCase(Locale.ROOT);
            if (var4.length > 0 && var4[0].equalsIgnoreCase("reload") && var1.hasPermission("ffa.chatcustomization.reload")) {
               this.ac.b();
               this.ab.h1s7(var1, "chatcustomization-reloaded");
               return true;
            } else if (!var7.equals("chatcolour") && !var7.equals("chatcolor")) {
               if (!var1.hasPermission("ffa.tags") && !var1.hasPermission("ffa.admin")) {
                  this.ab.h1s7(var1, "no-permission");
                  return true;
               } else {
                  o3w8 var6 = this.ac;
                  this.ac.ab2c(var8, 0);
                  return true;
               }
            } else if (!var1.hasPermission("ffa.chatcolour") && !var1.hasPermission("ffa.admin")) {
               this.ab.h1s7(var1, "no-permission");
               return true;
            } else {
               o3w8 var5 = this.ac;
               this.ac.x7q9(var8, 0);
               return true;
            }
         }
      } else {
         this.ab.h1s7(var1, "only-players");
         return true;
      }
   }

   public final List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      return var4.length == 1 && var1.hasPermission("ffa.chatcustomization.reload")
         ? List.of("reload").stream().filter(var1x -> var1x.startsWith(var4[0].toLowerCase(Locale.ROOT))).toList()
         : List.of();
   }
}
