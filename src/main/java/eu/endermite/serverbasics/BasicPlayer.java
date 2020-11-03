package eu.endermite.serverbasics;

import lombok.Builder;
import org.bukkit.OfflinePlayer;

@Builder
public class BasicPlayer {

    private final OfflinePlayer player;
    String displayName;
    boolean fly;

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

}
