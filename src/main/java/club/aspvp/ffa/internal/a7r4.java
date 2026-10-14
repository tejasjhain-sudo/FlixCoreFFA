package club.aspvp.ffa.internal;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class a7r4 {
   private final p ef;
   private final t eg;
   private final b eh;
   private final c ei;
   private final u ej;
   private t.ab2c ci;

   public a7r4(p var1, t var2, b var3, c var4, u var5) {
      this.ef = var1;
      this.eg = var2;
      this.eh = var3;
      this.ei = var4;
      this.ej = var5;
   }

   public final void m() {
      this.n();
      if (this.ef.x7q9("display", true)) {
         long var1 = this.b8t3("actionbar").getLong("update-interval-ticks", 40L);
         this.ci = this.eg.ab2c(this::o, 20L, Math.max(5L, var1));
      }
   }

   public final void n() {
      if (this.ci != null) {
         this.ci.cancel();
         this.ci = null;
      }
   }

   private void o() {
      for (Player var2 : Bukkit.getOnlinePlayers()) {
         this.eg
            .ab2c(
               var2,
               () -> {
                  if (this.ef.x7q9("display", true)) {
                     c var3 = this.ei;
                     if (this.ei.F(var2).gn) {
                        String var4 = "actionbar";
                        String var6;
                        ConfigurationSection var7;
                        ConfigurationSection var8;
                        ConfigurationSection var9;
                        if ((
                                 var8 = !(var9 = this.b8t3(var4)).getBoolean("enabled", true)
                                    ? null
                                    : (
                                       (var6 = this.eh.v(var2).orElse(null)) != null && (var7 = var9.getConfigurationSection("arenas." + var6)) != null
                                          ? var7
                                          : var9.getConfigurationSection("default")
                                    )
                              )
                              == null
                           || !var8.getBoolean("enabled", true)) {
                           return;
                        }

                        u var10001 = this.ej;
                        String var2x = var8.getString("message", "");
                        var2.sendActionBar(var10001.m4z1(var2, var2x, true));
                     }
                  }
               }
            );
      }
   }

   private ConfigurationSection b8t3(String var1) {
      ConfigurationSection var2;
      return (var2 = this.ef.h("display.yml").getConfigurationSection(var1)) == null ? this.ef.h("display.yml").createSection(var1) : var2;
   }
}
