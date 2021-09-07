package eu.endermite.serverbasics.config;

import com.google.common.collect.ImmutableList;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.util.BasicWarp;

import java.util.HashMap;
import java.util.List;

public class LocationsCache {

    public BasicWarp spawn;
    private HashMap<String, BasicWarp> warps;
    private HashMap<String, BasicWarp> homes;

    public LocationsCache() {
        ServerBasics.getInstance().getDatabase().getSpawn().thenAccept(spawn -> this.spawn = spawn);
        ServerBasics.getInstance().getDatabase().getWarps().thenAccept(warps -> {
            warps.remove("spawn");
            this.warps = warps;
        });
    }

    public boolean isSpawnSet() {
        return spawn != null;
    }

    public void setSpawn(BasicWarp basicWarp) {
        ServerBasics.getInstance().getDatabase().saveSpawn(basicWarp).thenRun(() -> spawn = basicWarp);
    }

    public void clearSpawn() {
        ServerBasics.getInstance().getDatabase().deleteSpawn().thenRun(() -> spawn = null);
    }

    public void addWarp(BasicWarp basicWarp) {
        ServerBasics.getInstance().getDatabase().saveWarp(basicWarp).thenRun(() -> warps.put(basicWarp.getWarpId(), basicWarp));
    }

    public void deleteWarp(String warpId) {
        ServerBasics.getInstance().getDatabase().deleteWarp(warpId).thenRun(() -> warps.remove(warpId));
    }

    public BasicWarp getWarp(String warpId) {
        return warps.get(warpId);
    }

    public boolean warpExists(String warpId) {
        return warps.containsKey(warpId);
    }

    /**
     * @return Immutable list of all cached warp IDs
     */
    public List<BasicWarp> warpList() {
        return ImmutableList.copyOf(warps.values());
    }


}
