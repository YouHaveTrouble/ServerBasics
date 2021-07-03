package eu.endermite.serverbasics.config;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.storage.ServerDatabase;
import org.bukkit.Location;

import java.util.HashMap;

public class LocationsCache {

    public Location spawn;
    public HashMap<String, Location> warps;
    public HashMap<String, Location> homes;

    public LocationsCache() {

        ServerBasics plugin = ServerBasics.getInstance();

        this.spawn = ServerDatabase.getSpawn(ServerBasics.getConfigCache().getServerUuid());


    }



    public boolean isSpawnSet() {
        return spawn != null;
    }

    public void setSpawn(Location sBasicLocation) {
        spawn = sBasicLocation;
    }


}
