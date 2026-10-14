package club.aspvp.ffa.internal;

import club.aspvp.ffa.TwoCubeFFA;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;

public final class e7z1 {
   private final JavaPlugin cM;
   public final p cN;
   private final t cO;
   public final File cP;
   private final Set<String> cQ = ConcurrentHashMap.newKeySet();
   private final Set<String> cR = ConcurrentHashMap.newKeySet();
   private final Map<String, Long> cS = new ConcurrentHashMap<>();
   private t.ab2c cT;

   public e7z1(TwoCubeFFA var1, p var2, t var3) {
      this.cM = var1;
      this.cN = var2;
      this.cO = var3;
      this.cP = new File(var1.getDataFolder(), "arena-snapshots");
      if (!this.cP.exists() && !this.cP.mkdirs()) {
         var1.getLogger().warning("Could not create arena snapshot folder: " + this.cP.getAbsolutePath());
      }
   }

   public final void r5n0(j var1) {
      this.ab2c(var1, null);
   }

   private void ab2c(j var1, Runnable var2) {
      if (this.enabled() && var1 != null && var1.S() != null) {
         String var3 = var1.O().toLowerCase();
         Long var4;
         if ((var4 = this.cS.get(var3)) != null && var4 > System.currentTimeMillis()) {
            y.w7k3("ARENA", "Snapshot skipped for arena '" + var1.O() + "': in failure cooldown.");
            ab2c(var2);
         } else if (!this.cQ.add(var3)) {
            y.w7k3("ARENA", "Snapshot skipped for arena '" + var1.O() + "': already saving.");
            ab2c(var2);
         } else {
            World var5;
            k var14;
            if ((var5 = Bukkit.getWorld((var14 = var1.S()).fW)) == null) {
               this.cQ.remove(var3);
               this.cM.getLogger().warning("Could not snapshot arena '" + var1.O() + "': world is not loaded.");
               ab2c(var2);
            } else {
               e7z1.ab2c var15;
               int var6 = (var15 = e7z1.ab2c.ab2c(var14, var5)).o3w8();
               p var10 = this.cN;
               long var9;
               long var11 = (var9 = this.cN.gt.getConfig().getLong("arena-regen.max-arena-size", 5000000L)) < 0L ? 50000000L : Math.min(var9, 50000000L);
               if ((long)var6 > var11) {
                  this.cQ.remove(var3);
                  this.cM
                     .getLogger()
                     .warning("Arena '" + var1.O() + "' snapshot skipped: " + var6 + " blocks exceeds arena-regen.max-arena-size (" + var11 + ").");
                  ab2c(var2);
               } else {
                  File var7 = this.z4c9(var1.O());
                  File var8 = this.v8m5(var1.O());

                  try {
                     var17 = new e7z1.k2w6(var7, var8, var1.O(), var5.getName(), var15, var6);
                  } catch (IOException var13) {
                     this.cQ.remove(var3);
                     this.n2x6(var3);
                     this.cM.getLogger().warning("Could not open snapshot for arena '" + var1.O() + "': " + ab2c(var13, this.cP));
                     ab2c(var2);
                     return;
                  }

                  var10 = this.cN;
                  int var19 = Math.max(1, this.cN.gt.getConfig().getInt("arena-regen.snapshot-blocks-per-tick", 10000));
                  y.w7k3(
                     "ARENA",
                     "Snapshot started for arena '"
                        + var1.O()
                        + "': blocks="
                        + var6
                        + ", batch="
                        + var19
                        + ", temp="
                        + var8.getAbsolutePath()
                        + ", target="
                        + var7.getAbsolutePath()
                        + "."
                  );
                  e7z1.x7q9 var16 = new e7z1.x7q9(var15);
                  this.ab2c(var1, var3, var5, var15, var16, var17, var7, var6, var19, var2);
               }
            }
         }
      } else {
         y.w7k3("ARENA", "Snapshot skipped: regen disabled, arena missing, or region missing.");
         ab2c(var2);
      }
   }

