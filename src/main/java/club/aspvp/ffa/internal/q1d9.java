package club.aspvp.ffa.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class q1d9 {
   private final p ek;
   private final u el;
   private final t em;
   public c n2x6;

   public q1d9(p var1, u var2, t var3) {
      this.ek = var1;
      this.el = var2;
      this.em = var3;
   }

   public final void a(Player var1) {
      ConfigurationSection var2;
      String var3 = (var2 = this.g1v7("ffa")).getString("menu_title", var2.getString("title", "&0&lFFA GAMEMODES"));
      Inventory var5 = this.ab2c(var1, h1s7.aY, null, var3, x7q9(var2.getInt("size", 36)));
      if ((var2 = var2.getConfigurationSection("items")) != null) {
         this.ab2c(var1, var5, var2, false);
         this.ab2c(var1, var5, var2, true);
      }

      var1.openInventory(var5);
   }

   public final void b(Player var1) {
      ConfigurationSection var2;
      String var3 = (var2 = this.g1v7("settings")).getString("menu_title", var2.getString("title", "&0&lPLAYER SETTINGS"));
      Inventory var5 = this.ab2c(var1, h1s7.be, null, var3, x7q9(var2.getInt("size", 27)));
      if ((var2 = var2.getConfigurationSection("items")) != null) {
         this.x7q9(var1, var5, var2, false);
         this.x7q9(var1, var5, var2, true);
      }

      var1.openInventory(var5);
   }

   public final void m4z1(Player var1, int var2) {
      ConfigurationSection var5;
      if ((var5 = this.m4z1(this.g1v7("settings"), var2)) != null && x7q9(var1, var5)) {
         String var3;
         if (!(var3 = var5.getString("setting", "")).isBlank() && this.n2x6 != null) {
            this.n2x6.w7k3(var1, var3);
         }

         for (String var4 : var5.getStringList("click_commands")) {
            this.f3y8(var1, var4);
         }

         if (!var5.getBoolean("close-after-click", false)) {
            this.em.ab2c(var1, () -> this.b(var1), 1L);
         }
      }
   }

   private ItemStack ab2c(Player var1, ItemStack var2, ConfigurationSection var3, Map<String, String> var4, String var5, List<String> var6) {
      ItemMeta var7;
      if ((var7 = (var2 = var2.clone()).getItemMeta()) != null) {
         var5 = var3 == null ? var5 : var3.getString("display_name", var3.getString("name", var5));
         List var9;
         if ((var9 = var3 == null ? var6 : var3.getStringList("lore")).isEmpty()) {
            var9 = var6;
         }

         var7.setDisplayName(this.el.u2q5(var1, this.el.m4z1(var5, var4)));
         ArrayList var12 = new ArrayList();

         for (String var13 : var9) {
            var12.add(this.el.u2q5(var1, this.el.m4z1(var13, var4)));
         }

         var7.setLore(var12);
         var2.setItemMeta(var7);
      }

      return var2;
   }

   public Inventory ab2c(Player var1, h1s7 var2, String var3, String var4, int var5) {
      return Bukkit.createInventory(new p6d2(var2, null), var5, this.el.u2q5(var1, var4));
   }

   public ConfigurationSection g1v7(String var1) {
      switch (var1) {
         case "ffa":
            return this.ek.h("ffa_menu.yml");
         case "kiteditor":
            return this.ek.h("kiteditor_menu.yml");
         case "settings":
            return this.ek.h("settings_menu.yml");
         default:
            return new YamlConfiguration();
      }
   }

   public static int x7q9(int var0) {
      return Math.max(9, Math.min(54, (var0 + 8) / 9 * 9));
   }

   private static Material ab2c(String var0, Material var1) {
      if (var0 != null && !var0.isBlank()) {
         try {
            return Material.valueOf(var0.toUpperCase());
         } catch (IllegalArgumentException var2) {
            return var1;
         }
      } else {
         return var1;
      }
   }

   public static boolean x7q9(Player var0, ConfigurationSection var1) {
      String var2;
      return (var2 = var1.getString("permission", "")).isBlank() || var0.hasPermission(var2) || var0.hasPermission("ffa.admin");
   }

   private static List<Integer> x7q9(ConfigurationSection var0, int var1) {
      ArrayList var2 = new ArrayList();
      int var3;
      if (var0.contains("slot") && (var3 = var0.getInt("slot", -1)) >= 0 && var3 < var1) {
         var2.add(var3);
      }

      Iterator var10 = var0.getList("slots", List.of()).iterator();

      while (var10.hasNext()) {
         String var6;
         if ((var6 = String.valueOf(var10.next()).trim()).matches("\\d+")) {
            int var9;
            if ((var9 = Integer.parseInt(var6)) >= 0 && var9 < var1) {
               var2.add(var9);
            }
         } else if (var6.matches("\\d+\\s*-\\s*\\d+")) {
            String[] var7;
            int var4 = Integer.parseInt((var7 = var6.split("-", 2))[0].trim());
            int var8 = Integer.parseInt(var7[1].trim());

            for (int var5 = Math.max(0, Math.min(var4, var8)); var5 <= Math.min(var1 - 1, Math.max(var4, var8)); var5++) {
               var2.add(var5);
            }
         }
      }

      return var2;
   }

   public ConfigurationSection m4z1(ConfigurationSection var1, int var2) {
      ConfigurationSection var3;
      if ((var3 = var1.getConfigurationSection("items")) == null) {
         return null;
      } else {
         int var6 = x7q9(var1.getInt("size", 36));

         for (String var5 : var3.getKeys(false)) {
            ConfigurationSection var8;
            if ((var8 = var3.getConfigurationSection(var5)) != null && var8.contains("slot") && x7q9(var8, var6).contains(var2)) {
               return var8;
            }
         }

         for (String var9 : var3.getKeys(false)) {
            ConfigurationSection var10;
            if ((var10 = var3.getConfigurationSection(var9)) != null && !var10.contains("slot") && x7q9(var10, var6).contains(var2)) {
               return var10;
            }
         }

         return null;
      }
   }

   public void ab2c(Player var1, Inventory var2, ConfigurationSection var3, boolean var4) {
      for (String var6 : var3.getKeys(false)) {
         ConfigurationSection var7;
         if ((var7 = var3.getConfigurationSection(var6)) != null && var7.contains("slot") == var4 && x7q9(var1, var7)) {
            ItemStack var9 = this.ab2c(
               var1,
               new ItemStack(ab2c(var7.getString("material"), Material.STONE)),
               var7,
               Map.of(),
               var7.getString("display_name", var7.getString("name", var6)),
               var7.getStringList("lore")
            );

            for (int var8 : x7q9(var7, var2.getSize())) {
               var2.setItem(var8, var9.clone());
            }
         }
      }
   }

   private void x7q9(Player var1, Inventory var2, ConfigurationSection var3, boolean var4) {
      for (String var6 : var3.getKeys(false)) {
         ConfigurationSection var7;
         if ((var7 = var3.getConfigurationSection(var6)) != null && var7.contains("slot") == var4 && x7q9(var1, var7)) {
            String var8 = var7.getString("setting", "");
            boolean var12;
            String var9 = (var12 = this.n2x6 == null || var8.isBlank() || this.n2x6.d9r1(var1, var8))
               ? var7.getString("enabled_material", var7.getString("material"))
               : var7.getString("disabled_material", var7.getString("material"));
            Map var13 = Map.of(
               "state",
               var12 ? var7.getString("enabled-text", "Enabled") : var7.getString("disabled-text", "Disabled"),
               "state_color",
               var12 ? var7.getString("enabled-color", "&a") : var7.getString("disabled-color", "&c"),
               "status",
               var12 ? var7.getString("enabled-text", "Enabled") : var7.getString("disabled-text", "Disabled"),
               "status_color",
               var12 ? var7.getString("enabled-color", "&a") : var7.getString("disabled-color", "&c")
            );
            ItemStack var10 = this.ab2c(
               var1,
               new ItemStack(ab2c(var9, Material.STONE)),
               var7,
               var13,
               var7.getString("display_name", var7.getString("name", var6)),
               var7.getStringList("lore")
            );

            for (int var14 : x7q9(var7, var2.getSize())) {
               var2.setItem(var14, var10.clone());
            }
         }
      }
   }

   public void f3y8(Player var1, String var2) {
      if (var2 != null && !var2.isBlank()) {
         String var3;
         if ((var3 = (var2 = var2.trim()).toLowerCase(Locale.ROOT)).equals("[close]")) {
            var1.closeInventory();
         } else if (var3.startsWith("[player]")) {
            this.em.ab2c(var1, () -> var1.performCommand(var2.substring(8).trim()), 1L);
         } else if (var3.startsWith("[console]")) {
            var2 = var2.substring(9).trim().replace("%player%", var1.getName());
            this.em.x7q9(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var2));
         } else if (var3.startsWith("[message]")) {
            u var10001 = this.el;
            var3 = var2.substring(9).trim();
            var1.sendMessage(var10001.m4z1(var1, var3, true));
         } else if (var3.startsWith("[sound]")) {
            if (this.n2x6 == null || this.n2x6.H(var1)) {
               String[] var7 = var2.substring(7).trim().split("\\s+");

               try {
                  Sound var10 = Sound.valueOf(var7[0].toUpperCase(Locale.ROOT));
                  float var4 = var7.length > 1 ? Float.parseFloat(var7[1]) : 1.0F;
                  float var8 = var7.length > 2 ? Float.parseFloat(var7[2]) : 1.0F;
                  var1.playSound(var1.getLocation(), var10, var4, var8);
               } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException var5) {
               }
            }
         }
      }
   }
}
