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
      String var3 = this.bj.gt.getConfig().getString("command-restrictions.bypass-permission", "ffa.command.bypass");
      if (this.bj.gt.getConfig().getBoolean("command-restrictions.enabled", true)
         && this.bk.w(var2)
         && !var2.hasPermission(var3)
         && !var2.hasPermission("ffa.admin")) {
         String raw = var1.getMessage().startsWith("/") ? var1.getMessage().substring(1) : var1.getMessage();
         String cmd = raw.trim().toLowerCase(Locale.ROOT);

         if (isAllowedInArena(cmd)) {
            return;
         }

         var1.setCancelled(true);
         this.bl.h1s7(var2, "command-blocked-in-arena");
      }
   }

   private boolean isAllowedInArena(String cmd) {
      String root = cmd.split("\\s+")[0];
      if (root.contains(":")) {
         root = root.substring(root.indexOf(':') + 1);
      }

      // Standalone FFA gameplay commands
      switch (root) {
         case "duel":
         case "duels":
         case "cage":
         case "ffaduel":
         case "ffaduels":
         case "koth":
         case "ffakoth":
         case "airdrop":
         case "ffaairdrop":
         case "crate":
         case "supplycrate":
         case "events":
         case "ffaevents":
         case "liveevents":
         case "stats":
         case "ffastats":
         case "top":
         case "ffatop":
         case "token":
         case "tokens":
         case "ffatoken":
         case "ffatokens":
         case "hat":
         case "leave":
         case "spawn":
         case "leaveffa":
         case "ffaleave":
         case "ffaspawn":
            return true;
         default:
            break;
      }

      // Main plugin command prefixes: check their subcommand
      if (root.equals("ffa") || root.equals("flixffa") || root.equals("flixcoreffa")
            || root.equals("twocubeffa") || root.equals("arena") || root.equals("freeforall")) {
         String[] parts = cmd.split("\\s+");
         if (parts.length <= 1) {
            return true;
         }
         String sub = parts[1];
         switch (sub) {
            case "duel":
            case "duels":
            case "accept":
            case "decline":
            case "forfeit":
            case "koth":
            case "airdrop":
            case "events":
            case "stats":
            case "top":
            case "token":
            case "tokens":
            case "leave":
            case "spawn":
            case "lobby":
            case "help":
               return true;
            default:
               break;
         }
      }

      // Configured custom allowed commands
      List<String> list = this.bj.gt.getConfig().getStringList("command-restrictions.allowed-commands");
      for (String allowed : list) {
         String clean = allowed.toLowerCase(Locale.ROOT).replaceFirst("^/", "").trim();
         if (cmd.equals(clean) || cmd.startsWith(clean + " ")) {
            return true;
         }
      }

      return false;
   }
}
