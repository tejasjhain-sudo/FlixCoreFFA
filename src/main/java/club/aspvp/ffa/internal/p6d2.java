package club.aspvp.ffa.internal;

import java.util.Objects;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class p6d2 implements InventoryHolder {
   public final h1s7 aW;
   public final String aX;

   public p6d2(h1s7 aW, String aX) {
      this.aW = aW;
      this.aX = aX;
   }

   public h1s7 aW() { return aW; }
   public String aX() { return aX; }

   public final Inventory getInventory() {
      return null;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof p6d2 other)) return false;
      return aW == other.aW && Objects.equals(aX, other.aX);
   }

   @Override
   public int hashCode() {
      return Objects.hash(aW, aX);
   }
}
