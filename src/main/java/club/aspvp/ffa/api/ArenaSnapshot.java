package club.aspvp.ffa.api;

import java.util.List;
import org.bukkit.Location;

public record ArenaSnapshot(String f, boolean g, boolean h, List<String> i, Location j, int k) {
   public ArenaSnapshot(String f, boolean g, boolean h, List<String> i, Location j, int k) {
      i = List.copyOf(i);
      j = j == null ? null : j.clone();
      this.f = f;
      this.g = g;
      this.h = h;
      this.i = i;
      this.j = j;
      this.k = k;
   }

   public final String id() {
      return this.f;
   }

   public final boolean enabled() {
      return this.g;
   }

   public final boolean buildAllowed() {
      return this.h;
   }

   public final List<String> kits() {
      return this.i;
   }

   public final Location spawn() {
      return this.j;
   }

   public final int players() {
      return this.k;
   }
}
