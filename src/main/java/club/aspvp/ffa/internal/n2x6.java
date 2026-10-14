package club.aspvp.ffa.internal;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class n2x6 implements Listener {
   private final p bm;
   private final u bn;
   private final o3w8 bo;
   private final PlainTextComponentSerializer bp = PlainTextComponentSerializer.plainText();

   public n2x6(p var1, u var2, o3w8 var3) {
      this.bm = var1;
      this.bn = var2;
      this.bo = var3;
   }

   @EventHandler
   public final void onJoin(PlayerJoinEvent var1) {
      this.bo.g1v7(var1.getPlayer());
   }

   @EventHandler
   public final void onQuit(PlayerQuitEvent var1) {
      o3w8 var10000 = this.bo;
      Player var2 = var1.getPlayer();
      o3w8 var3 = var10000;
      var10000.dO.remove(var2.getUniqueId());
      var3.dP.remove(var2.getUniqueId());
   }

   @EventHandler
   public final void onChat(AsyncChatEvent var1) {
      if (this.bm.x7q9("chat-customization", true)) {
         p var3 = this.bm;
         if (this.bm.gt.getConfig().getBoolean("chat-format.enabled", true)) {
            Player var2 = var1.getPlayer();
            String var4 = this.bo.l9m6(var2) + this.bp.serialize(var1.message());
            var3 = this.bm;
            String var5 = this.bm
               .gt
               .getConfig()
               .getString("chat-format.chat_format", "%flixcoreffa_tag% %luckperms_prefix% %player_name% &e&l» &7{MESSAGE}")
               .replace("{MESSAGE}", var4)
               .replace("%message%", var4);
            var1.renderer((var2x, var3x, var4x, var5x) -> {
               u var6x = this.bn;
               return this.bn.m4z1(var2x, var5, true);
            });
            return;
         }
      }
   }
}