   private void ab2c(j var1, String var2, World var3, e7z1.ab2c var4, e7z1.x7q9 var5, e7z1.k2w6 var6, File var7, int var8, int var9, Runnable var10) {
      if (!var5.hasNext()) {
         try {
            var6.x7q9(var7);
            this.cQ.remove(var2);
            this.cS.remove(var2);
            y.w7k3(
               "ARENA",
               "Snapshot complete for arena '" + var1.O() + "': blocks=" + var8 + ", size=" + var7.length() + " bytes, file=" + var7.getAbsolutePath() + "."
            );
            ab2c(var10);
         } catch (RuntimeException | IOException var13) {
            ab2c(var6);
            this.cQ.remove(var2);
            this.n2x6(var2);
            this.cM.getLogger().warning("Could not save regen snapshot for arena '" + var1.O() + "': " + ab2c(var13, var7));
            ab2c(var10);
         }
      } else {
         int var11 = var5.dh >> 4;
         int var12 = var5.dj >> 4;
         this.cO.ab2c(var3, var11, var12, () -> {
            int var13x = 0;

            try {
               for (; var13x < var9 && var5.hasNext() && ab2c(var5.dh, var5.dj, var11, var12); var13x++) {
                  int var14 = var5.dh;
                  int var15 = var5.di;
                  int var16 = var5.dj;
                  int var10001 = var14 - var4.cU;
                  int var10002 = var15 - var4.cV;
                  int var10003 = var16 - var4.cW;
                  String var18 = var3.getBlockAt(var14, var15, var16).getBlockData().getAsString();
                  int var17 = var10003;
                  var16 = var10002;
                  var15 = var10001;
                  var6.dx.writeInt(var15);
                  var6.dx.writeInt(var16);
                  var6.dx.writeInt(var17);
                  var6.dx.writeUTF(var18);
                  var5.di++;
                  if (var5.di > var5.da.cY) {
                     var5.di = var5.da.cV;
                     var5.dj++;
                  }

                  if (var5.dj > Math.min(var5.da.cZ, (var5.dg << 4) + 15)) {
                     var5.dj = var5.a7r4();
                     var5.dh++;
                  }

                  if (var5.dh > Math.min(var5.da.cX, (var5.df << 4) + 15)) {
                     var5.dg++;
                     if (var5.dg > var5.de) {
                        var5.dg = var5.dd;
                        var5.dh++;
                     }

                     if (var5.dg == var5.dd) {
                        var5.df++;
                     }

                     if (var5.df <= var5.dc) {
                        var5.dh = var5.i5k2();
                        var5.di = var5.da.cV;
                        var5.dj = var5.a7r4();
                     }
                  }
               }

               y.w7k3("ARENA", "Snapshot batch for arena '" + var1.O() + "': chunk=" + var11 + "," + var12 + ", blocks=" + var13x + ".");
               this.cO.ab2c(() -> this.ab2c(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10), 1L);
            } catch (RuntimeException | IOException var19) {
               ab2c(var6);
               this.cQ.remove(var2);
               this.n2x6(var2);
               this.cM.getLogger().warning("Could not save regen snapshot for arena '" + var1.O() + "': " + ab2c(var19, var7));
               ab2c(var10);
            }
         }, 1L);
      }
   }

   public final void ab2c(Collection<j> var1) {
      p var2 = this.cN;
      if (this.cN.gt.getConfig().getBoolean("arena-regen.auto-create-snapshots-on-startup", true) && var1 != null && !var1.isEmpty()) {
         y.w7k3("ARENA", "Startup snapshot check queued for " + var1.size() + " arenas.");
         this.ab2c(new ArrayList<>(var1), 0);
      } else {
         y.w7k3("ARENA", "Startup snapshot check skipped: disabled or no arenas.");
      }
   }

   private void ab2c(List<j> var1, int var2) {
      while (var2 < var1.size()) {
         j var3;
         if ((var3 = (j)var1.get(var2)) != null && !this.t9b4(var3)) {
            y.w7k3("ARENA", "Startup snapshot missing for arena '" + var3.O() + "'. Creating now (" + (var2 + 1) + "/" + var1.size() + ").");
            this.ab2c(var3, () -> this.ab2c(var1, var2 + 1));
            return;
         }

         if (var3 != null) {
            y.w7k3("ARENA", "Startup snapshot exists for arena '" + var3.O() + "': " + this.h1s7(var3) + ".");
         }

         var2++;
         var1 = var1;
         this = this;
      }
   }

