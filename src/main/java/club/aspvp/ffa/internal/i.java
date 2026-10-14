package club.aspvp.ffa.internal;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class i {
   private final p fL;
   private final u fM;
   public final Map<UUID, i.ab2c> fN = new ConcurrentHashMap<>();

   public i(p var1, u var2) {
      this.fL = var1;
      this.fM = var2;
   }

   public final void ac(Player var1) {
      FileConfiguration var2;
      int var3 = x7q9((var2 = this.fL.h("totempractice.yml")).getInt("gui.size", 27));
      Inventory var4 = Bukkit.createInventory(new p6d2(h1s7.bd, null), var3, this.fM.u2q5(var1, var2.getString("gui.title", "&8Totem Practice")));
      i.ab2c var5 = new i.ab2c(0, t9b4(var3));
      this.fN.put(var1.getUniqueId(), var5);
      this.x7q9(var1, var4, var5.fP);
      var1.openInventory(var4);
   }

   public final void ab2c(Player var1, Inventory var2, int var3) {
      i.ab2c var4;
      if ((var4 = this.fN.get(var1.getUniqueId())) != null) {
         FileConfiguration var5 = this.fL.h("totempractice.yml");
         if (var3 != var4.fP) {
            if (var5.getBoolean("game.close-on-miss", true)) {
               var1.closeInventory();
            }
         } else {
            var3 = var4.fO + var5.getInt("game.points-per-click", 1);
            int var7 = t9b4(var2.getSize());
            this.fN.put(var1.getUniqueId(), new i.ab2c(var3, var7));
            var2.clear();
            this.x7q9(var1, var2, var7);
            if (var5.getBoolean("game.show-score-actionbar", true)) {
               String var8 = "&aTotem Practice &8| &fScore: &e" + var3;
               var1.sendActionBar(this.fM.m4z1(var1, var8, true));
            }

            if (var5.getBoolean("game.play-sound", true)) {
               var1.playSound(var1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.7F, 1.4F);
            }
         }
      }
   }

   private void x7q9(Player var1, Inventory var2, int var3) {
      FileConfiguration var4 = this.fL.h("totempractice.yml");
      ItemStack var5;
      ItemMeta var6;
      if ((var6 = (var5 = new ItemStack(ab2c(var4.getString("gui.totem-item"), Material.TOTEM_OF_UNDYING))).getItemMeta()) != null) {
         var6.setDisplayName(this.fM.u2q5(var1, var4.getString("gui.totem-name", "&6&lTap the Totem!")));
         var6.setLore(var4.getStringList("gui.totem-lore").stream().map(var2x -> this.fM.u2q5(var1, var2x)).toList());
         var5.setItemMeta(var6);
      }

      var2.setItem(var3, var5);
   }

   private static int t9b4(int var0) {
      return ThreadLocalRandom.current().nextInt(Math.max(1, var0));
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

   static record ab2c(int fO, int fP) {
   }
}
