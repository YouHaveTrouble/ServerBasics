package eu.endermite.serverbasics.storage;

import eu.endermite.serverbasics.players.BasicPlayer;
import org.bukkit.Location;

import java.util.UUID;

public interface Database {

    void createTables();
    BasicPlayer getPlayer(UUID uuid);
    void savePlayer(BasicPlayer basicPlayer);
    void saveOption(UUID uuid, PlayerOption option, Object value);
    void saveSpawn(Location location);


}

enum PlayerOption {
    NICK("displayname"),
    GAMEMODE("gamemode");

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
    NAME("name"),
    PLAYER("player_uuid");

    private final String rowId;

    WarpOption(String rowId) {
        this.rowId = rowId;
    }

    public String getId() {
        return rowId;
    }
}
