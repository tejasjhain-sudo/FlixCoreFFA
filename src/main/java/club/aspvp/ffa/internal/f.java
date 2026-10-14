package club.aspvp.ffa.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class f {
   public final p fa;
   private final u fb;

   public f(p var1, u var2) {
      this.fa = var1;
      this.fb = var2;
   }

   public final void N(Player var1) {
      if (this.fa.x7q9("spawn-items", true) && this.fa.h("spawnitem.yml").getBoolean("enabled", true)) {
         if (this.fa.h("spawnitem.yml").getBoolean("clear-configured-slots", true)) {
            this.O(var1);
         }

         Iterator var2 = this.A().iterator();

         while (var2.hasNext()) {
            f.ab2c var3;
            String var4;
            if ((var3 = (f.ab2c)var2.next()).ab2c("enabled", true)
               && ((var4 = var3.C()).isBlank() || var1.hasPermission(var4))
               && q8v3(var1, var3.ab2c("slot", -1))) {
               if (x6y3(var3.B())) {
                  var1.getInventory().setItem(var3.ab2c("slot", -1), null);
               } else {
                  var1.getInventory().setItem(var3.ab2c("slot", -1), this.ab2c(var1, var3));
               }
            }
         }

         var1.updateInventory();
      }
   }

   private void O(Player var1) {
      Iterator var2 = this.A().iterator();

      while (var2.hasNext()) {
         f.ab2c var3;
         ItemStack var4;
         if ((var3 = (f.ab2c)var2.next()).ab2c("enabled", true)
            && q8v3(var1, var3.ab2c("slot", -1))
            && ((var4 = var1.getInventory().getItem(var3.ab2c("slot", -1))) == null || var4.isSimilar(this.ab2c(var1, var3)))) {
            var1.getInventory().setItem(var3.ab2c("slot", -1), null);
         }
      }
   }

   public final boolean q8v3(Player var1, ItemStack var2) {
      if (this.fa.x7q9("spawn-items", true) && this.fa.h("spawnitem.yml").getBoolean("enabled", true) && var2 != null && !var2.getType().isAir()) {
         Iterator var3 = this.A().iterator();

         while (var3.hasNext()) {
            f.ab2c var4;
            if ((var4 = (f.ab2c)var3.next()).ab2c("enabled", true) && var2.isSimilar(this.ab2c(var1, var4))) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public ItemStack ab2c(Player var1, f.ab2c var2) {
      ItemStack var3;
      if ((var3 = var2.F()) != null) {
         return var3;
      } else {
         Material var7 = ab2c(var2.D(), Material.STONE);
         ItemStack var4;
         ItemMeta var5;
         if ((var5 = (var4 = new ItemStack(var7)).getItemMeta()) != null) {
            var5.setDisplayName(this.fb.u2q5(var1, var2.m8p0(var7.name())));
            List var6 = var2.E().stream().map(var2x -> this.fb.u2q5(var1, var2x)).toList();
            var5.setLore(var6);
            var4.setItemMeta(var5);
         }

         return var4;
      }
   }

   public static boolean x6y3(String var0) {
      String var1;
      return (var1 = var0 == null ? "" : var0.trim().toLowerCase()).equals("ffa leave") || var1.equals("/ffa leave");
   }

   private static Material ab2c(String var0, Material var1) {
      if (var0 == null) {
         return var1;
      } else {
         try {
            return Material.valueOf(var0.trim().toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var2) {
            return var1;
         }
      }
   }

   private static boolean q8v3(Player var0, int var1) {
      return var1 >= 0 && var1 < var0.getInventory().getSize();
   }

   public List<f.ab2c> A() {
      FileConfiguration var1 = this.fa.h("spawnitem.yml");
      ArrayList var2 = new ArrayList();
      if (!var1.isList("spawn-items")) {
         ConfigurationSection var7;
         if ((var7 = var1.getConfigurationSection("items")) == null) {
            return var2;
         } else {
            for (String var4 : var7.getKeys(false)) {
               ConfigurationSection var5;
               if ((var5 = var7.getConfigurationSection(var4)) != null) {
                  var2.add(f.ab2c.ab2c(var4, var5));
               }
            }

            return var2;
         }
      } else {
         for (Map var3 : var1.getMapList("spawn-items")) {
            var2.add(f.ab2c.x7q9(var3));
         }

         return var2;
      }
   }

   public static final class ab2c {
      private final String fc;
      private final ConfigurationSection fd;
      private final Map<?, ?> fe;

      private ab2c(String var1, ConfigurationSection var2, Map<?, ?> var3) {
         this.fc = var1;
         this.fd = var2;
         this.fe = var3;
      }

      static f.ab2c ab2c(String var0, ConfigurationSection var1) {
         return new f.ab2c(var0, var1, null);
      }

      static f.ab2c x7q9(Map<?, ?> var0) {
         return new f.ab2c(ab2c(var0.get("name"), "spawn-item"), null, var0);
      }

      public String B() {
         return this.r5n0("command", "");
      }

      public String C() {
         return this.r5n0("permission", "");
      }

      String D() {
         return this.r5n0("material", "STONE");
      }

      String m8p0(String var1) {
         return this.r5n0("display_name", this.r5n0("display-name", var1));
      }

      List<String> E() {
         if (this.fd != null) {
            return this.fd.getStringList("lore");
         } else {
            Object var1;
            if (!((var1 = this.fe.get("lore")) instanceof List)) {
               return List.of();
            } else {
               var1 = (List)var1;
               ArrayList var2 = new ArrayList();

               for (Object var3 : var1) {
                  var2.add(String.valueOf(var3));
               }

               return var2;
            }
         }
      }

      ItemStack F() {
         if (this.fd != null) {
            ItemStack var7;
            return (var7 = this.fd.getItemStack("item")) == null ? null : var7.clone();
         } else {
            Object var1;
            if ((var1 = this.fe.get("item")) instanceof ItemStack) {
               ItemStack var6;
               return (var6 = (ItemStack)var1).clone();
            } else {
               if (var1 instanceof Map var3) {
                  LinkedHashMap var2 = new LinkedHashMap();
                  var3.forEach((var1x, var2x) -> var2.put(String.valueOf(var1x), var2x));
                  if ((var1 = ConfigurationSerialization.deserializeObject(var2)) instanceof ItemStack) {
                     ItemStack var5;
                     return (var5 = (ItemStack)var1).clone();
                  }
               }

               return null;
            }
         }
      }

      public boolean ab2c(String var1, boolean var2) {
         if (this.fd != null) {
            return this.fd.getBoolean(var1, true);
         } else {
            Object var3;
            return (var3 = this.fe.get(var1)) == null ? true : Boolean.parseBoolean(String.valueOf(var3));
         }
      }

      public int ab2c(String var1, int var2) {
         if (this.fd != null) {
            return this.fd.getInt(var1, -1);
         } else {
            Object var4;
            if ((var4 = this.fe.get(var1)) instanceof Number) {
               Number var5;
               return (var5 = (Number)var4).intValue();
            } else {
               try {
                  return var4 == null ? -1 : Integer.parseInt(String.valueOf(var4));
               } catch (NumberFormatException var3) {
                  return -1;
               }
            }
         }
      }

      private String r5n0(String var1, String var2) {
         return this.fd != null ? this.fd.getString(var1, var2) : ab2c(this.fe.get(var1), var2);
      }

      private static String ab2c(Object var0, String var1) {
         return var0 == null ? var1 : String.valueOf(var0);
      }
   }
}
