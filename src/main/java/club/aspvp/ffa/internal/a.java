package club.aspvp.ffa.internal;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public final class a {
   private final v ex;
   private final t ey;
   private final Map<UUID, Map<String, l>> ez = new ConcurrentHashMap<>();
   private final Map<UUID, Long> eA = new ConcurrentHashMap<>();

   public a(v var1, t var2) {
      this.ex = var1;
      this.ey = var2;
   }

   public final CompletableFuture<Void> s(Player var1) {
      UUID var2 = var1.getUniqueId();
      long var3 = this.eA.merge(var2, 1L, Long::sum);
      CompletableFuture var5 = new CompletableFuture();
      this.ex.t9b4(var1.getUniqueId()).thenAccept(var6 -> {
         ConcurrentHashMap var7 = new ConcurrentHashMap();
         var6.forEach((var2xx, var3xx) -> m4z1(var2xx, var3xx).ifPresent(var2xxx -> var7.put(var2xx, var2xxx)));
         this.ey.ab2c(var1, () -> {
            if (var1.isOnline() && this.eA.getOrDefault(var2, 0L) == var3) {
               this.ez.merge(var2, var7, (var0, var1xxx) -> {
                  var1xxx.putAll((Map<? extends String, ? extends l>)var0);
                  return var1xxx;
               });
               y.w7k3("KIT", "Loaded " + this.ez.get(var2).size() + " personal kits for " + var1.getName() + ".");
               var5.complete(null);
            } else {
               var5.complete(null);
            }
         });
      }).exceptionally(var1x -> {
         var5.completeExceptionally(var1x);
         return null;
      });
      return var5;
   }

   public final Optional<l> v8m5(Player var1, String var2) {
      Map var3;
      return (var3 = this.ez.get(var1.getUniqueId())) == null ? Optional.empty() : Optional.ofNullable((l)var3.get(var2.toLowerCase()));
   }

   public final void ab2c(Player var1, String var2, m8p0 var3) {
      l var5 = var3.x7q9(var2, var1);
      this.ez.computeIfAbsent(var1.getUniqueId(), var0 -> new ConcurrentHashMap<>()).put(var5.V(), var5);
      v var10000 = this.ex;
      UUID var10001 = var1.getUniqueId();
      String var10002 = var5.V();
      YamlConfiguration var4 = new YamlConfiguration();
      var5.k2w6(var4.createSection("kit"));
      var10000.x7q9(var10001, var10002, var4.saveToString()).exceptionally(var2x -> {
         y.w7k3("DATABASE", "Could not save personal kit '" + var5.V() + "' for " + var1.getName() + ": " + var2x.getMessage());
         return null;
      });
      y.w7k3("KIT", "Saved personal kit '" + var5.V() + "' for " + var1.getName() + ".");
   }

   public final void t(Player var1) {
      this.eA.merge(var1.getUniqueId(), 1L, Long::sum);
      this.ez.remove(var1.getUniqueId());
   }

   private static Optional<l> m4z1(String var0, String var1) {
      try {
         YamlConfiguration var2;
         (var2 = new YamlConfiguration()).loadFromString(var1);
         ConfigurationSection var4;
         return (var4 = var2.getConfigurationSection("kit")) == null ? Optional.empty() : Optional.of(l.x7q9(var0, var4));
      } catch (Exception var3) {
         y.w7k3("KIT", "Skipped malformed personal kit '" + var0 + "': " + var3.getMessage());
         return Optional.empty();
      }
   }
}
