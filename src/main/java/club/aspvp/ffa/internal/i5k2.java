package club.aspvp.ffa.internal;

import club.aspvp.ffa.api.event.FfaCrossServerRequestEvent;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Bukkit;

public class i5k2 {
   private final p dZ;
   private final v ea;
   private final t eb;
   private final b ec;
   private final Map<String, Long> ed = new ConcurrentHashMap<>();
   private final AtomicBoolean ee = new AtomicBoolean();
   private t.ab2c ci;

   public i5k2(p var1, v var2, t var3, b var4) {
      this.dZ = var1;
      this.ea = var2;
      this.eb = var3;
      this.ec = var4;
   }

   public final void g() {
      this.h();
      if (this.i()) {
         long var1 = Math.max(20L, this.dZ.h("crossserver.yml").getLong("poll-interval-ticks", 40L));
         this.ci = this.eb.ab2c(this::k, 20L, var1);
         this.k();
         y.w7k3("CROSSSERVER", "Started heartbeat for server-id=" + this.j() + ".");
      }
   }

   public final void h() {
      if (this.ci != null) {
         this.ci.cancel();
         this.ci = null;
         y.w7k3("CROSSSERVER", "Stopped heartbeat.");
      }
   }

   public final String c2f4(String var1) {
      if (!this.i()) {
         return this.dZ.h("crossserver.yml").getString("placeholders.disabled", "disabled");
      } else {
         String var2 = s6h0(var1);
         Long var6;
         if ((var6 = this.ed.get(var2)) == null) {
            return this.dZ.h("crossserver.yml").getString("placeholders.offline", "offline");
         } else {
            long var4;
            return (var4 = Math.max(0L, System.currentTimeMillis() - var6)) > this.l()
               ? this.dZ.h("crossserver.yml").getString("placeholders.offline", "offline")
               : this.dZ
                  .h("crossserver.yml")
                  .getString("placeholders.ping-format", "%ping%ms")
                  .replace("%ping%", String.valueOf(var4))
                  .replace("%server%", var1);
         }
      }
   }

   public final boolean i() {
      return this.dZ.h("crossserver.yml").getBoolean("enabled", false);
   }

   public final String j() {
      return s6h0(this.dZ.h("crossserver.yml").getString("server-id", "ffa-1"));
   }

   public final CompletableFuture<Boolean> ab2c(String var1, String var2, String var3) {
      if (!this.i()) {
         return CompletableFuture.completedFuture(Boolean.FALSE);
      } else if (!(var1 = s6h0(var1)).isBlank() && var2 != null && !var2.isBlank()) {
         y.w7k3("CROSSSERVER", "Sending request type=" + var2 + " from=" + this.j() + " to=" + var1 + ".");
         return this.ea.ab2c(this.j(), var1, var2, var3, System.currentTimeMillis());
      } else {
         return CompletableFuture.completedFuture(Boolean.FALSE);
      }
   }

   private void k() {
      long var1 = System.currentTimeMillis();
      this.ea.ab2c(this.j(), var1, Bukkit.getOnlinePlayers().size(), this.ec.w()).exceptionally(var0 -> {
         y.w7k3("CROSSSERVER", "Heartbeat save failed: " + var0.getMessage());
         return null;
      });
      this.ea.an().thenAccept(var1x -> {
         this.ed.clear();
         var1x.forEach((var1xx, var2) -> this.ed.put(s6h0(var1xx), var2));
         y.w7k3("CROSSSERVER", "Heartbeat saved. Known servers=" + this.ed.keySet());
      }).exceptionally(var0 -> {
         y.w7k3("CROSSSERVER", "Heartbeat refresh failed: " + var0.getMessage());
         return null;
      });
      if (this.ee.compareAndSet(false, true)) {
         this.ea.ab2c(this.j(), var1 - this.l()).thenAccept(var1x -> {
            for (v.x7q9 var3 : var1x) {
               this.eb.x7q9(() -> Bukkit.getPluginManager().callEvent(new FfaCrossServerRequestEvent(var3.hf, var3.hg, var3.hh, var3.hi, var3.hj, var3.hk)));
            }

            if (!var1x.isEmpty()) {
               y.w7k3("CROSSSERVER", "Delivered " + var1x.size() + " request(s) to " + this.j() + ".");
            }
         }).exceptionally(var0 -> {
            y.w7k3("CROSSSERVER", "Request polling failed: " + var0.getMessage());
            return null;
         }).whenComplete((var1x, var2) -> this.ee.set(false));
      }
   }

   private long l() {
      return Math.max(1L, this.dZ.h("crossserver.yml").getLong("request-ttl-seconds", 30L)) * 1000L;
   }

   private static String s6h0(String var0) {
      return var0 == null ? "" : var0.trim().toLowerCase(Locale.ROOT);
   }
}
