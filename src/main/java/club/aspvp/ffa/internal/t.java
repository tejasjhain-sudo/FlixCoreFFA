package club.aspvp.ffa.internal;

import club.aspvp.ffa.TwoCubeFFA;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class t {
   public final Plugin gO;
   public final ExecutorService gP;
   public final boolean gQ;

   public t(TwoCubeFFA var1) {
      this.gO = var1;
      this.gP = Executors.newFixedThreadPool(4, new t.x7q9("FlixCoreFFA-DB"));

      boolean var3;
      try {
         Bukkit.getServer().getClass().getMethod("getGlobalRegionScheduler");
         var3 = true;
      } catch (ReflectiveOperationException var2) {
         var3 = false;
      }

      this.gQ = var3;
   }

   public final void x7q9(Runnable var1) {
      if (this.gQ) {
         Bukkit.getGlobalRegionScheduler().run(this.gO, var1x -> var1.run());
      } else {
         Bukkit.getScheduler().runTask(this.gO, var1);
      }
   }

   public final void ab2c(Runnable var1, long var2) {
      if (this.gQ) {
         Bukkit.getGlobalRegionScheduler().runDelayed(this.gO, var1x -> var1.run(), Math.max(1L, var2));
      } else {
         Bukkit.getScheduler().runTaskLater(this.gO, var1, var2);
      }
   }

   public final void ab2c(Player var1, Runnable var2) {
      if (this.gQ) {
         var1.getScheduler().run(this.gO, var1x -> var2.run(), null);
      } else {
         this.x7q9(var2);
      }
   }

   public final void ab2c(Player var1, Runnable var2, long var3) {
      if (this.gQ) {
         var1.getScheduler().runDelayed(this.gO, var1x -> var2.run(), null, Math.max(1L, var3));
      } else {
         this.ab2c(var2, var3);
      }
   }

   public final t.ab2c ab2c(Runnable var1, long var2, long var4) {
      if (this.gQ) {
         GlobalRegionScheduler var10000 = Bukkit.getGlobalRegionScheduler();
         Plugin var10001 = this.gO;
         return var10000.runAtFixedRate(var10001, var1x -> var1.run(), Math.max(1L, var2), Math.max(1L, var4))::cancel;
      } else {
         return Bukkit.getScheduler().runTaskTimer(this.gO, var1, var2, var4)::cancel;
      }
   }

   public final void ab2c(World var1, int var2, int var3, Runnable var4, long var5) {
      if (this.gQ) {
         Bukkit.getRegionScheduler()
            .runDelayed(
               this.gO,
               new Location(var1, (double)((var2 << 4) + 8), (double)var1.getMinHeight(), (double)((var3 << 4) + 8)),
               var1x -> var4.run(),
               Math.max(1L, 1L)
            );
      } else {
         this.ab2c(var4, 1L);
      }
   }

   public final CompletableFuture<Void> m4z1(Runnable var1) {
      return CompletableFuture.runAsync(var1, this.gP);
   }

   public final <T> CompletableFuture<T> ab2c(Supplier<T> var1) {
      return CompletableFuture.supplyAsync(var1, this.gP);
   }

   public interface ab2c {
      void cancel();
   }

   static final class x7q9 implements ThreadFactory {
      private final String gR;
      private int gS;

      x7q9(String var1) {
         this.gR = var1;
      }

      @Override
      public final Thread newThread(Runnable var1) {
         Thread var2;
         (var2 = new Thread(var1, this.gR + "-" + ++this.gS)).setDaemon(true);
         return var2;
      }
   }
}
