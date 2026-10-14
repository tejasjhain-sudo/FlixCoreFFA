package club.aspvp.ffa.internal;

import club.aspvp.ffa.TwoCubeFFA;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class p {
   public final JavaPlugin gt;
   private final Map<String, FileConfiguration> gu = new ConcurrentHashMap<>();
   private final Map<String, File> gv = new ConcurrentHashMap<>();

   public p(TwoCubeFFA var1) {
      this.gt = var1;
   }

   public final void af() {
      this.gt.saveDefaultConfig();
      this.h1s7("messages_en.yml", "lang/messages_en.yml");
      this.h1s7("messages_ja.yml", "lang/messages_ja.yml");
      this.h1s7("messages_es.yml", "lang/messages_es.yml");
      this.h1s7("messages_fr.yml", "lang/messages_fr.yml");
      this.h1s7("messages_hi_latn.yml", "lang/messages_hi_latn.yml");
      this.h1s7("messages_ar.yml", "lang/messages_ar.yml");
      this.j("messages.yml");
      String var2 = "arenas.yml";
      this.h1s7(var2, var2);
      var2 = "kits.yml";
      this.h1s7(var2, var2);
      var2 = "scoreboard.yml";
      this.h1s7(var2, var2);
      var2 = "display.yml";
      this.h1s7(var2, var2);
      var2 = "sounds.yml";
      this.h1s7(var2, var2);
      this.h1s7("titles_en.yml", "lang/titles_en.yml");
      this.h1s7("titles_ja.yml", "lang/titles_ja.yml");
      this.h1s7("titles_es.yml", "lang/titles_es.yml");
      this.h1s7("titles_fr.yml", "lang/titles_fr.yml");
      this.h1s7("titles_hi_latn.yml", "lang/titles_hi_latn.yml");
      this.h1s7("titles_ar.yml", "lang/titles_ar.yml");
      this.j("titles.yml");
      var2 = "spawnitem.yml";
      this.h1s7(var2, var2);
      var2 = "spawnpvp.yml";
      this.h1s7(var2, var2);
      var2 = "announcements.yml";
      this.h1s7(var2, var2);
      this.x7q9("totempractice.yml", "menus/totempractice.yml", "totempractice.yml");
      this.h1s7("kiteditor.yml", "menus/kiteditor.yml");
      this.h1s7("kiteditor_menu.yml", "menus/kiteditor-menu.yml");
      this.h1s7("armor_trimmer.yml", "menus/armor_trimmer.yml");
      this.h1s7("ffa_menu.yml", "menus/ffa.yml");
      this.h1s7("settings_menu.yml", "menus/settings.yml");
      this.h1s7("tags.yml", "menus/tags/tags.yml");
      this.h1s7("tags_gui.yml", "menus/tags/menus/tags-gui.yml");
      this.h1s7("chat_colours.yml", "menus/chatcolour/chat-colours.yml");
      this.h1s7("chatcolour_gui.yml", "menus/chatcolour/chatcolour-gui.yml");
      var2 = "database.yml";
      this.h1s7(var2, var2);
      var2 = "crossserver.yml";
      this.h1s7(var2, var2);
      var2 = "events.yml";
      this.h1s7(var2, var2);
      var2 = "duels.yml";
      this.h1s7(var2, var2);
   }

   public final void ag() {
      this.gt.reloadConfig();
      this.gu.clear();
      this.gv.clear();
      this.af();
   }

   public final FileConfiguration ah() {
      return this.gt.getConfig();
   }

   public final boolean x7q9(String var1, boolean var2) {
      return this.gt.getConfig().getBoolean("features." + var1, var2);
   }

   public final FileConfiguration h(String var1) {
      return this.gu.get(var1);
   }

   public final String ai() {
      String var1;
      if ((var1 = this.gt.getConfig().getString("settings.language", "en")) == null) {
         return "en";
      } else {
         var1 = var1.toLowerCase().replace("-", "_");
         switch (var1) {
            case "ja":
            case "jp":
            case "japanese":
               return "ja";
            case "es":
            case "spanish":
            case "sp":
            case "espanol":
               return "es";
            case "fr":
            case "french":
            case "francais":
               return "fr";
            case "hi":
            case "hindi":
            case "hi_latn":
            case "hindi_latin":
            case "hindi-latin":
               return "hi_latn";
            case "ar":
            case "arabic":
            case "other":
            case "others":
               return "ar";
            default:
               return "en";
         }
      }
   }

   public final FileConfiguration p6d2(String var1, String var2) {
      FileConfiguration var3;
      if ((var3 = this.gu.get(var2 + "_" + this.ai() + ".yml")) != null) {
         return var3;
      } else {
         FileConfiguration var4;
         return (var4 = this.gu.get(var2 + "_en.yml")) == null ? this.gu.get(var1) : var4;
      }
   }

   public final void i(String var1) {
      FileConfiguration var2 = this.gu.get(var1);
      File var3 = this.gv.get(var1);
      if (var2 != null && var3 != null) {
         try {
            var2.save(var3);
         } catch (IOException var4) {
            this.gt.getLogger().severe("Could not save " + var1 + ": " + var4.getMessage());
         }
      }
   }

   private void h1s7(String var1, String var2) {
      File var3;
      if (!(var3 = new File(this.gt.getDataFolder(), var2)).exists()) {
         this.gt.saveResource(var2, false);
      }

      this.gv.put(var1, var3);
      this.gu.put(var1, this.x7q9(var3, var2));
   }

   private void j(String var1) {
      File var2;
      if ((var2 = new File(this.gt.getDataFolder(), var1)).exists()) {
         this.gv.put(var1, var2);
         this.gu.put(var1, this.x7q9(var2, var1));
      }
   }

   private void x7q9(String var1, String var2, String var3) {
      File var4;
      if (!(var4 = new File(this.gt.getDataFolder(), var2)).exists()) {
         File var5;
         if ((var5 = new File(this.gt.getDataFolder(), var3)).exists()) {
            try {
               File var6;
               if ((var6 = var4.getParentFile()) != null) {
                  var6.mkdirs();
               }

               Files.copy(var5.toPath(), var4.toPath());
            } catch (IOException var7) {
               this.gt.getLogger().warning("Could not migrate " + var3 + " to " + var2 + ": " + var7.getMessage());
            }
         }

         if (!var4.exists()) {
            this.gt.saveResource(var2, false);
         }
      }

      this.gv.put(var1, var4);
      this.gu.put(var1, this.x7q9(var4, var2));
   }

   private FileConfiguration x7q9(File var1, String var2) {
      YamlConfiguration var8 = YamlConfiguration.loadConfiguration(var1);

      try (InputStream var3 = this.gt.getResource(var2)) {
         if (var3 != null) {
            var8.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(var3, StandardCharsets.UTF_8)));
         }
      } catch (IOException var7) {
         this.gt.getLogger().warning("Could not load defaults for " + var2 + ": " + var7.getMessage());
      }

      return var8;
   }
}
