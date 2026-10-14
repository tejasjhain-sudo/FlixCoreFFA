package club.aspvp.ffa.internal;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class s {
   private static final DateTimeFormatter gG = DateTimeFormatter.ofPattern("HH:mm:ss");
   private static final Pattern gH = Pattern.compile("%(?:twocubeffa|flixcoreffa|flixffa)_ping_([A-Za-z0-9_.-]+)%");
   private static final Pattern gI = Pattern.compile("%\\s*((?:ffa|flixcoreffa|flixffa|twocubeffa)_tokens?|playerpoints_tokens)\\s*%", 2);
   private final p gJ;
   private final h gK;
   private final b gL;
   private final u2q5 gM;
   private final i5k2 gN;
   public o3w8 o3w8;

   public s(p var1, h var2, b var3, u2q5 var4, i5k2 var5) {
      this.gJ = var1;
      this.gK = var2;
      this.gL = var3;
      this.gM = var4;
      this.gN = var5;
   }

   public final String j4n9(Player var1, String var2) {
      if (var2 == null) {
         return "";
      } else {
         var2 = var2.replace("%server_time_HH:mm:ss%", LocalTime.now().format(gG));
         if (var1 != null) {
            o var3 = this.gK.W(var1);
            String var4 = this.gL.v(var1).orElse("None");
            p var7 = this.gJ;
            String var10000;
            if (!this.gJ.gt.getConfig().getBoolean("player-display.prefix-before-name.enabled", true)) {
               var10000 = "";
            } else {
               var7 = this.gJ;
               String var6;
               if (!(var6 = this.gJ.gt.getConfig().getString("player-display.prefix-before-name.value", "")).isBlank()) {
                  var7 = this.gJ;
                  if (this.gJ.gt.getConfig().getBoolean("player-display.prefix-before-name.space-after", true)) {
                     var6 = var6 + " ";
                  }
               }

               var10000 = var6;
            }

            String var5 = var10000;
            var10000 = var2.replace("%player_name%", var1.getName())
               .replace("%ffa_name_prefix%", var5)
               .replace("%ffa_player_display%", var5 + var1.getName())
               .replace("%ffa_kills%", String.valueOf(var3.u))
               .replace("%ffa_deaths%", String.valueOf(var3.v))
               .replace("%ffa_kdr%", var3.ae())
               .replace("%ffa_killstreak%", String.valueOf(var3.w))
               .replace("%ffa_best_killstreak%", String.valueOf(var3.x))
               .replace("%ffa_token%", String.valueOf(var3.y))
               .replace("%ffa_tokens%", String.valueOf(var3.y))
               .replace("%PLAYERPOINTS_tokens%", String.valueOf(var3.y))
               .replace("%twocubeffa_token%", String.valueOf(var3.y))
               .replace("%flixcoreffa_token%", String.valueOf(var3.y))
               .replace("%flixcoreffa_tokens%", String.valueOf(var3.y))
               .replace("%flixffa_token%", String.valueOf(var3.y))
               .replace("%flixffa_tokens%", String.valueOf(var3.y))
               .replace("%twocubeffa_tokens%", String.valueOf(var3.y))
               .replace("%ffa_xp%", String.valueOf(var3.z))
               .replace("%ffa_level%", String.valueOf(var3.A))
               .replace("%ffa_arena%", var4)
               .replace("%ffa_kit%", this.gL.y(var1))
               .replace("%ffa_in_game%", this.gL.w(var1) ? "true" : "false")
               .replace("%ffa_player_ping%", String.valueOf(var1.getPing()))
               .replace("%flixcoreffa_player_ping%", String.valueOf(var1.getPing()))
               .replace("%flixffa_player_ping%", String.valueOf(var1.getPing()))
               .replace("%twocubeffa_player_ping%", String.valueOf(var1.getPing()));
            int var17 = var3.y;
            var5 = var10000;
            var2 = gI.matcher(var5).replaceAll(Matcher.quoteReplacement(String.valueOf(Math.max(0, var17))));
            if (this.o3w8 != null) {
               var2 = var2.replace("%ffa_tag%", this.o3w8.j4n9(var1))
                  .replace("%flixcoreffa_tag%", this.o3w8.j4n9(var1))
                  .replace("%flixffa_tag%", this.o3w8.j4n9(var1))
                  .replace("%twocubeffa_tag%", this.o3w8.j4n9(var1))
                  .replace("%ffa_tag_name%", this.o3w8.u2q5(var1))
                  .replace("%flixcoreffa_tag_name%", this.o3w8.u2q5(var1))
                  .replace("%flixffa_tag_name%", this.o3w8.u2q5(var1))
                  .replace("%twocubeffa_tag_name%", this.o3w8.u2q5(var1))
                  .replace("%ffa_tag_id%", this.o3w8.e7z1(var1))
                  .replace("%flixcoreffa_tag_id%", this.o3w8.e7z1(var1))
                  .replace("%flixffa_tag_id%", this.o3w8.e7z1(var1))
                  .replace("%twocubeffa_tag_id%", this.o3w8.e7z1(var1))
                  .replace("%ffa_chatcolor_chat_colour%", this.o3w8.l9m6(var1))
                  .replace("%flixcoreffa_chatcolor_chat_colour%", this.o3w8.l9m6(var1))
                  .replace("%flixffa_chatcolor_chat_colour%", this.o3w8.l9m6(var1))
                  .replace("%twocubeffa_chatcolor_chat_colour%", this.o3w8.l9m6(var1))
                  .replace("%ffa_chatcolor_colour_name%", this.o3w8.o3w8(var1))
                  .replace("%flixcoreffa_chatcolor_colour_name%", this.o3w8.o3w8(var1))
                  .replace("%flixffa_chatcolor_colour_name%", this.o3w8.o3w8(var1))
                  .replace("%twocubeffa_chatcolor_colour_name%", this.o3w8.o3w8(var1));
            }
         }

         var2 = var2.replace("%ffa_players%", String.valueOf(this.gL.w()));

         for (j var14 : this.gM.arenas()) {
            var2 = var2.replace("%ffa_players_" + var14.O() + "%", String.valueOf(this.gL.i5k2(var14.O())));
         }

         s var16 = this;
         Matcher var10 = gH.matcher(var2);
         StringBuffer var13 = new StringBuffer();

         while (var10.find()) {
            var10.appendReplacement(var13, Matcher.quoteReplacement(var16.gN.c2f4(var10.group(1))));
         }

         var10.appendTail(var13);
         var2 = var13.toString();
         if (var1 != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            var2 = PlaceholderAPI.setPlaceholders(var1, var2);
         }

         return var2;
      }
   }
}
