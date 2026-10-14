package club.aspvp.ffa.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public interface FlixCoreFFAAPI extends TwoCubeFFAAPI {
   static FlixCoreFFAAPI get() {
      RegisteredServiceProvider<TwoCubeFFAAPI> var0;
      if ((var0 = Bukkit.getServicesManager().getRegistration(TwoCubeFFAAPI.class)) == null) {
         throw new IllegalStateException("FlixCore FFA API is not available");
      } else {
         Object provider = var0.getProvider();
         if (provider instanceof FlixCoreFFAAPI flixApi) {
            return flixApi;
         }
         return null;
      }
   }
}
