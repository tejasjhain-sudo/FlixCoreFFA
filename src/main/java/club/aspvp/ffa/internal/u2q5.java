package club.aspvp.ffa.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class u2q5 {
   private final p cw;
   private final v cx;
   private final Map<String, j> cy = new ConcurrentHashMap<>();
   private final Map<UUID, Location> cz = new ConcurrentHashMap<>();
   public final Map<UUID, u2q5.x7q9> cA = new ConcurrentHashMap<>();
   private volatile m cB;
   public e7z1 p6d2;

   public u2q5(p var1, v var2) {
      this.cw = var1;
      this.cx = var2;
   }

   public final void d9r1() {
      this.cy.clear();
      FileConfiguration var1;
      ConfigurationSection var2;
      if ((var2 = (var1 = this.cw.h("arenas.yml")).getConfigurationSection("arenas")) != null) {
         for (String var4 : var2.getKeys(false)) {
            ConfigurationSection var5;
            if ((var5 = var2.getConfigurationSection(var4)) != null) {
               Map var10000 = this.cy;
               String var10001 = var4.toLowerCase(Locale.ROOT);
               j var7;
               (var7 = new j(var4)).g = var5.getBoolean("enabled", false);
               var7.h = var5.getBoolean("build-allowed", false);
               var7.fR.addAll(var5.getStringList("kits"));
               var7.fS.addAll(j.r5n0(var5.getStringList("allowed-break-blocks")));
               var7.fT.addAll(j.r5n0(var5.getStringList("allowed-place-blocks")));
               String var6 = var5.getString("kit", "");
               if (var7.fR.isEmpty() && var6 != null && !var6.isBlank()) {
                  var7.fR.add(var6);
               }

               var7.fU = m.r5n0(var5.getConfigurationSection("spawn"));
               var7.fV = k.m4z1(var5.getConfigurationSection("region"));
               var10000.put(var10001, var7);
            } else {
               y.w7k3("ARENA", "Skipped malformed arena entry '" + var4 + "'.");
            }
         }
      }

      this.cB = m.r5n0(var1.getConfigurationSection("lobby"));
      y.w7k3("ARENA", "Loaded " + this.cy.size() + " arenas.");
   }

   public final void y5p8() {
      String var1 = this.c2f4();
      if (this.s6h0()) {
         this.cx.z4c9("arenas.yml", var1);
      }
   }

   private String c2f4() {
      FileConfiguration var1;
      (var1 = this.cw.h("arenas.yml")).set("arenas", null);
      var1.set("lobby", null);
      ConfigurationSection var2 = var1.createSection("arenas");
      Iterator var3 = this.cy.values().iterator();

      while (var3.hasNext()) {
         j var4;
         (var4 = (j)var3.next()).x7q9(var2.createSection(var4.O()));
      }

      if (this.cB != null) {
         this.cB.t9b4(var1.createSection("lobby"));
      }

      this.cw.i("arenas.yml");
      return var1.saveToString();
   }

   private boolean s6h0() {
      p var1 = this.cw;
      return this.cw.gt.getConfig().getBoolean("settings.store-arenas-in-database", false)
         || this.cw
            .h("database.yml")
            .getBoolean("snapshots.save-kits-and-arenas", this.cw.h("database.yml").getBoolean("storage.save-kits-and-arenas-snapshot", true));
   }

   public final boolean ab2c(String var1, k var2) {
      if (!(var1 = ab.r(var1)).isBlank() && !this.cy.containsKey(var1)) {
         j var3;
         (var3 = new j(var1)).fV = var2;
         this.cy.put(var1, var3);
         this.y5p8();
         this.k2w6(var3);
         return true;
      } else {
         return false;
      }
   }

   public final boolean f3y8(String var1) {
      j var2;
      if ((var2 = this.cy.remove(ab.r(var1))) == null) {
         return false;
      } else {
         if (this.p6d2 != null) {
            this.p6d2.h1s7(var2.O());
         }

         this.y5p8();
         return true;
      }
   }

   public Optional<j> arena(String var1) {
      return Optional.ofNullable(this.cy.get(ab.r(var1)));
   }

   public Collection<j> arenas() {
      return this.cy.values();
   }

   public final void ab2c(Location var1) {
      this.cB = m.q8v3(var1);
      this.y5p8();
   }

   public final Location b8t3() {
      return this.cB == null ? null : this.cB.ac();
   }

   public final void ab2c(j var1, Location var2) {
      m var3 = m.q8v3(var2);
      var1.fU = var3;
      this.y5p8();
   }

   public final void x7q9(j var1, String var2) {
      String var3 = ab.r(var2);
      var1.fR.clear();
      if (var3 != null && !var3.isBlank()) {
         var1.fR.add(var3);
      }

      this.y5p8();
      y.w7k3("ARENA", "Set primary kit '" + var1.Q() + "' for arena '" + var1.O() + "'.");
   }

   public final boolean m4z1(j var1, String var2) {
      var2 = ab.r(var2);
      if (!var1.c(var2)) {
         return false;
      } else {
         this.y5p8();
         y.w7k3("ARENA", "Linked kit '" + var2 + "' to arena '" + var1.O() + "'.");
         return true;
      }
   }

   public final boolean q8v3(j var1, String var2) {
      var2 = ab.r(var2);
      if (!var1.d(var2)) {
         return false;
      } else {
         this.y5p8();
         y.w7k3("ARENA", "Unlinked kit '" + var2 + "' from arena '" + var1.O() + "'.");
         return true;
      }
   }

   public final void m4z1(j var1) {
      var1.fR.clear();
      this.y5p8();
      y.w7k3("ARENA", "Cleared linked kits for arena '" + var1.O() + "'.");
   }

   public final boolean p6d2(String var1) {
      var1 = ab.r(var1);
      boolean var2 = false;

      for (j var4 : this.cy.values()) {
         var2 |= var4.d(var1);
      }

      if (var2) {
         this.y5p8();
         y.w7k3("ARENA", "Removed deleted kit '" + var1 + "' from all arena links.");
      }

      return var2;
   }

   public final void ab2c(j var1, Location var2, Location var3) {
      k var4 = k.ab2c(var2, var3);
      var1.fV = var4;
      this.y5p8();
      this.k2w6(var1);
      y.w7k3("ARENA", "Set API region for arena '" + var1.O() + "'.");
   }

   public final void q8v3(j var1) {
      Object var3 = null;
      var1.fV = (k)var3;
      this.y5p8();
      y.w7k3("ARENA", "Cleared region for arena '" + var1.O() + "'.");
   }

   public final void c2f4(Player var1) {
      this.cA.put(var1.getUniqueId(), new u2q5.x7q9(null, null, null));
   }

   public final Optional<String> s6h0(Player var1) {
      return Optional.ofNullable(this.cA.get(var1.getUniqueId())).map(u2q5.x7q9::j4n9);
   }

   public final boolean b8t3(Player var1) {
      return this.cA.containsKey(var1.getUniqueId());
   }

   public final u2q5.ab2c ab2c(Player var1, int var2, Location var3) {
      u2q5.x7q9 var4;
      if ((var4 = this.cA.get(var1.getUniqueId())) == null) {
         return u2q5.ab2c.cC;
      } else {
         j var5 = var4.j4n9() == null ? null : this.arena(var4.j4n9()).orElse(null);
         if (var4.j4n9() != null && var5 == null) {
            this.cA.remove(var1.getUniqueId());
            return u2q5.ab2c.cC;
         } else {
            u2q5.x7q9 var7 = var2 == 1 ? new u2q5.x7q9(var4.j4n9(), var3, var4.cL) : new u2q5.x7q9(var4.j4n9(), var4.cK, var3);
            this.cA.put(var1.getUniqueId(), var7);
            if (!var7.g1v7()) {
               return var2 == 1 ? u2q5.ab2c.cD : u2q5.ab2c.cE;
            } else if (!var7.cK.getWorld().equals(var7.cL.getWorld())) {
               return u2q5.ab2c.cF;
            } else if (var5 == null) {
               return u2q5.ab2c.cG;
            } else {
               k var6 = k.ab2c(var7.cK, var7.cL);
               var5.fV = var6;
               this.cA.remove(var1.getUniqueId());
               this.y5p8();
               this.k2w6(var5);
               return u2q5.ab2c.cH;
            }
         }
      }
   }

   public void k2w6(j var1) {
      if (this.p6d2 != null) {
         this.p6d2.r5n0(var1);
      }
   }

   public static enum ab2c {
      cC,
      cD,
      cE,
      cF,
      cG,
      cH;
   }

   public static record x7q9(String cJ, Location cK, Location cL) {

      public final boolean g1v7() {
         return this.cK != null && this.cL != null;
      }

      public final String j4n9() {
         return this.cJ;
      }

      public final Location u2q5() {
         return this.cK;
      }

      public final Location e7z1() {
         return this.cL;
      }
   }
}
