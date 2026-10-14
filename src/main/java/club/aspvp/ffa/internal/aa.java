package club.aspvp.ffa.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public final class aa {
   private static final Logger hq = Logger.getLogger("FlixCoreFFA");
   private static boolean hr;
   private static Class<?> hs;
   private static Class<?> ht;
   private static Class<?> hu;
   private static Class<?> hv;
   private static Class<?> hw;
   private static Class<?> hx;
   private static Class<?> hy;
   private static Class<?> hz;
   private static Class<?> hA;
   private static Class<?> hB;
   private static Object hC;
   private static Object hD;
   private static Object hE;
   private static Object hF;
   private static Object hG;
   private static Method hH;
   private static Constructor<?> hI;
   private static Constructor<?> hJ;
   private static Constructor<?> hK;
   private static Constructor<?> hL;
   private static Constructor<?> hM;
   private static Method hN;
   private static Field hO;
   private static Method hP;
   private static final List<Constructor<?>> hQ = new ArrayList<>();
   private static final List<Constructor<?>> hR = new ArrayList<>();
   private static final List<Constructor<?>> hS = new ArrayList<>();

   public static boolean aA() {
      return hr;
   }

   public static void ab2c(Player var0, String var1, Component var2) throws Exception {
      Object var6 = x7q9(var2);

      try {
         ab2c(var0, ab2c(var1, 1, hG));
      } catch (Exception var3) {
      }

      ab2c(var0, ab2c(var1, 0, var6));
      Object var5;
      Object var10001;
      if (hL != null && (var5 = ab2c(var1, hG)) != null) {
         var10001 = hL.newInstance(hC, var5);
      } else {
         if (hM == null) {
            throw new UnsupportedOperationException("No supported ClientboundSetDisplayObjectivePacket constructor");
         }

         var10001 = hM.newInstance(hC, var1);
      }

      ab2c(var0, var10001);
   }

   public static void x7q9(Player var0, String var1, Component var2) throws Exception {
      ab2c(var0, ab2c(var1, 2, x7q9(var2)));
   }

   public static void e7z1(Player var0, String var1) throws Exception {
      ab2c(var0, ab2c(var1, 1, hG));
   }

   public static void ab2c(Player var0, String var1, String var2, int var3, Component var4) throws Exception {
      ab2c(var0, ab2c(var1, var2, var3, x7q9(var4)));
   }

   public static void x7q9(Player var0, String var1, String var2) throws Exception {
      ab2c(var0, d9r1(var1, var2));
   }

   private static Object ab2c(String var0, int var1, Object var2) throws Exception {
      Object var3;
      if (hK != null && (var3 = ab2c(var0, var2)) != null) {
         return hK.newInstance(var3, var1);
      } else {
         var3 = hQ.iterator();

         while (var3.hasNext()) {
            Constructor var4;
            Class[] var5;
            Object[] var6;
            (var6 = new Object[(var5 = (var4 = (Constructor)var3.next()).getParameterTypes()).length])[0] = var0;
            var6[1] = var1;
            Object var7 = var2 == null ? hG : var2;

            for (int var8 = 2; var8 < var5.length; var8++) {
               if (var5[var8] == Optional.class) {
                  if (var8 == 2) {
                     var6[var8] = var1 == 1 ? Optional.empty() : Optional.ofNullable(var7);
                  } else if (var8 == 3) {
                     var6[var8] = Optional.of(hD);
                  } else {
                     var6[var8] = Optional.empty();
                  }
               } else if (var5[var8] == hy) {
                  var6[var8] = hD;
               } else if (var5[var8].getName().equals("net.minecraft.network.chat.Component")) {
                  var6[var8] = var7;
               } else {
                  var6[var8] = m4z1(var5[var8]);
               }
            }

            try {
               return var4.newInstance(var6);
            } catch (Throwable var9) {
            }
         }

         throw new UnsupportedOperationException("No supported ClientboundSetObjectivePacket constructor");
      }
   }

   private static Object ab2c(String var0, String var1, int var2, Object var3) throws Exception {
      Iterator var4 = hR.iterator();

      while (var4.hasNext()) {
         Constructor var5;
         Class[] var6;
         Object[] var7;
         (var7 = new Object[(var6 = (var5 = (Constructor)var4.next()).getParameterTypes()).length])[0] = var0;
         var7[1] = var1;
         var7[2] = var2;

         for (int var8 = 3; var8 < var6.length; var8++) {
            if (var6[var8] == Optional.class) {
               if (var8 == 3) {
                  var7[var8] = Optional.ofNullable(var3);
               } else if (hF != null) {
                  var7[var8] = Optional.of(hF);
               } else {
                  var7[var8] = Optional.empty();
               }
            } else {
               var7[var8] = m4z1(var6[var8]);
            }
         }

         try {
            return var5.newInstance(var7);
         } catch (Throwable var9) {
         }
      }

      throw new UnsupportedOperationException("No supported ClientboundSetScorePacket constructor");
   }

   private static Object d9r1(String var0, String var1) throws Exception {
      Iterator var2 = hS.iterator();

      while (var2.hasNext()) {
         Constructor var3;
         Class[] var4 = (var3 = (Constructor)var2.next()).getParameterTypes();

         try {
            if (var4.length == 2) {
               return var3.newInstance(var0, var1);
            }

            if (var4.length == 1) {
               return var3.newInstance(var0);
            }
         } catch (Throwable var5) {
         }
      }

      throw new UnsupportedOperationException("No supported ClientboundResetScorePacket constructor");
   }

   private static Object ab2c(String var0, Object var1) throws Exception {
      if (hI != null && hJ != null && hE != null && hD != null) {
         Object var2 = hI.newInstance();
         Class[] var3;
         if ((var3 = hJ.getParameterTypes()).length < 5) {
            return null;
         } else {
            Object[] var4;
            (var4 = new Object[var3.length])[0] = var2;
            var4[1] = var0;
            var4[2] = hE;
            var4[3] = var1 == null ? hG : var1;
            var4[4] = hD;

            for (int var5 = 5; var5 < var3.length; var5++) {
               if (var3[var5] == boolean.class) {
                  var4[var5] = Boolean.FALSE;
               } else if (var3[var5] == Optional.class) {
                  var4[var5] = Optional.empty();
               } else {
                  var4[var5] = null;
               }
            }

            return hJ.newInstance(var4);
         }
      } else {
         return null;
      }
   }

   private static void ab2c(Player var0, Object var1) throws Exception {
      if (hN != null && hO != null && hP != null) {
         Object var5 = hN.invoke(var0);
         ab2c(hO.get(var5), hP, var1);
      } else {
         Object var3;
         Object var4;
         Method var2 = ab2c((var4 = ab2c((var3 = var0.getClass().getMethod("getHandle").invoke(var0)).getClass(), "connection").get(var3)).getClass());
         ab2c(var4, var2, var1);
      }
   }

   private static void ab2c(Object var0, Method var1, Object var2) throws Exception {
      Class[] var3;
      if ((var3 = var1.getParameterTypes()).length == 1) {
         var1.invoke(var0, var2);
      } else {
         Object[] var4;
         (var4 = new Object[var3.length])[0] = var2;

         for (int var5 = 1; var5 < var3.length; var5++) {
            var4[var5] = m4z1(var3[var5]);
         }

         var1.invoke(var0, var4);
      }
   }

   private static Object x7q9(Component var0) throws Exception {
      return var0 == null ? null : hH.invoke(null, var0);
   }

   private static Method ab2c(Class<?> var0) throws NoSuchMethodException {
      Method var1 = null;
      Method[] var2;
      int var3 = (var2 = var0.getMethods()).length;

      for (int var4 = 0; var4 < var3; var4++) {
         Method var5;
         Class[] var6;
         if ((var5 = var2[var4]).getName().equals("send")
            && (var6 = var5.getParameterTypes()).length > 0
            && (hs == null || var6[0].isAssignableFrom(hs) || hs.isAssignableFrom(var6[0]))
            && (var1 == null || var6.length < var1.getParameterCount())) {
            var1 = var5;
         }
      }

      if (var1 == null) {
         throw new NoSuchMethodException("send(Packet,...) not found in " + var0.getName());
      } else {
         var1.setAccessible(true);
         return var1;
      }
   }

   private static Field ab2c(Class<?> var0, String var1) throws NoSuchFieldException {
      for (Class var2 = var0; var2 != null; var2 = var2.getSuperclass()) {
         try {
            Field var3;
            (var3 = var2.getDeclaredField(var1)).setAccessible(true);
            return var3;
         } catch (NoSuchFieldException var4) {
         }
      }

      throw new NoSuchFieldException(var1 + " not found in " + var0.getName());
   }

   private static boolean x7q9(Class<?> var0) {
      return var0 == int.class || var0 == Integer.class;
   }

   private static Object m4z1(Class<?> var0) {
      if (!var0.isPrimitive()) {
         return null;
      } else if (var0 == boolean.class) {
         return Boolean.FALSE;
      } else if (var0 == byte.class) {
         return (byte)0;
      } else if (var0 == short.class) {
         return (short)0;
      } else if (var0 == int.class) {
         return 0;
      } else if (var0 == long.class) {
         return 0L;
      } else if (var0 == float.class) {
         return 0.0F;
      } else if (var0 == double.class) {
         return 0.0;
      } else {
         return var0 == char.class ? '\u0000' : null;
      }
   }

   static {
      try {
         hs = Class.forName("net.minecraft.network.protocol.Packet");
         ht = Class.forName("net.minecraft.network.protocol.game.ClientboundSetObjectivePacket");
         hu = Class.forName("net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket");
         hv = Class.forName("net.minecraft.network.protocol.game.ClientboundSetScorePacket");
         hw = Class.forName("net.minecraft.network.protocol.game.ClientboundResetScorePacket");
         hx = Class.forName("net.minecraft.world.scores.DisplaySlot");
         hy = Class.forName("net.minecraft.world.scores.criteria.ObjectiveCriteria$RenderType");
         hz = Class.forName("net.minecraft.world.scores.criteria.ObjectiveCriteria");
         hA = Class.forName("net.minecraft.world.scores.Scoreboard");
         hB = Class.forName("net.minecraft.world.scores.Objective");
         hC = Enum.valueOf(hx, "SIDEBAR");
         hD = Enum.valueOf(hy, "INTEGER");
         hE = ab2c(hz, "DUMMY").get(null);
         hG = (hH = Class.forName("io.papermc.paper.adventure.PaperAdventure").getMethod("asVanilla", Component.class)).invoke(null, Component.empty());

         try {
            Field var1;
            (var1 = Class.forName("net.minecraft.network.chat.numbers.BlankFormat").getDeclaredField("INSTANCE")).setAccessible(true);
            hF = var1.get(null);
         } catch (Throwable var8) {
            hF = null;
         }

         try {
            (hI = hA.getDeclaredConstructor()).setAccessible(true);
         } catch (Throwable var7) {
            hI = null;
         }

         Constructor[] var0;
         int var14 = (var0 = hB.getDeclaredConstructors()).length;

         for (int var2 = 0; var2 < var14; var2++) {
            Constructor var3;
            (var3 = var0[var2]).setAccessible(true);
            Class[] var4;
            if ((var4 = var3.getParameterTypes()).length >= 5 && var4[0] == hA && var4[1] == String.class) {
               hJ = var3;
               break;
            }
         }

         var14 = (var0 = ht.getDeclaredConstructors()).length;

         for (int var19 = 0; var19 < var14; var19++) {
            Constructor var23;
            (var23 = var0[var19]).setAccessible(true);
            Class[] var27;
            if ((var27 = var23.getParameterTypes()).length == 2 && var27[0] == hB && x7q9(var27[1])) {
               hK = var23;
            } else if (var27.length >= 2 && var27[0] == String.class && x7q9(var27[1])) {
               hQ.add(var23);
            }
         }

         hQ.sort(Comparator.<Constructor<?>>comparingInt(var0x -> var0x.getParameterTypes().length).reversed());
         var14 = (var0 = hu.getDeclaredConstructors()).length;

         for (int var20 = 0; var20 < var14; var20++) {
            Constructor var24;
            (var24 = var0[var20]).setAccessible(true);
            Class[] var28;
            if ((var28 = var24.getParameterTypes()).length == 2 && var28[0] == hx && var28[1] == hB) {
               hL = var24;
            } else if (var28.length == 2 && var28[1] == String.class) {
               hM = var24;
            }
         }

         var14 = (var0 = hv.getDeclaredConstructors()).length;

         for (int var21 = 0; var21 < var14; var21++) {
            Constructor var25;
            (var25 = var0[var21]).setAccessible(true);
            Class[] var29;
            if ((var29 = var25.getParameterTypes()).length >= 3 && var29[0] == String.class && var29[1] == String.class && x7q9(var29[2])) {
               hR.add(var25);
            }
         }

         hR.sort(Comparator.<Constructor<?>>comparingInt(var0x -> var0x.getParameterTypes().length).reversed());
         var14 = (var0 = hw.getDeclaredConstructors()).length;

         for (int var22 = 0; var22 < var14; var22++) {
            Constructor var26;
            (var26 = var0[var22]).setAccessible(true);
            Class[] var30;
            if ((var30 = var26.getParameterTypes()).length > 0 && var30[0] == String.class) {
               hS.add(var26);
            }
         }

         hS.sort(Comparator.<Constructor<?>>comparingInt(var0x -> var0x.getParameterTypes().length).reversed());

         try {
            hN = Class.forName("org.bukkit.craftbukkit.entity.CraftPlayer").getMethod("getHandle");
         } catch (Throwable var6) {
            hN = null;
         }

         try {
            hP = ab2c((hO = ab2c(Class.forName("net.minecraft.server.level.ServerPlayer"), "connection")).getType());
         } catch (Throwable var5) {
            hO = null;
            hP = null;
         }

         if (!(hr = !hR.isEmpty() && !hS.isEmpty() && (hK != null || !hQ.isEmpty()) && (hL != null || hM != null))) {
            hq.warning("[Scoreboard] Packet bridge incomplete; sidebar disabled.");
            return;
         }

         hq.info("[Scoreboard] Packet bridge ready for Paper/Folia.");
      } catch (Throwable var9) {
         hr = false;
         hq.log(Level.WARNING, "[Scoreboard] Packet bridge failed; sidebar disabled.", var9);
      }
   }
}
