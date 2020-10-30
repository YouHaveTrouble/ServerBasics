package eu.endermite.serverbasics.config;

import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class LocationsCache {

    private final FileConfiguration fileConfiguration;
    private final File locFile;
    public Location spawn;
    private HashMap<String, Location> warps;

    public LocationsCache() {

        ServerBasics plugin = ServerBasics.getInstance();

        locFile = new File(plugin.getDataFolder() + "/locations.yml");
        fileConfiguration = new YamlConfiguration();

        if (!locFile.exists()) {
            locFile.getParentFile().mkdirs();
            plugin.saveResource("locations.yml", false);
        }

        try {
            fileConfiguration.load(locFile);

            this.spawn = getLocation("spawn");

            ConfigurationSection warpsSection = fileConfiguration.getConfigurationSection("warps");

            if (warpsSection == null)
                return;

            for (String warp : warpsSection.getKeys(false)) {
                warps.put(warp, getLocation("warp."+warp));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Location getLocation(String configPath) {
        ConfigurationSection locationSection = fileConfiguration.getConfigurationSection(configPath);
        if (locationSection == null)
            return null;
        try {
            World world = Bukkit.getWorld(locationSection.getString("world"));
            double x = locationSection.getDouble("x");
            double y = locationSection.getDouble("y");
            double z = locationSection.getDouble("z");
            return new Location(world, x, y, z);
        } catch (NullPointerException e) {
            ServerBasics.getInstance().getLogger().warning("Detected entry for " + configPath + ", but could not parse the location!");
            return null;
        }


    }

    public boolean isSpawnSet() {
        return spawn != null;
    }

    public void setSpawn(Location location) {
        this.spawn = location;
        fileConfiguration.set("spawn.world", location.getWorld().getName());
        fileConfiguration.set("spawn.x", location.getX());
        fileConfiguration.set("spawn.y", location.getY());
        fileConfiguration.set("spawn.z", location.getZ());

        try {
            fileConfiguration.save(locFile);
        } catch (Exception e) {
            ServerBasics.getInstance().getLogger().warning("Could not save location data to long-term storage");
        }

    }

}