   public final void x7q9(Collection<j> var1) {
      y.w7k3("ARENA", "Manual snapshot-all queued for " + (var1 == null ? 0 : var1.size()) + " arenas.");
      if (var1 != null && !var1.isEmpty()) {
         this.x7q9(new ArrayList<>(var1), 0);
      }
   }

   private void x7q9(List<j> var1, int var2) {
      if (var2 >= var1.size()) {
         y.w7k3("ARENA", "Manual snapshot-all queue complete.");
      } else {
         j var3 = (j)var1.get(var2);
         y.w7k3("ARENA", "Manual snapshot-all processing arena '" + (var3 == null ? "null" : var3.O()) + "' (" + (var2 + 1) + "/" + var1.size() + ").");
         this.ab2c(var3, () -> this.x7q9(var1, var2 + 1));
      }
   }

   public final boolean t9b4(j var1) {
      return var1 != null && ab2c(this.z4c9(var1.O()));
   }

   public final boolean f3y8(j var1) {
      return var1 != null && this.cQ.contains(var1.O().toLowerCase());
   }

   public final boolean p6d2(j var1) {
      return var1 != null && this.cR.contains(var1.O().toLowerCase());
   }

   public final String h1s7(j var1) {
      return var1 == null ? this.cP.getAbsolutePath() : this.z4c9(var1.O()).getAbsolutePath();
   }

   public final void ab2c(Supplier<Collection<j>> var1, Predicate<j> var2) {
      this.l9m6();
      if (this.enabled()) {
         p var5 = this.cN;
         if (this.cN.gt.getConfig().getBoolean("arena-regen.auto.enabled", true)) {
            var5 = this.cN;
            long var3 = Math.max(1L, this.cN.gt.getConfig().getLong("arena-regen.auto.interval-seconds", 300L));
            var5 = this.cN;
            long var13 = Math.max(1L, this.cN.gt.getConfig().getLong("arena-regen.auto.initial-delay-seconds", var3));
            long var7 = var3 * 20L;
            long var9 = var13 * 20L;
            this.cT = this.cO.ab2c(() -> {
               Predicate var3x = var2;
               if (this.enabled() && var1 != null) {
                  p var4 = this.cN;
                  boolean var8 = this.cN.gt.getConfig().getBoolean("arena-regen.auto.only-empty-arenas", true);
                  ArrayList var5x = new ArrayList();
                  Iterator var7x = ((Collection)var1.get()).iterator();

                  while (var7x.hasNext()) {
                     j var6;
                     if ((var6 = (j)var7x.next()) != null && var6.S() != null) {
                        if (var8 && var3x != null && !var3x.test(var6)) {
                           y.w7k3("ARENA", "Skipped auto regen for arena '" + var6.O() + "' because players are inside.");
                        } else {
                           var5x.add(var6);
                        }
                     }
                  }

                  y.w7k3("ARENA", "Auto regen selected " + var5x.size() + " target arenas.");
                  this.m4z1(var5x);
               }
            }, var9, var7);
            y.w7k3("ARENA", "Auto arena regen scheduled every " + var3 + " seconds.");
            return;
         }
      }
   }

   public final void l9m6() {
      if (this.cT != null) {
         this.cT.cancel();
         this.cT = null;
      }
   }

   public final void m4z1(Collection<j> var1) {
      if (this.enabled() && var1 != null && !var1.isEmpty()) {
         List var3;
         if ((var3 = var1.stream().filter(var0 -> var0 != null && var0.S() != null).filter(var1x -> {
            String var2x = var1x.O().toLowerCase();
            if (!this.cR.add(var2x)) {
               return false;
            } else if (!ab2c(this.z4c9(var1x.O()))) {
               this.cR.remove(var2x);
               this.ab2c(var1x, null);
               return false;
            } else {
               return true;
            }
         }).toList()).isEmpty()) {
            y.w7k3("ARENA", "Regen-all skipped: no arenas with valid snapshots ready.");
         } else {
            y.w7k3("ARENA", "Regen-all queued for " + var3.size() + " arenas.");
            p var2 = this.cN;
            if (this.cN.gt.getConfig().getBoolean("arena-regen.countdown.enabled", true)) {
               this.x7q9(var3);
            } else {
               for (j var5 : var3) {
                  this.k2w6(var5, var5.O().toLowerCase());
               }
            }
         }
      }
   }

