package eu.endermite.serverbasics.storage;

import eu.endermite.serverbasics.players.BasicPlayer;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public interface Database {

    void createTables(String playerTableName, String warpTableName, String homesTableName);
    BasicPlayer getPlayer(UUID uuid);
    void savePlayer(BasicPlayer basicPlayer);
    void savePlayerOption(UUID uuid, PlayerOption option, Object value);
    void deletePlayer(UUID uuid);

    Location getSpawn();
    void saveSpawn(Location location);
    void deleteSpawn();

    HashMap<String, Location> getWarps();
    void saveWarp(Location location, String name);
    void deleteWarp(String name);

    HashMap<String, Location> getPlayerHomes(UUID uuid);
    void savePlayerHomes(HashMap<String, Location> homes);
    void deletePlayerHome(UUID uuid, String name);


}

enum PlayerOption {
    NICK("displayname"),
    GAMEMODE("gamemode"),
    HOMES("homes");

    private final String rowId;

    PlayerOption(String rowId) {
        this.rowId = rowId;
    }

    public String getId() {
        return rowId;
    }
}

enum WarpOption {
    LOCATION("location"),
    NAME("name");

    private final String rowId;

    WarpOption(String rowId) {
        this.rowId = rowId;
    }

    public String getId() {
        return rowId;
    }
}
