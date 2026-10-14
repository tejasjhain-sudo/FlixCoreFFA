package club.aspvp.ffa.internal;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class j4n9 {
   private final p cr;
   private final t cs;
   private final u ct;
   private final c cu;
   private final List<t.ab2c> cv = new ArrayList<>();

   public j4n9(p var1, t var2, u var3, c var4) {
      this.cr = var1;
      this.cs = var2;
      this.ct = var3;
      this.cu = var4;
   }

   public final void v8m5() {
      this.n2x6();
      if (this.cr.x7q9("announcements", true)) {
         ConfigurationSection var1;
         if ((var1 = this.cr.h("announcements.yml").getConfigurationSection("announcements.entries")) != null) {
            for (String var3 : var1.getKeys(false)) {
               ConfigurationSection var7;
               if ((var7 = var1.getConfigurationSection(var3)) != null && var7.getBoolean("enabled", true)) {
                  long var5 = Math.max(20L, var7.getLong("interval", 300L) * 20L);
                  this.cv.add(this.cs.ab2c(() -> {
                     ConfigurationSection var2 = var7;
                     j4n9 var5x = this;

                     for (Player var4 : Bukkit.getOnlinePlayers()) {
                        var5x.cs.ab2c(var4, () -> {
                           c var3x = this.cu;
                           if (this.cu.F(var4).gm) {
                              for (String var4x : var2.getStringList("lines")) {
                                 u var11 = this.ct;
                                 var4.sendMessage(this.ct.m4z1(var4, var4x, true));
                              }

                              if (var2.getBoolean("play-sound", false)) {
                                 ConfigurationSection var12 = var2;
                                 Player var10 = var4;

                                 try {
                                    Sound var8 = Sound.valueOf(var12.getString("sound", "ENTITY_PLAYER_LEVELUP"));
                                    var10.playSound(var10.getLocation(), var8, (float)var12.getDouble("volume", 1.0), (float)var12.getDouble("pitch", 1.0));
                                    return;
                                 } catch (IllegalArgumentException var7x) {
                                 }
                              }
                           }
                        });
                     }
                  }, var5, var5));
               }
            }
         }
      }
   }

   public final void n2x6() {
      this.cv.forEach(t.ab2c::cancel);
      this.cv.clear();
   }

   public final void w7k3() {
      this.v8m5();
   }
}
