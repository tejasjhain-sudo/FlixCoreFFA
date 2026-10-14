package club.aspvp.ffa.internal;

import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class q {
   public final p gw;
   private final t gx;
   private final b gy;
   private final c gz;
   private final h gA;
   private final u gB;
   private final Map<UUID, Long> gC = new ConcurrentHashMap<>();

   public q(p var1, t var2, b var3, c var4, h var5, u var6) {
      this.gw = var1;
      this.gx = var2;
      this.gy = var3;
      this.gz = var4;
      this.gA = var5;
      this.gB = var6;
   }

   public final void ad(Player var1) {
      if (this.gy.w(var1)) {
         this.b8t3(var1, "join");
         this.g1v7(var1, "join");
         p var2 = this.gw;
         if (this.gw.gt.getConfig().getBoolean("first-join-protection.enabled", true)) {
            Map var10000 = this.gC;
            UUID var10001 = var1.getUniqueId();
            long var10002 = System.currentTimeMillis();
            var2 = this.gw;
            var10000.put(var10001, var10002 + this.gw.gt.getConfig().getLong("first-join-protection.duration-seconds", 5L) * 1000L);
         }

         var2 = this.gw;
         if (this.gw.gt.getConfig().getBoolean("join-leave-messages.enabled", true)) {
            var2 = this.gw;
            this.ab2c(
               this.gw.gt.getConfig().getString("join-leave-messages.join-message", ""),
               var1,
               Map.of("player", var1.getName(), "arena", this.gy.v(var1).orElse("None"), "kit", this.gy.x(var1))
            );
         }
      }
   }

   public final void ae(Player var1) {
      if (this.gy.w(var1)) {
         this.gC.remove(var1.getUniqueId());
         this.b8t3(var1, "leave");
         p var2 = this.gw;
         if (this.gw.gt.getConfig().getBoolean("join-leave-messages.enabled", true)) {
            var2 = this.gw;
            FileConfiguration var10000 = this.gw.gt.getConfig();
            var2 = this.gw;
            String var5 = var10000.getString("join-leave-messages.leave-message", this.gw.gt.getConfig().getString("join-leave-messages.quit-message", ""));
            this.ab2c(var5, var1, Map.of("player", var1.getName(), "arena", this.gy.v(var1).orElse("None"), "kit", this.gy.x(var1)));
         }
      }
   }

   public final void x7q9(Player var1, Player var2) {
      this.b8t3(var1, "death");
      p var4 = this.gw;
      String var3 = this.gw.gt.getConfig().getString("death-messages.format", "");
      String var10000;
      if (var2 == null) {
         var4 = this.gw;
         var10000 = this.gw.gt.getConfig().getString("death-messages.death-reasons.UNKNOWN", "died.");
      } else {
         var10000 = var2.getName();
      }

      String var5 = var10000;
      this.x7q9(var3, var1, Map.of("victim", var1.getName(), "attacker", var5, "player", var1.getName()));
   }

   public final boolean af(Player var1) {
      Long var2;
      if ((var2 = this.gC.get(var1.getUniqueId())) == null) {
         return false;
      } else if (var2 < System.currentTimeMillis()) {
         this.gC.remove(var1.getUniqueId());
         return false;
      } else {
         return true;
      }
   }

   public void ag(Player var1) {
      p var3 = this.gw;
      if (this.gw.gt.getConfig().getBoolean("killstreak-rewards.enabled", true)) {
         String var2 = String.valueOf(this.gA.W(var1).w);
         var3 = this.gw;

         for (String var4 : this.gw.gt.getConfig().getStringList("killstreak-rewards.rewards." + var2)) {
            this.ab2c(var1, var4, var2);
         }
      }
   }

   private void ab2c(Player var1, String var2, String var3) {
      String var4 = this.gB.m4z1(var2, this.ab2c(var1, Map.of("streak", var3)));
      this.gx.x7q9(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var4));
   }

   public void ah(Player var1) {
      p var2 = this.gw;
      if (this.gw.gt.getConfig().getBoolean("tokens.enabled", true)) {
         var2 = this.gw;
         if (this.gw.gt.getConfig().getBoolean("tokens.kill-message", true)) {
            int var6;
            if ((var6 = this.gA.Y(var1)) <= 0) {
               return;
            }

            String var3;
            if ((var3 = r.ab2c(this.gw, "token-kill-reward", "")) != null && !var3.isBlank()) {
               u var10000 = this.gB;
               String var4 = this.gB
                  .m4z1(
                     var3,
                     this.ab2c(
                        var1,
                        Map.of(
                           "amount",
                           String.valueOf(var6),
                           "base_amount",
                           String.valueOf(this.gA.N()),
                           "multiplier",
                           x7q9(this.gA.Z(var1)),
                           "tokens",
                           String.valueOf(this.gA.X(var1))
                        )
                     )
                  );
               Component var7 = var10000.m4z1(var1, var4, true);
               this.gx.ab2c(var1, () -> var1.sendMessage(var7));
               return;
            }

            return;
         }
      }
   }

   private static String x7q9(double var0) {
      return var0 == Math.rint(var0) ? String.valueOf((int)var0) : String.format(Locale.US, "%.2f", var0).replaceAll("0+$", "").replaceAll("\\.$", "");
   }

   private Map<String, String> ab2c(Player var1, Map<String, String> var2) {
      HashMap var3;
      (var3 = new HashMap()).put("prefix", r.ab2c(this.gw, "prefix", ""));
      var3.put("player", var1.getName());
      var3.put("player_name", var1.getName());
      var3.put("arena", this.gy.v(var1).orElse("None"));
      var3.put("kit", this.gy.x(var1));
      var3.put("kills", String.valueOf(this.gA.W(var1).u));
      var3.put("killstreak", String.valueOf(this.gA.W(var1).w));
      var3.put("streak", String.valueOf(this.gA.W(var1).w));
      var3.put("tokens", String.valueOf(this.gA.X(var1)));
      var3.put("ffa_token", String.valueOf(this.gA.X(var1)));
      var3.put("flixcoreffa_token", String.valueOf(this.gA.X(var1)));
      var3.put("flixffa_token", String.valueOf(this.gA.X(var1)));
      var3.put("twocubeffa_token", String.valueOf(this.gA.X(var1)));
      var3.putAll(var2);
      return var3;
   }

   public void b8t3(Player var1, String var2) {
      if (this.gz.H(var1)) {
         ConfigurationSection var4;
         if ((var4 = this.gw.h("sounds.yml").getConfigurationSection("sounds." + var2)) != null && var4.getBoolean("enabled", true)) {
            try {
               var1.playSound(
                  var1.getLocation(),
                  Sound.valueOf(var4.getString("sound", "ENTITY_EXPERIENCE_ORB_PICKUP")),
                  (float)var4.getDouble("volume", 1.0),
                  (float)var4.getDouble("pitch", 1.0)
               );
            } catch (IllegalArgumentException var3) {
            }
         }
      }
   }

   public void g1v7(Player var1, String var2) {
      if (this.gz.I(var1)) {
         ConfigurationSection var6;
         if ((var6 = this.u2q5(var2)) != null && var6.getBoolean("enabled", true)) {
            u var10001 = this.gB;
            String var5 = var6.getString("title", "");
            Component var8 = var10001.m4z1(var1, var5, true);
            u var10002 = this.gB;
            var5 = var6.getString("subtitle", "");
            var1.showTitle(
               Title.title(
                  var8,
                  var10002.m4z1(var1, var5, true),
                  Times.times(
                     Duration.ofMillis(var6.getLong("fade-in", 10L) * 50L),
                     Duration.ofMillis(var6.getLong("stay", 40L) * 50L),
                     Duration.ofMillis(var6.getLong("fade-out", 10L) * 50L)
                  )
               )
            );
         }
      }
   }

   private ConfigurationSection u2q5(String var1) {
      FileConfiguration var2;
      ConfigurationSection var3;
      if ((var3 = (var2 = this.gw.p6d2("titles.yml", "titles")) == null ? null : var2.getConfigurationSection("titles." + var1)) != null) {
         return var3;
      } else {
         return (var2 = this.gw.h("titles.yml")) == null ? null : var2.getConfigurationSection("titles." + var1);
      }
   }

   private void ab2c(String var1, Player var2, Map<String, String> var3) {
      this.ab2c(var1, var2, var3, var0 -> true);
   }

   public void x7q9(String var1, Player var2, Map<String, String> var3) {
      this.ab2c(var1, var2, var3, this.gz::G);
   }

   private void ab2c(String var1, Player var2, Map<String, String> var3, Predicate<Player> var4) {
      if (var1 != null && !var1.isBlank()) {
         u var10000 = this.gB;
         String var7 = this.gB.m4z1(var1, var3);
         Component var5 = var10000.m4z1(var2, var7, true);

         for (Player var8 : Bukkit.getOnlinePlayers()) {
            if (var4.test(var8)) {
               this.gx.ab2c(var8, () -> var8.sendMessage(var5));
            }
         }
      }
   }
}
