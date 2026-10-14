package club.aspvp.ffa.internal;

import org.bukkit.plugin.java.JavaPlugin;

public final class y {
   public static JavaPlugin cb;
   public static p ab2c;

   public static void w7k3(String var0, String var1) {
      if (cb != null && ab2c != null) {
         boolean var10000;
         label28: {
            if (ab2c != null) {
               p var3 = ab2c;
               if (ab2c.gt.getConfig().getBoolean("debug.enabled", false)) {
                  String var2 = var0 == null ? "general" : var0.toLowerCase();
                  var3 = ab2c;
                  if (!ab2c.gt.getConfig().getBoolean("debug.areas.all", false)) {
                     var3 = ab2c;
                     if (!ab2c.gt.getConfig().getBoolean("debug.areas." + var2, true)) {
                        var10000 = false;
                        break label28;
                     }
                  }

                  var10000 = true;
                  break label28;
               }
            }

            var10000 = false;
         }

         if (var10000) {
            cb.getLogger().info("[DEBUG/" + var0.toUpperCase() + "][" + Thread.currentThread().getName() + "] " + var1);
         }
      }
   }
}
