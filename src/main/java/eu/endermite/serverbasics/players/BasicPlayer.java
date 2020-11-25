package eu.endermite.serverbasics.players;

import lombok.Builder;
import lombok.NoArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import java.util.UUID;

/**
 * All player data that needs to be tracked in a plugin and convienience methods
 */
@Builder
public class BasicPlayer {

    private final OfflinePlayer player;
    private UUID uuid;
    private String displayName;
    private boolean fly;
    private GameMode gameMode;

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
    public OfflinePlayer getOfflinePlayer() {
        return player;
    }
    public boolean isOnline() {
        return player.isOnline();
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode newGamemode) {
        this.gameMode = newGamemode;
    }

}
