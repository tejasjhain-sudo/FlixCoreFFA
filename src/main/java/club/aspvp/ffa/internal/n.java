package club.aspvp.ffa.internal;

import java.util.Objects;

public final class n {
   public final boolean gk;
   public final boolean gl;
   public final boolean gm;
   public final boolean gn;
   public final boolean go;
   public final boolean gp;

   public n(boolean gk, boolean gl, boolean gm, boolean gn, boolean go, boolean gp) {
      this.gk = gk;
      this.gl = gl;
      this.gm = gm;
      this.gn = gn;
      this.go = go;
      this.gp = gp;
   }

   public boolean gk() { return gk; }
   public boolean gl() { return gl; }
   public boolean gm() { return gm; }
   public boolean gn() { return gn; }
   public boolean go() { return go; }
   public boolean gp() { return gp; }

   public static n ad() {
      return new n(true, true, true, true, true, true);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof n other)) return false;
      return gk == other.gk && gl == other.gl && gm == other.gm && gn == other.gn && go == other.go && gp == other.gp;
   }

   @Override
   public int hashCode() {
      return Objects.hash(gk, gl, gm, gn, go, gp);
   }
}
