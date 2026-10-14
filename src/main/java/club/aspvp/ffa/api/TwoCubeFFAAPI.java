package club.aspvp.ffa.api;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public interface TwoCubeFFAAPI {
   static TwoCubeFFAAPI get() {
      RegisteredServiceProvider var0;
      if ((var0 = Bukkit.getServicesManager().getRegistration(TwoCubeFFAAPI.class)) == null) {
         throw new IllegalStateException("FlixCore FFA API is not available");
      } else {
         return (TwoCubeFFAAPI)var0.getProvider();
      }
   }

   Collection<ArenaSnapshot> arenas();

   Optional<ArenaSnapshot> arena(String var1);

   Optional<ArenaRegionSnapshot> arenaRegion(String var1);

   boolean createArena(String var1);

   boolean deleteArena(String var1);

   boolean setArenaEnabled(String var1, boolean var2);

   boolean setArenaBuildAllowed(String var1, boolean var2);

   boolean setArenaSpawn(String var1, Location var2);

   boolean setArenaRegion(String var1, Location var2, Location var3);

   boolean clearArenaRegion(String var1);

   boolean setArenaPrimaryKit(String var1, String var2);

   boolean linkKit(String var1, String var2);

   boolean unlinkKit(String var1, String var2);

   boolean clearArenaKits(String var1);

   Location lobby();

   void setLobby(Location var1);

   Collection<KitSnapshot> kits();

   Optional<KitSnapshot> kit(String var1);

   boolean createKit(String var1);

   boolean deleteKit(String var1);

   boolean saveGlobalKit(String var1, Player var2);

   boolean applyKit(Player var1, String var2);

   boolean openGlobalKitEditor(Player var1, String var2);

   boolean openPersonalKitEditor(Player var1, String var2);

   boolean saveEditingKit(Player var1);

   boolean resetEditingKit(Player var1);

   boolean cleanKitEditor(Player var1);

   boolean closeKitEditor(Player var1);

   Optional<String> editingKit(Player var1);

   boolean isPersonalKitEditing(Player var1);

   boolean openTagsMenu(Player var1);

   boolean openChatColourMenu(Player var1);

   String selectedTag(Player var1);

   String selectedTagName(Player var1);

   String selectedChatColour(Player var1);

   String selectedChatColourName(Player var1);

   boolean join(Player var1, String var2);

   boolean leave(Player var1);

   boolean isInArena(Player var1);

   Optional<String> arenaOf(Player var1);

   String selectedKit(Player var1);

   boolean selectKit(Player var1, String var2);

   int totalPlayers();

   long playersInArena(String var1);

   StatsSnapshot stats(Player var1);

   Optional<StatsSnapshot> cachedStats(UUID var1);

   CompletableFuture<Optional<StatsSnapshot>> loadStats(String var1);

   void saveStats(Player var1);

   int tokens(Player var1);

   int addTokens(Player var1, int var2);

   int setTokens(Player var1, int var2);

   boolean crossServerEnabled();

   String localServerId();

   String crossServerPing(String var1);

   CompletableFuture<Boolean> sendCrossServerRequest(String var1, String var2, String var3);

   void reload();

   void saveAll();
}
