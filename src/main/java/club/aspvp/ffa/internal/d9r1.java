package club.aspvp.ffa.internal;

import java.util.Iterator;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class d9r1 implements Listener {
   private final q1d9 bF;
   private final x6y3 bG;
   private final l9m6 bH;
   private final i bI;
   private final o3w8 bJ;
   private final b bK;
   private final f bL;
   private final r bM;
   private final t bN;
   private final Runnable bO;

   public d9r1(q1d9 var1, x6y3 var2, l9m6 var3, i var4, o3w8 var5, b var6, f var7, r var8, t var9, Runnable var10) {
      this.bF = var1;
      this.bG = var2;
      this.bH = var3;
      this.bI = var4;
      this.bJ = var5;
      this.bK = var6;
      this.bL = var7;
      this.bM = var8;
      this.bN = var9;
      this.bO = var10;
   }

   @EventHandler
   public final void onClick(InventoryClickEvent var1) {
      HumanEntity var4;
      if ((var4 = var1.getWhoClicked()) instanceof Player) {
         Player var2 = (Player)var4;
         InventoryHolder var26;
         if ((var26 = var1.getInventory().getHolder()) instanceof p6d2) {
            p6d2 var3 = (p6d2)var26;
            if (var1.getClickedInventory() == null) {
               return;
            }

            if (var1.getClickedInventory() != var1.getInventory()) {
               if (var3.aW != h1s7.ba || var1.getClick().isShiftClick()) {
                  var1.setCancelled(true);
               }

               return;
            }

            var1.setCancelled(true);
            if (var3.aW == h1s7.ba) {
               if (this.bG.r5n0(var1.getSlot())) {
                  int var37;
                  if ((var37 = var1.getHotbarButton()) >= 0 && var37 < 9) {
                     x6y3 var47 = this.bG;
                     if (var37 >= 0 && var37 < 9) {
                        ItemStack var73 = var2.getInventory().getItem(var37);
                        ItemStack var83 = var2.getInventory().getItemInOffHand();
                        var2.getInventory().setItemInOffHand(x6y3.q8v3(var73));
                        var2.getInventory().setItem(var37, x6y3.r5n0(var83) ? new ItemStack(Material.AIR) : var83.clone());
                        var2.updateInventory();
                     }

                     this.bG.m(var2);
                     return;
                  }

                  x6y3 var106 = this.bG;
                  ItemStack var62 = var1.getCursor();
                  x6y3 var46 = var106;
                  ItemStack var72 = var2.getInventory().getItemInOffHand();
                  var2.getInventory().setItemInOffHand(x6y3.q8v3(var62));
                  var2.updateInventory();
                  ItemStack var18 = var46.k2w6(var72);
                  this.bG.m(var2);
                  this.ab2c(var2, var18);
                  return;
               }

               String var27;
               if ((var27 = this.bG.k2w6(var1.getSlot())) != null && x7q9(var1.getCursor())) {
                  l9m6 var45 = this.bH;
                  FileConfiguration var71;
                  if ((var71 = this.bH.dA.h("armor_trimmer.yml")).getBoolean("enabled", true) && l9m6.ab2c(var2, var71)) {
                     var45.ab2c(var2, var27, true);
                     return;
                  }

                  return;
               }

               if (var27 != null) {
                  ItemStack var105;
                  label221: {
                     x6y3 var104 = this.bG;
                     ItemStack var69 = var1.getCursor();
                     int var60 = var1.getSlot();
                     x6y3 var43 = var104;
                     if (!x6y3.r5n0(var69)) {
                        String var80;
                        if ((var80 = var43.k2w6(var60)) != null) {
                           ItemStack var91 = x6y3.k2w6(var2, var80);
                           var43.ab2c(var2, var80, var69);
                           var105 = var43.k2w6(var91);
                           break label221;
                        }

                        if (var43.r5n0(var60)) {
                           ItemStack var90 = var2.getInventory().getItemInOffHand();
                           var2.getInventory().setItemInOffHand(x6y3.q8v3(var69));
                           var2.updateInventory();
                           var105 = var43.k2w6(var90);
                           break label221;
                        }
                     }

                     var105 = null;
                  }

                  ItemStack var17 = var105;
                  x6y3 var44 = this.bG;
                  p6d2 var70;
                  InventoryHolder var81;
                  if ((var81 = var2.getOpenInventory().getTopInventory().getHolder()) instanceof p6d2 && (var70 = (p6d2)var81).aW == h1s7.ba) {
                     Inventory var82 = var2.getOpenInventory().getTopInventory();
                     FileConfiguration var92 = var44.en.h("kiteditor.yml");
                     ItemStack var36 = x6y3.k2w6(var2, var27);
                     var44.ab2c(var2, var82, var92, var27, var36);
                  }

                  this.ab2c(var2, var17);
                  return;
               }
            }

            if (var3.aW == h1s7.ba) {
               boolean var93;
               label319: {
                  x6y3 var10000 = this.bG;
                  ItemStack var8 = var1.getCursor();
                  int var7 = var1.getSlot();
                  x6y3 var5 = var10000;
                  FileConfiguration var9;
                  String var10;
                  if ((var10 = x6y3.ab2c(var9 = var10000.en.h("kiteditor.yml"), var7)) != null) {
                     if (!x6y3.r5n0(var8)) {
                        var5.ab2c(var2, var10, var8);
                        var93 = true;
                        break label319;
                     }
                  } else {
                     ConfigurationSection var28;
                     if ((var28 = var9.getConfigurationSection("offhand_slot")) != null && var28.getInt("slot", -1) == var7 && !x6y3.r5n0(var8)) {
                        var2.getInventory().setItemInOffHand(x6y3.q8v3(var8));
                        var2.updateInventory();
                        var93 = true;
                        break label319;
                     }
                  }

                  var93 = false;
               }

               if (var93) {
                  ItemStack var51 = var1.getCursor();
                  if (var51 != null && !var51.getType().isAir()) {
                     var51.setAmount(0);
                     var2.setItemOnCursor(null);
                  }

                  this.bG.d(var2);
                  return;
               }
            }

            if (var1.getCurrentItem() == null) {
               return;
            }

            switch (var3.aW) {
               case aY:
                  q1d9 var103 = this.bF;
                  int var59 = var1.getSlot();
                  Player var50 = var2;
                  q1d9 var42 = var103;
                  ConfigurationSection var68;
                  if ((var68 = var103.m4z1(var42.g1v7("ffa"), var59)) != null && q1d9.x7q9(var2, var68)) {
                     for (String var89 : var68.getStringList("click_commands")) {
                        var42.f3y8(var50, var89);
                     }

                     return;
                  }
                  break;
               case aZ:
                  q1d9 var102 = this.bF;
                  int var58 = var1.getSlot();
                  Player var49 = var2;
                  q1d9 var41 = var102;
                  ConfigurationSection var67;
                  if ((var67 = var102.m4z1(var41.g1v7("kiteditor"), var58)) != null && q1d9.x7q9(var2, var67)) {
                     for (String var88 : var67.getStringList("click_commands")) {
                        var41.f3y8(var49, var88);
                     }

                     return;
                  }
                  break;
               case ba:
                  String var100;
                  label284: {
                     int var55 = var1.getSlot();
                     int var15 = var55;
                     x6y3 var32 = this.bG;
                     ConfigurationSection var22;
                     if ((var22 = this.bG.en.h("kiteditor.yml").getConfigurationSection("buttons")) != null) {
                        for (String var33 : var22.getKeys(false)) {
                           ConfigurationSection var56;
                           if ((var56 = var22.getConfigurationSection(var33)) != null && var56.getInt("slot", -1) == var15) {
                              var100 = var33;
                              break label284;
                           }
                        }
                     }

                     var100 = "";
                  }

                  String var66 = var100;
                  switch (var66) {
                     case "armor_trim":
                        l9m6 var34 = this.bH;
                        FileConfiguration var23;
                        if (!(var23 = this.bH.dA.h("armor_trimmer.yml")).getBoolean("enabled", true) || !l9m6.ab2c(var2, var23)) {
                           return;
                        }

                        Player var35 = var2;
                        Iterator var57 = l9m6.dz.iterator();

                        String var112;
                        while (true) {
                           if (!var57.hasNext()) {
                              var112 = null;
                              break;
                           }

                           String var16 = (String)var57.next();
                           if (l9m6.m4z1(l9m6.k2w6(var35, var16))) {
                              var112 = var16;
                              break;
                           }
                        }

                        String var24 = var112;
                        var34.ab2c(var2, var24, true);
                        return;
                     case "save":
                        boolean var87 = this.bG.g(var2);
                        this.bG.e(var2);
                        this.bM.ab2c(var2, var87 ? "personal-kit-saved" : "kit-saved", Map.of("kit", this.bG.f(var2)));
                        if (!var87) {
                           this.bM.ab2c(var2, "kit-save-guide", Map.of("kit", this.bG.f(var2)));
                        }

                        var2.setItemOnCursor(new ItemStack(Material.AIR));
                        var2.closeInventory();
                        return;
                     case "reset":
                        this.bG.h(var2);
                        this.bM.ab2c(var2, "kit-reset", Map.of("kit", this.bG.f(var2)));
                        this.bG.d(var2);
                        return;
                     case "clean":
                        x6y3.i(var2);
                        this.bM.ab2c(var2, "kit-cleaned", Map.of("kit", this.bG.f(var2)));
                        this.bG.d(var2);
                        return;
                     case "leave":
                        var2.setItemOnCursor(new ItemStack(Material.AIR));
                        var2.closeInventory();
                        return;
                     default:
                        return;
                  }
               case bb:
                  l9m6 var99 = this.bH;
                  int var54 = var1.getSlot();
                  l9m6 var40 = var99;
                  ConfigurationSection var65;
                  if ((var65 = var99.dA.h("armor_trimmer.yml").getConfigurationSection("armor_selector")) == null) {
                     return;
                  }

                  ConfigurationSection var76 = var65.getConfigurationSection("armor_slots");

                  for (String var31 : l9m6.dz) {
                     ConfigurationSection var14;
                     if ((
                           (var14 = var76 == null ? null : var76.getConfigurationSection(var31)) == null
                              ? l9m6.w7k3(var31)
                              : var14.getInt("slot", l9m6.w7k3(var31))
                        )
                        == var54) {
                        if (l9m6.m4z1(l9m6.k2w6(var2, var31))) {
                           var40.ab2c(var2, var31, true);
                        }

                        return;
                     }
                  }

                  if (var54 == var65.getInt("back_button.slot", 22)) {
                     var40.dC.d(var2);
                  }

                  return;
               case bc:
                  l9m6 var98 = this.bH;
                  ClickType var64 = var1.getClick();
                  int var53 = var1.getSlot();
                  l9m6 var39 = var98;
                  ConfigurationSection var75 = var98.dA.h("armor_trimmer.yml").getConfigurationSection("selection_gui");
                  l9m6.ab2c var85 = var39.dD.get(var2.getUniqueId());
                  if (var75 != null && var85 != null) {
                     int var30 = var64 != ClickType.RIGHT && var64 != ClickType.SHIFT_RIGHT ? 1 : -1;
                     if (var53 == var75.getInt("pattern_selector.slot", 10)) {
                        var85.dG = l9m6.ab2c(var85.dG, l9m6.m8p0().size(), var30);
                        var39.ab2c(var2, var85.dE, false);
                     } else if (var53 == var75.getInt("material_selector.slot", 16)) {
                        var85.dH = l9m6.ab2c(var85.dH, l9m6.a().size(), var30);
                        var39.ab2c(var2, var85.dE, false);
                     } else {
                        if (var53 != var75.getInt("preview_item.slot", 13)) {
                           if (var53 == var75.getInt("back_button.slot", 22)) {
                              var39.dD.remove(var2.getUniqueId());
                              var39.dC.d(var2);
                           }

                           return;
                        }

                        l9m6.ab2c(var2, var85.dE, var39.ab2c(var85.dF, var85));
                        var39.dC.e(var2);
                        var39.dD.remove(var2.getUniqueId());
                        var39.dC.d(var2);
                     }
                  }
                  break;
               case bd:
                  this.bI.ab2c(var2, var1.getInventory(), var1.getSlot());
                  return;
               case be:
                  this.bF.m4z1(var2, var1.getSlot());
                  return;
               case bf:
                  o3w8 var94 = this.bJ;
                  int var10002 = var1.getSlot();
                  String var63 = var3.aX;
                  int var52 = var10002;
                  o3w8 var38 = var94;
                  int var74 = o3w8.y5p8(var63);
                  int var84 = o3w8.x7q9(var38.dI.h("tags_gui.yml").getInt("gui-settings.size", 45));
                  if (var52 == var38.dI.h("tags_gui.yml").getInt("previous-page.slot", -1)) {
                     var38.ab2c(var2, var74 - 1);
                  } else if (var52 == var38.dI.h("tags_gui.yml").getInt("next-page.slot", -1)) {
                     var38.ab2c(var2, var74 + 1);
                  } else if (var52 == var38.dI.h("tags_gui.yml").getInt("clear-tag.slot", -1)) {
                     var38.dO.remove(var2.getUniqueId());
                     var38.i5k2(var2);
                     u var10001 = var38.dJ;
                     String var19 = r.ab2c(var38.dI, "tag-cleared", "%prefix%&aTag cleared.");
                     var2.sendMessage(var10001.m4z1(var2, var19, true));
                     var38.ab2c(var2, var74);
                  } else {
                     o3w8.x7q9 var29;
                     if ((var29 = o3w8.ab2c(var38.c(), o3w8.ab2c(var84, var38.m4z1(var84)), var74, var52)) == null) {
                        return;
                     }

                     if (o3w8.t9b4(var2, var29.dY)) {
                        var38.dO.put(var2.getUniqueId(), var29.f());
                        var38.i5k2(var2);
                        o3w8.x6y3(var2);
                        u var108 = var38.dJ;
                        String var21 = var38.dJ
                           .m4z1(r.ab2c(var38.dI, "tag-selected", "%prefix%&aSelected tag %tag%&a."), Map.of("tag", var29.dX, "tag_name", var29.dW));
                        var2.sendMessage(var108.m4z1(var2, var21, true));
                        var38.ab2c(var2, var74);
                        return;
                     }

                     u var107 = var38.dJ;
                     String var20 = r.ab2c(var38.dI, "tag-locked", "%prefix%&cYou do not own this tag.");
                     var2.sendMessage(var107.m4z1(var2, var20, true));
                  }
                  break;
               case bg:
                  this.bJ.ab2c(var2, var1.getSlot(), var3.aX);
            }

            return;
         }
      }
   }

   @EventHandler
   public final void onDrag(InventoryDragEvent var1) {
      InventoryHolder var3;
      if (var1.getWhoClicked() instanceof Player && (var3 = var1.getInventory().getHolder()) instanceof p6d2) {
         p6d2 var2 = (p6d2)var3;
         if (var1.getRawSlots().stream().anyMatch(var1x -> var1x < var1.getInventory().getSize()) || var2.aW != h1s7.ba) {
            var1.setCancelled(true);
         }
      }
   }

   @EventHandler
   public final void onClose(InventoryCloseEvent var1) {
      HumanEntity var3;
      if ((var3 = var1.getPlayer()) instanceof Player) {
         Player var2 = (Player)var3;
         InventoryHolder var5;
         if ((var5 = var1.getInventory().getHolder()) instanceof p6d2) {
            p6d2 var4 = (p6d2)var5;
            if ((var4.aW == h1s7.ba || var4.aW == h1s7.bb || var4.aW == h1s7.bc) && this.bG.l(var2)) {
               this.bN.ab2c(var2, () -> {
                  if (!this.bK.w(var2)) {
                     this.bL.N(var2);
                  }
               }, 1L);
            }

            return;
         }
      }
   }

   private void ab2c(Player var1, ItemStack var2) {
      var2 = x7q9(var2) ? new ItemStack(Material.AIR) : var2.clone();
      this.bN.ab2c(var1, () -> var1.setItemOnCursor(var2), 1L);
   }

   private static boolean x7q9(ItemStack var0) {
      return var0 == null || var0.getType().isAir();
   }
}
