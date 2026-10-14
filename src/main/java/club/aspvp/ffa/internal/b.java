package club.aspvp.ffa.internal;

import club.aspvp.ffa.api.event.FfaArenaJoinEvent;
import club.aspvp.ffa.api.event.FfaArenaLeaveEvent;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class b {
   private final p eB;
   private final v eC;
   private final m8p0 eD;
   private final a eE;
   private final t eF;
   public final Map<UUID, String> eG = new ConcurrentHashMap<>();
   private final Map<UUID, String> eH = new ConcurrentHashMap<>();
   public final Map<UUID, ItemStack[]> eI = new ConcurrentHashMap<>();
   public final Map<UUID, ItemStack[]> eJ = new ConcurrentHashMap<>();
   public final Map<UUID, ItemStack> eK = new ConcurrentHashMap<>();
   public final Set<UUID> eL = ConcurrentHashMap.newKeySet();
   public final Set<UUID> eM = ConcurrentHashMap.newKeySet();
   private final Map<UUID, Long> eN = new ConcurrentHashMap<>();

   public b(p var1, v var2, m8p0 var3, a var4, t var5) {
      this.eB = var1;
      this.eC = var2;
      this.eD = var3;
      this.eE = var4;
      this.eF = var5;
   }

   public final void u(Player var1) {
      UUID var2 = var1.getUniqueId();
      long var3 = this.eN.merge(var2, 1L, Long::sum);
      this.eE.s(var1).thenRun(() -> this.eF.ab2c(var1, () -> {
            if (var1.isOnline() && this.w(var1)) {
               this.z(var1);
            }
         })).exceptionally(var1x -> {
         y.w7k3("KIT", "Could not load personal kits for " + var1.getName() + ": " + var1x.getMessage());
         return null;
      });
      this.eC.q8v3(var2).thenAccept(var5 -> {
         String var6 = var5.filter(var1xx -> this.eD.e7z1(var1xx).isPresent()).orElseGet(this.eD::s);
         this.eF.ab2c(var1, () -> {
            if (var1.isOnline() && this.eN.getOrDefault(var2, 0L) == var3) {
               this.eH.put(var2, var6);
               if (this.eG.containsKey(var2)) {
                  this.z(var1);
               }
            }
         });
      });
   }

   public final Optional<String> v(Player var1) {
      return Optional.ofNullable(this.eG.get(var1.getUniqueId()));
   }

   public final boolean w(Player var1) {
      return this.eG.containsKey(var1.getUniqueId());
   }

   public final long i5k2(String var1) {
      return this.eG.values().stream().filter(var1x -> var1x.equalsIgnoreCase(var1)).count();
   }

   public final int w() {
      return this.eG.size();
   }

   public final void q8v3(Player var1, j var2) {
      String var3;
      String var10000 = !(var3 = this.x(var1)).isBlank() && List.copyOf(var2.fR).stream().anyMatch(var1x -> var1x.equalsIgnoreCase(var3)) ? var3 : var2.Q();
      var3 = var10000;
      if (!var10000.isBlank()) {
         this.eN.merge(var1.getUniqueId(), 1L, Long::sum);
         this.eH.put(var1.getUniqueId(), var3);
         this.eC.k2w6(var1.getUniqueId(), var3);
         y.w7k3("JOIN", var1.getName() + " selected linked kit '" + var3 + "' for arena '" + var2.O() + "'.");
      }

      p var4 = this.eB;
      if (this.eB.gt.getConfig().getBoolean("settings.restore-inventory-on-leave", false)) {
         this.eI.putIfAbsent(var1.getUniqueId(), this.x7q9(var1.getInventory().getStorageContents()));
         this.eJ.putIfAbsent(var1.getUniqueId(), this.x7q9(var1.getInventory().getArmorContents()));
         this.eK.putIfAbsent(var1.getUniqueId(), ab2c(var1.getInventory().getItemInOffHand()));
      }

      this.eG.put(var1.getUniqueId(), var2.O());
      this.eM.remove(var1.getUniqueId());
      var1.setGameMode(GameMode.SURVIVAL);
      var1.setWalkSpeed(0.2F);
      var1.setFlySpeed(0.1F);
      var1.setAllowFlight(false);
      var1.setFlying(false);
      var1.setInvulnerable(false);
      var4 = this.eB;
      if (this.eB.gt.getConfig().getBoolean("settings.clear-inventory-on-join", true)) {
         var1.getInventory().clear();
      }

      this.z(var1);
      this.ab2c(var1, var2, "join");
      this.eF.ab2c(var1, () -> this.ab2c(var1, var2, "join-retry-1"), 5L);
      this.eF.ab2c(var1, () -> this.ab2c(var1, var2, "join-retry-2"), 20L);
      y.w7k3("JOIN", var1.getName() + " joined arena '" + var2.O() + "'.");
      Bukkit.getPluginManager().callEvent(new FfaArenaJoinEvent(var1, var2.O(), this.x(var1)));
   }

   private void ab2c(Player var1, j var2, String var3) {
      if (var1.isOnline() && this.eG.getOrDefault(var1.getUniqueId(), "").equalsIgnoreCase(var2.O())) {
         Location var4;
         if ((var4 = var2.fU == null ? null : var2.fU.ac()) != null && var4.getWorld() != null) {
            var1.teleportAsync(var4).thenAccept(var3x -> {
               if (!var3x) {
                  y.w7k3("JOIN", "Teleport failed for " + var1.getName() + " to arena '" + var2.O() + "' during " + var3 + ".");
               }
            });
         } else {
            y.w7k3("JOIN", "Could not teleport " + var1.getName() + " to arena '" + var2.O() + "' for " + var3 + ": spawn/world is missing.");
         }
      }
   }

   public final void ab2c(Player var1, Location var2) {
      this.ab2c(var1, var2, true);
   }

   public final void ab2c(Player var1, Location var2, boolean var3) {
      String var4;
      String var5;
      label35: {
         var4 = this.eG.remove(var1.getUniqueId());
         var5 = this.x(var1);
         boolean var6 = this.eM.remove(var1.getUniqueId());
         this.eL.remove(var1.getUniqueId());
         B(var1);
         ItemStack[] var7 = this.eI.remove(var1.getUniqueId());
         ItemStack[] var8 = this.eJ.remove(var1.getUniqueId());
         ItemStack var9 = this.eK.remove(var1.getUniqueId());
         if (!var6) {
            p var10 = this.eB;
            if (!this.eB.gt.getConfig().getBoolean("settings.restore-inventory-on-leave", false)) {
               break label35;
            }
         }

         if (var7 != null) {
            var1.getInventory().setStorageContents(var7);
         }

         if (var8 != null) {
            var1.getInventory().setArmorContents(var8);
         }

         if (var9 != null) {
            var1.getInventory().setItemInOffHand(var9);
         } else {
            var1.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
         }
      }

      var1.setGameMode(GameMode.SURVIVAL);
      var1.updateInventory();
      if (var3 && var2 != null) {
         var1.teleportAsync(var2);
      }

      y.w7k3("LEAVE", var1.getName() + " left FFA; arena kit inventory cleared.");
      if (var4 != null) {
         Bukkit.getPluginManager().callEvent(new FfaArenaLeaveEvent(var1, var4, var5));
      }
   }

   public final String x(Player var1) {
      return this.eH.getOrDefault(var1.getUniqueId(), this.eD.s());
   }

   public final String y(Player var1) {
      return this.w(var1) ? this.x(var1) : "None";
   }

   public final void n2x6(Player var1, String var2) {
      this.eN.merge(var1.getUniqueId(), 1L, Long::sum);
      this.eH.put(var1.getUniqueId(), var2);
      this.eC.k2w6(var1.getUniqueId(), var2);
      if (this.w(var1)) {
         this.z(var1);
      }
   }

   public final void z(Player var1) {
      String var2 = this.x(var1);
      l var3;
      if ((var3 = this.eE.v8m5(var1, var2).orElseGet(() -> this.eD.e7z1(var2).orElseGet(() -> this.eD.r().orElse(null)))) != null) {
         if (!var3.V().equalsIgnoreCase(var2)) {
            this.eH.put(var1.getUniqueId(), var3.V());
            this.eC.k2w6(var1.getUniqueId(), var3.V());
         }

         this.eD.r5n0(var1, var3);
         y.w7k3("KIT", "Applied kit '" + var3.V() + "' to " + var1.getName() + (this.eE.v8m5(var1, var2).isPresent() ? " (personal)." : " (global)."));
      } else {
         y.w7k3("KIT", "No kit found for " + var1.getName() + ": selected='" + var2 + "', fallback='" + this.eD.s() + "'.");
      }
   }

   public final void A(Player var1) {
      this.eN.merge(var1.getUniqueId(), 1L, Long::sum);
      this.eE.t(var1);
      this.eG.remove(var1.getUniqueId());
      this.eH.remove(var1.getUniqueId());
      this.eI.remove(var1.getUniqueId());
      this.eJ.remove(var1.getUniqueId());
      this.eK.remove(var1.getUniqueId());
      this.eL.remove(var1.getUniqueId());
      this.eM.remove(var1.getUniqueId());
   }

   public ItemStack[] x7q9(ItemStack[] var1) {
      ItemStack[] var2 = new ItemStack[var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2[var3] = ab2c(var1[var3]);
      }

      return var2;
   }

   public static ItemStack ab2c(ItemStack var0) {
      return var0 == null ? null : var0.clone();
   }

   public static void B(Player var0) {
      var0.getInventory().clear();
      var0.getInventory().setArmorContents(null);
      var0.getInventory().setItemInOffHand(new ItemStack(Material.AIR));

      for (PotionEffect var2 : var0.getActivePotionEffects()) {
         var0.removePotionEffect(var2.getType());
      }
   }
}
