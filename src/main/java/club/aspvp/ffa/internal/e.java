package club.aspvp.ffa.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class e {
   private final String eS = "f" + Integer.toHexString((int)(System.nanoTime() & 65535L));
   private final p eT;
   private final t eU;
   private final b eV;
   private final c eW;
   private final u eX;
   private final Set<UUID> eY = ConcurrentHashMap.newKeySet();
   private final Map<UUID, Integer> eZ = new ConcurrentHashMap<>();
   private t.ab2c ci;

   public e(p var1, t var2, b var3, c var4, u var5) {
      this.eT = var1;
      this.eU = var2;
      this.eV = var3;
      this.eW = var4;
      this.eX = var5;
   }

   public final void y() {
      this.ab2c(true);
      if (this.eT.x7q9("scoreboard", true) && this.eT.h("scoreboard.yml").getBoolean("enabled", true) && aa.aA()) {
         long var1 = this.eT.h("scoreboard.yml").getLong("update-interval-ticks", 20L);
         this.ci = this.eU.ab2c(this::z, 20L, Math.max(5L, var1));
      }
   }

   public void ab2c(boolean var1) {
      if (this.ci != null) {
         this.ci.cancel();
         this.ci = null;
      }

      if (!var1) {
         this.eY.clear();
         this.eZ.clear();
      } else {
         for (Player var2 : Bukkit.getOnlinePlayers()) {
            try {
               this.eU.ab2c(var2, () -> this.L(var2));
            } catch (IllegalStateException var3) {
               this.eY.remove(var2.getUniqueId());
               this.eZ.remove(var2.getUniqueId());
            }
         }
      }
   }

   public final void L(Player var1) {
      UUID var2 = var1.getUniqueId();
      if (this.eY.remove(var2)) {
         try {
            aa.e7z1(var1, this.x7q9(var2));
         } catch (Exception var3) {
         }
      }

      this.eZ.remove(var2);
   }

   public final void z() {
      if (aa.aA()) {
         for (Player var2 : Bukkit.getOnlinePlayers()) {
            this.eU
               .ab2c(
                  var2,
                  () -> {
                     Player var2x = var2;
                     e var12 = this;
                     if (aa.aA()) {
                        c var3 = this.eW;
                        if (!this.eW.F(var2).gk) {
                           this.L(var2);
                           return;
                        }

                        ConfigurationSection var10000;
                        label56: {
                           String var4;
                           if ((var4 = this.eV.v(var2).orElse(null)) != null) {
                              ConfigurationSection var17;
                              if ((var17 = this.eT.h("scoreboard.yml").getConfigurationSection("arenas." + var4)) != null) {
                                 var10000 = var17;
                                 break label56;
                              }

                              ConfigurationSection var18;
                              if ((var18 = this.eT.h("scoreboard.yml").getConfigurationSection("arena-default")) != null) {
                                 var10000 = var18;
                                 break label56;
                              }
                           }

                           ConfigurationSection var19;
                           var10000 = (var19 = this.eT.h("scoreboard.yml").getConfigurationSection("lobby")) != null
                              ? var19
                              : this.eT.h("scoreboard.yml").getConfigurationSection("default");
                        }

                        ConfigurationSection var13 = var10000;
                        if (!this.eT.x7q9("scoreboard", true) || var13 == null || !var13.getBoolean("enabled", true)) {
                           this.L(var2);
                           return;
                        }

                        UUID var20 = var2.getUniqueId();
                        String var5 = this.x7q9(var20);
                        List var6 = var13.getStringList("lines");

                        try {
                           u var23 = var12.eX;
                           String var10 = var13.getString("title", "&aFFA");
                           Component var14 = var23.m4z1(var2x, var10, true);
                           if (var12.eY.add(var20)) {
                              aa.ab2c(var2x, var5, var14);
                           } else {
                              aa.x7q9(var2x, var5, var14);
                           }

                           int var15 = var6.size();

                           for (int var7 = 0; var7 < var6.size(); var7++) {
                              String var10001 = "ffa_line_" + var7;
                              int var10003 = var15--;
                              u var10004 = var12.eX;
                              var10 = (String)var6.get(var7);
                              aa.ab2c(var2x, var10001, var5, var10003, var10004.m4z1(var2x, var10, true));
                           }

                           int var21 = var12.eZ.getOrDefault(var20, 0);

                           for (int var16 = var6.size(); var16 < var21; var16++) {
                              aa.x7q9(var2x, "ffa_line_" + var16, var5);
                           }

                           var12.eZ.put(var20, var6.size());
                           return;
                        } catch (Exception var11) {
                           var12.eY.remove(var20);
                           var12.eZ.remove(var20);
                           Bukkit.getLogger().log(Level.WARNING, "[FlixCore FFA] Failed to update packet scoreboard for " + var2x.getName(), (Throwable)var11);
                        }
                     }
                  }
               );
         }
      }
   }

   private String x7q9(UUID var1) {
      String var2;
      String var3 = (var2 = var1.toString().replace("-", "")).length() >= 8 ? var2.substring(0, 8) : var2;
      String var4;
      return (var4 = this.eS + "_" + var3).length() > 16 ? var4.substring(0, 16) : var4;
   }
}
