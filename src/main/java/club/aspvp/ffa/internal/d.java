package club.aspvp.ffa.internal;

import club.aspvp.ffa.TwoCubeFFA;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class d {
   public final NamespacedKey eQ;
   private final u eR;

   public d(TwoCubeFFA var1, u var2) {
      this.eQ = new NamespacedKey(var1, "region_wand");
      this.eR = var2;
   }

   public final void J(Player var1) {
      var1.getInventory().addItem(new ItemStack[]{this.K(var1)});
   }

   private ItemStack K(Player var1) {
      ItemStack var2;
      ItemMeta var3;
      if ((var3 = (var2 = new ItemStack(Material.GOLDEN_AXE)).getItemMeta()) != null) {
         var3.setDisplayName(this.eR.u2q5(var1, "&#52FF00&lFFA Region Wand"));
         var3.setLore(
            List.of(
               this.eR.u2q5(var1, "&7Left click block: &fPosition 1"),
               this.eR.u2q5(var1, "&7Right click block: &fPosition 2"),
               this.eR.u2q5(var1, "&7Use &f/ffa setregion <arena> &7first.")
            )
         );
         var3.addItemFlags(ItemFlag.values());
         var3.getPersistentDataContainer().set(this.eQ, PersistentDataType.STRING, "true");
         var2.setItemMeta(var3);
      }

      return var2;
   }
}
