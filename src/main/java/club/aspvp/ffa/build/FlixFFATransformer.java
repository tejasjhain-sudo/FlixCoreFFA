package club.aspvp.ffa.build;

import club.aspvp.ffa.updater.PluginUpdater;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;

public class FlixFFATransformer {

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println("Usage: FlixFFATransformer <sourceJar> <classesDir> <resourcesDir> <outputJar>");
            System.exit(1);
        }

        File sourceJar = new File(args[0]);
        File classesDir = new File(args[1]);
        File resourcesDir = new File(args[2]);
        File outputJar = new File(args[3]);

        System.out.println("=================================================");
        System.out.println("   FlixCore FFA Advanced Transformer & Protector");
        System.out.println("=================================================");
        System.out.println("Base Jar: " + sourceJar.getAbsolutePath());
        System.out.println("Compiled Classes: " + classesDir.getAbsolutePath());
        System.out.println("Resources: " + resourcesDir.getAbsolutePath());
        System.out.println("Target Output: " + outputJar.getAbsolutePath());

        Map<String, byte[]> jarEntries = new LinkedHashMap<>();
        Remapper remapper = new Remapper() {
            @Override
            public String map(String internalName) {
                if (internalName != null && internalName.startsWith("in/twocube/ffa")) {
                    return "club/aspvp/ffa" + internalName.substring("in/twocube/ffa".length());
                }
                return internalName;
            }
        };

        int remappedCount = 0;

        // 1. Process base jar: remap in/twocube/ffa -> club/aspvp/ffa and strip debug tables
        try (JarInputStream jis = new JarInputStream(new FileInputStream(sourceJar))) {
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                String name = entry.getName();

                // Skip obsolete Sentinel licensing classes
                if (name.startsWith("dev/demeng/sentinel/")) {
                    continue;
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                jis.transferTo(baos);
                byte[] bytes = baos.toByteArray();

                if (name.endsWith(".class") && name.startsWith("in/twocube/ffa/")) {
                    byte[] remappedBytes = remapAndProtect(bytes, remapper);
                    String remappedName = name.replace("in/twocube/ffa/", "club/aspvp/ffa/");
                    jarEntries.put(remappedName, remappedBytes);
                    remappedCount++;
                } else if (!name.endsWith(".class")) {
                    // Resource file (will be overridden by resourcesDir if present)
                    jarEntries.put(name, bytes);
                } else {
                    jarEntries.put(name, bytes);
                }
            }
        }
        System.out.println("Remapped & sanitized " + remappedCount + " base classes from in.twocube.ffa -> club.aspvp.ffa");

        // 2. Add or overwrite compiled classes from classesDir
        if (classesDir.exists() && classesDir.isDirectory()) {
            Path classRoot = classesDir.toPath();
            Files.walk(classRoot)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".class"))
                .forEach(p -> {
                    String relPath = classRoot.relativize(p).toString().replace('\\', '/');
                    try {
                        byte[] bytes = Files.readAllBytes(p);
                        // Apply Layer 1 Anti-Decompilation: strip debug tables
                        byte[] protectedBytes = stripDebugTables(bytes);
                        jarEntries.put(relPath, protectedBytes);
                        System.out.println("Injected compiled class: " + relPath);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        }

        // 3. Add or overwrite resources from resourcesDir
        if (resourcesDir.exists() && resourcesDir.isDirectory()) {
            Path resRoot = resourcesDir.toPath();
            Files.walk(resRoot)
                .filter(Files::isRegularFile)
                .forEach(p -> {
                    String relPath = resRoot.relativize(p).toString().replace('\\', '/');
                    try {
                        byte[] bytes = Files.readAllBytes(p);
                        jarEntries.put(relPath, bytes);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            System.out.println("Injected all resources from " + resourcesDir.getName());
        }

        // 4. Write final output jar
        File parentDir = outputJar.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputJar), manifest)) {
            for (Map.Entry<String, byte[]> entry : jarEntries.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("META-INF/MANIFEST.MF")) {
                    continue;
                }
                JarEntry je = new JarEntry(entry.getKey());
                jos.putNextEntry(je);
                jos.write(entry.getValue());
                jos.closeEntry();
            }
        }

        System.out.println("=================================================");
        System.out.println("Protected Jar: " + outputJar.getAbsolutePath() + " (" + outputJar.length() + " bytes)");

        // 5. Layer 4: Encrypt binary payload core.bin
        File coreBin = new File(outputJar.getParentFile(), "core.bin");
        PluginUpdater.encryptFile(outputJar, coreBin);
        System.out.println("Encrypted Payload (core.bin): " + coreBin.getAbsolutePath() + " (" + coreBin.length() + " bytes)");
        System.out.println("=================================================");
    }

    /**
     * Remaps classes using ASM ClassRemapper and clears debug tables (Layer 1).
     */
    private static byte[] remapAndProtect(byte[] classBytes, Remapper remapper) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(0);
        ClassRemapper cv = new ClassRemapper(cw, remapper) {
            @Override
            public void visitSource(String source, String debug) {
                // Obfuscate / standardize source file name and strip debug info
                super.visitSource("FlixCoreFFA.java", null);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM9, mv) {
                    @Override
                    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
                        // Strip LocalVariableTable (Layer 1)
                    }

                    @Override
                    public void visitParameter(String name, int access) {
                        // Strip MethodParameters (Layer 1)
                    }
                };
            }
        };
        cr.accept(cv, 0);
        return cw.toByteArray();
    }

    /**
     * Strips LocalVariableTable, MethodParameters, and normalizes SourceFile (Layer 1).
     */
    private static byte[] stripDebugTables(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        cn.sourceFile = "FlixCoreFFA.java";
        cn.sourceDebug = null;

        for (MethodNode mn : cn.methods) {
            if (mn.localVariables != null) {
                mn.localVariables.clear();
            }
            if (mn.parameters != null) {
                mn.parameters.clear();
            }
        }

        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
