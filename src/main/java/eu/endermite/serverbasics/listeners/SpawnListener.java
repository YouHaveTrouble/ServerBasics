package eu.endermite.serverbasics.listeners;

import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!ServerBasics.getLocationsCache().isSpawnSet()) return;
        if (event.getRespawnFlags().isEmpty() || event.getRespawnFlags().contains(PlayerRespawnEvent.RespawnFlag.END_PORTAL)) {
            event.setRespawnLocation(ServerBasics.getLocationsCache().spawn.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerJoinEvent event) {
        if (!ServerBasics.getLocationsCache().isSpawnSet()) return;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId());
        if (offlinePlayer.hasPlayedBefore()) return;
        event.getPlayer().teleportAsync(ServerBasics.getLocationsCache().spawn.getLocation());
    }

}
