package club.aspvp.ffa.internal;

import club.aspvp.ffa.TwoCubeFFA;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public final class x {
   public static void ab2c(TwoCubeFFA param0, p param1, boolean param2, int param3, int param4, boolean param5, long param6) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.exps.Exprent.processSforms(org.jetbrains.java.decompiler.modules.decompiler.sforms.SFormsConstructor, org.jetbrains.java.decompiler.modules.decompiler.sforms.VarMapHolder, org.jetbrains.java.decompiler.modules.decompiler.stats.Statement, boolean)" because "expr" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.sforms.SFormsConstructor.ssaStatements(SFormsConstructor.java:126)
      //   at org.jetbrains.java.decompiler.modules.decompiler.sforms.SFormsConstructor.splitVariables(SFormsConstructor.java:94)
      //   at org.jetbrains.java.decompiler.modules.decompiler.StackVarsProcessor.simplifyStackVars(StackVarsProcessor.java:52)
      //   at org.jetbrains.java.decompiler.modules.decompiler.StackVarsProcessor.simplifyStackVars(StackVarsProcessor.java:40)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:292)
      //
      // Bytecode:
      // 000: ldc "Core"
      // 002: aload 0
      // 003: invokevirtual org/bukkit/plugin/java/JavaPlugin.getPluginMeta ()Lio/papermc/paper/plugin/configuration/PluginMeta;
      // 006: invokeinterface io/papermc/paper/plugin/configuration/PluginMeta.getVersion ()Ljava/lang/String; 1
      // 00b: invokestatic org/bukkit/Bukkit.getName ()Ljava/lang/String;
      // 00e: invokestatic org/bukkit/Bukkit.getBukkitVersion ()Ljava/lang/String;
      // 011: ldc "java.version"
      // 013: invokestatic java/lang/System.getProperty (Ljava/lang/String;)Ljava/lang/String;
      // 016: invokedynamic makeConcatWithConstants (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "&aLoaded &fTwoCubeFFA &7v\u0001 &8| &7Software: &f\u0001 &8| &7API: &f\u0001 &8| &7Java: &f\u0001" ]
      // 01b: invokestatic club/aspvp/ffa/internal/x.n2x6 (Ljava/lang/String;Ljava/lang/String;)V
      // 01e: ldc "Scheduler"
      // 020: iload 2
      // 021: ifeq 029
      // 024: ldc "Folia region scheduler"
      // 026: goto 02b
      // 029: ldc "Paper scheduler"
      // 02b: invokedynamic makeConcatWithConstants (Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "&aStarted &8| &7Mode: &f\u0001" ]
      // 030: invokestatic club/aspvp/ffa/internal/x.n2x6 (Ljava/lang/String;Ljava/lang/String;)V
      // 033: ldc "Database"
      // 035: aload 1
      // 036: invokestatic club/aspvp/ffa/internal/x.m4z1 (Lclub/aspvp/ffa/internal/p;)Ljava/lang/String;
      // 039: aload 1
      // 03a: ldc "database.yml"
      // 03c: invokevirtual club/aspvp/ffa/internal/p.h (Ljava/lang/String;)Lorg/bukkit/configuration/file/FileConfiguration;
      // 03f: ldc "cache-refresh-seconds"
      // 041: bipush 15
      // 043: invokevirtual org/bukkit/configuration/file/FileConfiguration.getInt (Ljava/lang/String;I)I
      // 046: invokedynamic makeConcatWithConstants (Ljava/lang/String;I)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "&aReady &8| &7Storage: &f\u0001 &8| &7Cache: &f\u0001s" ]
      // 04b: invokestatic club/aspvp/ffa/internal/x.n2x6 (Ljava/lang/String;Ljava/lang/String;)V
      // 04e: ldc "Data"
      // 050: iload 3
      // 051: iload 4
      // 053: aload 1
      // 054: ldc "spawnitem.yml"
      // 056: invokevirtual club/aspvp/ffa/internal/p.h (Ljava/lang/String;)Lorg/bukkit/configuration/file/FileConfiguration;
      // 059: ldc "items"
      // 05b: invokestatic club/aspvp/ffa/internal/x.m4z1 (Lorg/bukkit/configuration/file/FileConfiguration;Ljava/lang/String;)I
      // 05e: invokedynamic makeConcatWithConstants (III)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "&aLoaded &f\u0001 &7arenas &8| &f\u0001 &7kits &8| &f\u0001 &7spawn items" ]
      // 063: invokestatic club/aspvp/ffa/internal/x.n2x6 (Ljava/lang/String;Ljava/lang/String;)V
      // 066: ldc "Menus"
      // 068: aload 1
      // 069: astore 0
      // 06a: bipush 9
      // 06c: anewarray 79
      // 06f: dup
      // 070: bipush 0
      // 071: ldc "ffa_menu.yml"
      // 073: aastore
      // 074: dup
      // 075: bipush 1
      // 076: ldc "kiteditor.yml"
      // 078: aastore
      // 079: dup
      // 07a: bipush 2
      // 07b: ldc "kiteditor_menu.yml"
      // 07d: aastore
      // 07e: dup
      // 07f: bipush 3
      // 080: ldc "armor_trimmer.yml"
      // 082: aastore
      // 083: dup
      // 084: bipush 4
      // 085: ldc "totempractice.yml"
      // 087: aastore
      // 088: dup
      // 089: bipush 5
      // 08a: ldc "tags.yml"
      // 08c: aastore
      // 08d: dup
      // 08e: bipush 6
      // 090: ldc "tags_gui.yml"
      // 092: aastore
      // 093: dup
      // 094: bipush 7
      // 096: ldc "chat_colours.yml"
      // 098: aastore
      // 099: dup
      // 09a: bipush 8
      // 09c: ldc "chatcolour_gui.yml"
      // 09e: aastore
      // 09f: astore 2
      // 0a0: bipush 0
      // 0a1: istore 3
      // 0a2: aload 2
      // 0a3: dup
      // 0a4: astore 2
      // 0a5: arraylength
      // 0a6: pop
      // 0a7: bipush 0
      // 0a8: istore 4
      // 0aa: iload 4
      // 0ac: bipush 9
      // 0ae: if_icmpge 0c9
      // 0b1: aload 2
      // 0b2: iload 4
      // 0b4: aaload
      // 0b5: astore 8
      // 0b7: aload 0
      // 0b8: aload 8
      // 0ba: invokevirtual club/aspvp/ffa/internal/p.h (Ljava/lang/String;)Lorg/bukkit/configuration/file/FileConfiguration;
      // 0bd: ifnull 0c3
      // 0c0: iinc 3 1
      // 0c3: iinc 4 1
      // 0c6: goto 0aa
      // 0c9: iload 3
      // 0ca: invokedynamic makeConcatWithConstants (I)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "&aLoaded &f\u0001 &7menu files &8| &7Player GUI: &fmenus/ffa.yml" ]
      // 0cf: invokestatic club/aspvp/ffa/internal/x.n2x6 (Ljava/lang/String;Ljava/lang/String;)V
      // 0d2: ldc "Display"
      // 0d4: aload 1
      // 0d5: ldc "scoreboard.yml"
      // 0d7: invokevirtual club/aspvp/ffa/internal/p.h (Ljava/lang/String;)Lorg/bukkit/configuration/file/FileConfiguration;
      // 0da: ldc "enabled"
      // 0dc: invokestatic club/aspvp/ffa/internal/x.x7q9 (Lorg/bukkit/configuration/file/FileConfiguration;Ljava/lang/String;)Ljava/lang/String;
      // 0df: aload 1
      // 0e0: ldc "display.yml"
      // 0e2: invokevirtual club/aspvp/ffa/internal/p.h (Ljava/lang/String;)Lorg/bukkit/configuration/file/FileConfiguration;
      // 0e5: ldc "actionbar.enabled"
      // 0e7: invokestatic club/aspvp/ffa/internal/x.x7q9 (Lorg/bukkit/configuration/file/FileConfiguration;Ljava/lang/String;)Ljava/lang/String;
      // 0ea: aload 1
      // 0eb: ldc "titles.yml"
      // 0ed: ldc "titles"
      // 0ef: invokevirtual club/aspvp/ffa/internal/p.p6d2 (Ljava/lang/String;Ljava/lang/String;)Lorg/bukkit/configuration/file/FileConfiguration;
      // 0f2: ldc "titles"
      // 0f4: invokestatic club/aspvp/ffa/internal/x.m4z1 (Lorg/bukkit/configuration/file/FileConfiguration;Ljava/lang/String;)I
      // 0f7: invokedynamic makeConcatWithConstants (Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "&aLoaded &7scoreboard: \u0001 &8| &7actionbar: \u0001 &8| &7titles: &a\u0001 entries" ]
      // 0fc: invokestatic club/aspvp/ffa/internal/x.n2x6 (Ljava/lang/String;Ljava/lang/String;)V
      // 0ff: ldc "Titles"
      // 101: aload 1
      // 102: invokevirtual club/aspvp/ffa/internal/p.ai ()Ljava/lang/String;
      // 105: invokedynamic makeConcatWithConstants (Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "&aVerified &7edit title messages in &flang/titles_\u0001.yml" ]
      // 10a: invokestatic club/aspvp/ffa/internal/x.n2x6 (Ljava/lang/String;Ljava/lang/String;)V
      // 10d: ldc "Features"
      // 10f: aload 1
      // 110: ldc "spawnpvp.yml"
      // 112: invokevirtual club/aspvp/ffa/internal/p.h (Ljava/lang/String;)Lorg/bukkit/configuration/file/FileConfiguration;
      // 115: ldc "enabled"
      // 117: invokestatic club/aspvp/ffa/internal/x.x7q9 (Lorg/bukkit/configuration/file/FileConfiguration;Ljava/lang/String;)Ljava/lang/String;
      // 11a: aload 1
      // 11b: ldc "announcements.yml"
      // 11d: invokevirtual club/aspvp/ffa/internal/p.h (Ljava/lang/String;)Lorg/bukkit/configuration/file/FileConfiguration;
      // 120: ldc "enabled"
      // 122: invokestatic club/aspvp/ffa/internal/x.x7q9 (Lorg/bukkit/configuration/file/FileConfiguration;Ljava/lang/String;)Ljava/lang/String;
      // 125: aload 1
      // 126: dup
      // 127: astore 0
      // 128: getfield club/aspvp/ffa/internal/p.gt Lorg/bukkit/plugin/java/JavaPlugin;
      // 12b: invokevirtual org/bukkit/plugin/java/JavaPlugin.getConfig ()Lorg/bukkit/configuration/file/FileConfiguration;
      // 12e: ldc "debug.enabled"
      // 130: bipush 0
      // 131: invokevirtual org/bukkit/configuration/file/FileConfiguration.getBoolean (Ljava/lang/String;Z)Z
      // 134: ifeq 13c
      // 137: ldc "&aON"
      // 139: goto 13e
      // 13c: ldc "&cOFF"
      // 13e: invokedynamic makeConcatWithConstants (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "&7SpawnPvP: \u0001 &8| &7Announcements: \u0001 &8| &7Debug: \u0001" ]
      // 143: invokestatic club/aspvp/ffa/internal/x.n2x6 (Ljava/lang/String;Ljava/lang/String;)V
      // 146: ldc "CrossServer"
      // 148: iload 5
      // 14a: ifeq 162
      // 14d: aload 1
      // 14e: ldc "crossserver.yml"
      // 150: invokevirtual club/aspvp/ffa/internal/p.h (Ljava/lang/String;)Lorg/bukkit/configuration/file/FileConfiguration;
      // 153: ldc "server-id"
      // 155: ldc "ffa-1"
      // 157: invokevirtual org/bukkit/configuration/file/FileConfiguration.getString (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
      // 15a: invokedynamic makeConcatWithConstants (Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "&aON &8| &7Server ID: &f\u0001" ]
      // 15f: goto 164
      // 162: ldc "&cOFF"
      // 164: invokestatic club/aspvp/ffa/internal/x.n2x6 (Ljava/lang/String;Ljava/lang/String;)V
      // 167: ldc "Hooks"
      // 169: invokestatic club/aspvp/ffa/internal/x.av ()Ljava/lang/String;
      // 16c: invokestatic club/aspvp/ffa/internal/x.n2x6 (Ljava/lang/String;Ljava/lang/String;)V
      // 16f: ldc "API"
      // 171: ldc "&aRegistered &7Bukkit service: &fclub.aspvp.ffa.api.TwoCubeFFAAPI"
      // 173: invokestatic club/aspvp/ffa/internal/x.n2x6 (Ljava/lang/String;Ljava/lang/String;)V
      // 176: ldc "Ready"
      // 178: lload 6
      // 17a: invokedynamic makeConcatWithConstants (J)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "&aEnabled successfully in &f\u0001ms" ]
      // 17f: invokestatic club/aspvp/ffa/internal/x.n2x6 (Ljava/lang/String;Ljava/lang/String;)V
      // 182: ldc "STARTUP"
      // 184: invokestatic org/bukkit/Bukkit.getName ()Ljava/lang/String;
      // 187: invokestatic org/bukkit/Bukkit.getVersion ()Ljava/lang/String;
      // 18a: invokestatic org/bukkit/Bukkit.getBukkitVersion ()Ljava/lang/String;
      // 18d: aload 1
      // 18e: invokestatic club/aspvp/ffa/internal/x.m4z1 (Lclub/aspvp/ffa/internal/p;)Ljava/lang/String;
      // 191: invokestatic club/aspvp/ffa/internal/x.av ()Ljava/lang/String;
      // 194: iload 5
      // 196: invokedynamic makeConcatWithConstants (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "Server=\u0001, version=\u0001, bukkit=\u0001, storage=\u0001, hooks=\u0001, crossServer=\u0001" ]
      // 19b: invokestatic club/aspvp/ffa/internal/y.w7k3 (Ljava/lang/String;Ljava/lang/String;)V
      // 19e: return
   }

   public static void ab2c(TwoCubeFFA var0, p var1) {
      if (var1 != null) {
         n2x6("Shutdown", "&cDisabled &8| &7Version: &f" + var0.getPluginMeta().getVersion() + " &8| &aData saved and services stopped.");
      }
   }

   public static void x7q9(p var0) {
      n2x6(
         "Reload",
         "&aConfiguration reloaded &8| &7Storage: &f"
            + m4z1(var0)
            + " &8| &7Titles: &a"
            + m4z1(var0.p6d2("titles.yml", "titles"), "titles")
            + " entries &8| &7CrossServer: "
            + (var0.h("crossserver.yml").getBoolean("enabled", false) ? "&aON" : "&cOFF")
            + " &8| &7Debug: "
            + (var0.gt.getConfig().getBoolean("debug.enabled", false) ? "&aON" : "&cOFF")
      );
   }

   private static String m4z1(p var0) {
      String var1;
      return (var1 = var0.h("database.yml").getString("database", "SQLITE")) != null && !var1.isBlank() ? var1.toUpperCase() : "SQLITE";
   }

   private static String x7q9(FileConfiguration var0, String var1) {
      if (var0 == null) {
         return "&cMISSING";
      } else {
         return var0.getBoolean(var1, true) ? "&aON" : "&cOFF";
      }
   }

   private static int m4z1(FileConfiguration var0, String var1) {
      if (var0 == null) {
         return 0;
      } else {
         ConfigurationSection var2;
         return (var2 = var0.getConfigurationSection(var1)) == null ? 0 : var2.getKeys(false).size();
      }
   }

   private static String av() {
      ArrayList var0;
      x7q9(var0 = new ArrayList(), "PlaceholderAPI");
      x7q9(var0, "LuckPerms");
      x7q9(var0, "WorldGuard");
      x7q9(var0, "WorldEdit");
      return var0.isEmpty() ? "&7No optional hooks loaded" : String.join(" &8| ", var0);
   }

   private static void x7q9(List<String> var0, String var1) {
      if (Bukkit.getPluginManager().isPluginEnabled(var1)) {
         var0.add("&a" + var1 + " hooked");
      }
   }

   private static void n2x6(String var0, String var1) {
      Bukkit.getConsoleSender().sendMessage(w.n("&#2EB6FF[FlixCore FFA] &f" + var0 + " &8» " + var1));
   }
}
