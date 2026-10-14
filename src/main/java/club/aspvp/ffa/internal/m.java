package club.aspvp.ffa.internal;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public record m(String ge, double gf, double gg, double gh, float gi, float gj) {
   public static m q8v3(Location var0) {
      return new m(var0.getWorld().getName(), var0.getX(), var0.getY(), var0.getZ(), var0.getYaw(), var0.getPitch());
   }

   public static m r5n0(ConfigurationSection var0) {
      return var0 == null
         ? null
         : new m(
            var0.getString("world"),
            var0.getDouble("x"),
            var0.getDouble("y"),
            var0.getDouble("z"),
            (float)var0.getDouble("yaw"),
            (float)var0.getDouble("pitch")
         );
   }

   public final Location ac() {
      if (this.ge != null && !this.ge.isBlank()) {
         World var1;
         return (var1 = Bukkit.getWorld(this.ge)) == null ? null : new Location(var1, this.gf, this.gg, this.gh, this.gi, this.gj);
      } else {
         return null;
      }
   }

   public final void t9b4(ConfigurationSection var1) {
      var1.set("world", this.ge);
      var1.set("x", this.gf);
      var1.set("y", this.gg);
      var1.set("z", this.gh);
      var1.set("yaw", this.gi);
      var1.set("pitch", this.gj);
   }
}
