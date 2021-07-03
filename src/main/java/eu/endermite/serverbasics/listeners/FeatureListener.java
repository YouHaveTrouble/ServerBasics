package eu.endermite.serverbasics.listeners;

import eu.endermite.serverbasics.storage.PlayerDatabase;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.players.BasicPlayer;
import eu.endermite.serverbasics.players.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FeatureListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {

        Player player = event.getPlayer();

        BasicPlayer basicPlayer;

        if (PlayerDatabase.playerExists(player.getUniqueId())) {
            basicPlayer = PlayerDatabase.getPlayerfromStorage(player.getUniqueId());
            ServerBasics.getBasicPlayers().addBasicPlayer(basicPlayer);
        } else {
            basicPlayer = BasicPlayer.builder(player.getUniqueId())
                    .displayName(player.getDisplayName())
                    .fly(player.getAllowFlight())
                    .build();
            ServerBasics.getBasicPlayers().addBasicPlayer(basicPlayer);
            PlayerDatabase.createPlayerStorage(basicPlayer);
        }

        if (ServerBasics.getConfigCache().spawn_on_join)
            PlayerUtil.teleportPlayerToSpawn(player);

        boolean flying = ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).canFly();
        if (flying) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        //String nickname = ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).getDisplayName();
        //player.setDisplayName(nickname);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeave(org.bukkit.event.player.PlayerQuitEvent event) {
        BasicPlayer basicPlayer = ServerBasics.getBasicPlayers().getBasicPlayer(event.getPlayer().getUniqueId());
        PlayerDatabase.savePlayertoStorage(basicPlayer);
        ServerBasics.getBasicPlayers().removeBasicPlayer(basicPlayer);
    }

}
