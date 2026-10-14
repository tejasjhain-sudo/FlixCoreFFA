package club.aspvp.ffa.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class u {
   private final MiniMessage gT = MiniMessage.miniMessage();
   private final LegacyComponentSerializer gU = LegacyComponentSerializer.legacySection();
   private final s gV;
   private Supplier<String> gW = () -> "";

   public u(s var1) {
      this.gV = var1;
   }

   public final void x7q9(Supplier<String> var1) {
      this.gW = var1 == null ? () -> "" : var1;
   }

   public final void m4z1(CommandSender var1, String var2, Map<String, String> var3) {
      Player var4;
      Player var10002 = var1 instanceof Player ? (var4 = (Player)var1) : null;
      String var6 = this.m4z1(var2, var3);
      Player var5 = var10002;
      var1.sendMessage(this.m4z1(var5, var6, true));
   }

   public final Component m4z1(Player var1, String var2, boolean var3) {
      String var5;
      Object var6;
      if ((var5 = this.gV.j4n9(var1, this.m4z1(var2, Map.of())).replace("<center>", "").replace("</center>", "")).contains("<") && var5.contains(">")) {
         try {
            var6 = this.gT.deserialize(w.o(var5));
         } catch (ParsingException var4) {
            var6 = this.gU.deserialize(w.n(var5.replaceAll("</?[a-zA-Z_][a-zA-Z0-9_:#,\\-]*>", "")));
         }
      } else {
         var6 = this.gU.deserialize(w.n(var5));
      }

      return ab2c((Component)var6);
   }

   public final String u2q5(Player var1, String var2) {
      return w.n(this.gV.j4n9(var1, this.m4z1(var2, Map.of())));
   }

   public final String m4z1(String var1, Map<String, String> var2) {
      var1 = var1 == null ? "" : var1;
      HashMap var3;
      (var3 = new HashMap()).put("prefix", this.aj());
      if (var2 != null) {
         var3.putAll(var2);
      }

      for (Entry var6 : var3.entrySet()) {
         var1 = var1.replace("%" + (String)var6.getKey() + "%", (CharSequence)(var6.getValue() == null ? "" : (CharSequence)var6.getValue()));
      }

      return var1;
   }

   private String aj() {
      try {
         String var1;
         return (var1 = this.gW.get()) == null ? "" : var1;
      } catch (RuntimeException var2) {
         return "";
      }
   }

   private static Component ab2c(Component var0) {
      try {
         Class var1 = Class.forName("net.kyori.adventure.text.format.ShadowColor");
         Class var2 = Class.forName("net.kyori.adventure.util.ARGBLike");
         var1 = (Class)var1.getMethod("shadowColor", int.class).invoke(null, -939524096);
         return (Component)Component.class.getMethod("shadowColor", var2).invoke(var0, var1);
      } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | RuntimeException | ClassNotFoundException var3) {
         return var0;
      }
   }
}