   public final void z4c9(j var1) {
      if (this.enabled() && var1 != null && var1.S() != null) {
         String var2 = var1.O().toLowerCase();
         if (this.cR.add(var2)) {
            File var3;
            if (!ab2c(var3 = this.z4c9(var1.O()))) {
               this.cR.remove(var2);
               y.w7k3("ARENA", "Regen for arena '" + var1.O() + "' found no valid snapshot. Creating snapshot instead.");
               this.ab2c(var1, null);
            } else {
               y.w7k3("ARENA", "Regen requested for arena '" + var1.O() + "' using snapshot " + var3.getAbsolutePath() + ".");
               p var5 = this.cN;
               if (this.cN.gt.getConfig().getBoolean("arena-regen.countdown.enabled", true)) {
                  j var7 = var1;
                  var5 = this.cN;
                  int var4;
                  if ((var4 = Math.max(0, this.cN.gt.getConfig().getInt("arena-regen.countdown.seconds", 30))) <= 0) {
                     this.k2w6(var7, var2);
                  } else {
                     var5 = this.cN;
                     HashSet var12;
                     if ((var12 = new HashSet(this.cN.gt.getConfig().getIntegerList("arena-regen.countdown.alerts"))).isEmpty()) {
                        var12.addAll(List.of(30, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1));
                     }

                     int[] var9 = new int[]{var4};
                     t.ab2c[] var6;
                     (var6 = new t.ab2c[1])[0] = this.cO.ab2c(() -> {
                        if (var12.contains(var9[0])) {
                           Bukkit.broadcastMessage(this.ab2c("message", var7.O(), var9[0]));
                        }

                        if (var9[0] <= 0) {
                           var6[0].cancel();
                           this.k2w6(var7, var2);
                        } else {
                           var9[0]--;
                        }
                     }, 1L, 20L);
                  }
               } else {
                  this.k2w6(var1, var2);
               }
            }
         }
      }
   }

   private void x7q9(List<j> var1) {
      p var3 = this.cN;
      int var2;
      if ((var2 = Math.max(0, this.cN.gt.getConfig().getInt("arena-regen.countdown.seconds", 30))) > 0) {
         var3 = this.cN;
         HashSet var9;
         if ((var9 = new HashSet(this.cN.gt.getConfig().getIntegerList("arena-regen.countdown.alerts"))).isEmpty()) {
            var9.addAll(List.of(30, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1));
         }

         int[] var6 = new int[]{var2};
         t.ab2c[] var4;
         (var4 = new t.ab2c[1])[0] = this.cO.ab2c(() -> {
            if (var9.contains(var6[0])) {
               Bukkit.broadcastMessage(this.ab2c("all-message", "all", var6[0]));
            }

            if (var6[0] > 0) {
               var6[0]--;
            } else {
               var4[0].cancel();

               for (j var6x : var1) {
                  this.k2w6(var6x, var6x.O().toLowerCase());
               }
            }
         }, 1L, 20L);
      } else {
         for (j var5 : var1) {
            this.k2w6(var5, var5.O().toLowerCase());
         }
      }
   }

   private void k2w6(j var1, String var2) {
      File var3;
      if (!(var3 = this.z4c9(var1.O())).exists()) {
         this.cR.remove(var2);
         this.ab2c(var1, null);
      } else {
         e7z1.q8v3 var4;
         try {
            var4 = new e7z1.q8v3(var3);
         } catch (IOException var8) {
            this.cR.remove(var2);
            this.ab2c(var1.O(), var3);
            this.ab2c(var1, null);
            return;
         }

         World var9;
         if ((var9 = Bukkit.getWorld(var4.dp)) == null) {
            ab2c(var4);
            this.cR.remove(var2);
            this.cM.getLogger().warning("Could not regenerate arena '" + var1.O() + "': world is not loaded.");
         } else {
            p var5 = this.cN;
            int var10 = Math.max(1, this.cN.gt.getConfig().getInt("arena-regen.fast-blocks-per-tick", 12000));
            y.w7k3("ARENA", "Regen started for arena '" + var1.O() + "': blocks=" + var4.dt + ", batch=" + var10 + ", world=" + var4.dp + ".");
            HashMap var6 = new HashMap();
            e7z1.m4z1[] var7 = new e7z1.m4z1[1];
            this.ab2c(var1, var2, var9, var4, var10, var6, var7);
         }
      }
   }

