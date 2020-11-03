package eu.endermite.serverbasics.listeners;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.FlyCommand;
import eu.endermite.serverbasics.storage.PlayerDatabase;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

public class FlyListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {

        Player player = event.getPlayer();
        boolean flying = ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).canFly();

        if (flying) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerGameModeChangeEvent event) {

        Player player = event.getPlayer();

        if (event.getNewGameMode().equals(GameMode.CREATIVE) || event.getNewGameMode().equals(GameMode.SPECTATOR)) {
            ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).setFly(true);
            PlayerDatabase.saveSingleOption(player.getUniqueId(), "fly", true);
            return;
        }
        if (event.getNewGameMode().equals(GameMode.SURVIVAL) || event.getNewGameMode().equals(GameMode.ADVENTURE)) {
            ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).setFly(false);
            PlayerDatabase.saveSingleOption(player.getUniqueId(), "fly", false);
            return;
        }

    }

}
