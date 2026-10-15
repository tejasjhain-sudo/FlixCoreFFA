package club.aspvp.ffa.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public final class r {
   private static final Map<String, List<String>> gD = Map.of(
      "help",
      List.of(
         "&8&m--------------------------------",
         "&#2EB6FF&lFlixCore FFA &8| &fCommand Guide",
         "&bArena Setup:",
         "&f/ffa wand &8> &f/ffa pos1 &8> &f/ffa pos2 &8> &f/ffa create <arena>",
         "&f/ffa setspawn <arena> &8> &f/ffa kit create <kit> &8> &f/ffa arena enable <arena>",
         "&7Build blocks: &f/ffa build <arena> auto <kit> &7or &f/ffa build <arena> true",
         "",
         "&bPlayer:",
         "&f/ffa &8- &7Open arena menu",
         "&f/ffaarena <arena> &8- &7Join directly",
         "&f/ffa leave &8/ &f/leave &8- &7Leave arena",
         "&f/spawn &8- &7Go to lobby",
         "&f/ffa kit <kit> [arena] &8- &7Select kit or join with kit",
         "&f/token &8- &7View tokens",
         "&f/ffa stats [player] &8- &7View stats",
         "&f/ffa top &8- &7View leaderboard",
         "&8&m--------------------------------"
      ),
      "kit-help",
      List.of(
         "&8&m--------------------------------",
         "&#2EB6FF&lKit Commands",
         "&f/ffa kit <kit> &8- &7Select kit",
         "&f/ffa kit <kit> <arena> &8- &7Select and join",
         "&f/ffa kit link <kit> <arena> &8- &7Link kit to arena",
         "&f/ffa kit create <kit> &8- &7Create and edit kit",
         "&f/ffa kit edit <kit> &8- &7Edit global kit",
         "&f/ffa kit save <kit> &8- &7Save inventory",
         "&f/ffa kit give <player> <kit> &8- &7Give kit",
         "&f/ffa kit delete <kit> &8- &7Delete kit",
         "&f/ffa kit duplicate <kit> <newkit> &8- &7Copy kit",
         "&f/kiteditor <kit> &8- &7Personal kit editor",
         "&8&m--------------------------------"
      ),
      "token-help",
      List.of(
         "&8&m--------------------------------",
         "&#2EB6FF&lToken Commands",
         "&f/token &8- &7View your balance",
         "&f/token balance [player] &8- &7View a balance",
         "&f/token give <player> <amount> &8- &7Add tokens",
         "&f/token take <player> <amount> &8- &7Remove tokens",
         "&f/token set <player> <amount> &8- &7Set tokens",
         "&f/token giveall <amount> &8- &7Give online players tokens",
         "&8&m--------------------------------"
      ),
      "spawnpvp-help",
      List.of(
         "&8&m--------------------------------",
         "&#2EB6FF&lSpawnPvP Setup",
         "&f/ffa spawnpvp wand &8- &7Get setup axe",
         "&7Left-click corner 1, right-click corner 2",
         "&f/ffa spawnpvp block select &8- &7Use held outline block/slab",
         "&f/ffa spawnpvp info &8- &7View saved circle",
         "&f/ffa spawnpvp clear &8- &7Disable saved circle",
         "&f/ffa spawnpvp reload &8- &7Reload spawnpvp.yml",
         "&8&m--------------------------------"
      ),
      "afk-help",
      List.of(
         "&8&m--------------------------------",
         "&#2EB6FF&lAFK Zone Setup",
         "&f/ffa afk <name> &8- &7Create/load an AFK zone",
         "&7Left-click pos1, right-click pos2 with the axe",
         "&7File: &fplugins/FlixCoreFFA/afk/<name>-afk.yml",
         "&f/ffa afk reload &8- &7Reload AFK zones",
         "&8&m--------------------------------"
      )
   );
   private final p gE;
   private final u gF;

   public r(p var1, u var2) {
      this.gE = var1;
      this.gF = var2;
   }

   public final void h1s7(CommandSender var1, String var2) {
      this.ab2c(var1, var2, Map.of());
   }

   public final void ab2c(CommandSender var1, String var2, Map<String, String> var3) {
      if (!(var2 = ab2c(this.gE, var2, "")).isBlank()) {
         this.gF.m4z1(var1, this.x7q9(var2, var3), Map.of());
      }
   }

   public final void z4c9(CommandSender var1, String var2) {
      this.x7q9(var1, var2, Map.of());
   }

   public final void x7q9(CommandSender var1, String var2, Map<String, String> var3) {
      List<String> var4;
      if ((var4 = gD.get(var2)) != null) {
         for (String var10 : var4) {
            this.gF.m4z1(var1, this.x7q9(var10, var3), Map.of());
         }
      } else {
         var4 = ab2c(this.gE).getStringList(var2);
         FileConfiguration var5 = this.gE.h("messages.yml");
         if (var4.isEmpty() && var5 != null) {
            var4 = var5.getStringList(var2);
         }

         for (String var8 : var4) {
            this.gF.m4z1(var1, this.x7q9(var8, var3), Map.of());
         }
      }
   }

   public static FileConfiguration ab2c(p var0) {
      FileConfiguration var1;
      return (var1 = var0.p6d2("messages.yml", "messages")) == null ? var0.h("messages_en.yml") : var1;
   }

   public static String ab2c(p var0, String var1, String var2) {
      FileConfiguration var3;
      String val;
      if ((var3 = ab2c(var0)) != null && var3.isSet(var1)) {
         val = var3.getString(var1, var2);
      } else if ((var3 = var0.h("messages.yml")) != null && var3.isSet(var1)) {
         val = var3.getString(var1, var2);
      } else {
         FileConfiguration var4;
         val = (var4 = var0.h("messages_en.yml")) == null ? var2 : var4.getString(var1, var2);
      }
      if (val != null) {
         val = val.replace("TwoCubeFFA", "FlixCore FFA")
                  .replace("twocubeffa", "flixcoreffa")
                  .replace("TwoCube", "FlixCore")
                  .replace("twocube", "flixcore");
      }
      return val;
   }

   private String x7q9(String var1, Map<String, String> var2) {
      if (var1 != null) {
         var1 = var1.replace("TwoCubeFFA", "FlixCore FFA")
                    .replace("twocubeffa", "flixcoreffa")
                    .replace("TwoCube", "FlixCore")
                    .replace("twocube", "flixcore");
      }
      Map<String, String> var5 = new HashMap<>(var2);
      var5.putIfAbsent("prefix", ab2c(this.gE, "prefix", ""));

      for (Entry<String, String> var3 : var5.entrySet()) {
         var1 = var1.replace("%" + var3.getKey() + "%", var3.getValue());
      }

      return var1;
   }
}
