package club.aspvp.ffa.internal;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class g {
   private final p ff;
   public final r fg;
   public final u fh;
   public final Map<UUID, Integer> fi = new ConcurrentHashMap<>();
   private volatile g.q8v3 fj;

   public g(p var1, r var2, u var3) {
      this.ff = var1;
      this.fg = var2;
      this.fh = var3;
   }

   public final void k2w6(Player var1, int var2) {
      var2 = var2 == 2 ? 2 : 1;
      this.fi.put(var1.getUniqueId(), var2);
      this.fg.ab2c(var1, "spawnpvp-corner-select-started", Map.of("position", String.valueOf(var2)));
   }

   public final void P(Player var1) {
      Material var2;
      if ((var2 = b(this.J().getString("wand.material", "STONE_AXE"))) == null) {
         var2 = Material.STONE_AXE;
      }

      ItemMeta var3;
      ItemStack var4;
      if ((var3 = (var4 = new ItemStack(var2)).getItemMeta()) != null) {
         var3.setDisplayName(this.fh.u2q5(var1, this.J().getString("wand.display-name", "&#2EB6FF&lSpawnPvP Wand")));
         var3.setLore(this.J().getStringList("wand.lore").stream().map(var2x -> this.fh.u2q5(var1, var2x)).toList());
         var4.setItemMeta(var3);
      }

      var1.getInventory().addItem(new ItemStack[]{var4}).values().forEach(var1x -> var1.getWorld().dropItemNaturally(var1.getLocation(), var1x));
      this.fg.h1s7(var1, "spawnpvp-wand-given");
   }

   public final void Q(Player var1) {
      Material var2;
      if ((var2 = this.U(var1)) == null) {
         this.fg.h1s7(var1, "spawnpvp-hand-block-required");
      } else {
         this.J().set("zone.marker-material", var2.name());
         this.ff.i("spawnpvp.yml");
         this.fj = null;
         this.fg.ab2c(var1, "spawnpvp-material-selected", Map.of("material", var2.name()));
         this.T(var1);
      }
   }

   public void ab2c(Player var1, int var2, Block var3) {
      this.ab2c(var2, var3.getLocation());
      this.fg
         .ab2c(
            var1,
            "spawnpvp-corner-set",
            Map.of(
               "position",
               String.valueOf(var2),
               "world",
               var3.getWorld().getName(),
               "x",
               String.valueOf(var3.getX()),
               "y",
               String.valueOf(var3.getY()),
               "z",
               String.valueOf(var3.getZ())
            )
         );
      this.T(var1);
   }

   public final void R(Player var1) {
      FileConfiguration var2;
      (var2 = this.J()).set("zone.enabled", Boolean.FALSE);
      var2.set("zone.world", "");
      var2.set("zone.marker-material", "");
      var2.set("zone.pos1", null);
      var2.set("zone.pos2", null);
      this.ff.i("spawnpvp.yml");
      this.fj = null;
      this.fi.remove(var1.getUniqueId());
      this.fg.h1s7(var1, "spawnpvp-zone-cleared");
   }

   public final void S(Player var1) {
      g.q8v3 var2;
      if ((var2 = this.I()) != null && var2.ft) {
         this.fg
            .ab2c(
               var1,
               "spawnpvp-zone-info",
               Map.of(
                  "material",
                  var2.fv.name(),
                  "world",
                  var2.fu,
                  "x",
                  ab2c(var2.fw),
                  "y",
                  String.valueOf(var2.fx),
                  "z",
                  ab2c(var2.fy),
                  "radius",
                  ab2c(var2.fz),
                  "cells",
                  String.valueOf(var2.fG.size())
               )
            );
      } else {
         this.fg.h1s7(var1, "spawnpvp-zone-missing");
      }
   }

   public final boolean x7q9(Location var1) {
      g.q8v3 var2;
      if ((var2 = this.I()) != null && var2.ft && var1 != null && var1.getWorld() != null) {
         if (!var1.getWorld().getName().equals(var2.fu)) {
            return false;
         } else if (!ab2c(var1, var2)) {
            return false;
         } else {
            int var3 = Math.max(1, this.J().getInt("vertical-range", 8));
            return Math.abs(var1.getY() - (double)var2.fx) > (double)var3 ? false : var2.fG.contains(new g.x7q9(var1.getBlockX(), var1.getBlockZ()));
         }
      } else {
         return false;
      }
   }

   public final boolean G() {
      return this.J().getBoolean("enabled", true);
   }

   public ConfigurationSection u2q5(String var1) {
      FileConfiguration var2;
      ConfigurationSection var3;
      if ((var3 = (var2 = this.ff.p6d2("titles.yml", "titles")) == null ? null : var2.getConfigurationSection("titles." + var1)) != null) {
         return var3;
      } else {
         return (var2 = this.ff.h("titles.yml")) == null ? null : var2.getConfigurationSection("titles." + var1);
      }
   }

   public final void H() {
      this.ff.ag();
      this.fj = null;
   }

   private void T(Player var1) {
      Material var2 = b(this.J().getString("zone.marker-material", ""));
      g.m4z1 var3 = this.a("zone.pos1");
      g.m4z1 var4 = this.a("zone.pos2");
      if (var2 == null || var3 == null || var4 == null) {
         this.fg.h1s7(var1, "spawnpvp-setup-incomplete");
      } else if (!var3.fp.equals(var4.fp)) {
         this.fg.h1s7(var1, "spawnpvp-world-mismatch");
      } else {
         World var5;
         if ((var5 = var1.getServer().getWorld(var3.fp)) == null) {
            this.fg.ab2c(var1, "spawnpvp-world-missing", Map.of("world", var3.fp));
         } else {
            g.q8v3 var6;
            if ((var6 = this.ab2c(var5, var3, var4, var2)) == null) {
               this.fg.ab2c(var1, "spawnpvp-detect-failed", Map.of("material", var2.name()));
            } else {
               this.ab2c(var6);
               this.fg
                  .ab2c(
                     var1,
                     "spawnpvp-zone-saved",
                     Map.of(
                        "material",
                        var2.name(),
                        "world",
                        var6.fu,
                        "x",
                        ab2c(var6.fw),
                        "y",
                        String.valueOf(var6.fx),
                        "z",
                        ab2c(var6.fy),
                        "radius",
                        ab2c(var6.fz),
                        "cells",
                        String.valueOf(var6.fG.size())
                     )
                  );
            }
         }
      }
   }

   private g.q8v3 ab2c(World var1, g.m4z1 var2, g.m4z1 var3, Material var4) {
      int var5 = Math.min(var2.fq, var3.fq);
      int var6 = Math.max(var2.fq, var3.fq);
      int var7 = Math.min(var2.fr, var3.fr);
      int var8 = Math.max(var2.fr, var3.fr);
      int var9 = Math.min(var2.fs, var3.fs);
      int var30 = Math.max(var2.fs, var3.fs);
      int var31 = Math.max(16, this.J().getInt("max-marker-blocks", 512));
      int var10 = Math.max(64, this.J().getInt("max-shape-cells", 20000));
      HashSet var11 = new HashSet();
      HashSet var12 = new HashSet();

      for (int var13 = var7; var13 <= var8 && var11.size() < var31; var13++) {
         for (int var16 = var5; var16 <= var6 && var11.size() < var31; var16++) {
            for (int var17 = var9; var17 <= var30 && var11.size() < var31; var17++) {
               if (var1.getBlockAt(var16, var13, var17).getType() == var4) {
                  var11.add(new g.ab2c(var16, var13, var17));
                  var12.add(new g.x7q9(var16, var17));
               }
            }
         }
      }

      if (var11.size() < Math.max(3, this.J().getInt("minimum-marker-blocks", 6))) {
         return null;
      } else {
         Set var34;
         if ((var34 = ab2c(var12, var5, var6, var9, var30, var10)).size() < Math.max(var12.size() + 1, this.J().getInt("minimum-filled-cells", 8))) {
            return null;
         } else {
            double var35 = var34.stream().mapToDouble(g.x7q9::L).average().orElse((double)var5) + 0.5;
            double var18 = var11.stream().mapToDouble(g.ab2c::K).average().orElse((double)var7);
            double var20 = var34.stream().mapToDouble(g.x7q9::M).average().orElse((double)var9) + 0.5;
            double var22 = 0.0;
            Iterator var32 = var34.iterator();

            while (var32.hasNext()) {
               g.x7q9 var33;
               double var26 = (double)(var33 = (g.x7q9)var32.next()).L() + 0.5 - var35;
               double var28 = (double)var33.M() + 0.5 - var20;
               var22 = Math.max(var22, Math.sqrt(var26 * var26 + var28 * var28));
            }

            var22 += Math.max(0.0, this.J().getDouble("radius-buffer", 1.5));
            return new g.q8v3(true, var1.getName(), var4, var35, (int)Math.round(var18), var20, var22, var5, var6, var7, var8, var9, var30, Set.copyOf(var34));
         }
      }
   }

   private static Set<g.x7q9> ab2c(Set<g.x7q9> var0, int var1, int var2, int var3, int var4, int var5) {
      int var6 = var1 - 1;
      int var7 = var2 + 1;
      int var8 = var3 - 1;
      int var9 = var4 + 1;
      HashSet var10 = new HashSet();
      ArrayDeque var11;
      (var11 = new ArrayDeque()).add(new g.x7q9(var6, var8));

      while (!var11.isEmpty() && var10.size() <= var5) {
         g.x7q9 var12;
         if ((var12 = (g.x7q9)var11.removeFirst()).L() >= var6
            && var12.L() <= var7
            && var12.M() >= var8
            && var12.M() <= var9
            && !var0.contains(var12)
            && var10.add(var12)) {
            var11.add(new g.x7q9(var12.L() + 1, var12.M()));
            var11.add(new g.x7q9(var12.L() - 1, var12.M()));
            var11.add(new g.x7q9(var12.L(), var12.M() + 1));
            var11.add(new g.x7q9(var12.L(), var12.M() - 1));
         }
      }

      HashSet var16 = new HashSet();

      for (int var13 = var1; var13 <= var2 && var16.size() <= var5; var13++) {
         for (int var14 = var3; var14 <= var4 && var16.size() <= var5; var14++) {
            g.x7q9 var15 = new g.x7q9(var13, var14);
            if (!var10.contains(var15)) {
               var16.add(var15);
            }
         }
      }

      return (Set<g.x7q9>)(var16.size() > var5 ? Set.of() : var16);
   }

   private void ab2c(g.q8v3 var1) {
      FileConfiguration var2;
      (var2 = this.J()).set("zone.enabled", Boolean.TRUE);
      var2.set("zone.world", var1.fu);
      var2.set("zone.marker-material", var1.fv.name());
      var2.set("zone.center-x", var1.fw);
      var2.set("zone.center-y", var1.fx);
      var2.set("zone.center-z", var1.fy);
      var2.set("zone.radius", var1.fz);
      var2.set("zone.bounds.min-x", var1.fA);
      var2.set("zone.bounds.max-x", var1.fB);
      var2.set("zone.bounds.min-y", var1.fC);
      var2.set("zone.bounds.max-y", var1.fD);
      var2.set("zone.bounds.min-z", var1.fE);
      var2.set("zone.bounds.max-z", var1.fF);
      this.ff.i("spawnpvp.yml");
      this.fj = var1;
   }

   private g.q8v3 I() {
      g.q8v3 var1 = this.fj;
      if (this.fj != null) {
         return var1;
      } else {
         FileConfiguration var5 = this.J();
         if (this.G() && var5.getBoolean("zone.enabled", false)) {
            String var2 = var5.getString("zone.world", "");
            Material var6 = b(var5.getString("zone.marker-material", ""));
            if (!var2.isBlank() && var6 != null) {
               g.m4z1 var3 = this.a("zone.pos1");
               g.m4z1 var4 = this.a("zone.pos2");
               World var8 = Bukkit.getWorld(var2);
               if (var3 != null && var4 != null && var8 != null && (var1 = this.ab2c(var8, var3, var4, var6)) != null) {
                  this.fj = var1;
                  return var1;
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   private static boolean ab2c(Location var0, g.q8v3 var1) {
      return var0.getBlockX() >= var1.fA && var0.getBlockX() <= var1.fB && var0.getBlockZ() >= var1.fE && var0.getBlockZ() <= var1.fF;
   }

   private void ab2c(int var1, Location var2) {
      FileConfiguration var3 = this.J();
      String var4 = "zone.pos" + var1;
      var3.set(var4 + ".world", var2.getWorld().getName());
      var3.set(var4 + ".x", var2.getBlockX());
      var3.set(var4 + ".y", var2.getBlockY());
      var3.set(var4 + ".z", var2.getBlockZ());
      this.ff.i("spawnpvp.yml");
      this.fj = null;
   }

   private g.m4z1 a(String var1) {
      FileConfiguration var2;
      String var3;
      return !(var3 = (var2 = this.J()).getString(var1 + ".world", "")).isBlank()
            && var2.contains(var1 + ".x")
            && var2.contains(var1 + ".y")
            && var2.contains(var1 + ".z")
         ? new g.m4z1(var3, var2.getInt(var1 + ".x"), var2.getInt(var1 + ".y"), var2.getInt(var1 + ".z"))
         : null;
   }

   private Material U(Player var1) {
      ItemStack var2 = var1.getInventory().getItemInMainHand();
      if (this.k2w6(var1, var2)) {
         return null;
      } else {
         return var2 != null && !var2.getType().isAir() && var2.getType().isBlock() ? var2.getType() : null;
      }
   }

   public boolean k2w6(Player var1, ItemStack var2) {
      if (var2 != null && !var2.getType().isAir()) {
         Material var3 = b(this.J().getString("wand.material", "STONE_AXE"));
         if (var2.getType() != (var3 == null ? Material.STONE_AXE : var3)) {
            return false;
         } else {
            ItemMeta var4;
            return (var4 = var2.getItemMeta()) != null && var4.hasDisplayName()
               ? var4.getDisplayName().equals(this.fh.u2q5(var1, this.J().getString("wand.display-name", "&#2EB6FF&lSpawnPvP Wand")))
               : false;
         }
      } else {
         return false;
      }
   }

   private static Material b(String var0) {
      if (var0 != null && !var0.isBlank()) {
         try {
            return Material.valueOf(var0.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var1) {
            return null;
         }
      } else {
         return null;
      }
   }

   public FileConfiguration J() {
      return this.ff.h("spawnpvp.yml");
   }

   private static String ab2c(double var0) {
      return String.format(Locale.US, "%.2f", var0);
   }

   static record ab2c(int fk, int fl, int fm) {
      public final int K() {
         return this.fl;
      }
   }

   static record m4z1(String fp, int fq, int fr, int fs) {
   }

   static record q8v3(
      boolean ft, String fu, Material fv, double fw, int fx, double fy, double fz, int fA, int fB, int fC, int fD, int fE, int fF, Set<g.x7q9> fG
   ) {

      q8v3(boolean ft, String fu, Material fv, double fw, int fx, double fy, double fz, int fA, int fB, int fC, int fD, int fE, int fF, Set<g.x7q9> fG) {
         this.fu = fu;
         this.fv = fv;
         this.fw = fw;
         this.fx = fx;
         this.fy = fy;
         this.fz = fz;
         this.fA = fA;
         this.fB = fB;
         this.fC = fC;
         this.fD = fD;
         this.fE = fE;
         this.fF = fF;
         this.fG = fG;
      }
   }

   static record x7q9(int fn, int fo) {
      public final int L() {
         return this.fn;
      }

      public final int M() {
         return this.fo;
      }
   }
}