   private void ab2c(j var1, String var2, World var3, e7z1.q8v3 var4, int var5, Map<String, BlockData> var6, e7z1.m4z1[] var7) {
      try {
         if (!ab2c(var4, var7)) {
            var4.close();
            this.cR.remove(var2);
            y.w7k3("ARENA", "Regen complete for arena '" + var1.O() + "'.");
            return;
         }
      } catch (RuntimeException | IOException var10) {
         ab2c(var4);
         this.cR.remove(var2);
         this.cM.getLogger().warning("Could not regenerate arena '" + var1.O() + "': " + var10.getMessage());
         return;
      }

      e7z1.m4z1 var8 = var7[0];
      int var9 = var4.dq + var8.dk >> 4;
      int var11 = var4.ds + var8.dm >> 4;
      this.cO.ab2c(var3, var9, var11, () -> {
         int var10x = 0;

         try {
            ab2c(var3, var9, var11, var1.S(), var1.O());

            while (var10x < var5 && ab2c(var4, var7)) {
               e7z1.m4z1 var11x = var7[0];
               int var12 = var4.dq + var11x.dk;
               int var13 = var4.dr + var11x.dl;
               int var14 = var4.ds + var11x.dm;
               if (!ab2c(var12, var14, var9, var11)) {
                  break;
               }

               BlockData var16 = (BlockData)var6.computeIfAbsent(var11x.dn, Bukkit::createBlockData);
               var3.getBlockAt(var12, var13, var14).setBlockData(var16, false);
               var7[0] = null;
               var10x++;
            }

            ab2c(var3, var9, var11, var1.S(), var1.O());
            if (!ab2c(var4, var7)) {
               var4.close();
               this.cR.remove(var2);
               y.w7k3("ARENA", "Regen complete for arena '" + var1.O() + "'.");
            } else {
               y.w7k3("ARENA", "Regen batch for arena '" + var1.O() + "': chunk=" + var9 + "," + var11 + ", blocks=" + var10x + ".");
               this.cO.ab2c(() -> this.ab2c(var1, var2, var3, var4, var5, var6, var7), 1L);
            }
         } catch (RuntimeException | IOException var15) {
            ab2c(var4);
            this.cR.remove(var2);
            this.cM.getLogger().warning("Could not regenerate arena '" + var1.O() + "': " + var15.getMessage());
         }
      }, 1L);
   }

   private static boolean ab2c(e7z1.q8v3 var0, e7z1.m4z1[] var1) throws IOException {
      if (var1[0] != null) {
         return true;
      } else if (!var0.q1d9()) {
         return false;
      } else {
         var1[0] = var0.x6y3();
         return true;
      }
   }

   private String ab2c(String var1, String var2, int var3) {
      p var4 = this.cN;
      var1 = this.cN
         .gt
         .getConfig()
         .getString(
            "arena-regen.countdown." + var1,
            var1.equals("all-message") ? "%prefix%&eAll arenas regenerating in &f%seconds%s&e." : "%prefix%&eArena &f%arena% &eregenerating in &f%seconds%s&e."
         );
      String var6 = r.ab2c(this.cN, "prefix", "");
      return w.n(var1.replace("%prefix%", var6).replace("%arena%", var2).replace("%seconds%", String.valueOf(var3)));
   }

   private static void ab2c(World var0, int var1, int var2, k var3, String var4) {
      int var5 = 0;
      Entity[] var8;
      var1 = (var8 = var0.getChunkAt(var1, var2).getEntities()).length;

      for (int var10 = 0; var10 < var1; var10++) {
         Entity var6;
         if ((var6 = var8[var10]) instanceof Item) {
            Item var11;
            Location var7 = (var11 = (Item)var6).getLocation();
            if (var3.m4z1(var7)) {
               var11.remove();
               var5++;
            }
         }
      }

      if (var5 > 0) {
         y.w7k3("ARENA", "Cleared " + var5 + " dropped items from arena '" + var4 + "'.");
      }
   }

   public final void h1s7(String var1) {
      if (var1 != null && !var1.isBlank()) {
         try {
            Files.deleteIfExists(this.z4c9(var1).toPath());
            Files.deleteIfExists(new File(this.cP, var1 + ".arena.tmp").toPath());
         } catch (IOException var3) {
            this.cM.getLogger().warning("Could not delete regen snapshot for arena '" + var1 + "': " + var3.getMessage());
         }
      }
   }

