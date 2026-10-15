package club.aspvp.ffa.hook;

import club.aspvp.ffa.internal.b;
import club.aspvp.ffa.internal.h;
import club.aspvp.ffa.internal.i5k2;
import club.aspvp.ffa.internal.o;
import club.aspvp.ffa.internal.o3w8;
import club.aspvp.ffa.internal.t;
import club.aspvp.ffa.internal.u2q5;
import club.aspvp.ffa.internal.v;
import club.aspvp.ffa.internal.w;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FfaPlaceholderExpansion extends PlaceholderExpansion {
   private final Plugin plugin;
   private final String identifier;
   private final h stats;
   private final b players;
   private final u2q5 arenas;
   private final v database;
   private final i5k2 crossServer;
   private final o3w8 chatCustomization;
   private final Map<String, List<o>> topCache = new ConcurrentHashMap<>();

   public FfaPlaceholderExpansion(Plugin var1, h var2, b var3, u2q5 var4, v var5, t var6, i5k2 var7, o3w8 var8, String var9) {
      this.plugin = var1;
      this.identifier = var9;
      this.stats = var2;
      this.players = var3;
      this.arenas = var4;
      this.database = var5;
      this.crossServer = var7;
      this.chatCustomization = var8;
      var6.ab2c(this::refreshTop, 20L, 1200L);
      this.refreshTop();
   }

   public String getIdentifier() {
      return this.identifier;
   }

   public String getAuthor() {
      return "FlixCore";
   }

   public String getVersion() {
      return this.plugin.getDescription().getVersion();
   }

   public boolean persist() {
      return true;
   }

   public String onRequest(OfflinePlayer var1, String var2) {
      if (var1 == null) {
         return "";
      } else {
         Player var3;
         o var6 = (var3 = var1.getPlayer()) == null
            ? this.stats.m4z1(var1.getUniqueId()).orElse(new o(var1.getUniqueId(), var1.getName() == null ? "Unknown" : var1.getName()))
            : this.stats.W(var3);
         String var7;
         String var4 = var7 = var2.toLowerCase(Locale.ROOT);
         switch (var4) {
            case "kills":
               return String.valueOf(var6.u);
            case "deaths":
               return String.valueOf(var6.v);
            case "killstreak":
            case "streak":
               return String.valueOf(var6.w);
            case "best_killstreak":
            case "beststreak":
            case "best_streak":
               return String.valueOf(var6.x);
            case "token":
            case "tokens":
            case "coins":
               return String.valueOf(var6.y);
            case "level":
               return String.valueOf(var6.A);
            case "xp":
               return String.valueOf(var6.z);
            case "kdr":
               return var6.ae();
            case "arena":
               if (var3 == null) {
                  return "None";
               }

               return this.players.v(var3).orElse("None");
            case "kit":
               if (var3 == null) {
                  return "None";
               }

               return this.players.y(var3);
            case "in_game":
            case "ingame":
               if (var3 != null && this.players.w(var3)) {
                  return "true";
               }

               return "false";
            case "players":
            case "online":
               return String.valueOf(this.players.w());
            case "player":
            case "player_name":
            case "name":
               return var1.getName() == null ? "" : var1.getName();
            case "tag":
               if (var3 == null) {
                  return "";
               }

               return w.n(this.chatCustomization.j4n9(var3));
            case "tag_name":
               if (var3 == null) {
                  return "";
               }

               return this.chatCustomization.u2q5(var3);
            case "tag_id":
               if (var3 == null) {
                  return "";
               }

               return this.chatCustomization.e7z1(var3);
            case "chatcolor":
            case "chatcolor_chat_colour":
               if (var3 == null) {
                  return "";
               }

               return w.n(this.chatCustomization.l9m6(var3));
            case "chatcolor_name":
            case "chatcolor_colour_name":
               if (var3 == null) {
                  return "";
               }

               return this.chatCustomization.o3w8(var3);
            case "ping":
            case "player_ping":
               if (var3 == null) {
                  return "0";
               }

               return String.valueOf(var3.getPing());
            default:
               return this.dynamic(var7);
         }
      }
   }

   private String dynamic(String var1) {
      if (var1.startsWith("players_")) {
         return String.valueOf(this.players.i5k2(var1.substring(8)));
      } else {
         return var1.startsWith("ping_") ? this.crossServer.c2f4(var1.substring(5)) : this.top(var1);
      }
   }

   private String top(String var1) {
      String[] var6;
      if ((var6 = var1.split("_")).length == 4 && var6[0].equals("top")) {
         String var2 = var6[1];

         int var3;
         try {
            var3 = Integer.parseInt(var6[2]) - 1;
         } catch (NumberFormatException var5) {
            return "";
         }

         List var4;
         if ((var4 = this.topCache.get(var2)) != null && var3 >= 0 && var3 < var4.size()) {
            o var9 = (o)var4.get(var3);
            if (var6[3].equals("name")) {
               return var9.name;
            } else if (!var6[3].equals("value")) {
               return "";
            } else {
               switch (var2) {
                  case "kills":
                     return String.valueOf(var9.u);
                  case "killstreak":
                     return String.valueOf(var9.x);
                  default:
                     return "";
               }
            }
         } else {
            return "";
         }
      } else {
         return "";
      }
   }

   private void refreshTop() {
      this.database.x7q9("kills", 3).thenAccept(var1 -> this.topCache.put("kills", (List<o>)var1));
      this.database.x7q9("best_killstreak", 3).thenAccept(var1 -> this.topCache.put("killstreak", (List<o>)var1));
   }
}
