package club.aspvp.ffa.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

public final class w {
   private static final Pattern hl = Pattern.compile("&#([A-Fa-f0-9]{6})");
   private static final Pattern hm = Pattern.compile("(?<![&<:])#([A-Fa-f0-9]{6})");

   public static String n(String var0) {
      if (var0 == null) {
         return "";
      } else {
         var0 = p(var0);
         Matcher var3 = hl.matcher(var0);
         StringBuffer var1 = new StringBuffer();

         while (var3.find()) {
            var3.appendReplacement(var1, ChatColor.of("#" + var3.group(1)).toString());
         }

         var3.appendTail(var1);
         return ChatColor.translateAlternateColorCodes('&', var1.toString());
      }
   }

   public static String o(String var0) {
      if (var0 == null) {
         return "";
      } else {
         var0 = p(var0);
         var0 = ab2c(
            ab2c(
               ab2c(
                  ab2c(
                     ab2c(
                        ab2c(
                           ab2c(
                              ab2c(
                                 ab2c(
                                    ab2c(
                                       ab2c(
                                          ab2c(
                                             ab2c(
                                                ab2c(
                                                   ab2c(
                                                      ab2c(
                                                         ab2c(
                                                            ab2c(
                                                               ab2c(
                                                                  ab2c(
                                                                     ab2c(ab2c(hl.matcher(var0).replaceAll("<#$1>"), '0', "black"), '1', "dark_blue"),
                                                                     '2',
                                                                     "dark_green"
                                                                  ),
                                                                  '3',
                                                                  "dark_aqua"
                                                               ),
                                                               '4',
                                                               "dark_red"
                                                            ),
                                                            '5',
                                                            "dark_purple"
                                                         ),
                                                         '6',
                                                         "gold"
                                                      ),
                                                      '7',
                                                      "gray"
                                                   ),
                                                   '8',
                                                   "dark_gray"
                                                ),
                                                '9',
                                                "blue"
                                             ),
                                             'a',
                                             "green"
                                          ),
                                          'b',
                                          "aqua"
                                       ),
                                       'c',
                                       "red"
                                    ),
                                    'd',
                                    "light_purple"
                                 ),
                                 'e',
                                 "yellow"
                              ),
                              'f',
                              "white"
                           ),
                           'k',
                           "obfuscated"
                        ),
                        'l',
                        "bold"
                     ),
                     'm',
                     "strikethrough"
                  ),
                  'n',
                  "underlined"
               ),
               'o',
               "italic"
            ),
            'r',
            "reset"
         );
         return hm.matcher(var0).replaceAll("<#$1>");
      }
   }

   private static String ab2c(String var0, char var1, String var2) {
      return var0.replace("&" + var1, "<" + var2 + ">").replace("&" + Character.toUpperCase(var1), "<" + var2 + ">");
   }

   private static String p(String var0) {
      return hm.matcher(var0).replaceAll("&$0");
   }
}
