package club.aspvp.ffa.internal;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public final class k {
   public final String fW;
   public final double fX;
   public final double fY;
   public final double fZ;
   public final double ga;
   public final double gb;
   public final double gc;

   public k(String fW, double fX, double fY, double fZ, double ga, double gb, double gc) {
      this.fW = fW;
      this.fX = fX;
      this.fY = fY;
      this.fZ = fZ;
      this.ga = ga;
      this.gb = gb;
      this.gc = gc;
   }

   public String fW() { return fW; }
   public double fX() { return fX; }
   public double fY() { return fY; }
   public double fZ() { return fZ; }
   public double ga() { return ga; }
   public double gb() { return gb; }
   public double gc() { return gc; }

   public static k ab2c(Location var0, Location var1) {
      double var2 = (double)Math.min(var0.getBlockY(), var1.getBlockY());
      double var4 = (double)Math.max(var0.getBlockY(), var1.getBlockY()) + 1.0;
      if (var0.getBlockY() == var1.getBlockY() && var0.getWorld() != null) {
         var2 = (double)var0.getWorld().getMinHeight();
         var4 = (double)var0.getWorld().getMaxHeight();
      }

      return new k(
         var0.getWorld().getName(),
         (double)Math.min(var0.getBlockX(), var1.getBlockX()),
         var2,
         (double)Math.min(var0.getBlockZ(), var1.getBlockZ()),
         (double)Math.max(var0.getBlockX(), var1.getBlockX()) + 1.0,
         var4,
         (double)Math.max(var0.getBlockZ(), var1.getBlockZ()) + 1.0
      );
   }

   public static k m4z1(ConfigurationSection var0) {
      return var0 == null
         ? null
         : new k(
            var0.getString("world"),
            var0.getDouble("min-x"),
            var0.getDouble("min-y"),
            var0.getDouble("min-z"),
            var0.getDouble("max-x"),
            var0.getDouble("max-y"),
            var0.getDouble("max-z")
         );
   }

   public final boolean m4z1(Location var1) {
      return var1 != null
         && var1.getWorld() != null
         && var1.getWorld().getName().equals(this.fW)
         && var1.getX() >= this.fX
         && var1.getX() < this.ga
         && (this.U() || var1.getY() >= this.fY && var1.getY() < this.gb)
         && var1.getZ() >= this.fZ
         && var1.getZ() < this.gc;
   }

   private boolean U() {
      return this.gb - this.fY <= 1.0;
   }

   public final void q8v3(ConfigurationSection var1) {
      var1.set("world", this.fW);
      var1.set("min-x", this.fX);
      var1.set("min-y", this.fY);
      var1.set("min-z", this.fZ);
      var1.set("max-x", this.ga);
      var1.set("max-y", this.gb);
      var1.set("max-z", this.gc);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof k other)) return false;
      return Double.compare(fX, other.fX) == 0 && Double.compare(fY, other.fY) == 0 && Double.compare(fZ, other.fZ) == 0
         && Double.compare(ga, other.ga) == 0 && Double.compare(gb, other.gb) == 0 && Double.compare(gc, other.gc) == 0
         && Objects.equals(fW, other.fW);
   }

   @Override
   public int hashCode() {
      return Objects.hash(fW, fX, fY, fZ, ga, gb, gc);
   }
}
