package club.aspvp.ffa.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class t9b4 implements CommandExecutor, TabCompleter {
   private final h aK;
   private final v aL;
   private final r aM;
   private final t aN;

   public t9b4(h var1, v var2, r var3, t var4) {
      this.aK = var1;
      this.aL = var2;
      this.aM = var3;
      this.aN = var4;
   }

   public final boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length != 0 && !var4[0].equalsIgnoreCase("balance")) {
         String var8 = var4[0].toLowerCase(Locale.ROOT);
         switch (var8) {
            case "give":
               this.ab2c(var1, var4, t9b4.ab2c.aO);
               break;
            case "take":
               this.ab2c(var1, var4, t9b4.ab2c.aP);
               break;
            case "set":
               this.ab2c(var1, var4, t9b4.ab2c.aQ);
               break;
            case "giveall":
               t9b4 var7 = this;
               if (!var1.hasPermission("ffa.admin") && !var1.hasPermission("ffa.token.admin")) {
                  this.aM.h1s7(var1, "no-permission");
               } else if (var4.length < 2) {
                  this.aM.z4c9(var1, "token-help");
               } else {
                  Integer var15;
                  if ((var15 = m4z1(var4[1])) == null) {
                     this.aM.h1s7(var1, "token-invalid-amount");
                  } else {
                     int var12 = 0;

                     for (Player var6 : Bukkit.getOnlinePlayers()) {
                        var7.aK.r5n0(var6, var15);
                        var12++;
                     }

                     var7.aM.ab2c(var1, "token-giveall", Map.of("amount", String.valueOf(var15), "players", String.valueOf(var12)));
                  }
               }
               break;
            default:
               this.aM.z4c9(var1, "token-help");
         }

         return true;
      } else {
         if (!var1.hasPermission("ffa.admin") && !var1.hasPermission("ffa.token")) {
            this.aM.h1s7(var1, "no-permission");
         } else if (var4.length < 2) {
            if (var1 instanceof Player var13) {
               this.aM.ab2c(var13, "token-balance", Map.of("tokens", String.valueOf(this.aK.X(var13))));
            } else {
               this.aM.z4c9(var1, "token-help");
            }
         } else {
            Player var14;
            if ((var14 = Bukkit.getPlayerExact(var4[1])) != null) {
               this.aM.ab2c(var1, "token-balance-other", Map.of("player", var14.getName(), "tokens", String.valueOf(this.aK.X(var14))));
            } else {
               this.aL
                  .l(var4[1])
                  .thenAccept(
                     var3x -> this.ab2c(
                           var1,
                           () -> var3x.ifPresentOrElse(
                                 var2xxx -> this.aM.ab2c(var1, "token-balance-other", Map.of("player", var2xxx.name, "tokens", String.valueOf(var2xxx.y))),
                                 () -> this.aM.ab2c(var1, "player-missing", Map.of("player", var4[1]))
                              )
                        )
                  )
                  .exceptionally(var2x -> this.ab2c(var1, var2x));
            }
         }

         return true;
      }
   }

   private void ab2c(CommandSender var1, String[] var2, t9b4.ab2c var3) {
      if (!var1.hasPermission("ffa.admin") && !var1.hasPermission("ffa.token.admin")) {
         this.aM.h1s7(var1, "no-permission");
      } else if (var2.length < 3) {
         this.aM.z4c9(var1, "token-help");
      } else {
         Integer var4;
         if ((var4 = m4z1(var2[2])) == null) {
            this.aM.h1s7(var1, "token-invalid-amount");
         } else {
            Player var5;
            if ((var5 = Bukkit.getPlayerExact(var2[1])) != null) {
               int var6 = switch (var3) {
                  case aO -> this.aK.r5n0(var5, var4);
                  case aP -> this.aK.r5n0(var5, -var4);
                  case aQ -> this.aK.t9b4(var5, var4);
               };
               this.aM.ab2c(var1, var3.aR, Map.of("player", var5.getName(), "amount", String.valueOf(var4), "tokens", String.valueOf(var6)));
            } else {
               this.ab2c(var2[1], var4, var3)
                  .thenAccept(
                     var5x -> this.ab2c(
                           var1,
                           () -> var5x.ifPresentOrElse(
                                 var4xxx -> this.aM
                                       .ab2c(var1, var3.aR, Map.of("player", var4xxx.name, "amount", String.valueOf(var4), "tokens", String.valueOf(var4xxx.y))),
                                 () -> this.aM.ab2c(var1, "player-missing", Map.of("player", var2[1]))
                              )
                        )
                  )
                  .exceptionally(var2x -> this.ab2c(var1, var2x));
            }
         }
      }
   }

   private CompletableFuture<Optional<o>> ab2c(String var1, int var2, t9b4.ab2c var3) {
      return this.aL.l(var1).thenCompose(var3x -> {
         if (var3x.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.empty());
         } else {
            o var4 = var3x.get();
            switch (var3) {
               case aO:
                  var4.p6d2(var4.y + var2);
                  break;
               case aP:
                  var4.p6d2(var4.y - var2);
                  break;
               case aQ:
                  var4.p6d2(var2);
            }

            return this.aL.x7q9(var4).thenApply(var1xx -> Optional.of(var4));
         }
      });
   }

   private static Integer m4z1(String var0) {
      try {
         int var2;
         return (var2 = Integer.parseInt(var0)) > 0 ? var2 : null;
      } catch (NumberFormatException var1) {
         return null;
      }
   }

   private void ab2c(CommandSender var1, Runnable var2) {
      if (var1 instanceof Player var3) {
         this.aN.ab2c(var3, var2);
      } else {
         this.aN.x7q9(var2);
      }
   }

   private Void ab2c(CommandSender var1, Throwable var2) {
      y.w7k3("DATABASE", "Token command failed for " + var1.getName() + ": " + var2.getMessage());
      this.ab2c(var1, () -> this.aM.h1s7(var1, "database-error"));
      return null;
   }

   public final List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length == 1) {
         return ab2c(var4[0], List.of("balance", "give", "take", "set", "giveall"));
      } else {
         return var4.length == 2 && List.of("balance", "give", "take", "set").contains(var4[0].toLowerCase(Locale.ROOT))
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

   static enum ab2c {
      aO("token-given"),
      aP("token-taken"),
      aQ("token-set");

      final String aR;

      private ab2c(String var3) {
         this.aR = var3;
      }
   }
}
