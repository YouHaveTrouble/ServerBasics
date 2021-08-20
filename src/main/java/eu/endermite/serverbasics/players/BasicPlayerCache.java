package eu.endermite.serverbasics.players;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BasicPlayerCache {

    private final HashMap<UUID, BasicPlayer> basicPlayers = new HashMap<>();

    public BasicPlayerCache() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            BasicPlayer.fromPlayer(player).thenAccept(basicPlayer -> basicPlayers.put(player.getUniqueId(), basicPlayer));
        }
    }

    public CompletableFuture<BasicPlayer> getBasicPlayer(UUID uuid) {
        BasicPlayer basicPlayer = basicPlayers.get(uuid);
        if (basicPlayer != null) return CompletableFuture.completedFuture(basicPlayer);
        return BasicPlayer.fromDatabase(uuid);
    }

    public void addBasicPlayer(BasicPlayer basicPlayer) {
        basicPlayers.put(basicPlayer.getUuid(), basicPlayer);
    }

    public void removeBasicPlayer(UUID uuid) {
        basicPlayers.remove(uuid);
    }

}
