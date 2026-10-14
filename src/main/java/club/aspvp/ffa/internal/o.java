package club.aspvp.ffa.internal;

import java.util.UUID;

public final class o {
   public final UUID gq;
   public volatile String name;
   public volatile int u;
   public volatile int v;
   public volatile int w;
   public volatile int x;
   public volatile int gr;
   public volatile int gs;
   public volatile int y;
   public volatile int z;
   public volatile int A;

   public o(UUID var1, String var2) {
      this.gq = var1;
      this.name = var2;
      this.A = 1;
   }

   public final void f3y8(int var1) {
      this.w = var1;
      this.x = Math.max(this.x, var1);
   }

   public final void p6d2(int var1) {
      this.y = Math.max(0, var1);
   }

   public final void h1s7(int var1) {
      this.A = Math.max(1, var1);
   }

   public final String ae() {
      return ab.ab2c(this.u, this.v);
   }
}
