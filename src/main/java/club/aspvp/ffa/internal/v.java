package club.aspvp.ffa.internal;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import club.aspvp.ffa.TwoCubeFFA;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class v {
   private final JavaPlugin gX;
   private final p gY;
   private final t gZ;
   public HikariDataSource ha;
   private String type;
   private volatile CompletableFuture<Void> hb = CompletableFuture.completedFuture(null);
   private final Map<UUID, CompletableFuture<Void>> hc = new ConcurrentHashMap<>();

   public v(TwoCubeFFA var1, p var2, t var3) {
      this.gX = var1;
      this.gY = var2;
      this.gZ = var3;
   }

   public final CompletableFuture<Void> am() {
      this.hb = this.gZ
         .m4z1(
            () -> {
               try {
                  FileConfiguration var2;
                  String var3 = (var2 = this.gY.h("database.yml")).getString("database", var2.getString("storage.type", "SQLITE"));
                  this.type = var3.equalsIgnoreCase("mysql") ? "mysql" : "sqlite";
                  HikariConfig var4;
                  (var4 = new HikariConfig()).setPoolName("FlixCoreFFA-" + this.type);
                  var4.setMaximumPoolSize(this.type.equals("mysql") ? var2.getInt("mysql.pool.maximum-pool-size", var2.getInt("mysql.pool-size", 10)) : 1);
                  var4.setConnectionTimeout(var2.getLong("mysql.pool.connection-timeout-ms", var2.getLong("mysql.connection-timeout-ms", 10000L)));
                  if (this.type.equals("mysql")) {
                     var4.setDriverClassName("com.mysql.cj.jdbc.Driver");
                     if ((var3 = var2.getString("mysql.params", null)) == null || var3.isBlank()) {
                        var3 = "useSSL=" + var2.getBoolean("mysql.use-ssl", false) + "&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=utf8";
                     }

                     String var5 = "jdbc:mysql://"
                        + var2.getString("mysql.host", "127.0.0.1")
                        + ":"
                        + var2.getInt("mysql.port", 3306)
                        + "/"
                        + var2.getString("mysql.database", "FlixCoreFFA")
                        + "?"
                        + var3;
                     var4.setJdbcUrl(var5);
                     var4.setUsername(var2.getString("mysql.user", var2.getString("mysql.username", "root")));
                     var4.setPassword(var2.getString("mysql.password"));
                  } else {
                     var4.setDriverClassName("org.sqlite.JDBC");
                     File var48;
                     File var56;
                     if ((var56 = (var48 = this.ab2c(var2)).getParentFile()) != null && !var56.exists()) {
                        var56.mkdirs();
                     }

                     var4.setJdbcUrl("jdbc:sqlite:" + var48.getAbsolutePath());
                  }

                  this.ha = new HikariDataSource(var4);
                  v var1 = this;

                  try (
                     Connection var42 = this.ao();
                     Statement var49 = var42.createStatement();
                  ) {
                     var49.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS ffa_stats (\n  uuid VARCHAR(36) PRIMARY KEY,\n  name VARCHAR(32) NOT NULL,\n  kills INT NOT NULL DEFAULT 0,\n  deaths INT NOT NULL DEFAULT 0,\n  killstreak INT NOT NULL DEFAULT 0,\n  best_killstreak INT NOT NULL DEFAULT 0,\n  coins INT NOT NULL DEFAULT 0,\n  points INT NOT NULL DEFAULT 0,\n  tokens INT NOT NULL DEFAULT 0,\n  xp INT NOT NULL DEFAULT 0,\n  level INT NOT NULL DEFAULT 1\n)\n"
                     );
                     ab2c(var42, "ffa_stats", "tokens", "INT NOT NULL DEFAULT 0");
                     var49.executeUpdate("CREATE TABLE IF NOT EXISTS ffa_selected_kits (\n  uuid VARCHAR(36) PRIMARY KEY,\n  kit VARCHAR(64) NOT NULL\n)\n");
                     var49.executeUpdate("CREATE TABLE IF NOT EXISTS ffa_snapshots (\n  snapshot_key VARCHAR(64) PRIMARY KEY,\n  yaml LONGTEXT NOT NULL\n)\n");
                     var49.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS ffa_player_kits (\n  uuid VARCHAR(36) NOT NULL,\n  kit_id VARCHAR(64) NOT NULL,\n  yaml LONGTEXT NOT NULL,\n  updated_at BIGINT NOT NULL DEFAULT 0,\n  PRIMARY KEY(uuid, kit_id)\n)\n"
                     );
                     ab2c(var42, "ffa_player_kits", "updated_at", "BIGINT NOT NULL DEFAULT 0");
                     var49.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS ffa_chat_preferences (\n  uuid VARCHAR(36) PRIMARY KEY,\n  tag VARCHAR(64) NOT NULL DEFAULT '',\n  chat_colour VARCHAR(64) NOT NULL DEFAULT ''\n)\n"
                     );
                     var49.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS ffa_player_settings (\n  uuid VARCHAR(36) PRIMARY KEY,\n  scoreboard BOOLEAN NOT NULL DEFAULT 1,\n  kill_messages BOOLEAN NOT NULL DEFAULT 1,\n  announcements BOOLEAN NOT NULL DEFAULT 1,\n  actionbar BOOLEAN NOT NULL DEFAULT 1,\n  sounds BOOLEAN NOT NULL DEFAULT 1,\n  titles BOOLEAN NOT NULL DEFAULT 1\n)\n"
                     );
                     ab2c(var42, "ffa_player_settings", "actionbar", "BOOLEAN NOT NULL DEFAULT 1");
                     ab2c(var42, "ffa_player_settings", "sounds", "BOOLEAN NOT NULL DEFAULT 1");
                     ab2c(var42, "ffa_player_settings", "titles", "BOOLEAN NOT NULL DEFAULT 1");
                     var49.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS ffa_server_status (\n  server_id VARCHAR(64) PRIMARY KEY,\n  last_seen BIGINT NOT NULL,\n  online_players INT NOT NULL DEFAULT 0,\n  ffa_players INT NOT NULL DEFAULT 0\n)\n"
                     );
                     if ("mysql".equals(var1.type)) {
                        var49.executeUpdate(
                           "CREATE TABLE IF NOT EXISTS ffa_crossserver_requests (\n  id INTEGER PRIMARY KEY AUTO_INCREMENT,\n  source_server VARCHAR(64) NOT NULL,\n  target_server VARCHAR(64) NOT NULL,\n  request_type VARCHAR(64) NOT NULL,\n  payload LONGTEXT NOT NULL,\n  created_at BIGINT NOT NULL,\n  processed BOOLEAN NOT NULL DEFAULT 0\n)\n"
                        );
                     } else {
                        var49.executeUpdate(
                           "CREATE TABLE IF NOT EXISTS ffa_crossserver_requests (\n  id INTEGER PRIMARY KEY AUTOINCREMENT,\n  source_server VARCHAR(64) NOT NULL,\n  target_server VARCHAR(64) NOT NULL,\n  request_type VARCHAR(64) NOT NULL,\n  payload LONGTEXT NOT NULL,\n  created_at BIGINT NOT NULL,\n  processed BOOLEAN NOT NULL DEFAULT 0\n)\n"
                        );
                     }
                  }

                  var1 = this;
                  var2 = this.gY.h("database.yml");
                  if ("mysql".equals(this.type) && var2.getBoolean("migrate.sqlite-to-mysql", var2.getBoolean("storage.migrate-sqlite-to-mysql", false))) {
                     this.gX.getLogger().info("SQLite to MySQL migration is enabled. Existing MySQL rows will be upserted.");
                     File var50;
                     if ((var50 = this.ab2c(var2)).exists()) {
                        (var4 = new HikariConfig()).setDriverClassName("org.sqlite.JDBC");
                        var4.setJdbcUrl("jdbc:sqlite:" + var50.getAbsolutePath());
                        var4.setMaximumPoolSize(1);

                        try {
                           HikariDataSource var51 = new HikariDataSource(var4);

                           try (Connection var57 = var51.getConnection()) {
                              try (
                                 Statement var44 = var57.createStatement();
                                 ResultSet var53 = var44.executeQuery("SELECT * FROM ffa_stats");
                              ) {
                                 while (var53.next()) {
                                    var1.q8v3(var1.ab2c(var53));
                                 }
                              }

                              try (
                                 Statement var45 = var57.createStatement();
                                 ResultSet var54 = var45.executeQuery("SELECT uuid,kit FROM ffa_selected_kits");
                              ) {
                                 while (var54.next()) {
                                    try (
                                       Connection var6 = var1.ao();
                                       PreparedStatement var7 = var6.prepareStatement(var1.ar());
                                    ) {
                                       var7.setString(1, var54.getString("uuid"));
                                       var7.setString(2, var54.getString("kit"));
                                       var7.executeUpdate();
                                    }
                                 }
                              } catch (SQLException var33) {
                                 var1.gX.getLogger().warning("SQLite migration of ffa_selected_kits skipped: " + var33.getMessage());
                              }

                              try (
                                 Statement var46 = var57.createStatement();
                                 ResultSet var55 = var46.executeQuery("SELECT uuid,kit_id,yaml FROM ffa_player_kits");
                              ) {
                                 while (var55.next()) {
                                    try (
                                       Connection var58 = var1.ao();
                                       PreparedStatement var59 = var58.prepareStatement(var1.as());
                                    ) {
                                       var59.setString(1, var55.getString("uuid"));
                                       var59.setString(2, var55.getString("kit_id"));
                                       var59.setString(3, var55.getString("yaml"));
                                       var59.setLong(4, System.currentTimeMillis());
                                       var59.executeUpdate();
                                    }
                                 }
                              } catch (SQLException var30) {
                                 var1.gX.getLogger().warning("SQLite migration of ffa_player_kits skipped: " + var30.getMessage());
                              }
                           } catch (Throwable var37) {
                              try {
                                 var51.close();
                              } catch (Throwable var11) {
                                 var37.addSuppressed(var11);
                              }

                              throw var37;
                           }

                           var51.close();
                        } catch (SQLException var38) {
                           var1.gX.getLogger().warning("SQLite migration skipped: " + var38.getMessage());
                        }
                     }
                  }

                  y.w7k3("DATABASE", "Database started using " + this.type.toUpperCase() + ".");
               } catch (SQLException var40) {
                  throw new IllegalStateException("Database startup failed", var40);
               }
            }
         );
      return this.hb;
   }

   public final CompletableFuture<o> m4z1(UUID var1, String var2) {
      return this.gZ.ab2c(() -> {
         this.ap();

         try {
            try (
               Connection var3 = this.ao();
               PreparedStatement var4 = var3.prepareStatement("SELECT * FROM ffa_stats WHERE uuid=?");
            ) {
               var4.setString(1, var1.toString());

               try (ResultSet var5 = var4.executeQuery()) {
                  if (var5.next()) {
                     return this.ab2c(var5);
                  }
               }

               o var15 = new o(var1, var2);
               this.q8v3(var15);
               return var15;
            }
         } catch (SQLException var12) {
            throw new IllegalStateException(var12);
         }
      });
   }

   public final CompletableFuture<Optional<o>> l(String var1) {
      return this.gZ.ab2c(() -> {
         this.ap();

         try {
            Optional var4;
            try (
               Connection var2 = this.ao();
               PreparedStatement var3 = var2.prepareStatement("SELECT * FROM ffa_stats WHERE LOWER(name)=LOWER(?)");
            ) {
               var3.setString(1, var1);

               try (ResultSet var12 = var3.executeQuery()) {
                  var4 = var12.next() ? Optional.of(this.ab2c(var12)) : Optional.empty();
               }
            }

            return var4;
         } catch (SQLException var11) {
            throw new IllegalStateException(var11);
         }
      });
   }

   public final CompletableFuture<Void> x7q9(o var1) {
      o var2;
      o var10000 = var2 = new o(var1.gq, var1.name);
      int var4 = var1.u;
      var10000.u = var4;
      var4 = var1.v;
      var2.v = var4;
      var2.f3y8(var1.w);
      var4 = var1.x;
      var2.x = var4;
      var4 = var1.gr;
      var2.gr = var4;
      var4 = var1.gs;
      var2.gs = var4;
      var2.p6d2(var1.y);
      var4 = var1.z;
      var2.z = var4;
      var2.h1s7(var1.A);
      UUID var6 = var2.gq;
      return this.hc.compute(var6, (var3, var4x) -> {
         CompletableFuture var5;
         (var5 = (var4x == null ? CompletableFuture.completedFuture(null) : var4x.handle((var0, var1xx) -> null)).thenCompose(var2xx -> this.gZ.m4z1(() -> {
               this.ap();

               try {
                  this.q8v3(var2);
               } catch (SQLException var2xxx) {
                  throw new IllegalStateException(var2xxx);
               }
            }))).whenComplete((var3x, var4xx) -> this.hc.remove(var6, var5));
         return var5;
      });
   }

   public final void m4z1(o var1) {
      try {
         this.ap();
         this.q8v3(var1);
      } catch (RuntimeException | SQLException var3) {
         this.gX.getLogger().warning("Could not save stats for " + var1.name + " during shutdown: " + var3.getMessage());
      }
   }

   public final CompletableFuture<Void> q8v3(UUID var1, String var2) {
      return this.gZ.m4z1(() -> {
         this.ap();

         try {
            this.q8v3(new o(var1, var2));
         } catch (SQLException var3) {
            throw new IllegalStateException(var3);
         }
      });
   }

   public final CompletableFuture<List<o>> x7q9(String var1, int var2) {
      String finalCol = List.of("kills", "best_killstreak", "xp", "level", "tokens").contains(var1) ? var1 : "kills";
      return this.gZ.ab2c(() -> {
         this.ap();
         ArrayList var3 = new ArrayList();

         try {
            try (
               Connection var4x = this.ao();
               PreparedStatement var12 = var4x.prepareStatement("SELECT * FROM ffa_stats ORDER BY " + finalCol + " DESC LIMIT ?");
            ) {
               var12.setInt(1, var2);

               try (ResultSet var13 = var12.executeQuery()) {
                  while (var13.next()) {
                     var3.add(this.ab2c(var13));
                  }
               }

               return var3;
            }
         } catch (SQLException var11) {
            throw new IllegalStateException(var11);
         }
      });
   }

   public final CompletableFuture<Void> k2w6(UUID var1, String var2) {
      return this.gZ.m4z1(() -> {
         this.ap();

         try {
            try (
               Connection var3 = this.ao();
               PreparedStatement var4 = var3.prepareStatement(this.ar());
            ) {
               var4.setString(1, var1.toString());
               var4.setString(2, var2);
               var4.executeUpdate();
               y.w7k3("DATABASE", "Saved selected kit '" + var2 + "' for " + var1 + ".");
            }
         } catch (SQLException var9) {
            throw new IllegalStateException(var9);
         }
      });
   }

   public final CompletableFuture<Optional<String>> q8v3(UUID var1) {
      return this.gZ.ab2c(() -> {
         this.ap();

         try {
            Optional var4;
            try (
               Connection var2 = this.ao();
               PreparedStatement var3 = var2.prepareStatement("SELECT kit FROM ffa_selected_kits WHERE uuid=?");
            ) {
               var3.setString(1, var1.toString());

               try (ResultSet var12 = var3.executeQuery()) {
                  var4 = var12.next() ? Optional.ofNullable(var12.getString("kit")) : Optional.empty();
               }
            }

            return var4;
         } catch (SQLException var11) {
            throw new IllegalStateException(var11);
         }
      });
   }

   public final CompletableFuture<v.ab2c> k2w6(UUID var1) {
      return this.gZ.ab2c(() -> {
         this.ap();

         try {
            v.ab2c var4;
            try (
               Connection var2 = this.ao();
               PreparedStatement var3 = var2.prepareStatement("SELECT tag,chat_colour FROM ffa_chat_preferences WHERE uuid=?");
            ) {
               var3.setString(1, var1.toString());

               try (ResultSet var12 = var3.executeQuery()) {
                  var4 = var12.next() ? new v.ab2c(var12.getString("tag"), var12.getString("chat_colour")) : new v.ab2c("", "");
               }
            }

            return var4;
         } catch (SQLException var11) {
            throw new IllegalStateException(var11);
         }
      });
   }

   public final CompletableFuture<Void> ab2c(UUID var1, String var2, String var3) {
      return this.gZ
         .m4z1(
            () -> {
               this.ap();

               try {
                  try (
                     Connection var4 = this.ao();
                     PreparedStatement var5 = var4.prepareStatement(
                        "mysql".equals(this.type)
                           ? "INSERT INTO ffa_chat_preferences(uuid,tag,chat_colour) VALUES(?,?,?) ON DUPLICATE KEY UPDATE tag=VALUES(tag),chat_colour=VALUES(chat_colour)"
                           : "INSERT INTO ffa_chat_preferences(uuid,tag,chat_colour) VALUES(?,?,?) ON CONFLICT(uuid) DO UPDATE SET tag=excluded.tag,chat_colour=excluded.chat_colour"
                     );
                  ) {
                     var5.setString(1, var1.toString());
                     var5.setString(2, var2 == null ? "" : var2);
                     var5.setString(3, var3 == null ? "" : var3);
                     var5.executeUpdate();
                  }
               } catch (SQLException var10) {
                  throw new IllegalStateException(var10);
               }
            }
         );
   }

   public final CompletableFuture<n> r5n0(UUID var1) {
      return this.gZ
         .ab2c(
            () -> {
               this.ap();

               try {
                  try (
                     Connection var2 = this.ao();
                     PreparedStatement var3 = var2.prepareStatement(
                        "SELECT scoreboard,kill_messages,announcements,actionbar,sounds,titles FROM ffa_player_settings WHERE uuid=?"
                     );
                  ) {
                     var3.setString(1, var1.toString());

                     try (ResultSet var4 = var3.executeQuery()) {
                        if (var4.next()) {
                           return new n(
                              var4.getBoolean("scoreboard"),
                              var4.getBoolean("kill_messages"),
                              var4.getBoolean("announcements"),
                              ab2c(var4, "actionbar", true),
                              ab2c(var4, "sounds", true),
                              ab2c(var4, "titles", true)
                           );
                        }
                     }

                     n var14 = n.ad();
                     this.ab2c(var2, var1, var14);
                     return var14;
                  }
               } catch (SQLException var11) {
                  throw new IllegalStateException(var11);
               }
            }
         );
   }

   public final CompletableFuture<Void> x7q9(UUID var1, n var2) {
      return this.gZ.m4z1(() -> {
         this.ap();

         try {
            try (Connection var3 = this.ao()) {
               this.ab2c(var3, var1, var2);
            }
         } catch (SQLException var6) {
            throw new IllegalStateException(var6);
         }
      });
   }

   public final CompletableFuture<Void> x7q9(UUID var1, String var2, String var3) {
      return this.gZ.m4z1(() -> {
         this.ap();

         try {
            try (
               Connection var4 = this.ao();
               PreparedStatement var5 = var4.prepareStatement(this.as());
            ) {
               var5.setString(1, var1.toString());
               var5.setString(2, var2);
               var5.setString(3, var3);
               var5.setLong(4, System.currentTimeMillis());
               var5.executeUpdate();
               y.w7k3("DATABASE", "Saved personal kit '" + var2 + "' for " + var1 + ".");
            }
         } catch (SQLException var10) {
            throw new IllegalStateException(var10);
         }
      });
   }

   public final CompletableFuture<Map<String, String>> t9b4(UUID var1) {
      return this.gZ.ab2c(() -> {
         this.ap();
         HashMap var2 = new HashMap();

         try {
            try (
               Connection var3 = this.ao();
               PreparedStatement var4 = var3.prepareStatement("SELECT kit_id,yaml FROM ffa_player_kits WHERE uuid=?");
            ) {
               var4.setString(1, var1.toString());

               try (ResultSet var12 = var4.executeQuery()) {
                  while (var12.next()) {
                     var2.put(var12.getString("kit_id").toLowerCase(), var12.getString("yaml"));
                  }
               }

               return var2;
            }
         } catch (SQLException var11) {
            throw new IllegalStateException(var11);
         }
      });
   }

   public final CompletableFuture<Void> z4c9(String var1, String var2) {
      return this.gZ
         .m4z1(
            () -> {
               this.ap();

               try {
                  try (
                     Connection var3 = this.ao();
                     PreparedStatement var4 = var3.prepareStatement(
                        "mysql".equals(this.type)
                           ? "INSERT INTO ffa_snapshots(snapshot_key,yaml) VALUES(?,?) ON DUPLICATE KEY UPDATE yaml=VALUES(yaml)"
                           : "INSERT INTO ffa_snapshots(snapshot_key,yaml) VALUES(?,?) ON CONFLICT(snapshot_key) DO UPDATE SET yaml=excluded.yaml"
                     );
                  ) {
                     var4.setString(1, var1);
                     var4.setString(2, var2);
                     var4.executeUpdate();
                  }
               } catch (SQLException var9) {
                  throw new IllegalStateException(var9);
               }
            }
         );
   }

   public final CompletableFuture<Void> ab2c(String var1, long var2, int var4, int var5) {
      return this.gZ
         .m4z1(
            () -> {
               this.ap();

               try {
                  try (
                     Connection var6 = this.ao();
                     PreparedStatement var7 = var6.prepareStatement(
                        "mysql".equals(this.type)
                           ? "INSERT INTO ffa_server_status(server_id,last_seen,online_players,ffa_players) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE last_seen=VALUES(last_seen),online_players=VALUES(online_players),ffa_players=VALUES(ffa_players)"
                           : "INSERT INTO ffa_server_status(server_id,last_seen,online_players,ffa_players) VALUES(?,?,?,?) ON CONFLICT(server_id) DO UPDATE SET last_seen=excluded.last_seen,online_players=excluded.online_players,ffa_players=excluded.ffa_players"
                     );
                  ) {
                     var7.setString(1, var1);
                     var7.setLong(2, var2);
                     var7.setInt(3, var4);
                     var7.setInt(4, var5);
                     var7.executeUpdate();
                  }
               } catch (SQLException var12) {
                  throw new IllegalStateException(var12);
               }
            }
         );
   }

   public final CompletableFuture<Map<String, Long>> an() {
      return this.gZ.ab2c(() -> {
         this.ap();
         HashMap var1 = new HashMap();

         try {
            try (
               Connection var2 = this.ao();
               PreparedStatement var3 = var2.prepareStatement("SELECT server_id,last_seen FROM ffa_server_status");
               ResultSet var4 = var3.executeQuery();
            ) {
               while (var4.next()) {
                  var1.put(var4.getString("server_id"), var4.getLong("last_seen"));
               }

               var1 = var1;
            }

            return var1;
         } catch (SQLException var11) {
            throw new IllegalStateException(var11);
         }
      });
   }

   public final CompletableFuture<Boolean> ab2c(String var1, String var2, String var3, String var4, long var5) {
      return this.gZ
         .ab2c(
            () -> {
               this.ap();

               try {
                  try (
                     Connection var7 = this.ao();
                     PreparedStatement var8 = var7.prepareStatement(
                        "INSERT INTO ffa_crossserver_requests(source_server,target_server,request_type,payload,created_at,processed)\nVALUES(?,?,?,?,?,0)\n"
                     );
                  ) {
                     var8.setString(1, var1);
                     var8.setString(2, var2);
                     var8.setString(3, var3);
                     var8.setString(4, var4 == null ? "" : var4);
                     var8.setLong(5, var5);
                     return var8.executeUpdate() > 0;
                  }
               } catch (SQLException var13) {
                  throw new IllegalStateException(var13);
               }
            }
         );
   }

   public final CompletableFuture<List<v.x7q9>> ab2c(String var1, long var2) {
      return this.gZ
         .ab2c(
            () -> {
               this.ap();
               ArrayList<v.x7q9> var4 = new ArrayList<>();
               ArrayList<v.x7q9> var5 = new ArrayList<>();

               try {
                  ArrayList var23;
                  try (Connection var6 = this.ao()) {
                     try (PreparedStatement var7 = var6.prepareStatement("UPDATE ffa_crossserver_requests SET processed=1 WHERE processed=0 AND created_at<?")) {
                        var7.setLong(1, var2);
                        var7.executeUpdate();
                     }

                     try (PreparedStatement var21 = var6.prepareStatement(
                           "SELECT id,source_server,target_server,request_type,payload,created_at\nFROM ffa_crossserver_requests\nWHERE target_server=? AND processed=0 AND created_at>=?\nORDER BY id ASC LIMIT 100\n"
                        )) {
                        var21.setString(1, var1);
                        var21.setLong(2, var2);

                        try (ResultSet var19 = var21.executeQuery()) {
                           while (var19.next()) {
                              var5.add(
                                 new v.x7q9(
                                    var19.getLong("id"),
                                    var19.getString("source_server"),
                                    var19.getString("target_server"),
                                    var19.getString("request_type"),
                                    var19.getString("payload"),
                                    var19.getLong("created_at")
                                 )
                              );
                           }
                        }
                     }

                     try (PreparedStatement var22 = var6.prepareStatement("UPDATE ffa_crossserver_requests SET processed=1 WHERE id=? AND processed=0")) {
                        for (v.x7q9 var2x : var5) {
                           var22.setLong(1, var2x.hf);
                           if (var22.executeUpdate() == 1) {
                              var4.add(var2x);
                           }
                        }
                     }

                     return var4;
                  }
               } catch (SQLException var18) {
                  throw new IllegalStateException(var18);
               }
            }
         );
   }

   private Connection ao() throws SQLException {
      if (this.ha == null) {
         throw new SQLException("Data source is not ready yet");
      } else {
         return this.ha.getConnection();
      }
   }

   private File ab2c(FileConfiguration var1) {
      String var3 = var1.getString("sqlite.file", "flixcoreffa.db");
      File var2;
      return !(var2 = new File(var3)).isAbsolute() && !var3.contains("/") && !var3.contains("\\") ? new File(this.gX.getDataFolder(), var3) : var2;
   }

   private void ap() {
      CompletableFuture var1 = this.hb;
      if (this.hb != null) {
         var1.join();
      }
   }

   private o ab2c(ResultSet var1) throws SQLException {
      o var2;
      o var10000 = var2 = new o(UUID.fromString(var1.getString("uuid")), var1.getString("name"));
      int var4 = var1.getInt("kills");
      var10000.u = var4;
      var4 = var1.getInt("deaths");
      var2.v = var4;
      var2.f3y8(var1.getInt("killstreak"));
      var4 = var1.getInt("best_killstreak");
      var2.x = var4;
      var4 = var1.getInt("coins");
      var2.gr = var4;
      var4 = var1.getInt("points");
      var2.gs = var4;
      var2.p6d2(ab2c(var1, "tokens", 0));
      var4 = var1.getInt("xp");
      var2.z = var4;
      var2.h1s7(var1.getInt("level"));
      return var2;
   }

   private void q8v3(o var1) throws SQLException {
      try (
         Connection var2 = this.ao();
         PreparedStatement var3 = var2.prepareStatement(this.aq());
      ) {
         var3.setString(1, var1.gq.toString());
         var3.setString(2, var1.name);
         var3.setInt(3, var1.u);
         var3.setInt(4, var1.v);
         var3.setInt(5, var1.w);
         var3.setInt(6, var1.x);
         var3.setInt(7, var1.gr);
         var3.setInt(8, var1.gs);
         var3.setInt(9, var1.y);
         var3.setInt(10, var1.z);
         var3.setInt(11, var1.A);
         var3.executeUpdate();
      }
   }

   private String aq() {
      return "mysql".equals(this.type)
         ? "INSERT INTO ffa_stats(uuid,name,kills,deaths,killstreak,best_killstreak,coins,points,tokens,xp,level)\nVALUES(?,?,?,?,?,?,?,?,?,?,?)\nON DUPLICATE KEY UPDATE name=VALUES(name),kills=VALUES(kills),deaths=VALUES(deaths),\nkillstreak=VALUES(killstreak),best_killstreak=VALUES(best_killstreak),coins=VALUES(coins),\npoints=VALUES(points),tokens=VALUES(tokens),xp=VALUES(xp),level=VALUES(level)\n"
         : "INSERT INTO ffa_stats(uuid,name,kills,deaths,killstreak,best_killstreak,coins,points,tokens,xp,level)\nVALUES(?,?,?,?,?,?,?,?,?,?,?)\nON CONFLICT(uuid) DO UPDATE SET name=excluded.name,kills=excluded.kills,deaths=excluded.deaths,\nkillstreak=excluded.killstreak,best_killstreak=excluded.best_killstreak,coins=excluded.coins,\npoints=excluded.points,tokens=excluded.tokens,xp=excluded.xp,level=excluded.level\n";
   }

   private static void ab2c(Connection var0, String var1, String var2, String var3) throws SQLException {
      try (ResultSet var4 = var0.getMetaData().getColumns(null, null, var1, var2)) {
         if (var4.next()) {
            return;
         }
      }

      try {
         Statement var11 = var0.createStatement();

         try {
            var11.executeUpdate("ALTER TABLE " + var1 + " ADD COLUMN " + var2 + " " + var3);
         } catch (Throwable var7) {
            if (var11 != null) {
               try {
                  var11.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var11 == null) {
            return;
         }

         var11.close();
      } catch (SQLException var8) {
         String var10;
         if (!(var10 = var8.getMessage() == null ? "" : var8.getMessage().toLowerCase()).contains("duplicate") && !var10.contains("exists")) {
            throw var8;
         }
      }
   }

   private static int ab2c(ResultSet var0, String var1, int var2) throws SQLException {
      try {
         var0.findColumn(var1);
         return var0.getInt(var1);
      } catch (SQLException var3) {
         return 0;
      }
   }

   private static boolean ab2c(ResultSet var0, String var1, boolean var2) throws SQLException {
      try {
         var0.findColumn(var1);
         return var0.getBoolean(var1);
      } catch (SQLException var3) {
         return true;
      }
   }

   private String ar() {
      return "mysql".equals(this.type)
         ? "INSERT INTO ffa_selected_kits(uuid,kit) VALUES(?,?) ON DUPLICATE KEY UPDATE kit=VALUES(kit)"
         : "INSERT INTO ffa_selected_kits(uuid,kit) VALUES(?,?) ON CONFLICT(uuid) DO UPDATE SET kit=excluded.kit";
   }

   private String as() {
      return "mysql".equals(this.type)
         ? "INSERT INTO ffa_player_kits(uuid,kit_id,yaml,updated_at) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE yaml=VALUES(yaml),updated_at=VALUES(updated_at)"
         : "INSERT INTO ffa_player_kits(uuid,kit_id,yaml,updated_at) VALUES(?,?,?,?) ON CONFLICT(uuid,kit_id) DO UPDATE SET yaml=excluded.yaml,updated_at=excluded.updated_at";
   }

   private void ab2c(Connection var1, UUID var2, n var3) throws SQLException {
      try (PreparedStatement var6 = var1.prepareStatement(
            "mysql".equals(this.type)
               ? "INSERT INTO ffa_player_settings(uuid,scoreboard,kill_messages,announcements,actionbar,sounds,titles) VALUES(?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE scoreboard=VALUES(scoreboard),kill_messages=VALUES(kill_messages),announcements=VALUES(announcements),actionbar=VALUES(actionbar),sounds=VALUES(sounds),titles=VALUES(titles)"
               : "INSERT INTO ffa_player_settings(uuid,scoreboard,kill_messages,announcements,actionbar,sounds,titles) VALUES(?,?,?,?,?,?,?) ON CONFLICT(uuid) DO UPDATE SET scoreboard=excluded.scoreboard,kill_messages=excluded.kill_messages,announcements=excluded.announcements,actionbar=excluded.actionbar,sounds=excluded.sounds,titles=excluded.titles"
         )) {
         var6.setString(1, var2.toString());
         var6.setBoolean(2, var3.gk);
         var6.setBoolean(3, var3.gl);
         var6.setBoolean(4, var3.gm);
         var6.setBoolean(5, var3.gn);
         var6.setBoolean(6, var3.go);
         var6.setBoolean(7, var3.gp);
         var6.executeUpdate();
      }
   }

   public static final class ab2c {
      public final String hd;
      public final String he;

      public ab2c(String hd, String he) {
         this.hd = hd;
         this.he = he;
      }

      public String hd() { return hd; }
      public String he() { return he; }
   }

   public static final class x7q9 {
      public final long hf;
      public final String hg;
      public final String hh;
      public final String hi;
      public final String hj;
      public final long hk;

      public x7q9(long hf, String hg, String hh, String hi, String hj, long hk) {
         this.hf = hf;
         this.hg = hg;
         this.hh = hh;
         this.hi = hi;
         this.hj = hj;
         this.hk = hk;
      }

      public long hf() { return hf; }
      public String hg() { return hg; }
      public String hh() { return hh; }
      public String hi() { return hi; }
      public String hj() { return hj; }
      public long hk() { return hk; }
   }
}
