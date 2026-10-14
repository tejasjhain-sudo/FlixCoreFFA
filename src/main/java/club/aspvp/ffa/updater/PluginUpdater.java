package club.aspvp.ffa.updater;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginUpdater {
    public static final String REPO = "tejasjhain-sudo/FlixCoreFFA";
    public static final byte[] AES_KEY = "FlixCoreFFA2026K".getBytes(StandardCharsets.UTF_8);
    public static final byte[] MAGIC = new byte[]{'F', 'L', 'I', 'X'};

    private final JavaPlugin plugin;
    private final String currentVersion;
    private String latestVersion = null;

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
                HttpClient client = HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.ALWAYS)
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.github.com/repos/" + REPO + "/releases/latest"))
                        .header("Accept", "application/vnd.github+json")
                        .header("User-Agent", "FlixCoreFFA-Updater/1.0")
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();

                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() == 200) {
                    String json = resp.body();
                    this.latestVersion = extractJsonString(json, "tag_name").replace("v", "").trim();

                    if (isNewer(latestVersion, currentVersion)) {
                        String msg = "§8[§6★§8] §eA new update is available: §aFlixCore FFA v" + latestVersion + " §7(Current: v" + currentVersion + ")";
                        Bukkit.getConsoleSender().sendMessage(msg);
                        Bukkit.getConsoleSender().sendMessage("§7Run §e/ffa update §7to download and install automatically.");
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.isOp() || p.hasPermission("ffa.admin")) {
                                p.sendMessage(msg + " §7Run §e/ffa update §7to install.");
                            }
                        }
                        if (notifier != null && !(notifier instanceof Player && ((Player) notifier).isOp())) {
                            notifier.sendMessage(msg + " §7Run §e/ffa update §7to install.");
                        }
                    } else if (notifier != null) {
                        notifier.sendMessage("§8[§6★§8] §aYou are running the latest version (v" + currentVersion + ").");
                    }
                } else if (notifier != null) {
                    notifier.sendMessage("§cCould not query GitHub releases API (HTTP " + resp.statusCode() + ").");
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
        sender.sendMessage("§8[§6★§8] §eConnecting to GitHub Releases repository...");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            File tempTarget = null;
            File decryptedTarget = null;
            try {
                File updateFolder = new File(plugin.getDataFolder().getParentFile(), "update");
                if (!updateFolder.exists()) updateFolder.mkdirs();

                String finalJarName = plugin.getName() + ".jar";
                tempTarget = new File(updateFolder, finalJarName + ".download.tmp");

                // Target URL: direct latest release asset download
                String targetUrl = "https://github.com/" + REPO + "/releases/latest/download/core.bin";
                sender.sendMessage("§8[§6★§8] §eDownloading latest encrypted payload (§bcore.bin§e)...");

                HttpClient client = HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.ALWAYS)
                        .connectTimeout(Duration.ofSeconds(15))
                        .build();

                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(targetUrl))
                        .header("User-Agent", "FlixCoreFFA-Updater/1.0")
                        .timeout(Duration.ofSeconds(60))
                        .GET()
                        .build();

                HttpResponse<InputStream> resp = client.send(req, HttpResponse.BodyHandlers.ofInputStream());
                if (resp.statusCode() != 200) {
                    throw new IOException("GitHub returned HTTP " + resp.statusCode() + " while downloading core.bin.");
                }

                try (InputStream in = resp.body(); OutputStream out = new FileOutputStream(tempTarget)) {
                    in.transferTo(out);
                }

                if (!tempTarget.exists() || tempTarget.length() == 0) {
                    throw new IOException("Downloaded file is empty or missing.");
                }

                sender.sendMessage("§8[§6★§8] §7Downloaded " + (tempTarget.length() / 1024) + " KB. Decrypting AES-128 protected payload...");

                File jarToVerify = tempTarget;
                if (isEncrypted(tempTarget)) {
                    decryptedTarget = new File(updateFolder, finalJarName + ".decrypted.tmp");
                    decryptFile(tempTarget, decryptedTarget);
                    jarToVerify = decryptedTarget;
                } else {
                    sender.sendMessage("§7Payload is standard binary jar.");
                }

                // Verify valid plugin.yml inside jar
                try (JarFile jf = new JarFile(jarToVerify)) {
                    JarEntry entry = jf.getJarEntry("plugin.yml");
                    if (entry == null) {
                        throw new IOException("Downloaded payload is not a valid Bukkit plugin (missing plugin.yml).");
                    }
                }

                // Move to plugins/update/FlixCoreFFA.jar
                File finalDestination = new File(updateFolder, finalJarName);
                Files.move(jarToVerify.toPath(), finalDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);

                sender.sendMessage("§8=================================================");
                sender.sendMessage("§6§l           FlixCore FFA - Update Complete");
                sender.sendMessage("§8=================================================");
                sender.sendMessage("§a✔ Update successfully downloaded and verified!");
                sender.sendMessage("§e📁 Saved to: §fplugins/update/" + finalJarName);
                sender.sendMessage("§a🔄 Restart your server to automatically apply the new version.");
                sender.sendMessage("§8=================================================");
            } catch (Exception e) {
                sender.sendMessage("§cFailed to install update: " + e.getMessage());
                plugin.getLogger().severe("Update failed: " + e.getMessage());
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
}