   public final void v8m5(j var1) {
      if (this.enabled() && var1 != null && var1.S() != null) {
         File var2;
         if ((var2 = this.z4c9(var1.O())).exists()) {
            String var3 = var1.O().toLowerCase();
            this.cR.remove(var3);
            if (this.cR.add(var3)) {
               try (e7z1.q8v3 var16 = new e7z1.q8v3(var2)) {
                  World var4;
                  if ((var4 = Bukkit.getWorld(var16.dp)) != null) {
                     HashMap var5 = new HashMap();

                     while (var16.q1d9()) {
                        e7z1.m4z1 var6 = var16.x6y3();
                        BlockData var7 = (BlockData)var5.computeIfAbsent(var6.dn, Bukkit::createBlockData);
                        var4.getBlockAt(var16.dq + var6.dk, var16.dr + var6.dl, var16.ds + var6.dm).setBlockData(var7, false);
                     }

                     y.w7k3("ARENA", "Regenerated arena '" + var1.O() + "' during shutdown.");
                     return;
                  }

                  this.cM.getLogger().warning("Could not regenerate arena '" + var1.O() + "' on shutdown: world is not loaded.");
               } catch (RuntimeException | IOException var14) {
                  this.cM.getLogger().warning("Could not regenerate arena '" + var1.O() + "' on shutdown: " + var14.getMessage());
                  return;
               } finally {
                  this.cR.remove(var3);
               }
            }
         }
      }
   }

   private boolean enabled() {
      p var1 = this.cN;
      return this.cN.gt.getConfig().getBoolean("arena-regen.enabled", true);
   }

   private File z4c9(String var1) {
      return new File(this.cP, var1 + ".arena");
   }

   private File v8m5(String var1) {
      return new File(this.cP, var1 + ".arena.tmp");
   }

   private static boolean ab2c(File var0) {
      return var0.exists() && var0.isFile() && var0.length() > 0L;
   }

   private void ab2c(String var1, File var2) {
      try {
         Files.deleteIfExists(var2.toPath());
         this.cM.getLogger().warning("Deleted broken regen snapshot for arena '" + var1 + "'. A fresh snapshot will be created.");
      } catch (IOException var4) {
         this.cM.getLogger().warning("Could not delete broken regen snapshot for arena '" + var1 + "': " + ab2c(var4, var2));
      }
   }

   private void n2x6(String var1) {
      this.cS.put(var1, System.currentTimeMillis() + 30000L);
   }

   private static boolean ab2c(int var0, int var1, int var2, int var3) {
      return var0 >> 4 == var2 && var1 >> 4 == var3;
   }

   private static void ab2c(Runnable var0) {
      if (var0 != null) {
         var0.run();
      }
   }

   private static String ab2c(Exception var0, File var1) {
      String var2 = var0.getMessage();
      StringBuilder var3 = new StringBuilder(var0.getClass().getSimpleName());
      if (var2 != null && !var2.isBlank()) {
         var3.append(": ").append(var2);
      }

      var3.append(" [path=").append(var1.getAbsolutePath()).append(']');
      if ((var1 = var1.isDirectory() ? var1 : var1.getParentFile()) != null) {
         var3.append(" [folderExists=").append(var1.exists()).append(", canWrite=").append(var1.canWrite()).append(']');
      }

      return var3.toString();
   }

   private static void ab2c(AutoCloseable var0) {
      try {
         if (var0 != null) {
            var0.close();
         }
      } catch (Exception var1) {
      }
   }

   static record ab2c(int cU, int cV, int cW, int cX, int cY, int cZ) {

      static e7z1.ab2c ab2c(k var0, World var1) {
         int var2 = (int)Math.floor(var0.fX);
         int var3 = Math.max(var1.getMinHeight(), (int)Math.floor(var0.fY));
         int var4 = (int)Math.floor(var0.fZ);
         int var5 = (int)Math.ceil(var0.ga) - 1;
         int var8 = Math.min(var1.getMaxHeight() - 1, (int)Math.ceil(var0.gb) - 1);
         int var7 = (int)Math.ceil(var0.gc) - 1;
         return new e7z1.ab2c(var2, var3, var4, var5, var8, var7);
      }

