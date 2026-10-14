package club.aspvp.ffa.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class m4z1 implements CommandExecutor, TabCompleter {
   private static final Map<String, m4z1.ab2c> ad = Map.ofEntries(
      Map.entry("ffa", new m4z1.ab2c("config.yml", "features.ffa")),
      Map.entry("kiteditor", new m4z1.ab2c("config.yml", "features.kiteditor")),
      Map.entry("totempractice", new m4z1.ab2c("config.yml", "features.totempractice")),
      Map.entry("spawn-items", new m4z1.ab2c("config.yml", "features.spawn-items")),
      Map.entry("announcements", new m4z1.ab2c("config.yml", "features.announcements")),
      Map.entry("afk", new m4z1.ab2c("config.yml", "features.afk")),
      Map.entry("scoreboard", new m4z1.ab2c("config.yml", "features.scoreboard")),
      Map.entry("display", new m4z1.ab2c("config.yml", "features.display")),
      Map.entry("chat-customization", new m4z1.ab2c("config.yml", "features.chat-customization")),
      Map.entry("tokens", new m4z1.ab2c("config.yml", "tokens.enabled")),
      Map.entry("spawnpvp", new m4z1.ab2c("spawnpvp.yml", "enabled")),
      Map.entry("auto-respawn", new m4z1.ab2c("config.yml", "auto-respawn.enabled")),
      Map.entry("first-join-protection", new m4z1.ab2c("config.yml", "first-join-protection.enabled")),
      Map.entry("killstreak-rewards", new m4z1.ab2c("config.yml", "killstreak-rewards.enabled")),
      Map.entry("cross-server", new m4z1.ab2c("crossserver.yml", "enabled"))
   );
   private final p ae;
   private final r af;
   private final t ag;
   private final v ah;
   private final u2q5 ai;
   private final e7z1 aj;
   private final m8p0 ak;
   private final h al;
   private final b am;
   private final q1d9 an;
   private final e ao;
   private final q ap;
   private final j4n9 aq;
   private final g1v7 ar;
   private final i as;
   private final x6y3 at;
   private final d au;
   private final f av;
   private final g aw;
   private final Runnable ax;

   public m4z1(
      p var1,
      r var2,
      t var3,
      v var4,
      u2q5 var5,
      e7z1 var6,
      m8p0 var7,
      h var8,
      b var9,
      q1d9 var10,
      e var11,
      q var12,
      j4n9 var13,
      i var14,
      x6y3 var15,
      d var16,
      f var17,
      g var18,
      g1v7 var19,
      Runnable var20
   ) {
      this.ae = var1;
      this.af = var2;
      this.ag = var3;
      this.ah = var4;
      this.ai = var5;
      this.aj = var6;
      this.ak = var7;
      this.al = var8;
      this.am = var9;
      this.an = var10;
      this.ao = var11;
      this.ap = var12;
      this.aq = var13;
      this.ar = var19;
      this.as = var14;
      this.at = var15;
      this.au = var16;
      this.av = var17;
      this.aw = var18;
      this.ax = var20;
   }

   public final boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      String var5;
      if ((var5 = var2.getName().toLowerCase(Locale.ROOT)).equals("leave")) {
         this.ab2c(var1);
         return true;
      } else if (var5.equals("spawn")) {
         if (this.m4z1(var1, "ffa.spawn")) {
            this.x7q9(var1);
         }

         return true;
      } else if (var3.equalsIgnoreCase("arena") && var4.length > 0 && x7q9(var4[0])) {
         String[] var15;
         (var15 = new String[var4.length + 1])[0] = "arena";
         System.arraycopy(var4, 0, var15, 1, var4.length);
         this.x7q9(var1, var15);
         return true;
      } else if (var3.equalsIgnoreCase("ffaarena")) {
         if (var4.length == 0) {
            this.af.h1s7(var1, "ffaarena-usage");
            return true;
         } else {
            this.ab2c(var1, new String[]{"join", var4[0]});
            return true;
         }
      } else if (var4.length == 0) {
         if (var1 instanceof Player var14) {
            if (this.ab2c(var1, "ffa", true) && this.m4z1(var1, "ffa.menu")) {
               this.an.a(var14);
            }
         } else {
            this.af.z4c9(var1, "help");
         }

         return true;
      } else if (var4[0].equalsIgnoreCase("help")) {
         this.af.z4c9(var1, "help");
         return true;
      } else {
         y.w7k3("COMMAND", var1.getName() + " executed /" + var2.getName() + " " + String.join(" ", var4));
         String var13 = var4[0].toLowerCase(Locale.ROOT);
         switch (var13) {
            case "join":
               this.ab2c(var1, var4);
               break;
            case "leave":
               this.ab2c(var1);
               break;
            case "lobby":
               this.x7q9(var1);
               break;
            case "spawn":
               Player var36;
               if ((var36 = this.q8v3(var1)) != null) {
                  this.am.v(var36).flatMap(this.ai::arena).ifPresentOrElse(var1x -> {
                     if (var1x.fU != null && var1x.fU.ac() != null) {
                        var36.teleportAsync(var1x.fU.ac());
                     }
                  }, () -> this.af.h1s7(var36, "not-in-arena"));
               }
               break;
            case "setlobby":
               Player var35;
               if ((var35 = this.q8v3(var1)) != null && this.m4z1(var1, "ffa.arena.edit")) {
                  this.ai.ab2c(var35.getLocation());
                  this.af.h1s7(var35, "lobby-set");
               }
               break;
            case "setspawn":
               Player var48;
               j var57;
               if ((var48 = this.q8v3(var1)) != null && this.m4z1(var1, "ffa.arena.edit") && var4.length >= 2 && (var57 = this.q8v3(var1, var4[1])) != null) {
                  this.ai.ab2c(var57, var48.getLocation());
                  this.af.ab2c(var48, "spawn-set", Map.of("arena", var57.O()));
                  this.af.ab2c(var48, "spawn-next-step", Map.of("arena", var57.O()));
               }
               break;
            case "create":
               if (this.m4z1(var1, "ffa.arena.create") && var4.length >= 2) {
                  if (!this.ai.arena(var4[1]).isPresent()) {
                     k var47 = null;
                     if (var1 instanceof Player var56) {
                        u2q5 var71 = this.ai;
                        u2q5.x7q9 var87;
                        Optional var105;
                        if ((var87 = this.ai.cA.get(var56.getUniqueId())) != null && var87.g1v7() && var87.u2q5().getWorld().equals(var87.e7z1().getWorld())) {
                           var71.cA.remove(var56.getUniqueId());
                           var105 = Optional.of(k.ab2c(var87.u2q5(), var87.e7z1()));
                        } else {
                           var105 = Optional.empty();
                        }

                        if ((var47 = (k)var105.orElse(null)) == null) {
                           this.af.h1s7(var56, "arena-create-select-region");
                           return true;
                        }
                     }

                     if (this.ai.ab2c(var4[1], var47)) {
                        this.af.ab2c(var1, "arena-created", Map.of("arena", var4[1]));
                        this.af.ab2c(var1, "arena-setup-complete", Map.of("arena", var4[1]));
                        return true;
                     }
                  }

                  this.af.ab2c(var1, "arena-exists", Map.of("arena", var4[1]));
               }
               break;
            case "delete":
               if (this.m4z1(var1, "ffa.arena.delete") && var4.length >= 2) {
                  if (this.am.i5k2(var4[1]) > 0L) {
                     this.af.ab2c(var1, "arena-not-empty", Map.of("arena", var4[1]));
                  } else if (this.ai.f3y8(var4[1])) {
                     this.af.ab2c(var1, "arena-deleted", Map.of("arena", var4[1]));
                  } else {
                     this.af.ab2c(var1, "arena-missing", Map.of("arena", var4[1]));
                  }
               }
               break;
            case "list":
               this.af.h1s7(var1, "arena-list-header");
               this.ai
                  .arenas()
                  .stream()
                  .sorted(Comparator.comparing(j::O))
                  .forEach(
                     var2x -> this.af
                           .ab2c(
                              var1,
                              "arena-list-entry",
                              Map.of("arena", var2x.O(), "enabled", String.valueOf(var2x.g), "players", String.valueOf(this.am.i5k2(var2x.O())))
                           )
                  );
               this.af.h1s7(var1, "arena-list-footer");
               break;
            case "edit":
               Player var46;
               j var55;
               if ((var46 = this.q8v3(var1)) != null
                  && this.m4z1(var1, "ffa.arena.edit")
                  && var4.length >= 2
                  && (var55 = this.q8v3(var1, var4[1])) != null
                  && var55.R() != null
                  && var55.R().ac() != null) {
                  var46.teleportAsync(var55.R().ac());
               }
               break;
            case "reload":
               if (this.m4z1(var1, "ffa.reload")) {
                  this.ax.run();
                  this.af.h1s7(var1, "reloaded");
               }
               break;
            case "kit":
               if (this.m4z1(var1, "ffa.kit")) {
                  if (var4.length >= 2) {
                     String var45 = var4[1].toLowerCase(Locale.ROOT);
                     switch (var45) {
                        case "give":
                           String[] var70 = var4;
                           if (this.ab2c(var1, "kiteditor", true) && this.m4z1(var1, "ffa.kiteditor") && var70.length >= 4) {
                              Player var86;
                              if ((var86 = Bukkit.getPlayerExact(var70[2])) == null) {
                                 this.af.ab2c(var1, "player-missing", Map.of("player", var70[2]));
                              } else {
                                 this.ak.e7z1(var70[3]).ifPresentOrElse(var3x -> {
                                    this.ak.r5n0(var86, var3x);
                                    this.af.ab2c(var1, "kit-given", Map.of("player", var86.getName(), "kit", var3x.V()));
                                 }, () -> this.af.ab2c(var1, "kit-missing", Map.of("kit", var70[3])));
                              }
                           }
                           break;
                        case "set":
                           Player var68;
                           if ((var68 = this.q8v3(var1)) != null) {
                              if (!var68.hasPermission("ffa.kiteditor") && !var68.hasPermission("ffa.kit")) {
                                 this.af.h1s7(var68, "no-permission");
                              } else if (var4.length < 3) {
                                 this.af.h1s7(var68, "unknown-command");
                              } else {
                                 this.ak.e7z1(var4[2]).ifPresentOrElse(var2x -> {
                                    this.am.n2x6(var68, var2x.V());
                                    this.af.ab2c(var68, "kit-selected", Map.of("kit", var2x.V()));
                                 }, () -> this.af.ab2c(var68, "kit-missing", Map.of("kit", var4[2])));
                              }
                           }
                           break;
                        case "link":
                           j var85;
                           if (this.ab2c(var1, "kiteditor", true)
                              && this.m4z1(var1, "ffa.kiteditor")
                              && var4.length >= 4
                              && (var85 = this.q8v3(var1, var4[3])) != null) {
                              this.ak.e7z1(var4[2]).ifPresentOrElse(var3x -> {
                                 if (this.ai.m4z1(var85, var3x.V())) {
                                    this.af.ab2c(var1, "arena-kit-linked", Map.of("kit", var3x.V(), "arena", var85.O()));
                                 } else {
                                    this.af.ab2c(var1, "arena-kit-already-linked", Map.of("kit", var3x.V(), "arena", var85.O()));
                                 }
                              }, () -> this.af.ab2c(var1, "kit-missing", Map.of("kit", var4[2])));
                           }
                           break;
                        case "remove":
                           if (this.ab2c(var1, "kiteditor", true) && this.m4z1(var1, "ffa.kiteditor") && var4.length >= 3) {
                              Player var84;
                              if ((var84 = Bukkit.getPlayerExact(var4[2])) == null) {
                                 this.af.ab2c(var1, "player-missing", Map.of("player", var4[2]));
                              } else {
                                 String var96;
                                 if ((var96 = this.ak.s()).isBlank()) {
                                    this.af.ab2c(var1, "kit-missing", Map.of("kit", "default"));
                                 } else {
                                    this.am.n2x6(var84, var96);
                                    this.af.ab2c(var1, "kit-removed", Map.of("player", var84.getName(), "kit", var96));
                                 }
                              }
                           }
                           break;
                        case "create":
                           Player var83;
                           if (this.ab2c(var1, "kiteditor", true)
                              && (var83 = this.q8v3(var1)) != null
                              && this.m4z1(var1, "ffa.kiteditor")
                              && var4.length >= 3
                              && !this.x7q9(var83)) {
                              String var95 = var4[2].toLowerCase(Locale.ROOT);
                              if (!this.ak.l9m6(var95)) {
                                 this.af.ab2c(var83, "kit-exists", Map.of("kit", var95));
                              } else {
                                 this.af.ab2c(var83, "kit-created", Map.of("kit", var95));
                                 this.at.h1s7(var83, var95);
                                 this.af.ab2c(var83, "kiteditor-opened", Map.of("kit", var95));
                              }
                           }
                           break;
                        case "save":
                           Player var82;
                           if (this.ab2c(var1, "kiteditor", true)
                              && (var82 = this.q8v3(var1)) != null
                              && this.m4z1(var1, "ffa.kiteditor")
                              && var4.length >= 3
                              && !this.x7q9(var82)) {
                              this.ak.ab2c(var4[2], var82);
                              String var94 = var4[2].toLowerCase(Locale.ROOT);
                              this.af.ab2c(var82, "kit-saved", Map.of("kit", var94));
                              this.af.ab2c(var82, "kit-save-guide", Map.of("kit", var94));
                           }
                           break;
                        case "delete":
                           if (this.ab2c(var1, "kiteditor", true) && this.m4z1(var1, "ffa.kiteditor") && var4.length >= 3) {
                              if (this.ak.o3w8(var4[2])) {
                                 this.ai.p6d2(var4[2]);
                                 this.af.ab2c(var1, "kit-deleted", Map.of("kit", var4[2].toLowerCase(Locale.ROOT)));
                              } else {
                                 this.af.ab2c(var1, "kit-missing", Map.of("kit", var4[2]));
                              }
                           }
                           break;
                        case "duplicate":
                           if (this.ab2c(var1, "kiteditor", true) && this.m4z1(var1, "ffa.kiteditor") && var4.length >= 4) {
                              String var107 = var4[2];
                              String var98 = var4[3];
                              String var93 = var107;
                              m8p0 var81 = this.ak;
                              l var99;
                              boolean var104;
                              if ((var99 = this.ak.e7z1(var93).orElse(null)) != null && !var81.ew.containsKey(ab.r(var98))) {
                                 l var100;
                                 (var100 = new l(ab.r(var98))).g(var99.W());
                                 var100.f3y8(var99.X() == null ? null : var99.X().clone());
                                 var100.t9b4(m8p0.q8v3(var99.Y()));
                                 var100.f3y8(m8p0.q8v3(var99.Z()));
                                 var100.p6d2(var99.aa() == null ? null : var99.aa().clone());
                                 var100.p6d2(new ArrayList<>(var99.ab()));
                                 var81.ew.put(var100.V(), var100);
                                 var81.q();
                                 var104 = true;
                              } else {
                                 var104 = false;
                              }

                              if (var104) {
                                 this.af.ab2c(var1, "kit-saved", Map.of("kit", var4[3].toLowerCase(Locale.ROOT)));
                              } else {
                                 this.af.ab2c(var1, "kit-missing", Map.of("kit", var4[2]));
                              }
                           }
                           break;
                        case "editor":
                        case "edit":
                           Player var67;
                           if (this.ab2c(var1, "kiteditor", true)
                              && (var67 = this.q8v3(var1)) != null
                              && this.m4z1(var67, "ffa.kiteditor")
                              && !this.x7q9(var67)) {
                              if (var4.length < 3) {
                                 this.af.h1s7(var67, "kiteditor-usage");
                              } else {
                                 String var80 = var4[2].toLowerCase(Locale.ROOT);
                                 if (this.ak.e7z1(var80).isEmpty()) {
                                    this.af.ab2c(var67, "kit-missing", Map.of("kit", var80));
                                 } else {
                                    this.at.h1s7(var67, var80);
                                    this.af.ab2c(var67, "kiteditor-opened", Map.of("kit", var80));
                                 }
                              }
                           }
                           break;
                        default:
                           Player var65;
                           if ((var65 = this.q8v3(var1)) != null) {
                              String var79 = var4[1];
                              if (this.ak.e7z1(var79).isEmpty()) {
                                 this.af.ab2c(var65, "kit-missing", Map.of("kit", var79));
                              } else {
                                 String var92 = this.ak.e7z1(var79).orElseThrow().V();
                                 this.am.n2x6(var65, var92);
                                 this.af.ab2c(var65, "kit-selected", Map.of("kit", var92));
                                 if (var4.length >= 3) {
                                    if (!var65.hasPermission("ffa.join")) {
                                       this.af.h1s7(var65, "no-permission");
                                    } else {
                                       j var97;
                                       if ((var97 = this.q8v3(var65, var4[2])) != null) {
                                          this.ab2c(var65, var97);
                                       }
                                    }
                                 }
                              }
                           }
                     }
                  } else {
                     this.af.z4c9(var1, "kit-help");
                  }
               }
               break;
            case "stats":
               if (this.m4z1(var1, "ffa.stats")) {
                  if (var4.length == 1) {
                     Player var44;
                     if ((var44 = this.q8v3(var1)) != null) {
                        this.ab2c(var1, this.al.W(var44));
                     }
                  } else {
                     this.ah
                        .l(var4[1])
                        .thenAccept(
                           var3x -> this.ab2c(
                                 var1,
                                 () -> var3x.ifPresentOrElse(
                                       var2xxx -> this.ab2c(var1, var2xxx), () -> this.af.ab2c(var1, "player-missing", Map.of("player", var4[1]))
                                    )
                              )
                        )
                        .exceptionally(var2x -> this.ab2c(var1, var2x));
                  }
               }
               break;
            case "top":
               if (this.m4z1(var1, "ffa.top")) {
                  this.ah
                     .x7q9("kills", 10)
                     .thenAccept(
                        var2x -> this.ab2c(
                              var1,
                              () -> {
                                 this.af.z4c9(var1, "top-header");

                                 for (int var3x = 0; var3x < var2x.size(); var3x++) {
                                    o var4x = var2x.get(var3x);
                                    this.af
                                       .ab2c(
                                          var1,
                                          "top-entry",
                                          Map.of("position", String.valueOf(var3x + 1), "player", var4x.name, "value", String.valueOf(var4x.u))
                                       );
                                 }

                                 this.af.h1s7(var1, "top-footer");
                              }
                           )
                     )
                     .exceptionally(var2x -> this.ab2c(var1, var2x));
               }
               break;
            case "resetstats":
               if (this.m4z1(var1, "ffa.stats.reset") && var4.length >= 2) {
                  String var43 = var4[1];
                  this.ah
                     .l(var43)
                     .thenAccept(
                        var3x -> {
                           if (var3x.isPresent()) {
                              UUID var4x = var3x.get().gq;
                              this.al
                                 .ab2c(var4x, var43)
                                 .thenRun(() -> this.ab2c(var1, () -> this.af.ab2c(var1, "stats-reset", Map.of("player", var43))))
                                 .exceptionally(var2xx -> this.ab2c(var1, var2xx));
                           } else {
                              this.ab2c(var1, () -> this.af.ab2c(var1, "player-missing", Map.of("player", var43)));
                           }
                        }
                     )
                     .exceptionally(var2x -> this.ab2c(var1, var2x));
               }
               break;
            case "arena":
               this.x7q9(var1, var4);
               break;
            case "regenall":
               this.m4z1(var1);
               break;
            case "snapshotadd":
            case "snapadd":
               this.ab2c(var1, var4.length >= 2 ? var4[1] : "all");
               break;
            case "snapshotall":
            case "snapall":
               this.ab2c(var1, "all");
               break;
            case "snapshotstatus":
            case "snapstatus":
               this.x7q9(var1, var4.length >= 2 ? var4[1] : "all");
               break;
            case "start":
               this.ab2c(var1, var4, true);
               break;
            case "stop":
               this.ab2c(var1, var4, false);
               break;
            case "setregion":
               Player var42;
               j var53;
               if ((var42 = this.q8v3(var1)) != null && this.m4z1(var1, "ffa.arena.edit") && var4.length >= 2 && (var53 = this.q8v3(var1, var4[1])) != null) {
                  u2q5 var78 = this.ai;
                  u2q5.x7q9 var64;
                  boolean var103;
                  if ((var64 = this.ai.cA.get(var42.getUniqueId())) == null
                     || !var64.g1v7()
                     || var64.j4n9() != null && !var64.j4n9().equalsIgnoreCase(var53.O())) {
                     var78.cA.put(var42.getUniqueId(), new u2q5.x7q9(var53.O(), null, null));
                     var103 = false;
                  } else {
                     var53.x7q9(k.ab2c(var64.u2q5(), var64.e7z1()));
                     var78.cA.remove(var42.getUniqueId());
                     var78.y5p8();
                     var78.k2w6(var53);
                     var103 = true;
                  }

                  if (var103) {
                     this.af.ab2c(var42, "region-set", Map.of("arena", var53.O()));
                  } else {
                     this.au.J(var42);
                     this.af.ab2c(var42, "region-selection-started", Map.of("arena", var53.O()));
                  }
               }
               break;
            case "build":
               if (this.m4z1(var1, "ffa.build")) {
                  r var101;
                  String var109;
                  Object var10001;
                  if (var4.length >= 3) {
                     j var41;
                     if ((var41 = this.q8v3(var1, var4[1])) == null) {
                        return true;
                     }

                     if ((var5 = var4[2].toLowerCase(Locale.ROOT)).equals("auto") || var5.equals("kit")) {
                        List var63;
                        if ((var63 = this.ab2c(var41, var4.length >= 4 ? var4[3] : var41.Q())) == null) {
                           this.af.ab2c(var1, "kit-missing", Map.of("kit", var4.length >= 4 ? var4[3] : var41.Q()));
                        } else {
                           var41.q8v3(var63);
                           var41.k2w6(var63);
                           this.ai.y5p8();
                           this.af
                              .ab2c(
                                 var1,
                                 "build-blocks-auto",
                                 Map.of(
                                    "arena",
                                    var41.O(),
                                    "kit",
                                    var4.length >= 4 ? var4[3] : var41.Q(),
                                    "blocks",
                                    var63.isEmpty() ? "none" : String.join(", ", var63)
                                 )
                              );
                        }

                        return true;
                     }

                     if (!var5.equals("break") && !var5.equals("place")) {
                        Boolean var62;
                        if ((var62 = ab2c(var4[2])) != null) {
                           var41.x7q9(var62);
                           this.ai.y5p8();
                           this.af.ab2c(var1, "build-arena-set", Map.of("arena", var41.O(), "state", String.valueOf(var62)));
                           return true;
                        }

                        var101 = this.af;
                        var10001 = var1;
                        var109 = "build-usage";
                     } else if (var4.length < 4) {
                        var101 = this.af;
                        var10001 = var1;
                        var109 = "build-usage";
                     } else {
                        byte var72 = 3;
                        String[] var59 = var4;
                        Object var102;
                        if (var4.length <= 3) {
                           var102 = null;
                        } else if (var4[3].equalsIgnoreCase("clear") || var4[3].equalsIgnoreCase("none")) {
                           var102 = List.of();
                        } else if (var4[3].equalsIgnoreCase("hand")) {
                           ItemStack var27;
                           Player var91;
                           var102 = (var91 = this.q8v3(var1)) == null
                              ? null
                              : (
                                 (var27 = var91.getInventory().getItemInMainHand()) != null && !var27.getType().isAir() && var27.getType().isBlock()
                                    ? List.of(var27.getType().name())
                                    : null
                              );
                        } else if (var4[3].equalsIgnoreCase("hotbar")) {
                           Player var90;
                           if ((var90 = this.q8v3(var1)) == null) {
                              var102 = null;
                           } else {
                              Player var26 = var90;
                              ItemStack[] var60 = new ItemStack[9];

                              for (int var77 = 0; var77 < 9; var77++) {
                                 var60[var77] = var26.getInventory().getItem(var77);
                              }

                              var102 = ab2c(var60);
                           }
                        } else if (var4[3].equalsIgnoreCase("inventory")) {
                           Player var89;
                           var102 = (var89 = this.q8v3(var1)) == null ? null : ab2c(var89.getInventory().getStorageContents());
                        } else if (!var4[3].equalsIgnoreCase("kit") && !var4[3].equalsIgnoreCase("auto")) {
                           ArrayList var88 = new ArrayList();
                           int var10 = 3;

                           label760:
                           while (true) {
                              if (var10 >= var59.length) {
                                 var102 = var88;
                                 break;
                              }

                              String[] var11;
                              int var12 = (var11 = var59[var10].split(",")).length;

                              for (int var25 = 0; var25 < var12; var25++) {
                                 String var73;
                                 String var74;
                                 if (!(var74 = (var73 = var11[var25]).trim().toUpperCase(Locale.ROOT)).isBlank()) {
                                    Material var75;
                                    if ((var75 = Material.matchMaterial(var74)) == null || !var75.isBlock()) {
                                       var102 = null;
                                       break label760;
                                    }

                                    String var76 = var75.name();
                                    if (!var88.contains(var76)) {
                                       var88.add(var76);
                                    }
                                 }
                              }

                              var10++;
                           }
                        } else {
                           String var9 = var4.length > 4 ? var4[4] : var41.Q();
                           var102 = this.ab2c(var41, var9);
                        }

                        var59 = (String[])var102;
                        if (var102 != null) {
                           if (var5.equals("break")) {
                              var41.q8v3(var59);
                           } else {
                              var41.k2w6(var59);
                           }

                           this.ai.y5p8();
                           this.af
                              .ab2c(
                                 var1,
                                 "build-blocks-set",
                                 Map.of("arena", var41.O(), "type", var5, "blocks", var59.isEmpty() ? "none" : String.join(", ", var59))
                              );
                           return true;
                        }

                        var101 = this.af;
                        var10001 = var1;
                        var109 = "build-usage";
                     }
                  } else {
                     Player var40;
                     if ((var40 = this.q8v3(var1)) == null) {
                        return true;
                     }

                     var101 = this.af;
                     var10001 = var40;
                     b var7 = this.am;
                     boolean var10002;
                     if (!this.am.eL.add(var40.getUniqueId())) {
                        var7.eL.remove(var40.getUniqueId());
                        var10002 = false;
                     } else {
                        var10002 = true;
                     }

                     var109 = var10002 ? "build-enabled" : "build-disabled";
                  }

                  var101.h1s7((CommandSender)var10001, var109);
               }
               break;
            case "spectate":
               Player var39;
               if ((var39 = this.q8v3(var1)) != null && this.m4z1(var1, "ffa.spectate") && var4.length >= 2) {
                  if (!this.ae.ah().getBoolean("spectate.enabled", true)) {
                     this.af.ab2c(var1, "feature-disabled", Map.of("feature", "spectate"));
                  } else if (this.am.w(var39)) {
                     this.af.h1s7(var39, "already-in-arena");
                  } else {
                     Player var51;
                     if ((var51 = Bukkit.getPlayerExact(var4[1])) == null) {
                        this.af.ab2c(var39, "player-missing", Map.of("player", var4[1]));
                     } else {
                        b var58 = this.am;
                        boolean var10000;
                        if ((var3 = this.am.v(var51).orElse(null)) != null && !var3.isBlank()) {
                           var58.eI.putIfAbsent(var39.getUniqueId(), var58.x7q9(var39.getInventory().getStorageContents()));
                           var58.eJ.putIfAbsent(var39.getUniqueId(), var58.x7q9(var39.getInventory().getArmorContents()));
                           var58.eK.putIfAbsent(var39.getUniqueId(), b.ab2c(var39.getInventory().getItemInOffHand()));
                           b.B(var39);
                           var58.eG.put(var39.getUniqueId(), var3);
                           var58.eL.remove(var39.getUniqueId());
                           var58.eM.add(var39.getUniqueId());
                           var39.setGameMode(GameMode.SPECTATOR);
                           var39.teleportAsync(var51.getLocation());
                           var10000 = true;
                        } else {
                           var10000 = false;
                        }

                        if (!var10000) {
                           this.af.ab2c(var39, "spectate-target-not-in-arena", Map.of("player", var51.getName()));
                        }
                     }
                  }
               }
               break;
            case "scoreboard":
               if (this.m4z1(var1, "ffa.scoreboard.reload") && var4.length >= 2 && var4[1].equalsIgnoreCase("reload")) {
                  this.ax.run();
                  this.af.h1s7(var1, "scoreboard-reloaded");
               }
               break;
            case "settings":
               if (this.m4z1(var1, "ffa.settings")) {
                  if (var4.length < 2 || var4[1].equalsIgnoreCase("list")) {
                     this.af.z4c9(var1, "settings-list-header");
                     ad.keySet().stream().sorted().forEach(var2x -> {
                        m4z1.ab2c var3x;
                        FileConfiguration var10000x;
                        if ((var3x = ad.get(var2x)).az.equals("config.yml")) {
                           p var4x = this.ae;
                           var10000x = this.ae.gt.getConfig();
                        } else {
                           var10000x = this.ae.h(var3x.az);
                        }

                        FileConfiguration var5x = var10000x;
                        this.af.ab2c(var1, "settings-list-entry", Map.of("setting", var2x, "state", var5x.getBoolean(var3x.aA, true) ? "ON" : "OFF"));
                     });
                     this.af.z4c9(var1, "settings-list-footer");
                  } else if (var4.length < 3) {
                     this.af.h1s7(var1, "settings-usage");
                  } else {
                     String var38 = var4[1].toLowerCase(Locale.ROOT);
                     m4z1.ab2c var50;
                     if ((var50 = ad.get(var38)) == null) {
                        this.af.ab2c(var1, "settings-unknown", Map.of("setting", var38));
                     } else {
                        Boolean var6;
                        if ((var6 = ab2c(var4[2])) == null) {
                           this.af.h1s7(var1, "settings-usage");
                        } else {
                           (var50.k2w6().equals("config.yml") ? this.ae.ah() : this.ae.h(var50.k2w6())).set(var50.r5n0(), var6);
                           if (var50.k2w6().equals("config.yml")) {
                              p var8 = this.ae;
                              this.ae.gt.saveConfig();
                           } else {
                              this.ae.i(var50.k2w6());
                           }

                           this.ax.run();
                           this.af.ab2c(var1, "settings-updated", Map.of("setting", var38, "state", var6 ? "ON" : "OFF"));
                        }
                     }
                  }
               }
               break;
            case "announcements":
               if (this.ab2c(var1, "announcements", true) && this.m4z1(var1, "ffa.reload") && var4.length >= 2 && var4[1].equalsIgnoreCase("reload")) {
                  this.aq.w7k3();
                  this.af.h1s7(var1, "announcements-reloaded");
               }
               break;
            case "afk":
               Player var37;
               if (this.ab2c(var1, "afk", true) && (var37 = this.q8v3(var1)) != null && this.m4z1(var1, "ffa.afk")) {
                  if (var4.length < 2) {
                     this.af.z4c9(var37, "afk-help");
                  } else if (var4[1].equalsIgnoreCase("reload")) {
                     this.ar.t9b4();
                     this.af.h1s7(var37, "afk-reloaded");
                  } else {
                     var5 = var4[1].toLowerCase(Locale.ROOT);
                     this.ar.m4z1(var37, var5);
                     this.af.ab2c(var37, "afk-setup-started", Map.of("zone", var5));
                  }
               }
               break;
            case "spawnpvp":
               this.q8v3(var1, var4);
               break;
            case "totempractice":
            case "totem":
               Player var19;
               if (this.ab2c(var1, "totempractice", true) && (var19 = this.q8v3(var1)) != null && this.m4z1(var1, "ffa.totempractice")) {
                  this.as.ac(var19);
               }
               break;
            case "wand":
            case "ward":
               Player var18;
               if ((var18 = this.q8v3(var1)) != null && this.m4z1(var1, "ffa.arena.edit")) {
                  this.ai.c2f4(var18);
                  this.au.J(var18);
                  this.af.h1s7(var18, "region-wand-given");
               }
               break;
            case "pos1":
               this.ab2c(var1, 1);
               break;
            case "pos2":
               this.ab2c(var1, 2);
               break;
            case "menu":
               Player var17;
               if ((var17 = this.q8v3(var1)) != null && this.ab2c(var1, "ffa", true)) {
                  this.an.a(var17);
               }
               break;
            default:
               this.af.h1s7(var1, "unknown-command");
         }

         return true;
      }
   }

   private void ab2c(CommandSender var1, String[] var2) {
      Player var3;
      if ((var3 = this.q8v3(var1)) != null && this.m4z1(var1, "ffa.join") && var2.length >= 2) {
         if (this.ab2c(var1, "ffa", true)) {
            if (this.am.w(var3)) {
               this.af.h1s7(var3, "already-in-arena");
            } else {
               j var4;
               if ((var4 = this.q8v3(var1, var2[1])) != null) {
                  this.ab2c(var3, var4);
               }
            }
         }
      }
   }

   private void ab2c(Player var1, j var2) {
      if (this.am.w(var1)) {
         this.af.h1s7(var1, "already-in-arena");
      } else if (!var2.g) {
         this.af.ab2c(var1, "arena-disabled", Map.of("arena", var2.O()));
      } else if (!var2.T()) {
         this.af.ab2c(var1, "arena-not-ready", Map.of("arena", var2.O()));
      } else {
         this.am.q8v3(var1, var2);
         this.ap.ad(var1);
         this.af.ab2c(var1, "joined", Map.of("arena", var2.O()));
      }
   }

   private void ab2c(CommandSender var1) {
      Player var2;
      if ((var2 = this.q8v3(var1)) != null && this.m4z1(var1, "ffa.leave")) {
         if (this.am.w(var2)) {
            this.ap.ae(var2);
            this.am.ab2c(var2, this.ai.b8t3());
            this.av.N(var2);
            this.ag.ab2c(var2, () -> {
               if (!this.am.w(var2)) {
                  if (this.ai.b8t3() != null) {
                     var2.teleportAsync(this.ai.b8t3());
                  }

                  this.av.N(var2);
               }
            }, 10L);
            this.af.h1s7(var2, "left");
         }
      }
   }

   private void x7q9(CommandSender var1) {
      Player var2;
      if ((var2 = this.q8v3(var1)) != null) {
         if (this.am.w(var2)) {
            this.ab2c(var1);
         } else if (this.ai.b8t3() == null) {
            this.af.h1s7(var2, "lobby-missing");
         } else {
            var2.teleportAsync(this.ai.b8t3());
         }
      }
   }

   private void x7q9(CommandSender var1, String[] var2) {
      if (this.m4z1(var1, "ffa.arena.edit") && var2.length >= 2) {
         String var3;
         if ((var3 = var2[1].toLowerCase(Locale.ROOT)).equals("regenall")) {
            this.m4z1(var1);
         } else if (List.of("snapshot", "snap", "regenadd", "snapshotadd", "snapadd").contains(var3)) {
            this.m4z1(var1, var2);
         } else if (var2.length >= 3) {
            if (var3.equals("regen") && var2[2].equalsIgnoreCase("all")) {
               this.m4z1(var1);
            } else {
               j var6;
               if ((var6 = this.q8v3(var1, var2[2])) != null) {
                  switch (var3) {
                     case "enable":
                        if (!var6.T()) {
                           this.af.ab2c(var1, "arena-not-ready", Map.of("arena", var6.O()));
                           return;
                        }

                        boolean var8 = true;
                        var6.g = var8;
                        this.ai.y5p8();
                        this.af.ab2c(var1, "arena-enabled", Map.of("arena", var6.O()));
                        this.af.ab2c(var1, "arena-ready", Map.of("arena", var6.O()));
                        return;
                     case "disable":
                        boolean var7 = false;
                        var6.g = var7;
                        this.ai.y5p8();
                        this.af.ab2c(var1, "arena-disabled-done", Map.of("arena", var6.O()));
                        return;
                     case "teleport":
                     case "tp":
                        Player var5;
                        if ((var5 = this.q8v3(var1)) != null && var6.fU != null && var6.fU.ac() != null) {
                           var5.teleportAsync(var6.fU.ac());
                        }

                        return;
                     case "regen":
                        this.aj.z4c9(var6);
                        this.af.ab2c(var1, "arena-regen-started", Map.of("arena", var6.O()));
                        return;
                     default:
                        this.af.h1s7(var1, "unknown-command");
                  }
               }
            }
         }
      }
   }

   private void m4z1(CommandSender var1) {
      if (this.m4z1(var1, "ffa.arena.edit")) {
         this.aj.m4z1(this.ai.arenas());
         this.af.h1s7(var1, "arena-regen-all-started");
      }
   }

   private void m4z1(CommandSender var1, String[] var2) {
      if (var2.length < 3) {
         this.ab2c(var1, "all");
      } else {
         String var3;
         if ((var3 = var2[2].toLowerCase(Locale.ROOT)).equals("folder") || var3.equals("path")) {
            e7z1 var4 = this.aj;
            this.af.ab2c(var1, "arena-regen-snapshot-folder", Map.of("path", this.aj.cP.getAbsolutePath()));
         } else if (var3.equals("status")) {
            this.x7q9(var1, var2.length >= 4 ? var2[3] : "all");
         } else if (!var3.equals("add") && !var3.equals("save") && !var3.equals("create")) {
            this.ab2c(var1, var2[2]);
         } else {
            this.ab2c(var1, var2.length >= 4 ? var2[3] : "all");
         }
      }
   }

   private void ab2c(CommandSender var1, String var2) {
      if (this.m4z1(var1, "ffa.arena.edit")) {
         if (var2 != null && !var2.isBlank() && !var2.equalsIgnoreCase("all")) {
            j var4;
            if ((var4 = this.q8v3(var1, var2)) != null) {
               this.aj.r5n0(var4);
               this.af.ab2c(var1, "arena-regen-snapshot-started", Map.of("arena", var4.O()));
               this.af.ab2c(var1, "arena-regen-snapshot-folder", Map.of("path", this.aj.h1s7(var4)));
            }
         } else {
            this.aj.x7q9(this.ai.arenas());
            this.af.h1s7(var1, "arena-regen-snapshot-all-started");
            e7z1 var3 = this.aj;
            this.af.ab2c(var1, "arena-regen-snapshot-folder", Map.of("path", this.aj.cP.getAbsolutePath()));
         }
      }
   }

   private void x7q9(CommandSender var1, String var2) {
      if (this.m4z1(var1, "ffa.arena.edit")) {
         if (var2 != null && !var2.isBlank() && !var2.equalsIgnoreCase("all")) {
            j var4;
            if ((var4 = this.q8v3(var1, var2)) != null) {
               this.ab2c(var1, var4);
            }
         } else {
            this.ai.arenas().stream().sorted(Comparator.comparing(j::O)).forEach(var2x -> this.ab2c(var1, var2x));
            e7z1 var3 = this.aj;
            this.af.ab2c(var1, "arena-regen-snapshot-folder", Map.of("path", this.aj.cP.getAbsolutePath()));
         }
      }
   }

   private void ab2c(CommandSender var1, j var2) {
      String var3 = this.aj.f3y8(var2)
         ? "arena-regen-snapshot-status-saving"
         : (this.aj.t9b4(var2) ? "arena-regen-snapshot-status-ready" : "arena-regen-snapshot-status-missing");
      this.af.ab2c(var1, var3, Map.of("arena", var2.O(), "path", this.aj.h1s7(var2), "regenerating", String.valueOf(this.aj.p6d2(var2))));
   }

   private void ab2c(CommandSender var1, String[] var2, boolean var3) {
      if (this.m4z1(var1, "ffa.arena.edit") && var2.length >= 2) {
         j var6;
         if ((var6 = this.q8v3(var1, var2[1])) != null) {
            if (var3 && !var6.T()) {
               this.af.ab2c(var1, "arena-not-ready", Map.of("arena", var6.O()));
            } else {
               var6.g = var3;
               this.ai.y5p8();
               this.af.ab2c(var1, var3 ? "arena-started" : "arena-stopped", Map.of("arena", var6.O()));
            }
         }
      }
   }

   private List<String> ab2c(j var1, String var2) {
      String var10000 = var2 != null && !var2.isBlank() ? var2 : var1.Q();
      String var4 = var10000;
      if (var10000 == null || var4.isBlank()) {
         var4 = this.ak.s();
      }

      l var5;
      if ((var5 = this.ak.e7z1(var4).orElse(null)) == null) {
         return null;
      } else {
         ArrayList var6;
         (var6 = new ArrayList()).addAll(var5.o);
         var6.addAll(var5.p);
         var6.add(var5.q);
         var6.add(var5.n);
         return ab2c(var6.toArray(ItemStack[]::new));
      }
   }

   private static List<String> ab2c(ItemStack[] var0) {
      ArrayList var1 = new ArrayList();
      ItemStack[] var5;
      int var2 = (var5 = var0).length;

      for (int var3 = 0; var3 < var2; var3++) {
         ItemStack var4;
         if ((var4 = var5[var3]) != null && !var4.getType().isAir() && var4.getType().isBlock()) {
            String var6 = var4.getType().name();
            if (!var1.contains(var6)) {
               var1.add(var6);
            }
         }
      }

      return var1;
   }

   private void ab2c(CommandSender var1, int var2) {
      Player var3;
      if ((var3 = this.q8v3(var1)) != null && this.m4z1(var1, "ffa.arena.edit")) {
         if (!this.ai.b8t3(var3)) {
            this.ai.c2f4(var3);
         }

         String var5 = this.ai.s6h0(var3).orElse("new arena");
         u2q5.ab2c var4 = this.ai.ab2c(var3, var2, var3.getLocation());
         switch (var4) {
            case cC:
               this.af.h1s7(var3, "region-no-selection");
               return;
            case cD:
               this.af.ab2c(var3, "region-wand-pos1", ab2c(var5, var3.getLocation()));
               return;
            case cE:
               this.af.ab2c(var3, "region-wand-pos2", ab2c(var5, var3.getLocation()));
               return;
            case cF:
               this.af.h1s7(var3, "region-world-mismatch");
               return;
            case cG:
               this.af.ab2c(var3, "region-wand-pos" + var2, ab2c(var5, var3.getLocation()));
               this.af.h1s7(var3, "region-selection-ready");
               return;
            case cH:
               this.af.ab2c(var3, "region-wand-pos" + var2, ab2c(var5, var3.getLocation()));
               this.af.ab2c(var3, "region-set", Map.of("arena", var5));
         }
      }
   }

   private static Map<String, String> ab2c(String var0, Location var1) {
      return Map.of("arena", var0, "x", String.valueOf(var1.getBlockX()), "y", String.valueOf(var1.getBlockY()), "z", String.valueOf(var1.getBlockZ()));
   }

   private void q8v3(CommandSender var1, String[] var2) {
      Player var3;
      if ((var3 = this.q8v3(var1)) != null && this.m4z1(var1, "ffa.spawnpvp")) {
         if (var2.length < 2) {
            this.af.z4c9(var3, "spawnpvp-help");
         } else {
            String var5 = var2[1].toLowerCase(Locale.ROOT);
            switch (var5) {
               case "1":
               case "pos1":
                  this.aw.k2w6(var3, 1);
                  return;
               case "2":
               case "pos2":
                  this.aw.k2w6(var3, 2);
                  return;
               case "wand":
               case "want":
                  this.aw.P(var3);
                  return;
               case "block":
               case "blocksel":
                  if (var2.length >= 3 && var2[2].equalsIgnoreCase("select")) {
                     this.aw.Q(var3);
                     return;
                  }

                  this.af.z4c9(var3, "spawnpvp-help");
                  return;
               case "info":
                  this.aw.S(var3);
                  return;
               case "clear":
               case "delete":
               case "remove":
                  this.aw.R(var3);
                  return;
               case "reload":
                  this.aw.H();
                  this.af.h1s7(var3, "spawnpvp-reloaded");
                  return;
               default:
                  this.af.z4c9(var3, "spawnpvp-help");
            }
         }
      }
   }

   private Player q8v3(CommandSender var1) {
      if (var1 instanceof Player) {
         Player var2;
         return var2 = (Player)var1;
      } else {
         this.af.h1s7(var1, "only-players");
         return null;
      }
   }

   private boolean m4z1(CommandSender var1, String var2) {
      if (!var1.hasPermission("ffa.admin") && !var1.hasPermission(var2)) {
         this.af.h1s7(var1, "no-permission");
         return false;
      } else {
         return true;
      }
   }

   private boolean ab2c(CommandSender var1, String var2, boolean var3) {
      if (this.ae.x7q9(var2, true)) {
         return true;
      } else {
         this.af.ab2c(var1, "feature-disabled", Map.of("feature", var2));
         return false;
      }
   }

   private boolean x7q9(Player var1) {
      if (!this.am.w(var1)) {
         return false;
      } else {
         this.af.h1s7(var1, "kiteditor-blocked-in-ffa");
         return true;
      }
   }

   private static Boolean ab2c(String var0) {
      var0 = var0.toLowerCase(Locale.ROOT);
      switch (var0) {
         case "on":
         case "true":
         case "enable":
         case "enabled":
         case "yes":
            return Boolean.TRUE;
         case "off":
         case "false":
         case "disable":
         case "disabled":
         case "no":
            return Boolean.FALSE;
         default:
            return null;
      }
   }

   private static boolean x7q9(String var0) {
      return List.of("enable", "disable", "teleport", "tp", "regen", "regenall", "snapshot", "snap", "regenadd", "snapshotadd", "snapadd")
         .contains(var0.toLowerCase(Locale.ROOT));
   }

   private void ab2c(CommandSender var1, Runnable var2) {
      if (var1 instanceof Player var3) {
         this.ag.ab2c(var3, var2);
      } else {
         this.ag.x7q9(var2);
      }
   }

   private Void ab2c(CommandSender var1, Throwable var2) {
      y.w7k3("DATABASE", "Command database operation failed for " + var1.getName() + ": " + var2.getMessage());
      this.ab2c(var1, () -> this.af.h1s7(var1, "database-error"));
      return null;
   }

   private j q8v3(CommandSender var1, String var2) {
      return this.ai.arena(var2).orElseGet(() -> {
         this.af.ab2c(var1, "arena-missing", Map.of("arena", var2));
         return null;
      });
   }

   private void ab2c(CommandSender var1, o var2) {
      this.af
         .x7q9(
            var1,
            "stats-format",
            Map.of(
               "player",
               var2.name,
               "kills",
               String.valueOf(var2.u),
               "deaths",
               String.valueOf(var2.v),
               "kdr",
               var2.ae(),
               "killstreak",
               String.valueOf(var2.w),
               "best_killstreak",
               String.valueOf(var2.x),
               "tokens",
               String.valueOf(var2.y),
               "xp",
               String.valueOf(var2.z),
               "level",
               String.valueOf(var2.A)
            )
         );
   }

   public final List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var3.equalsIgnoreCase("arena")) {
         if (var4.length == 1) {
            return ab2c(var4[0], List.of("enable", "disable", "teleport", "regen", "regenall", "snapshot", "snap", "regenadd", "snapshotadd", "snapadd"));
         } else if (var4.length == 2 && x7q9(var4[0])) {
            if (List.of("snapshot", "snap", "regenadd", "snapshotadd", "snapadd").contains(var4[0].toLowerCase(Locale.ROOT))) {
               ArrayList var14;
               (var14 = new ArrayList<>(List.of("add", "status", "folder", "all"))).addAll(this.ai.arenas().stream().map(j::O).toList());
               return ab2c(var4[1], var14);
            } else {
               return var4[0].equalsIgnoreCase("regenall") ? List.of() : ab2c(var4[1], this.ai.arenas().stream().map(j::O).toList());
            }
         } else if (var4.length == 3
            && List.of("snapshot", "snap", "regenadd", "snapshotadd", "snapadd").contains(var4[0].toLowerCase(Locale.ROOT))
            && List.of("add", "status", "save", "create").contains(var4[1].toLowerCase(Locale.ROOT))) {
            ArrayList var13;
            (var13 = new ArrayList<>(this.ai.arenas().stream().map(j::O).toList())).add("all");
            return ab2c(var4[2], var13);
         } else {
            return List.of();
         }
      } else if (var3.equalsIgnoreCase("ffaarena")) {
         return var4.length == 1 ? ab2c(var4[0], this.ai.arenas().stream().map(j::O).toList()) : List.of();
      } else if (var2.getName().equalsIgnoreCase("leave") || var2.getName().equalsIgnoreCase("spawn")) {
         return List.of();
      } else if (var4.length == 1) {
         return ab2c(
            var4[0],
            List.of(
               "help",
               "join",
               "leave",
               "lobby",
               "spawn",
               "setlobby",
               "setspawn",
               "create",
               "delete",
               "list",
               "edit",
               "reload",
               "kit",
               "stats",
               "top",
               "resetstats",
               "arena",
               "regenall",
               "snapshotadd",
               "snapadd",
               "snapshotall",
               "snapall",
               "snapshotstatus",
               "snapstatus",
               "start",
               "stop",
               "setregion",
               "build",
               "spectate",
               "scoreboard",
               "settings",
               "announcements",
               "afk",
               "spawnpvp",
               "totempractice",
               "totem",
               "wand",
               "ward",
               "pos1",
               "pos2",
               "menu"
            )
         );
      } else if (var4.length == 2 && List.of("join", "setspawn", "delete", "edit", "start", "stop", "setregion").contains(var4[0].toLowerCase(Locale.ROOT))) {
         return ab2c(var4[1], this.ai.arenas().stream().map(j::O).toList());
      } else if (var4.length == 2 && var4[0].equalsIgnoreCase("kit")) {
         ArrayList var12;
         (var12 = new ArrayList<>(List.of("give", "set", "link", "remove", "create", "save", "delete", "duplicate", "editor", "edit")))
            .addAll(this.ak.t().stream().map(var0 -> var0.V()).toList());
         return ab2c(var4[1], var12);
      } else if (var4.length == 3
         && var4[0].equalsIgnoreCase("kit")
         && List.of("set", "link", "delete", "duplicate", "editor", "edit").contains(var4[1].toLowerCase(Locale.ROOT))) {
         return ab2c(var4[2], this.ak.t().stream().map(var0 -> var0.V()).toList());
      } else if (var4.length == 4 && var4[0].equalsIgnoreCase("kit") && var4[1].equalsIgnoreCase("link")) {
         return ab2c(var4[3], this.ai.arenas().stream().map(j::O).toList());
      } else if (var4.length == 3 && var4[0].equalsIgnoreCase("kit") && List.of("give", "remove").contains(var4[1].toLowerCase(Locale.ROOT))) {
         return ab2c(var4[2], Bukkit.getOnlinePlayers().stream().<String>map(Player::getName).toList());
      } else if (var4.length == 3 && var4[0].equalsIgnoreCase("kit") && List.of("create", "save").contains(var4[1].toLowerCase(Locale.ROOT))) {
         return ab2c(var4[2], this.ak.t().stream().map(var0 -> var0.V()).toList());
      } else if (var4.length == 3 && var4[0].equalsIgnoreCase("kit")) {
         return ab2c(var4[2], this.ai.arenas().stream().map(j::O).toList());
      } else if (var4.length == 4 && var4[0].equalsIgnoreCase("kit") && var4[1].equalsIgnoreCase("give")) {
         return ab2c(var4[3], this.ak.t().stream().map(var0 -> var0.V()).toList());
      } else if (var4.length == 2 && var4[0].equalsIgnoreCase("arena")) {
         return ab2c(var4[1], List.of("enable", "disable", "teleport", "regen", "regenall", "snapshot", "snap", "regenadd", "snapshotadd", "snapadd"));
      } else if (var4.length == 3 && var4[0].equalsIgnoreCase("arena")) {
         String var11 = var4[1].toLowerCase(Locale.ROOT);
         if (List.of("snapshot", "snap", "regenadd", "snapshotadd", "snapadd").contains(var11)) {
            ArrayList var16;
            (var16 = new ArrayList<>(List.of("add", "status", "folder", "all"))).addAll(this.ai.arenas().stream().map(j::O).toList());
            return ab2c(var4[2], var16);
         } else {
            ArrayList var15 = new ArrayList<>(this.ai.arenas().stream().map(j::O).toList());
            if (List.of("regen").contains(var11)) {
               var15.add("all");
            }

            return ab2c(var4[2], var15);
         }
      } else if (var4.length == 4
         && var4[0].equalsIgnoreCase("arena")
         && List.of("snapshot", "snap", "regenadd", "snapshotadd", "snapadd").contains(var4[1].toLowerCase(Locale.ROOT))
         && List.of("add", "status", "save", "create").contains(var4[2].toLowerCase(Locale.ROOT))) {
         ArrayList var10;
         (var10 = new ArrayList<>(this.ai.arenas().stream().map(j::O).toList())).add("all");
         return ab2c(var4[3], var10);
      } else if (var4.length == 2 && List.of("snapshotadd", "snapadd", "snapshotstatus", "snapstatus").contains(var4[0].toLowerCase(Locale.ROOT))) {
         ArrayList var9;
         (var9 = new ArrayList<>(this.ai.arenas().stream().map(j::O).toList())).add("all");
         return ab2c(var4[1], var9);
      } else if (var4.length == 2 && var4[0].equalsIgnoreCase("scoreboard")) {
         return ab2c(var4[1], List.of("reload"));
      } else if (var4.length == 2 && var4[0].equalsIgnoreCase("settings")) {
         ArrayList var8;
         (var8 = new ArrayList<>(ad.keySet())).add("list");
         return ab2c(var4[1], var8);
      } else if (var4.length == 3 && var4[0].equalsIgnoreCase("settings")) {
         return ab2c(var4[2], List.of("on", "off", "true", "false", "enable", "disable"));
      } else if (var4.length == 2 && var4[0].equalsIgnoreCase("build")) {
         return ab2c(var4[1], this.ai.arenas().stream().map(j::O).toList());
      } else if (var4.length == 3 && var4[0].equalsIgnoreCase("build")) {
         return ab2c(var4[2], List.of("true", "false", "break", "place", "auto", "kit"));
      } else if (var4.length >= 4 && var4[0].equalsIgnoreCase("build") && List.of("break", "place").contains(var4[2].toLowerCase(Locale.ROOT))) {
         ArrayList var7;
         (var7 = new ArrayList<>(List.of("hand", "hotbar", "inventory", "kit", "auto", "clear")))
            .addAll(List.of("COBWEB", "OAK_PLANKS", "STONE", "END_STONE", "OBSIDIAN", "TNT"));
         return ab2c(var4[var4.length - 1], var7);
      } else if (var4.length == 4 && var4[0].equalsIgnoreCase("build") && List.of("auto", "kit").contains(var4[2].toLowerCase(Locale.ROOT))) {
         return ab2c(var4[3], this.ak.t().stream().map(var0 -> var0.V()).toList());
      } else if (var4.length == 2 && var4[0].equalsIgnoreCase("announcements")) {
         return ab2c(var4[1], List.of("reload"));
      } else if (var4.length == 2 && var4[0].equalsIgnoreCase("afk")) {
         g1v7 var5 = this.ar;
         ArrayList var6;
         (var6 = new ArrayList<>(this.ar.cf.keySet())).add("reload");
         return ab2c(var4[1], var6);
      } else if (var4.length == 2 && var4[0].equalsIgnoreCase("spawnpvp")) {
         return ab2c(var4[1], List.of("1", "2", "pos1", "pos2", "wand", "want", "block", "blocksel", "info", "clear", "reload"));
      } else if (var4.length == 3 && var4[0].equalsIgnoreCase("spawnpvp") && List.of("block", "blocksel").contains(var4[1].toLowerCase(Locale.ROOT))) {
         return ab2c(var4[2], List.of("select"));
      } else {
         return var4.length == 2 && List.of("spectate", "stats", "resetstats").contains(var4[0].toLowerCase(Locale.ROOT))
            ? ab2c(var4[1], Bukkit.getOnlinePlayers().stream().<String>map(Player::getName).toList())
            : List.of();
      }
   }

   private static List<String> ab2c(String var0, List<String> var1) {
      var0 = var0.toLowerCase(Locale.ROOT);
      ArrayList var2 = new ArrayList();
      Iterator var5 = var1.iterator();

      while (var5.hasNext()) {
         String var3;
         if ((var3 = (String)var5.next()).toLowerCase(Locale.ROOT).startsWith(var0)) {
            var2.add(var3);
         }
      }

      return var2;
   }

   static record ab2c(String az, String aA) {

      public final String k2w6() {
         return this.az;
      }

      public final String r5n0() {
         return this.aA;
      }
   }
}
