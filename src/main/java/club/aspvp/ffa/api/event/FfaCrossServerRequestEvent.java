package club.aspvp.ffa.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class FfaCrossServerRequestEvent extends Event {
   private static final HandlerList K = new HandlerList();
   private final long L;
   private final String M;
   private final String N;
   private final String O;
   private final String P;
   private final long Q;

   public FfaCrossServerRequestEvent(long var1, String var3, String var4, String var5, String var6, long var7) {
      this.L = var1;
      this.M = var3;
      this.N = var4;
      this.O = var5;
      this.P = var6;
      this.Q = var7;
   }

   public final long getRequestId() {
      return this.L;
   }

   public final String getSourceServer() {
      return this.M;
   }

   public final String getTargetServer() {
      return this.N;
   }

   public final String getRequestType() {
      return this.O;
   }

   public final String getPayload() {
      return this.P;
   }

   public final long getCreatedAt() {
      return this.Q;
   }

   public final HandlerList getHandlers() {
      return K;
   }

   public static HandlerList getHandlerList() {
      return K;
   }
}
