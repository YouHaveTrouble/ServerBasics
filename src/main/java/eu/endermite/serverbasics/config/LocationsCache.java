package eu.endermite.serverbasics.config;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.util.BasicWarp;
import org.bukkit.Location;

import java.util.HashMap;

public class LocationsCache {

    public BasicWarp spawn;
    public HashMap<String, Location> warps;
    public HashMap<String, Location> homes;

    public LocationsCache() {
        ServerBasics.getInstance().getDatabase().getSpawn().thenAccept(spawn -> this.spawn = spawn);
    }



    public boolean isSpawnSet() {
        return spawn != null;
    }

    public void setSpawn(BasicWarp sBasicLocation) {
        spawn = sBasicLocation;
    }


}
