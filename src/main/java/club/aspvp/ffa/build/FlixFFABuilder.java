package club.aspvp.ffa.build;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;

public class FlixFFABuilder {
    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println("Usage: FlixFFABuilder <sourceJar> <classesDir> <resourcesDir> <outputJar>");
            System.exit(1);
        }

        File sourceJar = new File(args[0]);
        File classesDir = new File(args[1]);
        File resourcesDir = new File(args[2]);
        File outputJar = new File(args[3]);

        System.out.println("Packaging FlixCoreFFA jar...");
        System.out.println("Base Jar: " + sourceJar.getAbsolutePath());
        System.out.println("Compiled Classes: " + classesDir.getAbsolutePath());
        System.out.println("Resources: " + resourcesDir.getAbsolutePath());
        System.out.println("Target Output: " + outputJar.getAbsolutePath());

        Map<String, byte[]> jarEntries = new LinkedHashMap<>();

        // 1. Read base jar entries, filtering out old sentinel and obsolete files
        try (JarInputStream jis = new JarInputStream(new FileInputStream(sourceJar))) {
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                String name = entry.getName();
                // Skip dev/demeng/sentinel (old license system)
                if (name.startsWith("dev/demeng/sentinel/")) {
                    continue;
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                jis.transferTo(baos);
                jarEntries.put(name, baos.toByteArray());
            }
        }

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
                        jarEntries.put(relPath, bytes);
                        System.out.println("Added class: " + relPath);
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
                        System.out.println("Added resource: " + relPath);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        }

        // 4. Write final jar
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
        System.out.println("SUCCESS! Output jar created at: " + outputJar.getAbsolutePath());
        System.out.println("Jar size: " + outputJar.length() + " bytes");
        System.out.println("Total entries: " + jarEntries.size());
        System.out.println("=================================================");
    }
}