      int o3w8() {
         long var1;
         if ((var1 = (long)(this.cX - this.cU + 1) * (long)(this.cY - this.cV + 1) * (long)(this.cZ - this.cW + 1)) >= 0L && var1 <= 50000000L) {
            return (int)var1;
         } else {
            throw new IllegalArgumentException("Invalid arena size: " + var1);
         }
      }
   }

   static final class k2w6 implements AutoCloseable {
      private final File dv;
      private final File dw;
      final DataOutputStream dx;
      private boolean dy;

      k2w6(File var1, File var2, String var3, String var4, e7z1.ab2c var5, int var6) throws IOException {
         this.dv = var1;
         this.dw = var2;
         if ((var1 = var1.getParentFile()) != null && !var1.exists() && !var1.mkdirs()) {
            throw new IOException("Could not create folder " + var1.getAbsolutePath());
         } else {
            Files.deleteIfExists(var2.toPath());
            this.dx = new DataOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(var2), 262144)) {
               {
                  this.def.setLevel(1);
               }
            });
            this.dx.writeInt(1);
            this.dx.writeUTF(var3);
            this.dx.writeUTF(var4);
            this.dx.writeInt(var5.cU);
            this.dx.writeInt(var5.cV);
            this.dx.writeInt(var5.cW);
            this.dx.writeInt(var5.cX);
            this.dx.writeInt(var5.cY);
            this.dx.writeInt(var5.cZ);
            this.dx.writeInt(var6);
            this.dx.flush();
         }
      }

      void x7q9(File var1) throws IOException {
         this.dx.flush();
         this.dx.close();

         try {
            Files.move(this.dw.toPath(), var1.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (IOException var2) {
            Files.move(this.dw.toPath(), var1.toPath(), StandardCopyOption.REPLACE_EXISTING);
         }

         this.dy = true;
      }

      @Override
      public final void close() throws IOException {
         if (!this.dy) {
            this.dx.close();
            Files.deleteIfExists(this.dw.toPath());
         }
      }
   }

   static record m4z1(int dk, int dl, int dm, String dn) {
   }

   static final class q8v3 implements AutoCloseable {
      private final DataInputStream dis;
      final String dp;
      final int dq;
      final int dr;
      final int ds;
      final int dt;
      private int du;

      q8v3(File var1) throws IOException {
         this.dis = new DataInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(var1), 262144)));
         int var2;
         if ((var2 = this.dis.readInt()) != 1) {
            throw new IOException("Unsupported snapshot format: " + var2);
         } else {
            this.dis.readUTF();
            this.dp = this.dis.readUTF();
            this.dq = this.dis.readInt();
            this.dr = this.dis.readInt();
            this.ds = this.dis.readInt();
            this.dis.readInt();
            this.dis.readInt();
            this.dis.readInt();
            this.dt = this.dis.readInt();
            if (this.dt < 0 || this.dt > 50000000) {
               throw new IOException("Invalid block count: " + this.dt);
            }
         }
      }

      boolean q1d9() {
         return this.du < this.dt;
      }

      e7z1.m4z1 x6y3() throws IOException {
         this.du++;
         return new e7z1.m4z1(this.dis.readInt(), this.dis.readInt(), this.dis.readInt(), this.dis.readUTF());
      }

      @Override
      public final void close() throws IOException {
         this.dis.close();
      }
   }

   static final class x7q9 {
      final e7z1.ab2c da;
      private final int db;
      final int dc;
      final int dd;
      final int de;
      int df;
      int dg;
      int dh;
      int di;
      int dj;

      x7q9(e7z1.ab2c var1) {
         this.da = var1;
         this.db = var1.cU >> 4;
         this.dc = var1.cX >> 4;
         this.dd = var1.cW >> 4;
         this.de = var1.cZ >> 4;
         this.df = this.db;
         this.dg = this.dd;
         this.dh = this.i5k2();
         this.di = var1.cV;
         this.dj = this.a7r4();
      }

      boolean hasNext() {
         return this.df <= this.dc;
      }

      int i5k2() {
         return Math.max(this.da.cU, this.df << 4);
      }

      int a7r4() {
         return Math.max(this.da.cW, this.dg << 4);
      }
   }
}
