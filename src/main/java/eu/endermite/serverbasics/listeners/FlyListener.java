package eu.endermite.serverbasics.listeners;

import eu.endermite.serverbasics.commands.FlyCommand;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

public class FlyListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {

        Player player = event.getPlayer();
        String flying = player.getPersistentDataContainer().get(FlyCommand.flyKey, PersistentDataType.STRING);

        if (flying != null && flying.equals("true")) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerGameModeChangeEvent event) {

        Player player = event.getPlayer();

        if (event.getNewGameMode().equals(GameMode.CREATIVE) || event.getNewGameMode().equals(GameMode.SPECTATOR)) {
            player.getPersistentDataContainer().set(FlyCommand.flyKey, PersistentDataType.STRING, "true");
            return;
        }
        if (event.getNewGameMode().equals(GameMode.SURVIVAL) || event.getNewGameMode().equals(GameMode.ADVENTURE)) {
            player.getPersistentDataContainer().set(FlyCommand.flyKey, PersistentDataType.STRING, "false");
            return;
        }

    }

}
