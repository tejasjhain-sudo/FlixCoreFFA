package club.aspvp.ffa.internal;

import club.aspvp.ffa.TwoCubeFFA;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class g1v7 {
   private final JavaPlugin cb;
   private final t cc;
   private final u cd;
   private final File ce;
   public final Map<String, g1v7.ab2c> cf = new ConcurrentHashMap<>();
   private final Map<UUID, g1v7.x7q9> cg = new ConcurrentHashMap<>();
   private final Map<UUID, g1v7.m4z1> ch = new ConcurrentHashMap<>();
   private t.ab2c ci;

   public g1v7(TwoCubeFFA var1, t var2, u var3) {
      this.cb = var1;
      this.cc = var2;
      this.cd = var3;
      this.ce = new File(var1.getDataFolder(), "afk");
   }

   public final void t9b4() {
      this.ce.mkdirs();
      this.cf.clear();
      File[] var1;
      if ((var1 = this.ce.listFiles((var0, var1x) -> var1x.toLowerCase(Locale.ROOT).endsWith("-afk.yml"))) != null) {
         File[] var7;
         int var2 = (var7 = var1).length;

         for (int var3 = 0; var3 < var2; var3++) {
            File var4;
            YamlConfiguration var5 = YamlConfiguration.loadConfiguration(var4 = var7[var3]);
            String var6 = var4.getName().substring(0, var4.getName().length() - 8).toLowerCase(Locale.ROOT);
            this.cf.put(var6, new g1v7.ab2c(var6, var4, var5));
         }
      }
   }

   public final void f3y8() {
      this.p6d2();
      this.ci = this.cc.ab2c(this::h1s7, 20L, 20L);
   }

   public final void p6d2() {
      if (this.ci != null) {
         this.ci.cancel();
         this.ci = null;
      }

      this.ch.values().forEach(g1v7.m4z1::z4c9);
      this.ch.clear();
   }

   public final void m4z1(Player var1, String var2) {
      String var3;
      var2 = (var3 = var2 == null ? "default" : var2.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]", "")).isBlank() ? "default" : var3;
      g1v7.ab2c var10 = this.cf.computeIfAbsent(var2, this::k2w6);
      this.cg.put(var1.getUniqueId(), new g1v7.x7q9(var2));
      var1 = var1;
      ItemStack var4;
      ItemMeta var5;
      if ((var5 = (var4 = new ItemStack(Material.GOLDEN_AXE)).getItemMeta()) != null) {
         var5.setDisplayName(this.cd.u2q5(var1, "&#4287f5&lAFK Zone Wand &8| &f" + var10.cj));
         var5.setLore(
            List.of(
               this.cd.u2q5(var1, "&7Left-click: &fSet AFK pos1"),
               this.cd.u2q5(var1, "&7Right-click: &fSet AFK pos2"),
               this.cd.u2q5(var1, "&7File: &fplugins/FlixCoreFFA/afk/" + var10.cj + "-afk.yml")
            )
         );
         var4.setItemMeta(var5);
      }

      var1.getInventory().addItem(new ItemStack[]{var4}).values().forEach(var1x -> var1.getWorld().dropItemNaturally(var1.getLocation(), var1x));
      var2 = "&#2EB6FF&lFlixCore FFA &8» " + "&aAFK setup started for &f" + var10.cj + "&a. Left-click pos1, right-click pos2.";
      var1.sendMessage(this.cd.m4z1(var1, var2, true));
   }

   public final boolean ab2c(Player var1, boolean var2, Location var3) {
      g1v7.x7q9 var4;
      if ((var4 = this.cg.get(var1.getUniqueId())) != null) {
         ItemStack var10000 = var1.getInventory().getItemInMainHand();
         String var6 = var4.cm;
         ItemStack var5 = var10000;
         if (var10000 != null && var5.getType() == Material.GOLDEN_AXE && var5.hasItemMeta() && var5.getItemMeta().getDisplayName() != null
            ? var5.getItemMeta().getDisplayName().contains(var6)
            : false) {
            g1v7.ab2c var8;
            if ((var8 = this.cf.get(var4.cm)) != null && var3 != null && var3.getWorld() != null) {
               String var9 = var2 ? "region.pos1" : "region.pos2";
               FileConfiguration var10 = var8.cl;
               var8.cl.set(var9 + ".world", var3.getWorld().getName());
               var10.set(var9 + ".x", var3.getBlockX());
               var10.set(var9 + ".y", var3.getBlockY());
               var10.set(var9 + ".z", var3.getBlockZ());
               this.ab2c(var8);
               if (var8.cl.isConfigurationSection("region.pos1") && var8.cl.isConfigurationSection("region.pos2")) {
                  var8.cl.set("enabled", Boolean.TRUE);
                  this.ab2c(var8);
               }

               u var10001 = this.cd;
               String var7 = "&#2EB6FF&lFlixCore FFA &8» "
                  + "&aAFK "
                  + (var2 ? "pos1" : "pos2")
                  + " saved for &f"
                  + var8.cj
                  + "&a. "
                  + (
                     var8.cl.isConfigurationSection("region.pos1") && var8.cl.isConfigurationSection("region.pos2")
                        ? "&7Zone is ready and enabled."
                        : "&7Now set the other position."
                  );
               var1.sendMessage(var10001.m4z1(var1, var7, true));
               return true;
            }

            return true;
         }
      }

      return false;
   }

   private void h1s7() {
      for (Player var2 : Bukkit.getOnlinePlayers()) {
         this.cc
            .ab2c(
               var2,
               () -> {
                  Location var9 = var2.getLocation();
                  Optional var3 = var9 != null && var9.getWorld() != null
                     ? this.cf
                        .values()
                        .stream()
                        .filter(var0 -> var0.cl.getBoolean("enabled", false))
                        .filter(
                           var2x -> {
                              FileConfiguration var21x = var2x.cl;
                              var9 = ab2c(var21x, "region.pos1");
                              Location var22x = ab2c(var21x, "region.pos2");
                              if (var9 != null
                                 && var22x != null
                                 && var9.getWorld() != null
                                 && var22x.getWorld() != null
                                 && var9.getWorld().getName().equals(var9.getWorld().getName())
                                 && var22x.getWorld().getName().equals(var9.getWorld().getName())) {
                                 double var8 = Math.min(var9.getX(), var22x.getX());
                                 double var10x = Math.max(var9.getX(), var22x.getX()) + 1.0;
                                 double var12x = Math.min(var9.getY(), var22x.getY());
                                 double var14 = Math.max(var9.getY(), var22x.getY()) + 1.0;
                                 double var16x = Math.min(var9.getZ(), var22x.getZ());
                                 double var18x = Math.max(var9.getZ(), var22x.getZ()) + 1.0;
                                 return var9.getX() >= var8
                                    && var9.getX() <= var10x
                                    && var9.getY() >= var12x
                                    && var9.getY() <= var14
                                    && var9.getZ() >= var16x
                                    && var9.getZ() <= var18x;
                              } else {
                                 return false;
                              }
                           }
                        )
                        .findFirst()
                     : Optional.empty();
                  g1v7.m4z1 var4 = this.ch.get(var2.getUniqueId());
                  if (var3.isEmpty()) {
                     if (var4 != null) {
                        this.ch.remove(var2.getUniqueId());
                        var4.z4c9();
                        g1v7.ab2c var36;
                        if ((var36 = this.cf.get(var4.cn)) != null) {
                           long var40 = Math.max(0L, (System.currentTimeMillis() - var4.co) / 1000L);
                           this.ab2c(var2, var36, "messages.left", Map.of("time", ab2c(var40)));
                        }
                     }
                  } else {
                     g1v7.ab2c var32 = (g1v7.ab2c)var3.get();
                     if (var4 == null || !var4.cn.equals(var32.cj)) {
                        g1v7.m4z1 var11 = new g1v7.m4z1(var32.cj);
                        this.ch.put(var2.getUniqueId(), var11);
                        this.ab2c(var2, var32, "messages.entered", Map.of("time", ab2c((long)var32.cl.getInt("reward-time-seconds", 60))));
                        String var10003 = var32.cl.getString("in-zone.title", "");
                        String var10004 = var32.cl.getString("in-zone.subtitle", "");
                        long var10005 = var32.cl.getLong("in-zone.fade-in", 10L);
                        long var10006 = var32.cl.getLong("in-zone.stay", 40L);
                        long var10007 = var32.cl.getLong("in-zone.fade-out", 10L);
                        long var27 = (long)var32.cl.getInt("reward-time-seconds", 60);
                        long var25 = var10007;
                        long var23 = var10006;
                        long var21 = var10005;
                        String var20 = var10004;
                        String var13 = var10003;
                        if (var13 != null && !var13.isBlank() || var20 != null && !var20.isBlank()) {
                           Map var33 = Map.of("time", ab2c(var27), "zone", var32.cj);
                           u var10001 = this.cd;
                           String var18 = this.ab2c(var13 == null ? "" : var13, var33);
                           Component var56 = var10001.m4z1(var2, var18, true);
                           u var10002 = this.cd;
                           var18 = this.ab2c(var20 == null ? "" : var20, var33);
                           var2.showTitle(
                              Title.title(
                                 var56,
                                 var10002.m4z1(var2, var18, true),
                                 Times.times(Duration.ofMillis(var21 * 50L), Duration.ofMillis(var23 * 50L), Duration.ofMillis(var25 * 50L))
                              )
                           );
                        }

                        long var45 = (long)var32.cl.getInt("reward-time-seconds", 60);
                        String var22;
                        if ((var22 = var32.cl.getString("in-zone.bossbar.name", "")) != null && !var22.isBlank()) {
                           BarColor var51 = r5n0(var32.cl.getString("in-zone.bossbar.color", "BLUE"));
                           BossBar var24;
                           (var24 = Bukkit.createBossBar(
                                 this.cd.u2q5(var2, this.ab2c(var22, Map.of("time", ab2c(var45), "zone", var32.cj))), var51, BarStyle.SOLID, new BarFlag[0]
                              ))
                              .addPlayer(var2);
                           var11.cq = var24;
                        }

                        var4 = this.ch.get(var2.getUniqueId());
                     }

                     if (var4 != null) {
                        long var7 = Math.max(0L, (System.currentTimeMillis() - var4.co) / 1000L);
                        int var5 = Math.max(1, var32.cl.getInt("reward-time-seconds", 60));
                        long var10 = Math.max(0L, (long)var5 - (System.currentTimeMillis() - var4.cp) / 1000L);
                        String var12 = ab2c(var10);
                        String var37;
                        if ((var37 = var32.cl.getString("in-zone.actionbar", "")) != null && !var37.isBlank()) {
                           u var57 = this.cd;
                           String var42 = this.ab2c(var37, Map.of("time", var12, "zone", var32.cj));
                           var2.sendActionBar(var57.m4z1(var2, var42, true));
                        }

                        BossBar var46 = var4.cq;
                        if (var4.cq != null) {
                           var46.setTitle(
                              this.cd.u2q5(var2, this.ab2c(var32.cl.getString("in-zone.bossbar.name", ""), Map.of("time", var12, "zone", var32.cj)))
                           );
                           int var49 = Math.max(1, var32.cl.getInt("reward-time-seconds", 60));
                           var46.setProgress(Math.max(0.0, Math.min(1.0, (double)var10 / (double)var49)));
                        }

                        if (System.currentTimeMillis() - var4.cp >= (long)var5 * 1000L) {
                           long var16 = var7;
                           g1v7.ab2c var35 = var32;
                           Player var34 = var2;
                           g1v7 var6 = this;
                           long var30 = System.currentTimeMillis();
                           var4.cp = var30;
                           Iterator var38 = var32.cl.getMapList("rewards").iterator();

                           while (var38.hasNext()) {
                              Map var47;
                              double var50 = ab2c((var47 = (Map)var38.next()).get("chance"), 100.0);
                              if (!(ThreadLocalRandom.current().nextDouble(100.0) > var50)) {
                                 Object var52;
                                 if ((var52 = var47.get("display")) != null && !String.valueOf(var52).isBlank()) {
                                    u var58 = var6.cd;
                                    String var43 = var6.ab2c(String.valueOf(var52), Map.of("time", ab2c(var16), "zone", var35.cj));
                                    var34.sendMessage(var58.m4z1(var34, var43, true));
                                 }

                                 Object var53;
                                 if ((var53 = var47.get("commands")) instanceof List) {
                                    List var54;
                                    for (Object var55 : var54 = (List)var53) {
                                       String var28 = var6.ab2c(String.valueOf(var55), Map.of("time", ab2c(var16), "zone", var35.cj))
                                          .replace("%player_name%", var34.getName())
                                          .replace("%player%", var34.getName());
                                       var6.cc.x7q9(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var28));
                                    }
                                 }
                              }
                           }

                           for (String var48 : var35.cl.getStringList("messages.reward")) {
                              u var59 = var6.cd;
                              String var44 = var6.ab2c(var48, Map.of("time", ab2c(var16), "zone", var35.cj));
                              var34.sendMessage(var59.m4z1(var34, var44, true));
                           }
                        }
                     }
                  }
               }
            );
      }
   }

   private void ab2c(Player var1, g1v7.ab2c var2, String var3, Map<String, String> var4) {
      if ((var3 = var2.cl.getString(var3, "")) != null && !var3.isBlank()) {
         HashMap var8;
         (var8 = new HashMap(var4)).put("zone", var2.cj);
         u var10001 = this.cd;
         String var6 = this.ab2c(var3, var8);
         var1.sendMessage(var10001.m4z1(var1, var6, true));
      }
   }

   private g1v7.ab2c k2w6(String var1) {
      File var2;
      YamlConfiguration var3;
      YamlConfiguration var4;
      (var4 = var3 = YamlConfiguration.loadConfiguration(var2 = new File(this.ce, var1 + "-afk.yml"))).addDefault("enabled", Boolean.FALSE);
      var4.addDefault("reward-time-seconds", 60);
      var4.addDefault("messages.entered", "&#4287f5You entered the AFK Zone! &7Reward in &#4287f5%flixcore_time%");
      var4.addDefault("messages.left", "&#4287f5You left the AFK Zone! &7Time stayed: &#4287f5%flixcore_time%");
      var4.addDefault("messages.reward", List.of("&#4287f5AFK Reward Claimed!", "&7You stayed AFK for &#4287f5%flixcore_time% &7and received your rewards!"));
      var4.addDefault("in-zone.title", "&#4287f5&lAFK MODE");
      var4.addDefault("in-zone.subtitle", "&7Next reward in: &#4287f5%flixcore_time%");
      var4.addDefault("in-zone.actionbar", "&#4287f5AFK Zone &7• Next reward in: &#4287f5%flixcore_time%");
      var4.addDefault("in-zone.bossbar.name", "&#4287f5&lAFK MODE &7| Next reward in: &#4287f5%flixcore_time%");
      var4.addDefault("in-zone.bossbar.color", "BLUE");
      ArrayList var5 = new ArrayList();
      LinkedHashMap var6;
      (var6 = new LinkedHashMap()).put("chance", 100);
      var6.put("display", "&aYou received your AFK reward.");
      var6.put("commands", List.of("token give %player_name% 4"));
      var5.add(var6);
      var4.addDefault("rewards", var5);
      var4.options().copyDefaults(true);

      try {
         var3.save(var2);
      } catch (IOException var7) {
         this.cb.getLogger().severe("Could not create AFK zone " + var1 + ": " + var7.getMessage());
      }

      return new g1v7.ab2c(var1, var2, var3);
   }

   private void ab2c(g1v7.ab2c var1) {
      try {
         var1.cl.save(var1.ck);
      } catch (IOException var4) {
         this.cb.getLogger().severe("Could not save AFK zone " + var1.cj + ": " + var4.getMessage());
      }
   }

   private static Location ab2c(FileConfiguration var0, String var1) {
      ConfigurationSection var2;
      if ((var2 = var0.getConfigurationSection(var1)) == null) {
         return null;
      } else {
         World var3;
         return (var3 = Bukkit.getWorld(var2.getString("world", ""))) == null
            ? null
            : new Location(var3, (double)var2.getInt("x"), (double)var2.getInt("y"), (double)var2.getInt("z"));
      }
   }

   private String ab2c(String var1, Map<String, String> var2) {
      var1 = var1 == null ? "" : var1;
      String var3 = var2.getOrDefault("time", "");
      String var5 = var2.getOrDefault("zone", "");
      return var1.replace("%flixcore_time%", var3)
         .replace("%flixcoreffa_time%", var3)
         .replace("%twocubecore_time%", var3)
         .replace("%twocubeffa_time%", var3)
         .replace("%afk_time%", var3)
         .replace("%afk_zone%", var5)
         .replace("%zone%", var5)
         .replace("%prefix%", "&#2EB6FF&lFlixCore FFA &8» ");
   }

   private static String ab2c(long var0) {
      long var2;
      long var4 = (var2 = Math.max(0L, var0)) / 60L;
      long var6 = var2 % 60L;
      return var4 > 0L ? var4 + "m " + var6 + "s" : var6 + "s";
   }

   private static double ab2c(Object var0, double var1) {
      if (var0 instanceof Number) {
         Number var4;
         return (var4 = (Number)var0).doubleValue();
      } else {
         try {
            return var0 == null ? 100.0 : Double.parseDouble(String.valueOf(var0));
         } catch (NumberFormatException var3) {
            return 100.0;
         }
      }
   }

   private static BarColor r5n0(String var0) {
      try {
         return BarColor.valueOf((var0 == null ? "BLUE" : var0).toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException var1) {
         return BarColor.BLUE;
      }
   }

   static record ab2c(String cj, File ck, FileConfiguration cl) {
   }

   static final class m4z1 {
      final String cn;
      final long co = System.currentTimeMillis();
      long cp = System.currentTimeMillis();
      BossBar cq;

      m4z1(String var1) {
         this.cn = var1;
      }

      void z4c9() {
         if (this.cq != null) {
            this.cq.removeAll();
            this.cq = null;
         }
      }
   }

   static record x7q9(String cm) {
   }
}
