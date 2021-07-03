package eu.endermite.serverbasics.players;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * All player data that needs to be tracked in a plugin and convienience methods
 */
public class BasicPlayer {

    private final Player player;
    private final UUID uuid;
    private String displayName;
    private boolean fly;

    public BasicPlayer(BasicPlayerBuilder builder) {
        this.uuid = builder.build().getUuid();
        this.player = Bukkit.getPlayer(uuid);
    }

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

    public BasicPlayer setGameMode(GameMode newGamemode) {
        player.setGameMode(newGamemode);
        return this;
    }

    public static BasicPlayerBuilder builder(UUID uuid) {
        return new BasicPlayerBuilder(uuid);
    }

    public static class BasicPlayerBuilder {

        private final UUID uuid;
        private boolean fly;
        private String displayName;

        public BasicPlayerBuilder(UUID uuid) {
            this.uuid = uuid;
        }

        public BasicPlayerBuilder fly(boolean fly) {
            this.fly = fly;
            return this;
        }

        public BasicPlayerBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }


        public BasicPlayer build() {
            return new BasicPlayer(this);
        }

    }

}


