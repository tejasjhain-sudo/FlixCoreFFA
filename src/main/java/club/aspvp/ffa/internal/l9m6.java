package club.aspvp.ffa.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public final class l9m6 {
   public static final List<String> dz = List.of("helmet", "chestplate", "leggings", "boots");
   public final p dA;
   private final u dB;
   public final x6y3 dC;
   public final Map<UUID, l9m6.ab2c> dD = new ConcurrentHashMap<>();

   public l9m6(p var1, u var2, x6y3 var3) {
      this.dA = var1;
      this.dB = var2;
      this.dC = var3;
   }

   public void ab2c(Player var1, String var2, boolean var3) {
      if (var2 != null && m4z1(k2w6(var1, var2))) {
         ConfigurationSection var4;
         if ((var4 = this.dA.h("armor_trimmer.yml").getConfigurationSection("selection_gui")) != null) {
            l9m6.ab2c var6;
            if ((var6 = var3 ? this.q8v3(var1, var2) : this.dD.get(var1.getUniqueId())) == null || !var6.dE.equals(var2)) {
               var6 = this.q8v3(var1, var2);
            }

            this.dD.put(var1.getUniqueId(), var6);
            Inventory var5 = Bukkit.createInventory(
               new p6d2(h1s7.bc, var2),
               x7q9(var4.getInt("size", 27)),
               this.dB.u2q5(var1, var4.getString("title", "&8Armor Trim Selector").replace("%armor%", var2))
            );
            this.ab2c(var1, var5, var4.getConfigurationSection("filler"));
            var5.setItem(
               var4.getInt("pattern_selector.slot", 10), this.ab2c(var1, var4.getConfigurationSection("pattern_selector"), m8p0(), var6.dG, "%pattern%")
            );
            var5.setItem(
               var4.getInt("material_selector.slot", 16), this.ab2c(var1, var4.getConfigurationSection("material_selector"), a(), var6.dH, "%material%")
            );
            var5.setItem(var4.getInt("preview_item.slot", 13), this.ab2c(var1, var4.getConfigurationSection("preview_item"), var6));
            ConfigurationSection var7;
            if ((var7 = var4.getConfigurationSection("back_button")) != null) {
               var5.setItem(
                  var7.getInt("slot", 22),
                  this.ab2c(var1, ab2c(var7.getString("material"), Material.ARROW), var7.getString("display_name", "&cBack"), var7.getStringList("lore"))
               );
            }

            this.ab2c(var1, var5);
         }
      } else {
         this.dC.d(var1);
      }
   }

   private l9m6.ab2c q8v3(Player var1, String var2) {
      ItemStack var6 = k2w6(var1, var2).clone();
      int var3 = 0;
      int var4 = 0;
      ItemMeta var5;
      ArmorMeta var7;
      if ((var5 = var6.getItemMeta()) instanceof ArmorMeta && (var7 = (ArmorMeta)var5).hasTrim()) {
         ArmorTrim var8 = var7.getTrim();
         var3 = Math.max(0, m8p0().indexOf(var8.getPattern()));
         var4 = Math.max(0, a().indexOf(var8.getMaterial()));
      }

      return new l9m6.ab2c(var2, var6, var3, var4);
   }

   private void ab2c(Player var1, Inventory var2) {
      InventoryHolder var3;
      p6d2 var5;
      if ((var3 = var1.getOpenInventory().getTopInventory().getHolder()) instanceof p6d2
         && ((var5 = (p6d2)var3).aW == h1s7.ba || var5.aW == h1s7.bb || var5.aW == h1s7.bc)) {
         this.dC.k(var1);
      }

      var1.openInventory(var2);
   }

   private ItemStack ab2c(Player var1, ConfigurationSection var2, List<?> var3, int var4, String var5) {
      Object var6 = var3.get(var4);
      String var7 = this.ab2c(var6);
      var6 = var6 instanceof TrimPattern var8 ? this.ab2c(var8) : this.ab2c((TrimMaterial)var6);
      ArrayList var12;
      (var12 = new ArrayList()).add(var2 == null ? "" : var2.getString("lore_header", ""));

      for (int var9 = 0; var9 < var3.size(); var9++) {
         String var10 = var2 == null
            ? (var9 == var4 ? "&a> " : "&7- ")
            : (var9 == var4 ? var2.getString("selected_prefix", "&a> ") : var2.getString("unselected_prefix", "&7- "));
         var12.add(var10 + this.ab2c(var3.get(var9)));
      }

      var12.add(var2 == null ? "" : var2.getString("lore_footer", ""));
      return this.ab2c(var1, (Material)var6, (var2 == null ? var5 : var2.getString("display_name", var5)).replace(var5, var7.toUpperCase(Locale.ROOT)), var12);
   }

   private ItemStack ab2c(Player var1, ConfigurationSection var2, l9m6.ab2c var3) {
      String var4 = x7q9(m8p0().get(var3.dG)).toUpperCase(Locale.ROOT);
      String var5 = x7q9(a().get(var3.dH)).toUpperCase(Locale.ROOT);
      List var6 = var2 == null
         ? List.of()
         : var2.getStringList("lore")
            .stream()
            .map(var4x -> var4x.replace("%pattern%", var4).replace("%material%", var5).replace("%armor%", d9r1(var3.dE)))
            .toList();
      return this.ab2c(
         var1,
         this.ab2c(var3.dF, var3),
         var2 == null ? "&fTrim Preview" : var2.getString("display_name", "&f%armor% Preview").replace("%armor%", d9r1(var3.dE)),
         var6
      );
   }

   public ItemStack ab2c(ItemStack var1, l9m6.ab2c var2) {
      ItemMeta var3;
      if ((var3 = (var1 = var1.clone()).getItemMeta()) instanceof ArmorMeta) {
         ArmorMeta var5;
         (var5 = (ArmorMeta)var3).setTrim(new ArmorTrim(a().get(var2.dH), m8p0().get(var2.dG)));
         var1.setItemMeta(var5);
      }

      return var1;
   }

   public static ItemStack k2w6(Player var0, String var1) {
      switch (var1) {
         case "helmet":
            return var0.getInventory().getHelmet();
         case "chestplate":
            return var0.getInventory().getChestplate();
         case "leggings":
            return var0.getInventory().getLeggings();
         case "boots":
            return var0.getInventory().getBoots();
         default:
            return null;
      }
   }

   public static void ab2c(Player var0, String var1, ItemStack var2) {
      switch (var1) {
         case "helmet":
            var0.getInventory().setHelmet(var2);
            break;
         case "chestplate":
            var0.getInventory().setChestplate(var2);
            break;
         case "leggings":
            var0.getInventory().setLeggings(var2);
            break;
         case "boots":
            var0.getInventory().setBoots(var2);
            break;
         default:
            return;
      }

      var0.updateInventory();
   }

   public static boolean m4z1(ItemStack var0) {
      return var0 != null && !var0.getType().isAir() && var0.getItemMeta() instanceof ArmorMeta;
   }

   public static boolean ab2c(Player var0, FileConfiguration var1) {
      return !var1.getBoolean("permissions.enabled", false) || var0.hasPermission(var1.getString("permissions.node", "ffa.kiteditor"));
   }

   public static List<TrimPattern> m8p0() {
      return List.of(
         TrimPattern.SENTRY,
         TrimPattern.DUNE,
         TrimPattern.COAST,
         TrimPattern.WILD,
         TrimPattern.WARD,
         TrimPattern.EYE,
         TrimPattern.VEX,
         TrimPattern.TIDE,
         TrimPattern.SNOUT,
         TrimPattern.RIB,
         TrimPattern.SPIRE,
         TrimPattern.WAYFINDER,
         TrimPattern.SHAPER,
         TrimPattern.SILENCE
      );
   }

   public static List<TrimMaterial> a() {
      return List.of(
         TrimMaterial.QUARTZ,
         TrimMaterial.IRON,
         TrimMaterial.NETHERITE,
         TrimMaterial.REDSTONE,
         TrimMaterial.COPPER,
         TrimMaterial.GOLD,
         TrimMaterial.EMERALD,
         TrimMaterial.DIAMOND,
         TrimMaterial.LAPIS,
         TrimMaterial.AMETHYST
      );
   }

   private Material ab2c(TrimPattern var1) {
      return ab2c(x7q9(var1).toUpperCase(Locale.ROOT) + "_ARMOR_TRIM_SMITHING_TEMPLATE", Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
   }

   private Material ab2c(TrimMaterial var1) {
      String var3 = x7q9(var1);
      switch (var3) {
         case "quartz":
            return Material.QUARTZ;
         case "iron":
            return Material.IRON_INGOT;
         case "netherite":
            return Material.NETHERITE_INGOT;
         case "redstone":
            return Material.REDSTONE;
         case "copper":
            return Material.COPPER_INGOT;
         case "gold":
            return Material.GOLD_INGOT;
         case "emerald":
            return Material.EMERALD;
         case "diamond":
            return Material.DIAMOND;
         case "lapis":
            return Material.LAPIS_LAZULI;
         case "amethyst":
            return Material.AMETHYST_SHARD;
         default:
            return Material.DIAMOND;
      }
   }

   private void ab2c(Player var1, Inventory var2, ConfigurationSection var3) {
      if (var3 != null && var3.getBoolean("enabled", true)) {
         ItemStack var4 = this.ab2c(var1, ab2c(var3.getString("material"), Material.BLACK_STAINED_GLASS_PANE), var3.getString("display_name", " "), List.of());

         for (int var5 = 0; var5 < var2.getSize(); var5++) {
            var2.setItem(var5, var4);
         }
      }
   }

   private ItemStack ab2c(Player var1, Material var2, String var3, List<String> var4) {
      return this.ab2c(var1, new ItemStack(var2), var3, var4);
   }

   private ItemStack ab2c(Player var1, ItemStack var2, String var3, List<String> var4) {
      ItemMeta var5;
      if ((var5 = (var2 = var2.clone()).getItemMeta()) != null) {
         var5.setDisplayName(this.dB.u2q5(var1, var3));
         var5.setLore(var4.stream().map(var2x -> this.dB.u2q5(var1, var2x)).toList());
         var2.setItemMeta(var5);
      }

      return var2;
   }

   public static int ab2c(int var0, int var1, int var2) {
      return Math.floorMod(var0 + var2, var1);
   }

   public static int w7k3(String var0) {
      switch (var0) {
         case "helmet":
            return 10;
         case "chestplate":
            return 12;
         case "leggings":
            return 14;
         case "boots":
            return 16;
         default:
            return 10;
      }
   }

   private static String d9r1(String var0) {
      return Character.toUpperCase(var0.charAt(0)) + var0.substring(1);
   }

   private static int x7q9(int var0) {
      return Math.max(9, Math.min(54, (var0 + 8) / 9 * 9));
   }

   private static Material ab2c(String var0, Material var1) {
      if (var0 == null) {
         return var1;
      } else {
         Material var2;
         return (var2 = Material.matchMaterial(var0.toUpperCase(Locale.ROOT))) == null ? var1 : var2;
      }
   }

   private String ab2c(Object var1) {
      TrimPattern var2;
      return var1 instanceof TrimPattern ? x7q9(var2 = (TrimPattern)var1) : x7q9((TrimMaterial)var1);
   }

   private static String x7q9(TrimPattern var0) {
      return Registry.TRIM_PATTERN.getKey(var0).getKey();
   }

   private static String x7q9(TrimMaterial var0) {
      return Registry.TRIM_MATERIAL.getKey(var0).getKey();
   }

   public static final class ab2c {
      public final String dE;
      public final ItemStack dF;
      public int dG;
      public int dH;

      ab2c(String var1, ItemStack var2, int var3, int var4) {
         this.dE = var1;
         this.dF = var2;
         this.dG = var3;
         this.dH = var4;
      }
   }
}
