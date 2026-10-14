package club.aspvp.ffa;

import club.aspvp.ffa.api.TwoCubeFFAAPI;
import club.aspvp.ffa.hook.FfaPlaceholderExpansion;
import club.aspvp.ffa.internal.a;
import club.aspvp.ffa.internal.a7r4;
import club.aspvp.ffa.internal.ab2c;
import club.aspvp.ffa.internal.b;
import club.aspvp.ffa.internal.b8t3;
import club.aspvp.ffa.internal.c;
import club.aspvp.ffa.internal.c2f4;
import club.aspvp.ffa.internal.d;
import club.aspvp.ffa.internal.d9r1;
import club.aspvp.ffa.internal.e;
import club.aspvp.ffa.internal.e7z1;
import club.aspvp.ffa.internal.f;
import club.aspvp.ffa.internal.f3y8;
import club.aspvp.ffa.internal.g;
import club.aspvp.ffa.internal.g1v7;
import club.aspvp.ffa.internal.h;
import club.aspvp.ffa.internal.i;
import club.aspvp.ffa.internal.i5k2;
import club.aspvp.ffa.internal.j;
import club.aspvp.ffa.internal.j4n9;
import club.aspvp.ffa.internal.k2w6;
import club.aspvp.ffa.internal.l9m6;
import club.aspvp.ffa.internal.m4z1;
import club.aspvp.ffa.internal.m8p0;
import club.aspvp.ffa.internal.n2x6;
import club.aspvp.ffa.internal.o;
import club.aspvp.ffa.internal.o3w8;
import club.aspvp.ffa.internal.p;
import club.aspvp.ffa.internal.q;
import club.aspvp.ffa.internal.q1d9;
import club.aspvp.ffa.internal.q8v3;
import club.aspvp.ffa.internal.r;
import club.aspvp.ffa.internal.r5n0;
import club.aspvp.ffa.internal.s;
import club.aspvp.ffa.internal.s6h0;
import club.aspvp.ffa.internal.t;
import club.aspvp.ffa.internal.t9b4;
import club.aspvp.ffa.internal.u;
import club.aspvp.ffa.internal.u2q5;
import club.aspvp.ffa.internal.v;
import club.aspvp.ffa.internal.v8m5;
import club.aspvp.ffa.internal.w7k3;
import club.aspvp.ffa.internal.x;
import club.aspvp.ffa.internal.x6y3;
import club.aspvp.ffa.internal.x7q9;
import club.aspvp.ffa.internal.y;
import club.aspvp.ffa.internal.y5p8;
import club.aspvp.ffa.internal.z;
import club.aspvp.ffa.internal.z4c9;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class TwoCubeFFA extends JavaPlugin {
   private p ab2c;
   private r x7q9;
   private t m4z1;
   private s q8v3;
   private u k2w6;
   private q r5n0;
   private v t9b4;
   private u2q5 f3y8;
   private e7z1 p6d2;
   private m8p0 h1s7;
   private h z4c9;
   private b v8m5;
   private c n2x6;
   private a w7k3;
   private q1d9 d9r1;
   private j4n9 y5p8;
   private g1v7 c2f4;
   private i s6h0;
   private x6y3 b8t3;
   private l9m6 g1v7;
   private d j4n9;
   private f u2q5;
   private g e7z1;
   private i5k2 l9m6;
   private o3w8 o3w8;
   private e i5k2;
   private a7r4 a7r4;
   private ab2c q1d9;
   private static final String PRODUCT_ID = "4";
   private club.aspvp.license.LicenseClient licenseClient;
   private club.aspvp.ffa.updater.PluginUpdater pluginUpdater;

   public void onEnable() {
      this.saveDefaultConfig();
      String licenseKey = this.getConfig().getString("license-key", "");
      this.licenseClient = new club.aspvp.license.LicenseClient(this, PRODUCT_ID, licenseKey);
      this.licenseClient.verifyOnStartup().thenAccept(isValid -> {
         if (isValid) {
            this.licenseClient.startHeartbeat(432000L);
            this.registerPluginFeatures();
            this.pluginUpdater = new club.aspvp.ffa.updater.PluginUpdater(this);
            this.pluginUpdater.checkForUpdates(null);
         }
      });
   }

   private void registerPluginFeatures() {
      long var1 = System.nanoTime();
      if (!this.ab2c()) {
         Bukkit.getPluginManager().disablePlugin(this);
         return;
      }
         this.ab2c = new p(this);
         this.ab2c.af();
         p var4 = this.ab2c;
         y.cb = this;
         y.ab2c = var4;
         this.m4z1 = new t(this);
         this.t9b4 = new v(this, this.ab2c, this.m4z1);
         this.t9b4.am().exceptionally(var1x -> {
            this.getLogger().severe("Database startup failed: " + var1x.getMessage());
            this.m4z1.x7q9(() -> Bukkit.getPluginManager().disablePlugin(this));
            return null;
         });
         this.f3y8 = new u2q5(this.ab2c, this.t9b4);
         this.p6d2 = new e7z1(this, this.ab2c, this.m4z1);
         e7z1 var12 = this.p6d2;
         u2q5 var6 = this.f3y8;
         this.f3y8.p6d2 = var12;
         this.h1s7 = new m8p0(this.ab2c, this.t9b4);
         this.f3y8.d9r1();
         this.p6d2.ab2c(this.f3y8.arenas());
         this.h1s7.p();
         this.z4c9 = new h(this.ab2c, this.t9b4);
         this.n2x6 = new c(this.t9b4);
         this.w7k3 = new a(this.t9b4, this.m4z1);
         this.v8m5 = new b(this.ab2c, this.t9b4, this.h1s7, this.w7k3, this.m4z1);
         this.p6d2.ab2c(this.f3y8::arenas, var1x -> this.v8m5.i5k2(var1x.O()) == 0L);
         this.l9m6 = new i5k2(this.ab2c, this.t9b4, this.m4z1, this.v8m5);
         this.q8v3 = new s(this.ab2c, this.z4c9, this.v8m5, this.f3y8, this.l9m6);
         this.k2w6 = new u(this.q8v3);
         this.k2w6.x7q9(() -> r.ab2c(this.ab2c).getString("prefix", r.ab2c(this.ab2c, "prefix", "")));
         this.r5n0 = new q(this.ab2c, this.m4z1, this.v8m5, this.n2x6, this.z4c9, this.k2w6);
         this.x7q9 = new r(this.ab2c, this.k2w6);
         this.d9r1 = new q1d9(this.ab2c, this.k2w6, this.m4z1);
         c var13 = this.n2x6;
         q1d9 var7 = this.d9r1;
         this.d9r1.n2x6 = var13;
         this.y5p8 = new j4n9(this.ab2c, this.m4z1, this.k2w6, this.n2x6);
         this.c2f4 = new g1v7(this, this.m4z1, this.k2w6);
         this.c2f4.t9b4();
         this.s6h0 = new i(this.ab2c, this.k2w6);
         this.b8t3 = new x6y3(this.ab2c, this.k2w6, this.h1s7, this.w7k3);
         this.g1v7 = new l9m6(this.ab2c, this.k2w6, this.b8t3);
         this.j4n9 = new d(this, this.k2w6);
         this.u2q5 = new f(this.ab2c, this.k2w6);
         this.e7z1 = new g(this.ab2c, this.x7q9, this.k2w6);
         this.o3w8 = new o3w8(this.ab2c, this.k2w6, this.m4z1, this.t9b4);
         o3w8 var14 = this.o3w8;
         s var8 = this.q8v3;
         this.q8v3.o3w8 = var14;
         this.i5k2 = new e(this.ab2c, this.m4z1, this.v8m5, this.n2x6, this.k2w6);
         this.a7r4 = new a7r4(this.ab2c, this.m4z1, this.v8m5, this.n2x6, this.k2w6);
         this.q1d9 = new ab2c(this.f3y8, this.h1s7, this.v8m5, this.z4c9, this.r5n0, this.b8t3, this.o3w8, this.l9m6, this::x7q9);
         Bukkit.getServicesManager().register(TwoCubeFFAAPI.class, this.q1d9, this, ServicePriority.Normal);
         Bukkit.getServicesManager().register(club.aspvp.ffa.api.FlixCoreFFAAPI.class, this.q1d9, this, ServicePriority.Normal);
         RegisteredServiceProvider var9;
         if ((var9 = Bukkit.getServicesManager().getRegistration(TwoCubeFFAAPI.class)) != null && var9.getProvider() == this.q1d9) {
            m4z1 var15 = new m4z1(
               this.ab2c,
               this.x7q9,
               this.m4z1,
               this.t9b4,
               this.f3y8,
               this.p6d2,
               this.h1s7,
               this.z4c9,
               this.v8m5,
               this.d9r1,
               this.i5k2,
               this.r5n0,
               this.y5p8,
               this.s6h0,
               this.b8t3,
               this.j4n9,
               this.u2q5,
               this.e7z1,
               this.c2f4,
               this::x7q9
            );
            PluginCommand var5;
            for (String varCmd : new String[]{"ffa", "flixffa", "flixcoreffa"}) {
               if ((var5 = this.getCommand(varCmd)) != null) {
                  final org.bukkit.command.CommandExecutor originalExec = var15;
                  final org.bukkit.command.TabCompleter originalTab = var15;
                  var5.setExecutor((sender, command, label, args) -> {
                     if (args.length > 0 && args[0].equalsIgnoreCase("update")) {
                        if (!sender.hasPermission("ffa.admin")) {
                           sender.sendMessage("§cYou do not have permission to use this command.");
                           return true;
                        }
                        if (this.pluginUpdater != null) {
                           this.pluginUpdater.downloadAndInstall(sender);
                        } else {
                           sender.sendMessage("§cUpdater service is not initialized.");
                        }
                        return true;
                     }
                     return originalExec.onCommand(sender, command, label, args);
                  });
                  var5.setTabCompleter((sender, command, alias, args) -> {
                     java.util.List<String> list = originalTab.onTabComplete(sender, command, alias, args);
                     if (args.length == 1 && sender.hasPermission("ffa.admin")) {
                        java.util.List<String> result = list == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(list);
                        if ("update".startsWith(args[0].toLowerCase())) {
                           result.add("update");
                        }
                        return result;
                     }
                     return list;
                  });
               }
            }

            if ((var5 = this.getCommand("leave")) != null) {
               var5.setExecutor(var15);
               var5.setTabCompleter(var15);
            }

            if ((var5 = this.getCommand("spawn")) != null) {
               var5.setExecutor(var15);
               var5.setTabCompleter(var15);
            }

            r5n0 var16 = new r5n0(this.d9r1, this.x7q9);
            if ((var5 = this.getCommand("settings")) != null) {
               var5.setExecutor(var16);
            }

            q8v3 var17 = new q8v3(this.x7q9);
            if ((var5 = this.getCommand("hat")) != null) {
               var5.setExecutor(var17);
            }

            k2w6 var18 = new k2w6(this.ab2c, this.b8t3, this.h1s7, this.v8m5, this.d9r1, this.x7q9);
            if ((var5 = this.getCommand("kiteditor")) != null) {
               var5.setExecutor(var18);
               var5.setTabCompleter(var18);
            }

            t9b4 var19 = new t9b4(this.z4c9, this.t9b4, this.x7q9, this.m4z1);
            if ((var5 = this.getCommand("token")) != null) {
               var5.setExecutor(var19);
               var5.setTabCompleter(var19);
            }

            f3y8 var20 = new f3y8(this.ab2c, this.s6h0, this.x7q9);
            if ((var5 = this.getCommand("totempractice")) != null) {
               var5.setExecutor(var20);
            }

            x7q9 var21 = new x7q9(this.ab2c, this.x7q9, this.o3w8);
            if ((var5 = this.getCommand("tags")) != null) {
               var5.setExecutor(var21);
               var5.setTabCompleter(var21);
            }

            PluginCommand var10;
            if ((var10 = this.getCommand("chatcolour")) != null) {
               var10.setExecutor(var21);
               var10.setTabCompleter(var21);
            }

            Bukkit.getPluginManager()
               .registerEvents(
                  new w7k3(this.ab2c, this.m4z1, this.f3y8, this.v8m5, this.z4c9, this.r5n0, this.u2q5, this.i5k2, this.a7r4, this.b8t3, this.n2x6, this.p6d2),
                  this
               );
            Bukkit.getPluginManager()
               .registerEvents(new d9r1(this.d9r1, this.b8t3, this.g1v7, this.s6h0, this.o3w8, this.v8m5, this.u2q5, this.x7q9, this.m4z1, this::x7q9), this);
            Bukkit.getPluginManager().registerEvents(new n2x6(this.ab2c, this.k2w6, this.o3w8), this);
            Bukkit.getPluginManager().registerEvents(new b8t3(this.s6h0), this);
            Bukkit.getPluginManager().registerEvents(new c2f4(this.u2q5, this.v8m5, this.e7z1), this);
            Bukkit.getPluginManager().registerEvents(new s6h0(this.e7z1), this);
            Bukkit.getPluginManager().registerEvents(new z4c9(this.c2f4), this);
            Bukkit.getPluginManager().registerEvents(new y5p8(this.f3y8, this.j4n9, this.x7q9), this);
            Bukkit.getPluginManager().registerEvents(new v8m5(this.ab2c, this.v8m5, this.x7q9), this);
            this.i5k2.y();
            this.a7r4.m();
            this.y5p8.v8m5();
            this.c2f4.f3y8();
            this.l9m6.g();
            Bukkit.getOnlinePlayers().forEach(var1x -> {
               this.z4c9.V(var1x);
               this.n2x6.E(var1x);
               this.v8m5.u(var1x);
               this.o3w8.g1v7(var1x);
            });
            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
               new FfaPlaceholderExpansion(this, this.z4c9, this.v8m5, this.f3y8, this.t9b4, this.m4z1, this.l9m6, this.o3w8, "ffa").register();
               new FfaPlaceholderExpansion(this, this.z4c9, this.v8m5, this.f3y8, this.t9b4, this.m4z1, this.l9m6, this.o3w8, "flixffa").register();
               new FfaPlaceholderExpansion(this, this.z4c9, this.v8m5, this.f3y8, this.t9b4, this.m4z1, this.l9m6, this.o3w8, "flixcoreffa").register();
               new FfaPlaceholderExpansion(this, this.z4c9, this.v8m5, this.f3y8, this.t9b4, this.m4z1, this.l9m6, this.o3w8, "twocubeffa").register();
               this.getLogger().info("PlaceholderAPI hook registered.");
            }

            t var11 = this.m4z1;
            x.ab2c(this, this.ab2c, this.m4z1.gQ, this.f3y8.arenas().size(), this.h1s7.t().size(), this.l9m6.i(), (System.nanoTime() - var1) / 1000000L);
         } else {
            this.getLogger().severe("FlixCore FFA API service registration failed.");
            Bukkit.getPluginManager().disablePlugin(this);
         }
   }

   public void onDisable() {
      Bukkit.getServicesManager().unregisterAll(this);
      if (this.i5k2 != null) {
         e var1 = this.i5k2;
         this.i5k2.ab2c(false);
      }

      if (this.a7r4 != null) {
         this.a7r4.n();
      }

      if (this.y5p8 != null) {
         this.y5p8.n2x6();
      }

      if (this.c2f4 != null) {
         this.c2f4.p6d2();
      }

      if (this.l9m6 != null) {
         this.l9m6.h();
      }

      if (this.p6d2 != null && this.f3y8 != null) {
         this.p6d2.l9m6();
         e7z1 var10000 = this.p6d2;
         Collection<j> var2 = this.f3y8.arenas();
         e7z1 var5 = var10000;
         if (var10000.cN.gt.getConfig().getBoolean("arena-regen.regenerate-on-shutdown", true)) {
            for (j var9 : var2) {
               var5.v8m5(var9);
            }
         }
      }

      if (this.m4z1 != null) {
         t var6 = this.m4z1;
         this.m4z1.gP.shutdown();

         try {
            if (!var6.gP.awaitTermination(15L, TimeUnit.SECONDS)) {
               var6.gO.getLogger().warning("Database tasks did not finish within 15 seconds; forcing shutdown.");
               var6.gP.shutdownNow();
            }
         } catch (InterruptedException var4) {
            Thread.currentThread().interrupt();
            var6.gP.shutdownNow();
         }
      }

      if (this.z4c9 != null) {
         h var7 = this.z4c9;

         for (o var11 : this.z4c9.fJ.values()) {
            var7.fI.m4z1(var11);
         }
      }

      if (this.t9b4 != null) {
         v var8 = this.t9b4;
         if (this.t9b4.ha != null) {
            var8.ha.close();
         }
      }

      if (this.ab2c != null) {
         x.ab2c(this, this.ab2c);
      }
   }

   private boolean ab2c() {
      String[] required = new String[]{
         "com.zaxxer.hikari.HikariConfig",
         "org.sqlite.JDBC",
         "com.mysql.cj.jdbc.Driver",
         "org.slf4j.LoggerFactory"
      };
      ClassLoader cl = this.getClass().getClassLoader();
      for (String lib : required) {
         try {
            Class.forName(lib, false, cl);
         } catch (ClassNotFoundException e) {
            this.getLogger().severe("Required Paper library did not load: " + lib);
            return false;
         }
      }
      return true;
   }

   private void x7q9() {
      this.ab2c.ag();
      this.f3y8.d9r1();
      this.p6d2.ab2c(this.f3y8.arenas());
      this.h1s7.p();
      this.o3w8.b();
      this.i5k2.y();
      this.a7r4.m();
      this.y5p8.w7k3();
      this.c2f4.t9b4();
      this.c2f4.f3y8();
      this.l9m6.g();
      x.x7q9(this.ab2c);
   }
}
