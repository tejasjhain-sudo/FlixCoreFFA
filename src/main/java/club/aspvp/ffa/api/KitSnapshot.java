package club.aspvp.ffa.api;

import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public record KitSnapshot(String l, String m, ItemStack n, List<ItemStack> o, List<ItemStack> p, ItemStack q, List<PotionEffect> r) {
   public KitSnapshot(String l, String m, ItemStack n, List<ItemStack> o, List<ItemStack> p, ItemStack q, List<PotionEffect> r) {
      n = ab2c(n);
      o = ab2c(o);
      p = ab2c(p);
      q = ab2c(q);
      r = List.copyOf(r);
      this.l = l;
      this.m = m;
      this.n = n;
      this.o = o;
      this.p = p;
      this.q = q;
      this.r = r;
   }

   private static List<ItemStack> ab2c(List<ItemStack> var0) {
      return var0.stream().map(KitSnapshot::ab2c).toList();
   }

   private static ItemStack ab2c(ItemStack var0) {
      return var0 == null ? null : var0.clone();
   }

   public final String id() {
      return this.l;
   }

   public final String displayName() {
      return this.m;
   }

   public final ItemStack icon() {
      return this.n;
   }

   public final List<ItemStack> contents() {
      return this.o;
   }

   public final List<ItemStack> armor() {
      return this.p;
   }

   public final ItemStack offhand() {
      return this.q;
   }

   public final List<PotionEffect> effects() {
      return this.r;
   }
}
