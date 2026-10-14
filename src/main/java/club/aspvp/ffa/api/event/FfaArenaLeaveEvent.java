package club.aspvp.ffa.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class FfaArenaLeaveEvent extends Event {
   private static final HandlerList G = new HandlerList();
   private final Player H;
   private final String I;
   private final String J;

   public FfaArenaLeaveEvent(Player var1, String var2, String var3) {
      this.H = var1;
      this.I = var2;
      this.J = var3;
   }

   public final Player getPlayer() {
      return this.H;
   }

   public final String getArena() {
      return this.I;
   }

   public final String getKit() {
      return this.J;
   }

   public final HandlerList getHandlers() {
      return G;
   }

   public static HandlerList getHandlerList() {
      return G;
   }
}
