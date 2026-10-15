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
   private static final Pattern gH = Pattern.compile("%(?:twocubeffa|flixcoreffa|flixffa|flixcore|ffa)_ping_([A-Za-z0-9_.-]+)%");
   private static final Pattern gI = Pattern.compile("%\\s*((?:ffa|flixcoreffa|flixffa|flixcore|twocubeffa)_tokens?|playerpoints_tokens|tokens?)\\s*%", 2);
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
            String killsStr = String.valueOf(var3.u);
            String deathsStr = String.valueOf(var3.v);
            String kdrStr = var3.ae();
            String streakStr = String.valueOf(var3.w);
            String bestStreakStr = String.valueOf(var3.x);
            String tokensStr = String.valueOf(var3.y);
            String xpStr = String.valueOf(var3.z);
            String levelStr = String.valueOf(var3.A);
            String kitStr = this.gL.y(var1);
            String inGameStr = this.gL.w(var1) ? "true" : "false";
            String pingStr = String.valueOf(var1.getPing());

            var10000 = var2.replace("%player_name%", var1.getName())
               .replace("%player%", var1.getName())
               .replace("%name%", var1.getName())
               .replace("%ffa_player%", var1.getName())
               .replace("%flixcoreffa_player%", var1.getName())
               .replace("%flixcore_player%", var1.getName())
               .replace("%ffa_name_prefix%", var5)
               .replace("%ffa_player_display%", var5 + var1.getName())
               .replace("%ffa_kills%", killsStr)
               .replace("%flixcoreffa_kills%", killsStr)
               .replace("%flixcore_kills%", killsStr)
               .replace("%flixffa_kills%", killsStr)
               .replace("%kills%", killsStr)
               .replace("%twocubeffa_kills%", killsStr)
               .replace("%ffa_deaths%", deathsStr)
               .replace("%flixcoreffa_deaths%", deathsStr)
               .replace("%flixcore_deaths%", deathsStr)
               .replace("%flixffa_deaths%", deathsStr)
               .replace("%deaths%", deathsStr)
               .replace("%twocubeffa_deaths%", deathsStr)
               .replace("%ffa_kdr%", kdrStr)
               .replace("%flixcoreffa_kdr%", kdrStr)
               .replace("%flixcore_kdr%", kdrStr)
               .replace("%flixffa_kdr%", kdrStr)
               .replace("%kdr%", kdrStr)
               .replace("%twocubeffa_kdr%", kdrStr)
               .replace("%ffa_killstreak%", streakStr)
               .replace("%ffa_streak%", streakStr)
               .replace("%flixcoreffa_killstreak%", streakStr)
               .replace("%flixcoreffa_streak%", streakStr)
               .replace("%flixcore_killstreak%", streakStr)
               .replace("%flixcore_streak%", streakStr)
               .replace("%flixffa_killstreak%", streakStr)
               .replace("%flixffa_streak%", streakStr)
               .replace("%killstreak%", streakStr)
               .replace("%streak%", streakStr)
               .replace("%twocubeffa_killstreak%", streakStr)
               .replace("%ffa_best_killstreak%", bestStreakStr)
               .replace("%ffa_beststreak%", bestStreakStr)
               .replace("%ffa_best_streak%", bestStreakStr)
               .replace("%flixcoreffa_best_killstreak%", bestStreakStr)
               .replace("%flixcoreffa_beststreak%", bestStreakStr)
               .replace("%flixcore_best_killstreak%", bestStreakStr)
               .replace("%flixcore_beststreak%", bestStreakStr)
               .replace("%flixffa_best_killstreak%", bestStreakStr)
               .replace("%best_killstreak%", bestStreakStr)
               .replace("%beststreak%", bestStreakStr)
               .replace("%best_streak%", bestStreakStr)
               .replace("%twocubeffa_best_killstreak%", bestStreakStr)
               .replace("%ffa_token%", tokensStr)
               .replace("%ffa_tokens%", tokensStr)
               .replace("%flixcoreffa_token%", tokensStr)
               .replace("%flixcoreffa_tokens%", tokensStr)
               .replace("%flixcore_token%", tokensStr)
               .replace("%flixcore_tokens%", tokensStr)
               .replace("%flixffa_token%", tokensStr)
               .replace("%flixffa_tokens%", tokensStr)
               .replace("%tokens%", tokensStr)
               .replace("%token%", tokensStr)
               .replace("%coins%", tokensStr)
               .replace("%PLAYERPOINTS_tokens%", tokensStr)
               .replace("%twocubeffa_token%", tokensStr)
               .replace("%twocubeffa_tokens%", tokensStr)
               .replace("%ffa_xp%", xpStr)
               .replace("%flixcoreffa_xp%", xpStr)
               .replace("%flixcore_xp%", xpStr)
               .replace("%flixffa_xp%", xpStr)
               .replace("%xp%", xpStr)
               .replace("%twocubeffa_xp%", xpStr)
               .replace("%ffa_level%", levelStr)
               .replace("%flixcoreffa_level%", levelStr)
               .replace("%flixcore_level%", levelStr)
               .replace("%flixffa_level%", levelStr)
               .replace("%level%", levelStr)
               .replace("%twocubeffa_level%", levelStr)
               .replace("%ffa_arena%", var4)
               .replace("%flixcoreffa_arena%", var4)
               .replace("%flixcore_arena%", var4)
               .replace("%flixffa_arena%", var4)
               .replace("%arena%", var4)
               .replace("%twocubeffa_arena%", var4)
               .replace("%ffa_kit%", kitStr)
               .replace("%flixcoreffa_kit%", kitStr)
               .replace("%flixcore_kit%", kitStr)
               .replace("%flixffa_kit%", kitStr)
               .replace("%kit%", kitStr)
               .replace("%twocubeffa_kit%", kitStr)
               .replace("%ffa_in_game%", inGameStr)
               .replace("%ffa_ingame%", inGameStr)
               .replace("%flixcoreffa_in_game%", inGameStr)
               .replace("%flixcore_in_game%", inGameStr)
               .replace("%in_game%", inGameStr)
               .replace("%ingame%", inGameStr)
               .replace("%twocubeffa_in_game%", inGameStr)
               .replace("%ffa_player_ping%", pingStr)
               .replace("%ffa_ping%", pingStr)
               .replace("%flixcoreffa_player_ping%", pingStr)
               .replace("%flixcoreffa_ping%", pingStr)
               .replace("%flixcore_player_ping%", pingStr)
               .replace("%flixcore_ping%", pingStr)
               .replace("%flixffa_player_ping%", pingStr)
               .replace("%flixffa_ping%", pingStr)
               .replace("%player_ping%", pingStr)
               .replace("%ping%", pingStr)
               .replace("%twocubeffa_player_ping%", pingStr)
               .replace("%twocubeffa_ping%", pingStr);
            int var17 = var3.y;
            var5 = var10000;
            var2 = gI.matcher(var5).replaceAll(Matcher.quoteReplacement(String.valueOf(Math.max(0, var17))));
            if (this.o3w8 != null) {
               String tag = this.o3w8.j4n9(var1);
               String tagName = this.o3w8.u2q5(var1);
               String tagId = this.o3w8.e7z1(var1);
               String chatColor = this.o3w8.l9m6(var1);
               String colourName = this.o3w8.o3w8(var1);
               var2 = var2.replace("%ffa_tag%", tag)
                  .replace("%flixcoreffa_tag%", tag)
                  .replace("%flixcore_tag%", tag)
                  .replace("%flixffa_tag%", tag)
                  .replace("%tag%", tag)
                  .replace("%twocubeffa_tag%", tag)
                  .replace("%ffa_tag_name%", tagName)
                  .replace("%flixcoreffa_tag_name%", tagName)
                  .replace("%flixcore_tag_name%", tagName)
                  .replace("%flixffa_tag_name%", tagName)
                  .replace("%tag_name%", tagName)
                  .replace("%twocubeffa_tag_name%", tagName)
                  .replace("%ffa_tag_id%", tagId)
                  .replace("%flixcoreffa_tag_id%", tagId)
                  .replace("%flixcore_tag_id%", tagId)
                  .replace("%flixffa_tag_id%", tagId)
                  .replace("%tag_id%", tagId)
                  .replace("%twocubeffa_tag_id%", tagId)
                  .replace("%ffa_chatcolor_chat_colour%", chatColor)
                  .replace("%flixcoreffa_chatcolor_chat_colour%", chatColor)
                  .replace("%flixcore_chatcolor_chat_colour%", chatColor)
                  .replace("%flixffa_chatcolor_chat_colour%", chatColor)
                  .replace("%chatcolor%", chatColor)
                  .replace("%twocubeffa_chatcolor_chat_colour%", chatColor)
                  .replace("%ffa_chatcolor_colour_name%", colourName)
                  .replace("%flixcoreffa_chatcolor_colour_name%", colourName)
                  .replace("%flixcore_chatcolor_colour_name%", colourName)
                  .replace("%flixffa_chatcolor_colour_name%", colourName)
                  .replace("%chatcolor_name%", colourName)
                  .replace("%twocubeffa_chatcolor_colour_name%", colourName);
            }
         }

         String playersCount = String.valueOf(this.gL.w());
         var2 = var2.replace("%ffa_players%", playersCount)
            .replace("%flixcoreffa_players%", playersCount)
            .replace("%flixcore_players%", playersCount)
            .replace("%flixffa_players%", playersCount)
            .replace("%ffa_online%", playersCount)
            .replace("%players%", playersCount)
            .replace("%online%", playersCount)
            .replace("%twocubeffa_players%", playersCount);

         for (j var14 : this.gM.arenas()) {
            String arenaPlayers = String.valueOf(this.gL.i5k2(var14.O()));
            var2 = var2.replace("%ffa_players_" + var14.O() + "%", arenaPlayers)
               .replace("%flixcoreffa_players_" + var14.O() + "%", arenaPlayers)
               .replace("%flixcore_players_" + var14.O() + "%", arenaPlayers)
               .replace("%flixffa_players_" + var14.O() + "%", arenaPlayers)
               .replace("%twocubeffa_players_" + var14.O() + "%", arenaPlayers);
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
