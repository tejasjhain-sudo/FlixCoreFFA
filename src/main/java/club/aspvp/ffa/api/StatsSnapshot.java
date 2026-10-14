package club.aspvp.ffa.api;

import java.util.UUID;

public record StatsSnapshot(UUID s, String t, int u, int v, int w, int x, int y, int z, int A, String B) {
   public final UUID uuid() {
      return this.s;
   }

   public final String name() {
      return this.t;
   }

   public final int kills() {
      return this.u;
   }

   public final int deaths() {
      return this.v;
   }

   public final int killstreak() {
      return this.w;
   }

   public final int bestKillstreak() {
      return this.x;
   }

   public final int tokens() {
      return this.y;
   }

   public final int xp() {
      return this.z;
   }

   public final int level() {
      return this.A;
   }

   public final String kdr() {
      return this.B;
   }
}
