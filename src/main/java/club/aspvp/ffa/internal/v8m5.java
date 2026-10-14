package club.aspvp.ffa.internal;

import java.util.List;
import java.util.Locale;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public final class v8m5 implements Listener {
   private final p bj;
   private final b bk;
   private final r bl;

   public v8m5(p var1, b var2, r var3) {
      this.bj = var1;
      this.bk = var2;
      this.bl = var3;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onCommand(PlayerCommandPreprocessEvent var1) {
      Player var2 = var1.getPlayer();
      p var4 = this.bj;
      String var3 = this.bj.gt.getConfig().getString("command-restrictions.bypass-permission", "ffa.command.bypass");
      var4 = this.bj;
      if (this.bj.gt.getConfig().getBoolean("command-restrictions.enabled", true)
         && this.bk.w(var2)
         && !var2.hasPermission(var3)
         && !var2.hasPermission("ffa.admin")) {
         String cmd = var1.getMessage().substring(1).trim().toLowerCase(Locale.ROOT);
         var4 = this.bj;
         List var8;
         if ((var8 = this.bj.gt.getConfig().getStringList("command-restrictions.allowed-commands")).isEmpty()) {
            var8 = List.of(
               "leave",
               "spawn",
               "ffa leave",
               "ffa spawn",
               "flixffa leave",
               "flixffa spawn",
               "flixcoreffa leave",
               "flixcoreffa spawn",
               "twocubeffa leave",
               "twocubeffa spawn",
               "arena leave",
               "arena spawn",
               "freeforall leave",
               "freeforall spawn"
            );
         }

         if (!var8.stream()
            .map(var0 -> String.valueOf(var0).toLowerCase(Locale.ROOT).replaceFirst("^/", ""))
            .anyMatch(var1x -> cmd.equals(var1x) || cmd.startsWith(var1x + " "))) {
            var1.setCancelled(true);
            this.bl.h1s7(var2, "command-blocked-in-arena");
         }
      }
   }
}
