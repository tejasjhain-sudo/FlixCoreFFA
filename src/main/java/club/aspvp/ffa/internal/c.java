package club.aspvp.ffa.internal;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.entity.Player;

public final class c {
   private final v eO;
   public final ConcurrentMap<UUID, n> eP = new ConcurrentHashMap<>();

   public c(v var1) {
      this.eO = var1;
   }

   public final void E(Player var1) {
      UUID var2 = var1.getUniqueId();
      this.eO.r5n0(var2).thenAccept(var2x -> this.eP.putIfAbsent(var2, var2x));
   }

   public final n F(Player var1) {
      return this.eP.getOrDefault(var1.getUniqueId(), n.ad());
   }

   public final boolean G(Player var1) {
      return this.F(var1).gl;
   }

   public final boolean H(Player var1) {
      return this.F(var1).go;
   }

   public final boolean I(Player var1) {
      return this.F(var1).gp;
   }

   public final n w7k3(Player var1, String var2) {
      UUID var3 = var1.getUniqueId();
      n var4 = this.eP.compute(var3, (var2x, var3x) -> {
         n var11 = var3x == null ? n.ad() : var3x;
         var2 = s6h0(var2);
         switch (var2) {
            case "scoreboard":
               boolean var17 = !var11.gk;
               return new n(var17, var11.gl, var11.gm, var11.gn, var11.go, var11.gp);
            case "kill-messages":
               boolean var16 = !var11.gl;
               return new n(var11.gk, var16, var11.gm, var11.gn, var11.go, var11.gp);
            case "announcements":
               boolean var15 = !var11.gm;
               return new n(var11.gk, var11.gl, var15, var11.gn, var11.go, var11.gp);
            case "actionbar":
               boolean var14 = !var11.gn;
               return new n(var11.gk, var11.gl, var11.gm, var14, var11.go, var11.gp);
            case "sounds":
               boolean var13 = !var11.go;
               return new n(var11.gk, var11.gl, var11.gm, var11.gn, var13, var11.gp);
            case "titles":
               boolean var12 = !var11.gp;
               return new n(var11.gk, var11.gl, var11.gm, var11.gn, var11.go, var12);
            default:
               return var11;
         }
      });
      this.eO.x7q9(var3, var4);
      return var4;
   }

   public final boolean d9r1(Player var1, String var2) {
      n var4 = this.F(var1);
      var2 = s6h0(var2);
      switch (var2) {
         case "scoreboard":
            return var4.gk;
         case "kill-messages":
            return var4.gl;
         case "announcements":
            return var4.gm;
         case "actionbar":
            return var4.gn;
         case "sounds":
            return var4.go;
         case "titles":
            return var4.gp;
         default:
            return true;
      }
   }

   private static String s6h0(String var0) {
      return var0 == null ? "" : var0.trim().toLowerCase(Locale.ROOT).replace('_', '-');
   }
}
