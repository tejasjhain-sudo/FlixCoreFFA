package club.aspvp.ffa.internal;

import club.aspvp.ffa.api.ArenaRegionSnapshot;
import club.aspvp.ffa.api.ArenaSnapshot;
import club.aspvp.ffa.api.KitSnapshot;
import club.aspvp.ffa.api.StatsSnapshot;
import club.aspvp.ffa.api.FlixCoreFFAAPI;
import club.aspvp.ffa.api.TwoCubeFFAAPI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ab2c implements FlixCoreFFAAPI, TwoCubeFFAAPI {
   private final u2q5 R;
   private final m8p0 S;
   private final b T;
   private final h U;
   private final q V;
   private final x6y3 W;
   private final o3w8 X;
   private final i5k2 Y;
   private final Runnable Z;

   public ab2c(u2q5 var1, m8p0 var2, b var3, h var4, q var5, x6y3 var6, o3w8 var7, i5k2 var8, Runnable var9) {
      this.R = var1;
      this.S = var2;
      this.T = var3;
      this.U = var4;
      this.V = var5;
      this.W = var6;
      this.X = var7;
      this.Y = var8;
      this.Z = var9;
   }

   @Override
   public final Collection<ArenaSnapshot> arenas() {
      return this.R.arenas().stream().map(this::x7q9).toList();
   }

   @Override
   public final Optional<ArenaSnapshot> arena(String var1) {
      return this.R.arena(var1).map(this::x7q9);
   }

   @Override
   public final Optional<ArenaRegionSnapshot> arenaRegion(String var1) {
      return this.R.arena(var1).map(j::S).map(this::ab2c);
   }

   @Override
   public final boolean createArena(String var1) {
      u2q5 var3 = this.R;
      return this.R.ab2c(var1, null);
   }

   @Override
   public final boolean deleteArena(String var1) {
      return this.R.f3y8(var1);
   }

   @Override
   public final boolean setArenaEnabled(String var1, boolean var2) {
      j var3;
      if ((var3 = this.R.arena(var1).orElse(null)) != null && (!var2 || var3.T())) {
         var3.g = var2;
         this.R.y5p8();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean setArenaBuildAllowed(String var1, boolean var2) {
      j var3;
      if ((var3 = this.R.arena(var1).orElse(null)) == null) {
         return false;
      } else {
         var3.h = var2;
         this.R.y5p8();
         return true;
      }
   }

   @Override
   public final boolean setArenaSpawn(String var1, Location var2) {
      j var3;
      if ((var3 = this.R.arena(var1).orElse(null)) != null && var2 != null && var2.getWorld() != null) {
         this.R.ab2c(var3, var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean setArenaRegion(String var1, Location var2, Location var3) {
      j var6;
      if ((var6 = this.R.arena(var1).orElse(null)) != null
         && var2 != null
         && var3 != null
         && var2.getWorld() != null
         && var2.getWorld().equals(var3.getWorld())) {
         this.R.ab2c(var6, var2, var3);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean clearArenaRegion(String var1) {
      j var2;
      if ((var2 = this.R.arena(var1).orElse(null)) == null) {
         return false;
      } else {
         this.R.q8v3(var2);
         return true;
      }
   }

   @Override
   public final boolean setArenaPrimaryKit(String var1, String var2) {
      j var3;
      if ((var3 = this.R.arena(var1).orElse(null)) != null && !this.S.e7z1(var2).isEmpty()) {
         this.R.x7q9(var3, var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean linkKit(String var1, String var2) {
      j var3;
      return (var3 = this.R.arena(var1).orElse(null)) != null && this.S.e7z1(var2).isPresent() && this.R.m4z1(var3, var2);
   }

   @Override
   public final boolean unlinkKit(String var1, String var2) {
      j var3;
      return (var3 = this.R.arena(var1).orElse(null)) != null && this.R.q8v3(var3, var2);
   }

   @Override
   public final boolean clearArenaKits(String var1) {
      j var2;
      if ((var2 = this.R.arena(var1).orElse(null)) == null) {
         return false;
      } else {
         this.R.m4z1(var2);
         return true;
      }
   }

   @Override
   public final Location lobby() {
      Location var1;
      return (var1 = this.R.b8t3()) == null ? null : var1.clone();
   }

   @Override
   public final void setLobby(Location var1) {
      if (var1 != null && var1.getWorld() != null) {
         this.R.ab2c(var1);
      }
   }

   @Override
   public final Collection<KitSnapshot> kits() {
      return this.S.t().stream().map(this::ab2c).toList();
   }

   @Override
   public final Optional<KitSnapshot> kit(String var1) {
      return this.S.e7z1(var1).map(this::ab2c);
   }

   @Override
   public final boolean createKit(String var1) {
      return this.S.l9m6(var1);
   }

   @Override
   public final boolean deleteKit(String var1) {
      if (!this.S.o3w8(var1)) {
         return false;
      } else {
         this.R.p6d2(var1);
         return true;
      }
   }

   @Override
   public final boolean saveGlobalKit(String var1, Player var2) {
      if (var2 != null && var1 != null && !var1.isBlank()) {
         this.S.ab2c(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean applyKit(Player var1, String var2) {
      l var3 = this.S.e7z1(var2).orElse(null);
      if (var1 != null && var3 != null) {
         this.S.r5n0(var1, var3);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean openGlobalKitEditor(Player var1, String var2) {
      if (var1 != null && !this.S.e7z1(var2).isEmpty()) {
         this.W.h1s7(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean openPersonalKitEditor(Player var1, String var2) {
      if (var1 != null && !this.S.e7z1(var2).isEmpty()) {
         this.W.z4c9(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean saveEditingKit(Player var1) {
      if (var1 != null && !this.W.f(var1).isBlank()) {
         this.W.e(var1);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean resetEditingKit(Player var1) {
      if (var1 != null && !this.W.f(var1).isBlank()) {
         this.W.h(var1);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean cleanKitEditor(Player var1) {
      if (var1 == null) {
         return false;
      } else {
         x6y3.i(var1);
         return true;
      }
   }

   @Override
   public final boolean closeKitEditor(Player var1) {
      if (var1 != null && !this.W.f(var1).isBlank()) {
         this.W.l(var1);
         var1.closeInventory();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final Optional<String> editingKit(Player var1) {
      return var1 != null && !this.W.f(var1).isBlank() ? Optional.of(this.W.f(var1)) : Optional.empty();
   }

   @Override
   public final boolean isPersonalKitEditing(Player var1) {
      return var1 != null && this.W.g(var1);
   }

   @Override
   public final boolean openTagsMenu(Player var1) {
      if (var1 == null) {
         return false;
      } else {
         o3w8 var3 = this.X;
         this.X.ab2c(var1, 0);
         return true;
      }
   }

   @Override
   public final boolean openChatColourMenu(Player var1) {
      if (var1 == null) {
         return false;
      } else {
         o3w8 var3 = this.X;
         this.X.x7q9(var1, 0);
         return true;
      }
   }

   @Override
   public final String selectedTag(Player var1) {
      return var1 == null ? "" : this.X.j4n9(var1);
   }

   @Override
   public final String selectedTagName(Player var1) {
      return var1 == null ? "" : this.X.u2q5(var1);
   }

   @Override
   public final String selectedChatColour(Player var1) {
      return var1 == null ? "" : this.X.l9m6(var1);
   }

   @Override
   public final String selectedChatColourName(Player var1) {
      return var1 == null ? "" : this.X.o3w8(var1);
   }

   @Override
   public final boolean join(Player var1, String var2) {
      j var4 = this.R.arena(var2).orElse(null);
      if (var1 != null && var4 != null && var4.g && var4.T() && !this.T.w(var1)) {
         this.T.q8v3(var1, var4);
         this.V.ad(var1);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean leave(Player var1) {
      if (var1 != null && this.T.w(var1)) {
         this.V.ae(var1);
         this.T.ab2c(var1, this.R.b8t3());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean isInArena(Player var1) {
      return var1 != null && this.T.w(var1);
   }

   @Override
   public final Optional<String> arenaOf(Player var1) {
      return var1 == null ? Optional.empty() : this.T.v(var1);
   }

   @Override
   public final String selectedKit(Player var1) {
      return var1 == null ? "None" : this.T.x(var1);
   }

   @Override
   public final boolean selectKit(Player var1, String var2) {
      if (var1 != null && !this.S.e7z1(var2).isEmpty()) {
         this.T.n2x6(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final int totalPlayers() {
      return this.T.w();
   }

   @Override
   public final long playersInArena(String var1) {
      return this.T.i5k2(var1);
   }

   @Override
   public final StatsSnapshot stats(Player var1) {
      return this.ab2c(this.U.W(var1));
   }

   @Override
   public final Optional<StatsSnapshot> cachedStats(UUID var1) {
      return this.U.m4z1(var1).map(this::ab2c);
   }

   @Override
   public final CompletableFuture<Optional<StatsSnapshot>> loadStats(String var1) {
      h var3 = this.U;
      return this.U.fI.l(var1).thenApply(var1x -> var1x.map(this::ab2c));
   }

   @Override
   public final void saveStats(Player var1) {
      if (var1 != null) {
         this.U.aa(var1);
      }
   }

   @Override
   public final int tokens(Player var1) {
      return var1 == null ? 0 : this.U.X(var1);
   }

   @Override
   public final int addTokens(Player var1, int var2) {
      return var1 == null ? 0 : this.U.r5n0(var1, var2);
   }

   @Override
   public final int setTokens(Player var1, int var2) {
      return var1 == null ? 0 : this.U.t9b4(var1, var2);
   }

   @Override
   public final boolean crossServerEnabled() {
      return this.Y.i();
   }

   @Override
   public final String localServerId() {
      return this.Y.j();
   }

   @Override
   public final String crossServerPing(String var1) {
      return this.Y.c2f4(var1);
   }

   @Override
   public final CompletableFuture<Boolean> sendCrossServerRequest(String var1, String var2, String var3) {
      return this.Y.ab2c(var1, var2, var3);
   }

   @Override
   public final void reload() {
      this.Z.run();
   }

   @Override
   public final void saveAll() {
      this.R.y5p8();
      this.S.q();
      h var1 = this.U;

      for (o var3 : this.U.fJ.values()) {
         var1.fI.x7q9(var3);
      }
   }

   private ArenaSnapshot x7q9(j var1) {
      return new ArenaSnapshot(var1.O(), var1.g, var1.P(), List.copyOf(var1.fR), var1.fU == null ? null : var1.fU.ac(), (int)this.T.i5k2(var1.O()));
   }

   private ArenaRegionSnapshot ab2c(k var1) {
      return new ArenaRegionSnapshot(var1.fW, var1.fX, var1.fY, var1.fZ, var1.ga, var1.gb, var1.gc);
   }

   private KitSnapshot ab2c(l var1) {
      return new KitSnapshot(var1.V(), var1.displayName, var1.n, var1.o, var1.p, var1.q, var1.r);
   }

   private StatsSnapshot ab2c(o var1) {
      return new StatsSnapshot(var1.gq, var1.name, var1.u, var1.v, var1.w, var1.x, var1.y, var1.z, var1.A, var1.ae());
   }
}
