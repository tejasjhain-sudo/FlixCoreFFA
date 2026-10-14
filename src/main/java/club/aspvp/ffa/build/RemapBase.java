package club.aspvp.ffa.build;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;
import java.io.*;
import java.util.jar.*;

public class RemapBase {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: RemapBase <inputJar> <outputJar>");
            System.exit(1);
        }

        File src = new File(args[0]);
        File dst = new File(args[1]);

        Remapper remapper = new Remapper() {
            @Override
            public String map(String internalName) {
                if (internalName != null && internalName.startsWith("in/twocube/ffa")) {
                    return "club/aspvp/ffa" + internalName.substring("in/twocube/ffa".length());
                }
                return internalName;
            }
        };

        File parent = dst.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();

        try (JarInputStream jis = new JarInputStream(new FileInputStream(src));
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(dst))) {
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                String name = entry.getName();
                if (name.startsWith("dev/demeng/sentinel/")) continue;

                byte[] bytes = jis.readAllBytes();
                if (name.endsWith(".class") && name.startsWith("in/twocube/ffa/")) {
                    ClassReader cr = new ClassReader(bytes);
                    ClassWriter cw = new ClassWriter(0);
                    ClassRemapper cv = new ClassRemapper(cw, remapper);
                    cr.accept(cv, 0);
                    byte[] remapped = cw.toByteArray();
                    String newName = name.replace("in/twocube/ffa/", "club/aspvp/ffa/");
                    jos.putNextEntry(new JarEntry(newName));
                    jos.write(remapped);
                } else if (!name.endsWith(".class")) {
                    jos.putNextEntry(new JarEntry(name));
                    jos.write(bytes);
                }
                jos.closeEntry();
            }
        }
    }
}
