package eu.endermite.serverbasics.listeners;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.players.BasicPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FeatureListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BasicPlayer.fromDatabase(player.getUniqueId()).thenAccept(basicPlayer -> {
            ServerBasics.getBasicPlayers().addBasicPlayer(basicPlayer);
            if (ServerBasics.getConfigCache().spawn_on_join)
                basicPlayer.teleportPlayer(ServerBasics.getLocationsCache().spawn.getLocation());
        });
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeave(org.bukkit.event.player.PlayerQuitEvent event) {
        ServerBasics.getBasicPlayers().removeBasicPlayer(event.getPlayer().getUniqueId());
    }

}
