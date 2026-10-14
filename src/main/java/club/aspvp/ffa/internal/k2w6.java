package club.aspvp.ffa.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class k2w6 implements CommandExecutor, TabCompleter {
   private final p aC;
   private final x6y3 aD;
   private final m8p0 aE;
   private final b aF;
   private final q1d9 aG;
   private final r aH;

   public k2w6(p var1, x6y3 var2, m8p0 var3, b var4, q1d9 var5, r var6) {
      this.aC = var1;
      this.aD = var2;
      this.aE = var3;
      this.aF = var4;
      this.aG = var5;
      this.aH = var6;
   }

   public final boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (!this.aC.x7q9("kiteditor", true)) {
            this.aH.ab2c(var5, "feature-disabled", Map.of("feature", "kiteditor"));
            return true;
         } else if (!var5.hasPermission("ffa.kiteditor.personal") && !var5.hasPermission("ffa.kiteditor")) {
            this.aH.h1s7(var5, "no-permission");
            return true;
         } else if (this.aF.w(var5)) {
            this.aH.h1s7(var5, "kiteditor-blocked-in-ffa");
            return true;
         } else if (var4.length > 0 && !var4[0].isBlank()) {
            String var8 = ab.r(var4[0]);
            if (this.aE.e7z1(var8).isEmpty()) {
               this.aH.ab2c(var5, "kit-missing", Map.of("kit", var8));
               return true;
            } else {
               this.aD.z4c9(var5, var8);
               this.aH.ab2c(var5, "personal-kiteditor-opened", Map.of("kit", var8));
               return true;
            }
         } else {
            q1d9 var6 = this.aG;
            ConfigurationSection var9;
            String var11 = (var9 = this.aG.g1v7("kiteditor")).getString("menu_title", var9.getString("title", "&0&lKITEDITOR"));
            Inventory var12 = var6.ab2c(var5, h1s7.aZ, null, var11, q1d9.x7q9(var9.getInt("size", 27)));
            ConfigurationSection var10;
            if ((var10 = var9.getConfigurationSection("items")) != null) {
               var6.ab2c(var5, var12, var10, false);
               var6.ab2c(var5, var12, var10, true);
            }

            var5.openInventory(var12);
            return true;
         }
      } else {
         this.aH.h1s7(var1, "only-players");
         return true;
      }
   }

   public final List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length != 1) {
         return List.of();
      } else {
         String var5 = var4[0].toLowerCase(Locale.ROOT);
         ArrayList var6 = new ArrayList();
         Iterator var7 = this.aE.t().iterator();

         while (var7.hasNext()) {
            l var8;
            if ((var8 = (l)var7.next()).V().toLowerCase(Locale.ROOT).startsWith(var5)) {
               var6.add(var8.V());
            }
         }

         return var6;
      }
   }
}
