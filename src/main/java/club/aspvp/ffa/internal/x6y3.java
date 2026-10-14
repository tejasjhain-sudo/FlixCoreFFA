package club.aspvp.ffa.internal;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public final class x6y3 {
   public final p en;
   private final u eo;
   private final m8p0 ep;
   private final a eq;
   private final Map<UUID, String> er = new ConcurrentHashMap<>();
   private final Set<UUID> es = ConcurrentHashMap.newKeySet();
   private final Set<UUID> et = ConcurrentHashMap.newKeySet();

   public x6y3(p var1, u var2, m8p0 var3, a var4) {
      this.en = var1;
      this.eo = var2;
      this.ep = var3;
      this.eq = var4;
   }

   public final void d(Player var1) {
      String var2;
      if ((var2 = this.er.get(var1.getUniqueId())) != null) {
         this.x7q9(var1, var2, false);
      }
   }

   public final void h1s7(Player var1, String var2) {
      this.es.remove(var1.getUniqueId());
      this.x7q9(var1, var2, true);
   }

   public final void z4c9(Player var1, String var2) {
      this.es.add(var1.getUniqueId());
      this.er.put(var1.getUniqueId(), var2.toLowerCase());
      this.eq.v8m5(var1, this.f(var1)).or(() -> this.ep.e7z1(this.f(var1))).ifPresentOrElse(var2x -> this.ep.r5n0(var1, var2x), () -> i(var1));
      this.x7q9(var1, var2, false);
   }

   private void x7q9(Player var1, String var2, boolean var3) {
      if (var2 != null && !var2.isBlank()) {
         this.er.put(var1.getUniqueId(), var2.toLowerCase());
      }

      if (var3) {
         this.ep.e7z1(this.f(var1)).ifPresentOrElse(var2x -> this.ep.r5n0(var1, var2x), () -> i(var1));
      }

      FileConfiguration var5;
      var3 = x7q9((var5 = this.en.h("kiteditor.yml")).getInt("size", 27));
      Inventory var9 = Bukkit.createInventory(
         new p6d2(h1s7.ba, null), var3, this.eo.u2q5(var1, this.eo.m4z1(var5.getString("title", "&8Kit Editor"), Map.of("kit", this.f(var1))))
      );
      this.ab2c(var1, var9, var5.getConfigurationSection("filler"));
      this.ab2c(var1, var9, var5);
      this.x7q9(var1, var9, var5);
      this.m4z1(var1, var9, var5.getConfigurationSection("buttons"));
      InventoryHolder var6;
      p6d2 var7;
      if ((var6 = var1.getOpenInventory().getTopInventory().getHolder()) instanceof p6d2
         && ((var7 = (p6d2)var6).aW == h1s7.ba || var7.aW == h1s7.bb || var7.aW == h1s7.bc)) {
         this.et.add(var1.getUniqueId());
      }

      var1.openInventory(var9);
   }

   public final void e(Player var1) {
      String var2 = this.f(var1);
      if (this.es.contains(var1.getUniqueId())) {
         this.eq.ab2c(var1, var2, this.ep);
      } else {
         this.ep.ab2c(var2, var1);
      }
   }

   public final String f(Player var1) {
      return this.er.getOrDefault(var1.getUniqueId(), "");
   }

   public final boolean g(Player var1) {
      return this.es.contains(var1.getUniqueId());
   }

   public final void h(Player var1) {
      if (this.g(var1)) {
         this.eq.v8m5(var1, this.f(var1)).or(() -> this.ep.e7z1(this.f(var1))).ifPresent(var2 -> this.ep.r5n0(var1, var2));
      } else {
         this.ep.e7z1(this.f(var1)).ifPresent(var2 -> this.ep.r5n0(var1, var2));
      }
   }

   public static void i(Player var0) {
      var0.setItemOnCursor(new ItemStack(Material.AIR));
      var0.getInventory().clear();
      var0.getInventory().setArmorContents(null);
      var0.getInventory().setItemInOffHand(new ItemStack(Material.AIR));

      for (PotionEffect var2 : var0.getActivePotionEffects()) {
         var0.removePotionEffect(var2.getType());
      }

      var0.updateInventory();
   }

   private void j(Player var1) {
      this.er.remove(var1.getUniqueId());
      this.es.remove(var1.getUniqueId());
      this.et.remove(var1.getUniqueId());
   }

   public final void k(Player var1) {
      this.et.add(var1.getUniqueId());
   }

   public final boolean l(Player var1) {
      if (this.et.remove(var1.getUniqueId())) {
         return false;
      } else if (!this.er.containsKey(var1.getUniqueId())) {
         return false;
      } else {
         var1.setItemOnCursor(new ItemStack(Material.AIR));
         i(var1);
         this.n(var1);
         this.j(var1);
         return true;
      }
   }

   public final void m(Player var1) {
      InventoryHolder var2;
      p6d2 var3;
      if ((var2 = var1.getOpenInventory().getTopInventory().getHolder()) instanceof p6d2 && (var3 = (p6d2)var2).aW == h1s7.ba) {
         this.x7q9(var1, var1.getOpenInventory().getTopInventory(), this.en.h("kiteditor.yml"));
      }
   }

   public final String k2w6(int var1) {
      return ab2c(this.en.h("kiteditor.yml"), var1);
   }

   public final boolean r5n0(int var1) {
      ConfigurationSection var2;
      return (var2 = this.en.h("kiteditor.yml").getConfigurationSection("offhand_slot")) != null && var2.getInt("slot", -1) == var1;
   }

   private void m4z1(Player var1, Inventory var2, ConfigurationSection var3) {
      if (var3 != null) {
         for (String var5 : var3.getKeys(false)) {
            ConfigurationSection var6;
            if ((var6 = var3.getConfigurationSection(var5)) != null) {
               ItemStack var7 = this.ab2c(
                  var1, ab2c(var6.getString("material"), Material.STONE), var6.getString("display_name", var5), var6.getStringList("lore")
               );
               var2.setItem(var6.getInt("slot", 0), var7);
            }
         }
      }
   }

   private void ab2c(Player var1, Inventory var2, FileConfiguration var3) {
      ItemStack[] var4 = var1.getInventory().getArmorContents();
      this.ab2c(var1, var2, var3, "helmet", var4.length > 3 ? var4[3] : null);
      this.ab2c(var1, var2, var3, "chestplate", var4.length > 2 ? var4[2] : null);
      this.ab2c(var1, var2, var3, "leggings", var4.length > 1 ? var4[1] : null);
      this.ab2c(var1, var2, var3, "boots", var4.length > 0 ? var4[0] : null);
   }

   public void ab2c(Player var1, Inventory var2, FileConfiguration var3, String var4, ItemStack var5) {
      ConfigurationSection var7;
      if ((var7 = var3.getConfigurationSection("armor_slots." + var4)) != null) {
         ItemStack var6 = var5 != null && !var5.getType().isAir()
            ? this.ab2c(var1, var5, var7.getString("display_name", var4), var7.getStringList("filled_lore"))
            : this.ab2c(
               var1, ab2c(var7.getString("empty_material"), Material.ARMOR_STAND), var7.getString("display_name", var4), var7.getStringList("empty_lore")
            );
         var2.setItem(var7.getInt("slot", 0), var6);
      }
   }

   private void x7q9(Player var1, Inventory var2, FileConfiguration var3) {
      ConfigurationSection var7;
      if ((var7 = var3.getConfigurationSection("offhand_slot")) != null) {
         ItemStack var4;
         List var5 = (var4 = var1.getInventory().getItemInOffHand()) != null && !var4.getType().isAir()
            ? var7.getStringList("filled_lore")
            : var7.getStringList("empty_lore");
         ItemStack var6 = var4 != null && !var4.getType().isAir()
            ? this.ab2c(var1, var4, var7.getString("display_name", "Offhand"), var5)
            : this.ab2c(var1, ab2c(var7.getString("empty_material"), Material.LIGHT_GRAY_DYE), var7.getString("display_name", "Offhand"), var5);
         var2.setItem(var7.getInt("slot", 0), var6);
      }
   }

   private void ab2c(Player var1, Inventory var2, ConfigurationSection var3) {
      if (var3 != null && var3.getBoolean("enabled", false)) {
         ItemStack var4 = this.ab2c(var1, ab2c(var3.getString("material"), Material.BLACK_STAINED_GLASS_PANE), var3.getString("display_name", " "), List.of());

         for (int var5 = 0; var5 < var2.getSize(); var5++) {
            var2.setItem(var5, var4);
         }
      }
   }

   private ItemStack ab2c(Player var1, Material var2, String var3, List<String> var4) {
      ItemStack var5 = new ItemStack(var2);
      return this.ab2c(var1, var5, var3, var4);
   }

   private ItemStack ab2c(Player var1, ItemStack var2, String var3, List<String> var4) {
      ItemMeta var5;
      if ((var5 = (var2 = var2.clone()).getItemMeta()) != null) {
         var5.setDisplayName(this.eo.u2q5(var1, var3 == null ? "" : var3));
         var5.setLore(var4.stream().map(var2x -> this.eo.u2q5(var1, var2x)).toList());
         var2.setItemMeta(var5);
      }

      return var2;
   }

   private static int x7q9(int var0) {
      return Math.max(9, Math.min(54, (var0 + 8) / 9 * 9));
   }

   private static Material ab2c(String var0, Material var1) {
      if (var0 == null) {
         return var1;
      } else {
         try {
            return Material.valueOf(var0.toUpperCase());
         } catch (IllegalArgumentException var2) {
            return var1;
         }
      }
   }

   public static String ab2c(FileConfiguration var0, int var1) {
      ConfigurationSection var5;
      if ((var5 = var0.getConfigurationSection("armor_slots")) == null) {
         return null;
      } else {
         for (String var3 : var5.getKeys(false)) {
            ConfigurationSection var4;
            if ((var4 = var5.getConfigurationSection(var3)) != null && var4.getInt("slot", -1) == var1) {
               return var3;
            }
         }

         return null;
      }
   }

   public void ab2c(Player var1, String var2, ItemStack var3) {
      ItemStack[] var4 = var1.getInventory().getArmorContents();
      var3 = q8v3(var3);
      var2 = var2.toLowerCase();
      switch (var2) {
         case "helmet":
            var4[3] = var3;
            break;
         case "chestplate":
            var4[2] = var3;
            break;
         case "leggings":
            var4[1] = var3;
            break;
         case "boots":
            var4[0] = var3;
            break;
         default:
            return;
      }

      var1.getInventory().setArmorContents(var4);
      var1.updateInventory();
   }

   public static ItemStack k2w6(Player var0, String var1) {
      ItemStack[] var3 = var0.getInventory().getArmorContents();
      var1 = var1.toLowerCase();
      switch (var1) {
         case "helmet":
            if (var3.length > 3) {
               return var3[3];
            }

            return null;
         case "chestplate":
            if (var3.length > 2) {
               return var3[2];
            }

            return null;
         case "leggings":
            if (var3.length > 1) {
               return var3[1];
            }

            return null;
         case "boots":
            if (var3.length > 0) {
               return var3[0];
            }

            return null;
         default:
            return null;
      }
   }

   public static ItemStack q8v3(ItemStack var0) {
      return var0 != null && !var0.getType().isAir() ? var0.clone() : new ItemStack(Material.AIR);
   }

   public ItemStack k2w6(ItemStack var1) {
      return r5n0(var1) ? null : var1.clone();
   }

   public static boolean r5n0(ItemStack var0) {
      return var0 == null || var0.getType().isAir();
   }

   private void n(Player var1) {
      ConfigurationSection var2 = this.u2q5("kiteditor-leave");
      p var3 = this.en;
      FileConfiguration var13 = this.en.gt.getConfig();
      if (var2 == null ? var13.getBoolean("leave_messages.enabled", true) : var2.getBoolean("enabled", true)) {
         String var4 = var2 == null ? var13.getString("leave_messages.title", "") : var2.getString("title", "");
         String var5 = var2 == null ? var13.getString("leave_messages.subtitle", "") : var2.getString("subtitle", "");
         if (var4 != null && !var4.isBlank() || var5 != null && !var5.isBlank()) {
            long var7 = var2 == null ? var13.getLong("leave_messages.fade_in", 5L) : var2.getLong("fade-in", 5L);
            long var9 = var2 == null ? var13.getLong("leave_messages.stay", 25L) : var2.getLong("stay", 25L);
            long var11 = var2 == null ? var13.getLong("leave_messages.fade_out", 5L) : var2.getLong("fade-out", 5L);
            var4 = var4 == null ? "" : var4;
            u var14 = this.eo;
            Component var10001 = this.eo.m4z1(var1, var4, true);
            var4 = var5 == null ? "" : var5;
            u var15 = this.eo;
            var1.showTitle(
               Title.title(
                  var10001,
                  this.eo.m4z1(var1, var4, true),
                  Times.times(Duration.ofMillis(var7 * 50L), Duration.ofMillis(var9 * 50L), Duration.ofMillis(var11 * 50L))
               )
            );
         }
      }
   }

   private ConfigurationSection u2q5(String var1) {
      FileConfiguration var2;
      ConfigurationSection var3;
      if ((var3 = (var2 = this.en.p6d2("titles.yml", "titles")) == null ? null : var2.getConfigurationSection("titles." + var1)) != null) {
         return var3;
      } else {
         return (var2 = this.en.h("titles.yml")) == null ? null : var2.getConfigurationSection("titles." + var1);
      }
   }
}
