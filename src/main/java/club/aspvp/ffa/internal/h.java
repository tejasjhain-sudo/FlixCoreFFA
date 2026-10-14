package club.aspvp.ffa.internal;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public class h {
   public final p fH;
   public final v fI;
   public final Map<UUID, o> fJ = new ConcurrentHashMap<>();
   private final Map<UUID, Long> fK = new ConcurrentHashMap<>();

   public h(p var1, v var2) {
      this.fH = var1;
      this.fI = var2;
   }

   public final CompletableFuture<o> V(Player var1) {
      UUID var2 = var1.getUniqueId();
      long var3 = this.fK.merge(var2, 1L, Long::sum);
      return this.fI.m4z1(var1.getUniqueId(), var1.getName()).thenApply(var5 -> {
         String var7 = var1.getName();
         var5.name = var7;
         if (var1.isOnline() && this.fK.getOrDefault(var2, 0L) == var3) {
            this.fJ.compute(var2, (var2xx, var3xx) -> {
               if (var3xx == null) {
                  return var5;
               } else {
                  int var4 = var5.u + var3xx.u;
                  var5.u = var4;
                  var4 = var5.v + var3xx.v;
                  var5.v = var4;
                  var5.f3y8(var3xx.w);
                  var4 = Math.max(var5.x, var3xx.x);
                  var5.x = var4;
                  var5.p6d2(var5.y + var3xx.y);
                  var4 = var5.z + var3xx.z;
                  var5.z = var4;
                  var5.h1s7(Math.max(var5.A, var3xx.A));
                  return var5;
               }
            });
         }

         return (o)var5;
      });
   }

   public final o W(Player var1) {
      return this.fJ.computeIfAbsent(var1.getUniqueId(), var1x -> new o(var1x, var1.getName()));
   }

   public final Optional<o> m4z1(UUID var1) {
      return Optional.ofNullable(this.fJ.get(var1));
   }

   public final CompletableFuture<Void> ab2c(UUID var1, String var2) {
      return this.fI.q8v3(var1, var2).thenRun(() -> this.fJ.computeIfPresent(var1, (var2xx, var3) -> new o(var1, var2)));
   }

   public final int X(Player var1) {
      return this.W(var1).y;
   }

   public final int Y(Player var1) {
      p var2 = this.fH;
      if (!this.fH.gt.getConfig().getBoolean("tokens.enabled", true)) {
         return 0;
      } else {
         var2 = this.fH;
         int var4;
         return (var4 = Math.max(0, this.fH.gt.getConfig().getInt("tokens.kill-reward", 1))) <= 0
            ? 0
            : Math.max(0, (int)Math.round((double)var4 * this.Z(var1)));
      }
   }

   public final double Z(Player var1) {
      double var2 = 1.0;
      if (var1 == null) {
         return 1.0;
      } else {
         p var4 = this.fH;
         Iterator var7 = this.fH.gt.getConfig().getMapList("tokens.multipliers").iterator();

         while (var7.hasNext()) {
            Map var5;
            String var6;
            if (!(var6 = x7q9((var5 = (Map)var7.next()).get("permission"))).isBlank() && var1.hasPermission(var6)) {
               var2 = Math.max(var2, ab2c(var5.get("multiplier"), 1.0));
            }
         }

         return var2;
      }
   }

   public final int N() {
      p var1 = this.fH;
      return Math.max(0, this.fH.gt.getConfig().getInt("tokens.kill-reward", 1));
   }

   public final int r5n0(Player var1, int var2) {
      o var4;
      (var4 = this.W(var1)).p6d2(var4.y + var2);
      this.fI.x7q9(var4);
      return var4.y;
   }

   public final int t9b4(Player var1, int var2) {
      o var3;
      (var3 = this.W(var1)).p6d2(var2);
      this.fI.x7q9(var3);
      return var3.y;
   }

   public final void aa(Player var1) {
      o var2;
      if ((var2 = this.fJ.get(var1.getUniqueId())) != null) {
         this.fI.x7q9(var2);
      }
   }

   public final void ab(Player var1) {
      this.fK.merge(var1.getUniqueId(), 1L, Long::sum);
      this.aa(var1);
      this.fJ.remove(var1.getUniqueId());
   }

   private static double ab2c(Object var0, double var1) {
      if (var0 instanceof Number) {
         Number var4;
         return (var4 = (Number)var0).doubleValue();
      } else {
         try {
            return var0 == null ? 1.0 : Double.parseDouble(String.valueOf(var0));
         } catch (NumberFormatException var3) {
            return 1.0;
         }
      }
   }

   private static String x7q9(Object var0) {
      return var0 == null ? "" : String.valueOf(var0).trim();
   }
}
