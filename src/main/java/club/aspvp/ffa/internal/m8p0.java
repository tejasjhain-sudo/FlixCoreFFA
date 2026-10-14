package club.aspvp.ffa.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public final class m8p0 {
   private final p eu;
   private final v ev;
   public final Map<String, l> ew = new ConcurrentHashMap<>();

   public m8p0(p var1, v var2) {
      this.eu = var1;
      this.ev = var2;
   }

   public final void p() {
      this.ew.clear();
      ConfigurationSection var1;
      if ((var1 = this.eu.h("kits.yml").getConfigurationSection("kits")) != null) {
         for (String var3 : var1.getKeys(false)) {
            ConfigurationSection var4;
            if ((var4 = var1.getConfigurationSection(var3)) != null) {
               this.ew.put(var3.toLowerCase(Locale.ROOT), l.x7q9(var3, var4));
            } else {
               y.w7k3("KIT", "Skipped malformed kit entry '" + var3 + "'.");
            }
         }
      }

      y.w7k3("KIT", "Loaded " + this.ew.size() + " global kits.");
   }

   public final void q() {
      String var1 = this.c2f4();
      if (this.s6h0()) {
         this.ev.z4c9("kits.yml", var1);
      }
   }

   private String c2f4() {
      FileConfiguration var1;
      (var1 = this.eu.h("kits.yml")).set("kits", null);
      ConfigurationSection var2 = var1.createSection("kits");
      Iterator var3 = this.ew.values().iterator();

      while (var3.hasNext()) {
         l var4;
         (var4 = (l)var3.next()).k2w6(var2.createSection(var4.V()));
      }

      this.eu.i("kits.yml");
      return var1.saveToString();
   }

   private boolean s6h0() {
      p var1 = this.eu;
      return this.eu.gt.getConfig().getBoolean("settings.store-kits-in-database", false)
         || this.eu
            .h("database.yml")
            .getBoolean("snapshots.save-kits-and-arenas", this.eu.h("database.yml").getBoolean("storage.save-kits-and-arenas-snapshot", true));
   }

   public final Optional<l> e7z1(String var1) {
      return Optional.ofNullable(this.ew.get(ab.r(var1)));
   }

   public final Optional<l> r() {
      p var1 = this.eu;
      String var2 = this.eu.gt.getConfig().getString("settings.default-kit", "sword");
      return this.e7z1(var2).or(() -> this.e7z1("sword")).or(() -> this.ew.values().stream().min(Comparator.comparing(l::V)));
   }

   public final String s() {
      return this.r().map(l::V).orElse("");
   }

   public final Collection<l> t() {
      return this.ew.values();
   }

   public final boolean l9m6(String var1) {
      if (!(var1 = ab.r(var1)).isBlank() && !this.ew.containsKey(var1)) {
         l var2;
         l var10000 = var2 = new l(var1);
         String var4 = "&a" + var1;
         var10000.displayName = var4;
         ItemStack var6 = new ItemStack(Material.IRON_SWORD);
         var2.n = var6;
         this.ew.put(var1, var2);
         this.q();
         return true;
      } else {
         return false;
      }
   }

   public final l ab2c(String var1, Player var2) {
      l var3 = this.x7q9(var1, var2);
      this.ew.put(var3.V(), var3);
      this.q();
      y.w7k3("KIT", "Saved global kit '" + var3.V() + "' from " + var2.getName() + ".");
      return var3;
   }

   public final l x7q9(String var1, Player var2) {
      var1 = ab.r(var1);
      l var3;
      l var10000 = var3 = new l(var1);
      String var4 = "&a" + var1;
      var10000.displayName = var4;
      ItemStack var6 = t9b4(var2.getInventory().getItemInMainHand());
      var3.n = var6;
      ArrayList var7 = new ArrayList<>(Arrays.asList(var2.getInventory().getStorageContents()));
      var3.o = var7;
      ArrayList var8 = new ArrayList<>(Arrays.asList(var2.getInventory().getArmorContents()));
      var3.p = var8;
      ItemStack var9 = var2.getInventory().getItemInOffHand().clone();
      var3.q = var9;
      ArrayList var10 = new ArrayList(var2.getActivePotionEffects());
      var3.r = var10;
      return var3;
   }

   public final boolean o3w8(String var1) {
      if (this.ew.remove(ab.r(var1)) == null) {
         return false;
      } else {
         this.q();
         return true;
      }
   }

   public final void r5n0(Player var1, l var2) {
      var1.getInventory().clear();
      var1.getInventory().setArmorContents(ab2c(var2.p, 4));
      var1.getInventory().setStorageContents(ab2c(var2.o, 36));
      var1.getInventory().setItemInOffHand(var2.q == null ? new ItemStack(Material.AIR) : var2.q.clone());

      for (PotionEffect var4 : var1.getActivePotionEffects()) {
         var1.removePotionEffect(var4.getType());
      }

      for (PotionEffect var6 : var2.r) {
         var1.addPotionEffect(var6);
      }

      var1.updateInventory();
   }

   private static ItemStack t9b4(ItemStack var0) {
      return var0 != null && !var0.getType().isAir() ? var0.clone() : new ItemStack(Material.IRON_SWORD);
   }

   public static ArrayList<ItemStack> q8v3(Collection<ItemStack> var0) {
      ArrayList var1 = new ArrayList();

      for (ItemStack var2 : var0) {
         var1.add(var2 == null ? null : var2.clone());
      }

      return var1;
   }

   private static ItemStack[] ab2c(Collection<ItemStack> var0, int var1) {
      ItemStack[] var2 = new ItemStack[var1];
      int var3 = 0;

      for (ItemStack var4 : var0) {
         if (var3 >= var1) {
            break;
         }

         var2[var3++] = var4 == null ? null : var4.clone();
      }

      return var2;
   }
}
