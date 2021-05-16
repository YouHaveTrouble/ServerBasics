package eu.endermite.serverbasics.players;

import eu.endermite.serverbasics.storage.PlayerDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.UUID;

public class BasicPlayerCache {

    private final HashMap<UUID, BasicPlayer> basicPlayers = new HashMap<>();

    public BasicPlayerCache() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            BasicPlayer basicPlayer = PlayerDatabase.getPlayerfromStorage(player.getUniqueId());
            basicPlayers.put(player.getUniqueId(), basicPlayer);
        }
    }

    public BasicPlayer getBasicPlayer(UUID uuid) {
        return basicPlayers.get(uuid);
    }

    public void addBasicPlayer(BasicPlayer basicPlayer) {
        basicPlayers.put(basicPlayer.getUuid(), basicPlayer);
    }

    public void removeBasicPlayer(BasicPlayer basicPlayer) {
        basicPlayers.remove(basicPlayer.getUuid());
    }
    public void removeBasicPlayer(UUID uuid) {
        basicPlayers.remove(uuid);
    }

}
