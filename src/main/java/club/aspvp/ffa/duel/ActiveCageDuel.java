package club.aspvp.ffa.duel;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActiveCageDuel {

    public final UUID player1UUID;
    public final String player1Name;
    public final UUID player2UUID;
    public final String player2Name;
    public final String arenaId;
    public final String kitName;
    public final Location cageCenter;
    public final int radiusXZ;
    public final int heightY;

    public final Map<Block, BlockData> originalBlocks = new HashMap<>();
    public BossBar bossBar;
    public BukkitTask timerTask;
    public BukkitTask particleTask;

    public int secondsElapsed = 0;
    public boolean active = true;
    public boolean frozen = true;

    public ActiveCageDuel(UUID player1UUID, String player1Name, UUID player2UUID, String player2Name,
                          String arenaId, String kitName, Location cageCenter, int radiusXZ, int heightY) {
        this.player1UUID = player1UUID;
        this.player1Name = player1Name;
        this.player2UUID = player2UUID;
        this.player2Name = player2Name;
        this.arenaId = arenaId;
        this.kitName = kitName;
        this.cageCenter = cageCenter;
        this.radiusXZ = radiusXZ;
        this.heightY = heightY;
    }

    public boolean involves(Player player) {
        if (player == null) return false;
        UUID uid = player.getUniqueId();
        return uid.equals(player1UUID) || uid.equals(player2UUID);
    }

    public UUID getOpponentUUID(UUID uid) {
        if (uid.equals(player1UUID)) return player2UUID;
        if (uid.equals(player2UUID)) return player1UUID;
        return null;
    }

    public Player getOpponent(Player player) {
        UUID oppUid = getOpponentUUID(player.getUniqueId());
        return oppUid != null ? Bukkit.getPlayer(oppUid) : null;
    }

    public boolean containsLocation(Location loc) {
        if (loc == null || loc.getWorld() == null || cageCenter.getWorld() == null) return false;
        if (!loc.getWorld().equals(cageCenter.getWorld())) return false;

        double minX = cageCenter.getX() - radiusXZ;
        double maxX = cageCenter.getX() + radiusXZ;
        double minZ = cageCenter.getZ() - radiusXZ;
        double maxZ = cageCenter.getZ() + radiusXZ;
        double minY = cageCenter.getY();
        double maxY = cageCenter.getY() + heightY;

        return loc.getX() >= minX && loc.getX() <= maxX
                && loc.getY() >= minY && loc.getY() <= maxY
                && loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

    public void restoreBlocks() {
        for (Map.Entry<Block, BlockData> entry : originalBlocks.entrySet()) {
            entry.getKey().setBlockData(entry.getValue(), false);
        }
        originalBlocks.clear();
    }
}
