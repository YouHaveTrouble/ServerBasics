package eu.endermite.serverbasics.listeners;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.players.BasicPlayer;
import eu.endermite.serverbasics.storage.PlayerDatabase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayerCacheListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (PlayerDatabase.playerExists(player.getUniqueId())) {
            System.out.println("Player exists, loading data from database");
            BasicPlayer basicPlayer = PlayerDatabase.getPlayerfromStorage(player.getUniqueId());
            ServerBasics.getBasicPlayers().addBasicPlayer(basicPlayer);

        } else {
            System.out.println("Player does not exist, creating database entry");
            BasicPlayer basicPlayer = BasicPlayer.builder()
                    .uuid(player.getUniqueId())
                    .displayName(player.getDisplayName())
                    .player(player)
                    .fly(player.getAllowFlight())
                    .gameMode(player.getGameMode())
                    .build();
            ServerBasics.getBasicPlayers().addBasicPlayer(basicPlayer);
            PlayerDatabase.createPlayerStorage(basicPlayer);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeave(org.bukkit.event.player.PlayerQuitEvent event) {
        BasicPlayer basicPlayer = ServerBasics.getBasicPlayers().getBasicPlayer(event.getPlayer().getUniqueId());
        PlayerDatabase.savePlayertoStorage(basicPlayer);
        ServerBasics.getBasicPlayers().removeBasicPlayer(basicPlayer);
    }

}
