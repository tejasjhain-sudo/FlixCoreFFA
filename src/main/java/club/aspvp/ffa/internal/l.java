package club.aspvp.ffa.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public final class l {
   private final String gd;
   public String displayName;
   public ItemStack n;
   public List<ItemStack> o = new ArrayList<>();
   public List<ItemStack> p = new ArrayList<>();
   public ItemStack q;
   public List<PotionEffect> r = new ArrayList<>();

   public l(String var1) {
      this.gd = var1;
      this.displayName = var1;
   }

   public static l x7q9(String var0, ConfigurationSection var1) {
      l var2;
      (var2 = new l(var0)).displayName = var1.getString("display-name", var0);
      var2.n = var1.getItemStack("icon");
      var2.o = ab2c(var1, "contents", 36);
      var2.p = ab2c(var1, "armor", 4);
      var2.q = var1.getItemStack("offhand");
      var2.r = var1.getList("effects", new ArrayList());
      return var2;
   }

   public final void k2w6(ConfigurationSection var1) {
      var1.set("display-name", this.displayName);
      var1.set("icon", this.n);
      ab2c(var1, "contents", this.o);
      ab2c(var1, "armor", this.p);
      var1.set("offhand", this.q);
      var1.set("effects", this.r);
   }

   private static List<ItemStack> ab2c(ConfigurationSection var0, String var1, int var2) {
      ArrayList var3 = new ArrayList(Collections.nCopies(var2, null));
      ConfigurationSection var4;
      if ((var4 = var0.getConfigurationSection(var1)) != null) {
         for (String var10 : var4.getKeys(false)) {
            try {
               int var5;
               if ((var5 = Integer.parseInt(var10)) >= 0 && var5 < var2) {
                  var3.set(var5, var4.getItemStack(var10));
               }
            } catch (NumberFormatException var6) {
            }
         }

         return var3;
      } else {
         List var7 = var0.getList(var1, new ArrayList());

         for (int var9 = 0; var9 < Math.min(var2, var7.size()); var9++) {
            var3.set(var9, (ItemStack)var7.get(var9));
         }

         return var3;
      }
   }

   private static void ab2c(ConfigurationSection var0, String var1, List<ItemStack> var2) {
      var0.set(var1, null);
      var0 = var0.createSection(var1);

      for (int var5 = 0; var5 < var2.size(); var5++) {
         ItemStack var3;
         if ((var3 = (ItemStack)var2.get(var5)) != null && var3.getType() != Material.AIR) {
            var0.set(String.valueOf(var5), var3);
         }
      }
   }

   public final String V() {
      return this.gd;
   }

   public final String W() {
      return this.displayName;
   }

   public final void g(String var1) {
      this.displayName = var1;
   }

   public final ItemStack X() {
      return this.n;
   }

   public final void f3y8(ItemStack var1) {
      this.n = var1;
   }

   public final List<ItemStack> Y() {
      return this.o;
   }

   public final void t9b4(List<ItemStack> var1) {
      this.o = var1;
   }

   public final List<ItemStack> Z() {
      return this.p;
   }

   public final void f3y8(List<ItemStack> var1) {
      this.p = var1;
   }

   public final ItemStack aa() {
      return this.q;
   }

   public final void p6d2(ItemStack var1) {
      this.q = var1;
   }

   public final List<PotionEffect> ab() {
      return this.r;
   }

   public final void p6d2(List<PotionEffect> var1) {
      this.r = var1;
   }
}
