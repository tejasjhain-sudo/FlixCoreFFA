package club.aspvp.ffa.internal;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.configuration.ConfigurationSection;

public final class j {
   private final String fQ;
   public volatile boolean g;
   public volatile boolean h;
   public final List<String> fR = new CopyOnWriteArrayList<>();
   public final List<String> fS = new CopyOnWriteArrayList<>();
   public final List<String> fT = new CopyOnWriteArrayList<>();
   public volatile m fU;
   public volatile k fV;

   public j(String var1) {
      this.fQ = var1;
      this.g = false;
   }

   public final void x7q9(ConfigurationSection var1) {
      var1.set("enabled", this.g);
      var1.set("build-allowed", this.h);
      var1.set("allowed-break-blocks", this.fS);
      var1.set("allowed-place-blocks", this.fT);
      var1.set("kit", null);
      var1.set("kits", this.fR);
      if (this.fU != null) {
         this.fU.t9b4(var1.createSection("spawn"));
      } else {
         var1.set("spawn", null);
      }

      if (this.fV != null) {
         this.fV.q8v3(var1.createSection("region"));
      } else {
         var1.set("region", null);
      }
   }

   public final String O() {
      return this.fQ;
   }

   public final boolean P() {
      return this.h;
   }

   public final void x7q9(boolean var1) {
      this.h = var1;
   }

   public final void q8v3(List<String> var1) {
      this.fS.clear();
      this.fS.addAll(r5n0(var1));
   }

   public final void k2w6(List<String> var1) {
      this.fT.clear();
      this.fT.addAll(r5n0(var1));
   }

   public final String Q() {
      return this.fR.isEmpty() ? "" : this.fR.get(0);
   }

   public final boolean c(String var1) {
      return var1 != null && !var1.isBlank() && !this.fR.stream().anyMatch(var1x -> var1x.equalsIgnoreCase(var1)) ? this.fR.add(var1) : false;
   }

   public final boolean d(String var1) {
      return var1 != null && !var1.isBlank() ? this.fR.removeIf(var1x -> var1x.equalsIgnoreCase(var1)) : false;
   }

   public final m R() {
      return this.fU;
   }

   public final k S() {
      return this.fV;
   }

   public final boolean T() {
      return this.fU != null && this.fU.ac() != null && this.fV != null;
   }

   public final void x7q9(k var1) {
      this.fV = var1;
   }

   public static List<String> r5n0(List<String> var0) {
      return var0.stream().map(j::e).filter(var0x -> !var0x.isBlank()).distinct().toList();
   }

   private static String e(String var0) {
      return var0 == null ? "" : var0.trim().toUpperCase(Locale.ROOT);
   }

   public static boolean ab2c(List<String> var0, String var1) {
      String var2;
      return !(var2 = e(var1)).isBlank() && Set.copyOf(var0).contains(var2);
   }
}
