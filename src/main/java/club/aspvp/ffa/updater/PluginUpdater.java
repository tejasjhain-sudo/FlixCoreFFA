package club.aspvp.ffa.updater;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginUpdater {
    public static final String REPO = "tejasjhain-sudo/FlixCoreFFA";
    public static final byte[] AES_KEY = "FlixCoreFFA2026K".getBytes(StandardCharsets.UTF_8);
    public static final byte[] MAGIC = new byte[]{'F', 'L', 'I', 'X'};

    private final JavaPlugin plugin;
    private final String currentVersion;
    private String latestVersion = null;
    private String downloadUrl = null;

    public PluginUpdater(JavaPlugin plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    /**
     * Checks GitHub API for the latest release tag asynchronously.
     */
    public void checkForUpdates(CommandSender notifier) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URI uri = URI.create("https://api.github.com/repos/" + REPO + "/releases/latest");
                HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
                conn.setRequestProperty("Accept", "application/vnd.github+json");
                conn.setRequestProperty("User-Agent", "FlixCoreFFA-Updater/1.0");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);

                if (conn.getResponseCode() != 200) {
                    if (notifier != null) {
                        notifier.sendMessage("§cFailed to check for updates: HTTP " + conn.getResponseCode());
                    }
                    return;
                }

                String json = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                this.latestVersion = extractJsonString(json, "tag_name").replace("v", "").trim();

                // Find download URL for core.bin or .jar
                if (json.contains("\"browser_download_url\"")) {
                    for (String part : json.split("\"browser_download_url\": \"")) {
                        if (part.contains("core.bin") || part.contains(".jar")) {
                            this.downloadUrl = part.substring(0, part.indexOf("\""));
                            break;
                        }
                    }
                }

                if (isNewer(latestVersion, currentVersion)) {
                    String msg = "§8[§bFlixCore FFA§8] §aA new update is available: §ev" + latestVersion + " §7(Current: v" + currentVersion + ")";
                    Bukkit.getConsoleSender().sendMessage(msg);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.isOp() || p.hasPermission("ffa.admin")) {
                            p.sendMessage(msg + " §7Run §e/ffa update §7to install.");
                        }
                    }
                    if (notifier != null && !(notifier instanceof Player && ((Player) notifier).isOp())) {
                        notifier.sendMessage(msg + " §7Run §e/ffa update §7to install.");
                    }
                } else if (notifier != null) {
                    notifier.sendMessage("§8[§bFlixCore FFA§8] §aYou are running the latest version (v" + currentVersion + ").");
                }
            } catch (Exception e) {
                if (notifier != null) {
                    notifier.sendMessage("§cUpdate check failed: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Downloads, decrypts, validates, and installs the update into plugins/update/.
     */
    public void downloadAndInstall(CommandSender sender) {
        if (downloadUrl == null) {
            sender.sendMessage("§eChecking for available updates first...");
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    URI uri = URI.create("https://api.github.com/repos/" + REPO + "/releases/latest");
                    HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
                    conn.setRequestProperty("Accept", "application/vnd.github+json");
                    conn.setRequestProperty("User-Agent", "FlixCoreFFA-Updater/1.0");
                    if (conn.getResponseCode() == 200) {
                        String json = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                        this.latestVersion = extractJsonString(json, "tag_name").replace("v", "").trim();
                        if (json.contains("\"browser_download_url\"")) {
                            for (String part : json.split("\"browser_download_url\": \"")) {
                                if (part.contains("core.bin") || part.contains(".jar")) {
                                    this.downloadUrl = part.substring(0, part.indexOf("\""));
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {}

                if (downloadUrl == null) {
                    sender.sendMessage("§cNo update release asset found on GitHub.");
                    return;
                }
                executeDownload(sender);
            });
            return;
        }

        executeDownload(sender);
    }

    private void executeDownload(CommandSender sender) {
        sender.sendMessage("§eDownloading update from GitHub...");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            File tempTarget = null;
            File decryptedTarget = null;
            try {
                File updateFolder = new File(plugin.getDataFolder().getParentFile(), "update");
                if (!updateFolder.exists()) updateFolder.mkdirs();

                String finalJarName = plugin.getName() + ".jar";
                tempTarget = new File(updateFolder, finalJarName + ".download.tmp");

                // 1. Download payload
                HttpURLConnection conn = (HttpURLConnection) URI.create(downloadUrl).toURL().openConnection();
                conn.setRequestProperty("User-Agent", "FlixCoreFFA-Updater/1.0");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(30000);

                try (InputStream in = conn.getInputStream(); OutputStream out = new FileOutputStream(tempTarget)) {
                    in.transferTo(out);
                }

                File jarToVerify = tempTarget;

                // 2. Decrypt if payload is encrypted with MAGIC header
                if (isEncrypted(tempTarget)) {
                    sender.sendMessage("§7Decrypting protected payload...");
                    decryptedTarget = new File(updateFolder, finalJarName + ".decrypted.tmp");
                    decryptFile(tempTarget, decryptedTarget);
                    jarToVerify = decryptedTarget;
                }

                // 3. Verify valid plugin.yml inside jar
                try (JarFile jf = new JarFile(jarToVerify)) {
                    JarEntry entry = jf.getJarEntry("plugin.yml");
                    if (entry == null) {
                        throw new IOException("Downloaded payload is not a valid Bukkit plugin (missing plugin.yml).");
                    }
                }

                // 4. Move to plugins/update/FlixCoreFFA.jar
                File finalDestination = new File(updateFolder, finalJarName);
                Files.move(jarToVerify.toPath(), finalDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);

                sender.sendMessage("§8[§bFlixCore FFA§8] §aUpdate " + (latestVersion != null ? "v" + latestVersion + " " : "") + "downloaded successfully!");
                sender.sendMessage("§7The new version will automatically apply on the next server restart.");
            } catch (Exception e) {
                sender.sendMessage("§cFailed to install update: " + e.getMessage());
            } finally {
                if (tempTarget != null && tempTarget.exists()) tempTarget.delete();
                if (decryptedTarget != null && decryptedTarget.exists()) decryptedTarget.delete();
            }
        });
    }

    public static boolean isEncrypted(File file) {
        try (InputStream in = new FileInputStream(file)) {
            byte[] header = in.readNBytes(MAGIC.length);
            for (int i = 0; i < MAGIC.length; i++) {
                if (header[i] != MAGIC[i]) return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void encryptFile(File src, File dest) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(AES_KEY, "AES"));
        try (InputStream in = new FileInputStream(src); OutputStream out = new FileOutputStream(dest)) {
            out.write(MAGIC);
            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) != -1) {
                byte[] enc = cipher.update(buf, 0, r);
                if (enc != null) out.write(enc);
            }
            byte[] finalBytes = cipher.doFinal();
            if (finalBytes != null) out.write(finalBytes);
        }
    }

    public static void decryptFile(File src, File dest) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(AES_KEY, "AES"));
        try (InputStream in = new FileInputStream(src); OutputStream out = new FileOutputStream(dest)) {
            in.readNBytes(MAGIC.length); // skip MAGIC
            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) != -1) {
                byte[] dec = cipher.update(buf, 0, r);
                if (dec != null) out.write(dec);
            }
            byte[] finalBytes = cipher.doFinal();
            if (finalBytes != null) out.write(finalBytes);
        }
    }

    public static boolean isNewer(String remote, String current) {
        if (remote == null || current == null) return false;
        String[] r = remote.split("\\.");
        String[] c = current.split("\\.");
        for (int i = 0; i < Math.min(r.length, c.length); i++) {
            int ri = Integer.parseInt(r[i].replaceAll("[^0-9]", ""));
            int ci = Integer.parseInt(c[i].replaceAll("[^0-9]", ""));
            if (ri > ci) return true;
            if (ri < ci) return false;
        }
        return r.length > c.length;
    }

    private static String extractJsonString(String json, String key) {
        String search = "\"" + key + "\": \"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    public static void main(String[] args) throws Exception {
        if (args.length >= 3 && args[0].equals("--encrypt")) {
            File src = new File(args[1]);
            File dest = new File(args[2]);
            encryptFile(src, dest);
            System.out.println("Encrypted payload created at: " + dest.getAbsolutePath() + " (" + dest.length() + " bytes)");
        } else if (args.length >= 3 && args[0].equals("--decrypt")) {
            File src = new File(args[1]);
            File dest = new File(args[2]);
            decryptFile(src, dest);
            System.out.println("Decrypted jar created at: " + dest.getAbsolutePath() + " (" + dest.length() + " bytes)");
        } else {
            System.out.println("Usage: PluginUpdater --encrypt <src.jar> <dest.bin> | --decrypt <src.bin> <dest.jar>");
        }
    }
}
