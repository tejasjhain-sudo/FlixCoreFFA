package club.aspvp.ffa.internal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class o3w8 {
   public final p dI;
   public final u dJ;
   private final t dK;
   private final v dL;
   private final Map<String, o3w8.x7q9> dM = new LinkedHashMap<>();
   private final Map<String, o3w8.ab2c> dN = new LinkedHashMap<>();
   public final Map<UUID, String> dO = new ConcurrentHashMap<>();
   public final Map<UUID, String> dP = new ConcurrentHashMap<>();

   public o3w8(p var1, u var2, t var3, v var4) {
      this.dI = var1;
      this.dJ = var2;
      this.dK = var3;
      this.dL = var4;
      this.b();
   }

   public final void b() {
      this.dM.clear();
      this.dN.clear();
      ConfigurationSection var1;
      if ((var1 = this.dI.h("tags.yml").getConfigurationSection("tags")) != null) {
         for (String var3 : var1.getKeys(false)) {
            ConfigurationSection var4;
            if ((var4 = var1.getConfigurationSection(var3)) != null) {
               this.dM
                  .put(
                     var3.toLowerCase(Locale.ROOT),
                     new o3w8.x7q9(var3.toLowerCase(Locale.ROOT), var4.getString("name", var3), var4.getString("tag", ""), var4.getString("permission", ""))
                  );
            }
         }
      }

      ConfigurationSection var6;
      if ((var6 = this.dI.h("chat_colours.yml").getConfigurationSection("chat-colours")) != null) {
         for (String var8 : var6.getKeys(false)) {
            if ((var1 = var6.getConfigurationSection(var8)) != null) {
               this.dN
                  .put(
                     var8.toLowerCase(Locale.ROOT),
                     new o3w8.ab2c(
                        var8.toLowerCase(Locale.ROOT),
                        var1.getString("display-name", var8),
                        var1.getString("code", ""),
                        var1.getString("material", "WHITE_DYE"),
                        var1.getString("permission", "")
                     )
                  );
            }
         }
      }
   }

   public final void g1v7(Player var1) {
      this.dL.k2w6(var1.getUniqueId()).thenAccept(var2 -> {
         ab2c(this.dO, var1.getUniqueId(), var2.hd);
         ab2c(this.dP, var1.getUniqueId(), var2.he);
      });
   }

   public final void ab2c(Player var1, int var2) {
      ConfigurationSection var3;
      String var4 = (var3 = this.dI.h("tags_gui.yml").getConfigurationSection("gui-settings")) == null ? "&0&lTags" : var3.getString("title", "&0&lTags");
      int var12 = x7q9(var3 == null ? 45 : var3.getInt("size", 45));
      List var5 = this.c();
      int var6 = this.ab2c(var12, this.m4z1(var12), var5.size());
      var2 = Math.max(0, Math.min(var2, var6 - 1));
      Inventory var14 = Bukkit.createInventory(new p6d2(h1s7.bf, String.valueOf(var2)), var12, this.dJ.u2q5(var1, this.ab2c(var4, (o3w8.x7q9) null, var2, var6, var1)));
      this.x7q9(var1, var14, this.dI.h("tags_gui.yml").getConfigurationSection("extra-items"));
      this.ab2c(var1, var14, this.dI.h("tags_gui.yml").getConfigurationSection("previous-page"), this.ab2c((o3w8.x7q9) null, var2, var6, var1));
      this.ab2c(var1, var14, this.dI.h("tags_gui.yml").getConfigurationSection("next-page"), this.ab2c((o3w8.x7q9) null, var2, var6, var1));
      this.ab2c(var1, var14, this.dI.h("tags_gui.yml").getConfigurationSection("clear-tag"), this.ab2c((o3w8.x7q9) null, var2, var6, var1));
      this.ab2c(var1, var14, this.dI.h("tags_gui.yml").getConfigurationSection("search"), this.ab2c((o3w8.x7q9) null, var2, var6, var1));
      List var13 = ab2c(var12, this.m4z1(var12));
      int var7 = var2 * var13.size();
      ConfigurationSection var8 = this.dI.h("tags_gui.yml").getConfigurationSection("tag-item");

      for (int var9 = 0; var9 < var13.size() && var7 + var9 < var5.size(); var9++) {
         o3w8.x7q9 var10 = (o3w8.x7q9)var5.get(var7 + var9);
         var14.setItem((Integer)var13.get(var9), this.ab2c(var1, var10, var8, var2, var6));
      }

      var1.openInventory(var14);
   }

   public final void x7q9(Player var1, int var2) {
      ConfigurationSection var3;
      String var4 = (var3 = this.dI.h("chatcolour_gui.yml").getConfigurationSection("gui-settings")) == null
         ? "&0&lChat Colour"
         : var3.getString("title", "&0&lChat Colour");
      int var12 = x7q9(var3 == null ? 45 : var3.getInt("size", 45));
      List var5 = this.d();
      int var6 = this.ab2c(var12, this.q8v3(var12), var5.size());
      var2 = Math.max(0, Math.min(var2, var6 - 1));
      Inventory var14 = Bukkit.createInventory(new p6d2(h1s7.bg, String.valueOf(var2)), var12, this.dJ.u2q5(var1, this.ab2c(var4, (o3w8.ab2c) null, var2, var6, var1)));
      this.x7q9(var1, var14, this.dI.h("chatcolour_gui.yml").getConfigurationSection("extra-items"));
      this.ab2c(var1, var14, this.dI.h("chatcolour_gui.yml").getConfigurationSection("previous-page"), this.ab2c((o3w8.ab2c) null, var2, var6, var1));
      this.ab2c(var1, var14, this.dI.h("chatcolour_gui.yml").getConfigurationSection("next-page"), this.ab2c((o3w8.ab2c) null, var2, var6, var1));
      this.ab2c(var1, var14, this.dI.h("chatcolour_gui.yml").getConfigurationSection("clear-colour"), this.ab2c((o3w8.ab2c) null, var2, var6, var1));
      List var13 = ab2c(var12, this.q8v3(var12));
      int var7 = var2 * var13.size();
      ConfigurationSection var8 = this.dI.h("chatcolour_gui.yml").getConfigurationSection("colour-item");

      for (int var9 = 0; var9 < var13.size() && var7 + var9 < var5.size(); var9++) {
         o3w8.ab2c var10 = (o3w8.ab2c)var5.get(var7 + var9);
         var14.setItem((Integer)var13.get(var9), this.ab2c(var1, var10, var8, var2, var6));
      }

      var1.openInventory(var14);
   }

   public final void ab2c(Player var1, int var2, String var3) {
      int var7 = y5p8(var3);
      int var4 = x7q9(this.dI.h("chatcolour_gui.yml").getInt("gui-settings.size", 45));
      if (var2 == this.dI.h("chatcolour_gui.yml").getInt("previous-page.slot", -1)) {
         this.x7q9(var1, var7 - 1);
      } else if (var2 == this.dI.h("chatcolour_gui.yml").getInt("next-page.slot", -1)) {
         this.x7q9(var1, var7 + 1);
      } else if (var2 == this.dI.h("chatcolour_gui.yml").getInt("clear-colour.slot", -1)) {
         this.dP.remove(var1.getUniqueId());
         this.i5k2(var1);
         u var11 = this.dJ;
         String var9 = r.ab2c(this.dI, "chatcolour-cleared", "%prefix%&aChat colour cleared.");
         var1.sendMessage(var11.m4z1(var1, var9, true));
         this.x7q9(var1, var7);
      } else {
         o3w8.ab2c var6;
         if ((var6 = ab2c(this.d(), ab2c(var4, this.q8v3(var4)), var7, var2)) != null) {
            if (!t9b4(var1, var6.dU)) {
               u var10 = this.dJ;
               String var8 = r.ab2c(this.dI, "chatcolour-locked", "%prefix%&cYou do not own this chat colour.");
               var1.sendMessage(var10.m4z1(var1, var8, true));
            } else {
               this.dP.put(var1.getUniqueId(), var6.e());
               this.i5k2(var1);
               x6y3(var1);
               u var10001 = this.dJ;
               String var5 = this.dJ
                  .m4z1(r.ab2c(this.dI, "chatcolour-selected", "%prefix%&aSelected chat colour %colour%&a."), Map.of("colour", var6.dS + var6.dR));
               var1.sendMessage(var10001.m4z1(var1, var5, true));
               this.x7q9(var1, var7);
            }
         }
      }
   }

   public final String j4n9(Player var1) {
      o3w8.x7q9 var2;
      return (var2 = this.a7r4(var1)) != null && t9b4(var1, var2.dY) ? var2.dX : "";
   }

   public final String u2q5(Player var1) {
      o3w8.x7q9 var2;
      return (var2 = this.a7r4(var1)) != null && t9b4(var1, var2.dY) ? var2.dW : "";
   }

   public final String e7z1(Player var1) {
      o3w8.x7q9 var2;
      return (var2 = this.a7r4(var1)) != null && t9b4(var1, var2.dY) ? var2.f() : "";
   }

   public final String l9m6(Player var1) {
      o3w8.ab2c var2;
      return (var2 = this.q1d9(var1)) != null && t9b4(var1, var2.dU) ? var2.dS : "";
   }

   public final String o3w8(Player var1) {
      o3w8.ab2c var2;
      return (var2 = this.q1d9(var1)) != null && t9b4(var1, var2.dU) ? var2.dR : "";
   }

   public void i5k2(Player var1) {
      this.dL.ab2c(var1.getUniqueId(), this.dO.getOrDefault(var1.getUniqueId(), ""), this.dP.getOrDefault(var1.getUniqueId(), ""));
   }

   private o3w8.x7q9 a7r4(Player var1) {
      return this.dM.get(this.dO.get(var1.getUniqueId()));
   }

   private o3w8.ab2c q1d9(Player var1) {
      return this.dN.get(this.dP.get(var1.getUniqueId()));
   }

   private ItemStack ab2c(Player var1, o3w8.x7q9 var2, ConfigurationSection var3, int var4, int var5) {
      boolean var6 = var2.f().equals(this.dO.get(var1.getUniqueId()));
      boolean var7 = t9b4(var1, var2.dY);
      Material var8 = ab2c(var3 == null ? null : var3.getString(var6 ? "selected-material" : (var7 ? "material" : "locked-material")), Material.NAME_TAG);
      ab2c(var3, var6, var7);
      List var9 = var3 == null ? List.of() : var3.getStringList(var6 ? "selected-lore" : (var7 ? "lore" : "locked-lore"));
      return this.ab2c(
         var1,
         var8,
         var3 == null ? "%tag%" : var3.getString("name", "%tag%"),
         var9,
         this.ab2c(var2, var4, var5, var1),
         var6 && var3 != null && var3.getBoolean("selected-glow", true)
      );
   }

   private ItemStack ab2c(Player var1, o3w8.ab2c var2, ConfigurationSection var3, int var4, int var5) {
      boolean var6 = var2.e().equals(this.dP.get(var1.getUniqueId()));
      boolean var7 = t9b4(var1, var2.dU);
      String var8 = var6 && var3 != null
         ? var3.getString("selected-material", var2.dT)
         : (!var7 && var3 != null ? var3.getString("locked-material", "BARRIER") : var2.dT);
      List var9 = var3 == null ? List.of() : var3.getStringList(var6 ? "selected-lore" : (var7 ? "lore" : "locked-lore"));
      return this.ab2c(
         var1,
         ab2c(var8, Material.WHITE_DYE),
         var3 == null ? "%flixcoreffa_chatcolor_colour_name%" : var3.getString("name", "%flixcoreffa_chatcolor_colour_name%"),
         var9,
         this.ab2c(var2, var4, var5, var1),
         var6 && var3 != null && var3.getBoolean("selected-glow", true)
      );
   }

   private void x7q9(Player var1, Inventory var2, ConfigurationSection var3) {
      if (var3 != null) {
         for (String var5 : var3.getKeys(false)) {
            ConfigurationSection var8;
            if ((var8 = var3.getConfigurationSection(var5)) != null && var8.getBoolean("enabled", true)) {
               ItemStack var6 = this.ab2c(
                  var1,
                  ab2c(var8.getString("material"), Material.BLACK_STAINED_GLASS_PANE),
                  var8.getString("name", " "),
                  var8.getStringList("lore"),
                  Map.of(),
                  false
               );

               for (int var7 : this.x7q9(var8, var2.getSize())) {
                  var2.setItem(var7, var6.clone());
               }
            }
         }
      }
   }

   private void ab2c(Player var1, Inventory var2, ConfigurationSection var3, Map<String, String> var4) {
      if (var3 != null) {
         int var5;
         if ((var5 = var3.getInt("slot", -1)) >= 0 && var5 < var2.getSize()) {
            var2.setItem(
               var5,
               this.ab2c(
                  var1,
                  ab2c(var3.getString("material"), Material.STONE),
                  var3.getString("name", var3.getString("display_name", " ")),
                  var3.getStringList("lore"),
                  var4,
                  false
               )
            );
         }
      }
   }

   private ItemStack ab2c(Player var1, Material var2, String var3, List<String> var4, Map<String, String> var5, boolean var6) {
      ItemMeta var7;
      ItemStack var8;
      if ((var7 = (var8 = new ItemStack(var2)).getItemMeta()) != null) {
         var7.setDisplayName(this.dJ.u2q5(var1, ab2c(var3, var5)));
         var7.setLore(var4.stream().map(var3x -> this.dJ.u2q5(var1, ab2c(var3x, var5))).toList());
         var7.addItemFlags(ItemFlag.values());
         if (var6) {
            var7.setEnchantmentGlintOverride(Boolean.TRUE);
         }

         var8.setItemMeta(var7);
      }

      return var8;
   }

   private Map<String, String> ab2c(o3w8.x7q9 var1, int var2, int var3, Player var4) {
      String var5;
      o3w8.x7q9 var6 = (var5 = this.dO.get(var4.getUniqueId())) == null ? null : this.dM.get(var5);
      HashMap var7;
      (var7 = new HashMap()).put("tag", var1 == null ? this.j4n9(var4) : var1.dX);
      var7.put("tag_name", var1 == null ? this.u2q5(var4) : var1.dW);
      var7.put("twocubeffa_tag", this.j4n9(var4));
      var7.put("twocubeffa_tag_name", var6 == null ? "" : var6.dW);
      var7.put("twocubeffa_tag_permission", var1 == null ? "" : var1.dY);
      var7.put("flixcoreffa_tag", this.j4n9(var4));
      var7.put("flixcoreffa_tag_name", var6 == null ? "" : var6.dW);
      var7.put("flixcoreffa_tag_permission", var1 == null ? "" : var1.dY);
      var7.put("flixffa_tag", this.j4n9(var4));
      var7.put("flixffa_tag_name", var6 == null ? "" : var6.dW);
      var7.put("flixffa_tag_permission", var1 == null ? "" : var1.dY);
      String varTagStatus = var1 == null ? "" : ab2c(this.dI.h("tags_gui.yml").getConfigurationSection("tag-item"), var1.f().equals(var5), t9b4(var4, var1.dY));
      var7.put("twocubeffa_tag_status", varTagStatus);
      var7.put("flixcoreffa_tag_status", varTagStatus);
      var7.put("flixffa_tag_status", varTagStatus);
      var7.put("twocubeffa_tag_page", String.valueOf(var2 + 1));
      var7.put("twocubeffa_tag_total", String.valueOf(var3));
      var7.put("flixcoreffa_tag_page", String.valueOf(var2 + 1));
      var7.put("flixcoreffa_tag_total", String.valueOf(var3));
      var7.put("flixffa_tag_page", String.valueOf(var2 + 1));
      var7.put("flixffa_tag_total", String.valueOf(var3));
      var7.put("sort_mode", this.dI.h("tags_gui.yml").getString("gui-settings.sort-mode-text", "&fDefault"));
      return var7;
   }

   private Map<String, String> ab2c(o3w8.ab2c var1, int var2, int var3, Player var4) {
      String var5 = this.dP.get(var4.getUniqueId());
      HashMap var6;
      (var6 = new HashMap()).put("twocubeffa_chatcolor_chat_colour", var1 == null ? this.l9m6(var4) : var1.dS);
      var6.put("twocubeffa_chatcolor_colour_name", var1 == null ? this.o3w8(var4) : var1.dR);
      var6.put("twocubeffa_chatcolor_permission", var1 == null ? "" : var1.dU);
      var6.put("flixcoreffa_chatcolor_chat_colour", var1 == null ? this.l9m6(var4) : var1.dS);
      var6.put("flixcoreffa_chatcolor_colour_name", var1 == null ? this.o3w8(var4) : var1.dR);
      var6.put("flixcoreffa_chatcolor_permission", var1 == null ? "" : var1.dU);
      var6.put("flixffa_chatcolor_chat_colour", var1 == null ? this.l9m6(var4) : var1.dS);
      var6.put("flixffa_chatcolor_colour_name", var1 == null ? this.o3w8(var4) : var1.dR);
      var6.put("flixffa_chatcolor_permission", var1 == null ? "" : var1.dU);
      String varChatStatus = var1 == null ? "" : ab2c(this.dI.h("chatcolour_gui.yml").getConfigurationSection("colour-item"), var1.e().equals(var5), t9b4(var4, var1.dU));
      var6.put("twocubeffa_chatcolor_status", varChatStatus);
      var6.put("flixcoreffa_chatcolor_status", varChatStatus);
      var6.put("flixffa_chatcolor_status", varChatStatus);
      var6.put("twocubeffa_chatcolor_page", String.valueOf(var2 + 1));
      var6.put("twocubeffa_chatcolor_total", String.valueOf(var3));
      var6.put("flixcoreffa_chatcolor_page", String.valueOf(var2 + 1));
      var6.put("flixcoreffa_chatcolor_total", String.valueOf(var3));
      var6.put("flixffa_chatcolor_page", String.valueOf(var2 + 1));
      var6.put("flixffa_chatcolor_total", String.valueOf(var3));
      var6.put("sort_mode", this.dI.h("chatcolour_gui.yml").getString("gui-settings.sort-mode-text", "&fDefault"));
      return var6;
   }

   private String ab2c(String var1, o3w8.x7q9 var2, int var3, int var4, Player var5) {
      return ab2c(var1, this.ab2c(var2, var3, var4, var5));
   }

   private String ab2c(String var1, o3w8.ab2c var2, int var3, int var4, Player var5) {
      return ab2c(var1, this.ab2c(var2, var3, var4, var5));
   }

   private static String ab2c(String var0, Map<String, String> var1) {
      var0 = var0 == null ? "" : var0;

      for (Entry var2 : var1.entrySet()) {
         var0 = var0.replace("%" + (String)var2.getKey() + "%", (CharSequence)var2.getValue());
      }

      return var0;
   }

   private static String ab2c(ConfigurationSection var0, boolean var1, boolean var2) {
      if (var0 == null) {
         if (var1) {
            return "&aSelected";
         } else {
            return var2 ? "&aUnlocked" : "&cLocked";
         }
      } else {
         return var0.getString("status." + (var1 ? "selected" : (var2 ? "unlocked" : "locked")), var1 ? "&aSelected" : (var2 ? "&aUnlocked" : "&cLocked"));
      }
   }

   public Set<Integer> m4z1(int var1) {
      Set var2;
      ab2c(
         var2 = this.ab2c(this.dI.h("tags_gui.yml").getConfigurationSection("extra-items"), var1),
         this.dI.h("tags_gui.yml").getInt("previous-page.slot", -1),
         var1
      );
      ab2c(var2, this.dI.h("tags_gui.yml").getInt("next-page.slot", -1), var1);
      ab2c(var2, this.dI.h("tags_gui.yml").getInt("clear-tag.slot", -1), var1);
      ab2c(var2, this.dI.h("tags_gui.yml").getInt("search.slot", -1), var1);
      return var2;
   }

   private Set<Integer> q8v3(int var1) {
      Set var2;
      ab2c(
         var2 = this.ab2c(this.dI.h("chatcolour_gui.yml").getConfigurationSection("extra-items"), var1),
         this.dI.h("chatcolour_gui.yml").getInt("previous-page.slot", -1),
         var1
      );
      ab2c(var2, this.dI.h("chatcolour_gui.yml").getInt("next-page.slot", -1), var1);
      ab2c(var2, this.dI.h("chatcolour_gui.yml").getInt("clear-colour.slot", -1), var1);
      return var2;
   }

   private Set<Integer> ab2c(ConfigurationSection var1, int var2) {
      HashSet var3 = new HashSet();
      if (var1 == null) {
         return var3;
      } else {
         for (String var5 : var1.getKeys(false)) {
            ConfigurationSection var6;
            if ((var6 = var1.getConfigurationSection(var5)) != null && var6.getBoolean("enabled", true)) {
               var3.addAll(this.x7q9(var6, var2));
            }
         }

         return var3;
      }
   }

   public static List<Integer> ab2c(int var0, Set<Integer> var1) {
      ArrayList var2 = new ArrayList();

      for (int var3 = 0; var3 < var0; var3++) {
         if (!var1.contains(var3)) {
            var2.add(var3);
         }
      }

      return var2;
   }

   public static <T> T ab2c(List<T> var0, List<Integer> var1, int var2, int var3) {
      if ((var3 = var1.indexOf(var3)) < 0) {
         return null;
      } else {
         int var4;
         return (T)((var4 = var2 * var1.size() + var3) >= 0 && var4 < var0.size() ? var0.get(var4) : null);
      }
   }

   private int ab2c(int var1, Set<Integer> var2, int var3) {
      var1 = Math.max(1, ab2c(var1, var2).size());
      return Math.max(1, (int)Math.ceil((double)var3 / (double)var1));
   }

   public List<o3w8.x7q9> c() {
      return this.dM.values().stream().sorted(Comparator.comparing(o3w8.x7q9::f)).toList();
   }

   private List<o3w8.ab2c> d() {
      return this.dN.values().stream().sorted(Comparator.comparing(o3w8.ab2c::e)).toList();
   }

   private List<Integer> x7q9(ConfigurationSection var1, int var2) {
      ArrayList var3 = new ArrayList();
      if (var1.contains("slot")) {
         ab2c(var3, var1.getInt("slot", -1), var2);
      }

      Iterator var7 = var1.getList("slots", List.of()).iterator();

      while (var7.hasNext()) {
         String var4;
         if ((var4 = String.valueOf(var7.next()).trim()).matches("\\d+")) {
            ab2c(var3, Integer.parseInt(var4), var2);
         } else if (var4.matches("\\d+\\s*-\\s*\\d+")) {
            String[] var8;
            int var5 = Integer.parseInt((var8 = var4.split("-", 2))[0].trim());
            int var9 = Integer.parseInt(var8[1].trim());

            for (int var6 = Math.max(0, Math.min(var5, var9)); var6 <= Math.min(var2 - 1, Math.max(var5, var9)); var6++) {
               ab2c(var3, var6, var2);
            }
         }
      }

      return var3;
   }

   private static void ab2c(Set<Integer> var0, int var1, int var2) {
      if (var1 >= 0 && var1 < var2) {
         var0.add(var1);
      }
   }

   private static void ab2c(List<Integer> var0, int var1, int var2) {
      if (var1 >= 0 && var1 < var2) {
         var0.add(var1);
      }
   }

   public static int y5p8(String var0) {
      try {
         return Math.max(0, Integer.parseInt(var0 == null ? "0" : var0));
      } catch (NumberFormatException var1) {
         return 0;
      }
   }

   public static int x7q9(int var0) {
      return Math.max(9, Math.min(54, (var0 + 8) / 9 * 9));
   }

   private static Material ab2c(String var0, Material var1) {
      if (var0 != null && !var0.isBlank()) {
         try {
            return Material.valueOf(var0.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var2) {
            return var1;
         }
      } else {
         return var1;
      }
   }

   public static boolean t9b4(Player var0, String var1) {
      return var1 == null || var1.isBlank() || var0.hasPermission(var1) || var0.hasPermission("ffa.admin");
   }

   private static void ab2c(Map<UUID, String> var0, UUID var1, String var2) {
      if (var2 != null && !var2.isBlank()) {
         var0.put(var1, var2.toLowerCase(Locale.ROOT));
      } else {
         var0.remove(var1);
      }
   }

   public static void x6y3(Player var0) {
      try {
         var0.playSound(var0.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
      } catch (RuntimeException var1) {
      }
   }

   public static record ab2c(String dQ, String dR, String dS, String dT, String dU) {

      public final String e() {
         return this.dQ;
      }
   }

   public static record x7q9(String dV, String dW, String dX, String dY) {

      public final String f() {
         return this.dV;
      }
   }
}
