package club.aspvp.ffa.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class FfaArenaJoinEvent extends Event {
   private static final HandlerList C = new HandlerList();
   private final Player D;
   private final String E;
   private final String F;

   public FfaArenaJoinEvent(Player var1, String var2, String var3) {
      this.D = var1;
      this.E = var2;
      this.F = var3;
   }

   public final Player getPlayer() {
      return this.D;
   }

   public final String getArena() {
      return this.E;
   }

   public final String getKit() {
      return this.F;
   }

   public final HandlerList getHandlers() {
      return C;
   }

   public static HandlerList getHandlerList() {
      return C;
   }
}
