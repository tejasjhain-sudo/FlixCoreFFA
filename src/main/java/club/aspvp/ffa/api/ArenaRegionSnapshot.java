package club.aspvp.ffa.api;

public record ArenaRegionSnapshot(String x6y3, double m8p0, double a, double b, double c, double d, double e) {
   public final String world() {
      return this.x6y3;
   }

   public final double minX() {
      return this.m8p0;
   }

   public final double minY() {
      return this.a;
   }

   public final double minZ() {
      return this.b;
   }

   public final double maxX() {
      return this.c;
   }

   public final double maxY() {
      return this.d;
   }

   public final double maxZ() {
      return this.e;
   }
}
