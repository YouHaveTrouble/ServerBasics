package eu.endermite.serverbasics.players;

import eu.endermite.serverbasics.NMSHandler;
import eu.endermite.serverbasics.ServerBasics;
import lombok.Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * All player data that needs to be tracked in a plugin and convienience methods
 */
@Builder
public class BasicPlayer {

    private final UUID uuid;

    BasicPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean canFly() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;
        return player.getAllowFlight();
    }
    public boolean setFly(boolean newState) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;
        player.setAllowFlight(newState);
        return newState;
    }

    public Component getDisplayName() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore() || offlinePlayer.getName() == null) return Component.empty();
        if (offlinePlayer.isOnline())
            return offlinePlayer.getPlayer().displayName();
        else
            return Component.text(offlinePlayer.getName());
    }
    public boolean setDisplayName(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return false;
        if (offlinePlayer.isOnline()) {
            offlinePlayer.getPlayer().displayName(MiniMessage.markdown().parse(name));
            return true;
        } else {
            // TODO offline player display name
            return false;
        }
    }

    public GameMode getGameMode() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return null;
        if (offlinePlayer.isOnline())
            return offlinePlayer.getPlayer().getGameMode();
        else
            return NMSHandler.getOfflinePlayerGamemode(offlinePlayer);
    }

    public boolean setGameMode(GameMode gamemode) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return false;
        if (offlinePlayer.isOnline())
            Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> offlinePlayer.getPlayer().setGameMode(gamemode)) ;
        else
            NMSHandler.setOfflinePlayerGamemode(offlinePlayer, gamemode);
        return true;
    }

    public static BasicPlayer fromPlayer(Player player) {
        return new BasicPlayer(player.getUniqueId());
    }

    public static BasicPlayer fromOfflinePlayer(OfflinePlayer offlinePlayer) {
        return new BasicPlayer(offlinePlayer.getUniqueId());
    }

    public static BasicPlayer fromUuid(UUID uuid) {
        return new BasicPlayer(uuid);
    }

    public static CompletableFuture<BasicPlayer> fromDatabase(UUID uuid) {
        return ServerBasics.getInstance().getDatabase().getPlayer(uuid);
    }

}


