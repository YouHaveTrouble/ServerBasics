package eu.endermite.serverbasics.players;

import lombok.Builder;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * All player data that needs to be tracked in a plugin and convienience methods
 */
@Builder
public class BasicPlayer {

    private final Player player;
    private final UUID uuid;
    private String displayName;
    private boolean fly;

    public UUID getUuid() {
        return uuid;
    }

    public boolean canFly() {
        return fly;
    }
    public boolean setFly(boolean newState) {
        this.fly = newState;
        return newState;
    }

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String newName) {
        displayName = newName;
    }

    public GameMode getGameMode() {
        return player.getGameMode();
    }
    public void setGameMode(GameMode newGamemode) {
        player.setGameMode(newGamemode);
    }

}


