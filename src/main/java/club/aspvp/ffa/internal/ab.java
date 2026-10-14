package club.aspvp.ffa.internal;

import java.text.DecimalFormat;
import java.util.Locale;

public final class ab {
   private static final DecimalFormat hT = new DecimalFormat("0.00");

   public static String r(String var0) {
      return var0 == null ? "" : var0.toLowerCase(Locale.ROOT).replace(' ', '_');
   }

   public static String ab2c(int var0, int var1) {
      return var1 <= 0 ? hT.format((long)var0) : hT.format((double)var0 / (double)var1);
   }
}
