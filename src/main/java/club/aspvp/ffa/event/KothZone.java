package club.aspvp.ffa.event;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class KothZone {
    private final String worldName;
    private final double minX;
    private final double minY;
    private final double minZ;
    private final double maxX;
    private final double maxY;
    private final double maxZ;

    public KothZone(String worldName, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.worldName = worldName;
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public static KothZone fromCenter(Location center, double radiusXZ, double heightY) {
        return new KothZone(
                center.getWorld().getName(),
                center.getX() - radiusXZ,
                center.getY() - 1.0,
                center.getZ() - radiusXZ,
                center.getX() + radiusXZ,
                center.getY() + heightY,
                center.getZ() + radiusXZ
        );
    }

    public static KothZone fromCorners(Location loc1, Location loc2) {
        return new KothZone(
                loc1.getWorld().getName(),
                loc1.getX(),
                loc1.getY(),
                loc1.getZ(),
                loc2.getX(),
                loc2.getY(),
                loc2.getZ()
        );
    }

    public static KothZone fromConfig(ConfigurationSection sec) {
        if (sec == null) return null;
        String w = sec.getString("world");
        if (w == null) return null;
        return new KothZone(
                w,
                sec.getDouble("min-x"),
                sec.getDouble("min-y"),
                sec.getDouble("min-z"),
                sec.getDouble("max-x"),
                sec.getDouble("max-y"),
                sec.getDouble("max-z")
        );
    }

    public void saveToConfig(ConfigurationSection sec) {
        sec.set("world", worldName);
        sec.set("min-x", minX);
        sec.set("min-y", minY);
        sec.set("min-z", minZ);
        sec.set("max-x", maxX);
        sec.set("max-y", maxY);
        sec.set("max-z", maxZ);
    }

    public boolean contains(Location loc) {
        if (loc == null || loc.getWorld() == null) return false;
        if (!loc.getWorld().getName().equalsIgnoreCase(worldName)) return false;
        return loc.getX() >= minX && loc.getX() <= maxX
                && loc.getY() >= minY && loc.getY() <= maxY
                && loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public String getWorldName() {
        return worldName;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMinZ() {
        return minZ;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMaxZ() {
        return maxZ;
    }
}
