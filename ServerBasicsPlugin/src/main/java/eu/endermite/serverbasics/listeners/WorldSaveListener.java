package eu.endermite.serverbasics.listeners;

import eu.endermite.serverbasics.storage.PlayerDatabase;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.players.BasicPlayer;
import eu.endermite.serverbasics.players.FunctionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Collection;


public class WorldSaveListener implements Listener {

    /**
     * Save player positions to database on world save
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldSave(org.bukkit.event.world.WorldSaveEvent event) {
        savePlayerPositions(event.getWorld().getPlayers());
    }

    /**
     * Workaround for /save-all not firing world save event
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldSaveCommandPlayer(org.bukkit.event.player.PlayerCommandPreprocessEvent event) {

        if (!event.getMessage().startsWith("/save-all"))
            return;

        if (event.getPlayer().hasPermission("bukkit.command.save.perform") || event.getPlayer().isOp())
            savePlayerPositions(Bukkit.getOnlinePlayers());
    }

    /**
     * Workaround for /save-all not firing world save event
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldSaveCommandConsole(org.bukkit.event.server.ServerCommandEvent event) {

        if (!event.getCommand().startsWith("save-all"))
            return;

        if (event.getSender().hasPermission("bukkit.command.save.perform") || event.getSender().isOp())
            savePlayerPositions(Bukkit.getOnlinePlayers());
    }

    private void savePlayerPositions(Collection<? extends Player> players) {
        Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
            for (Player player : players) {
                BasicPlayer basicPlayer = ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId());
                basicPlayer.setLocation(player.getLocation());
                PlayerDatabase.saveSingleOption(player.getUniqueId(), "location", FunctionUtil.jsonFromLocation(basicPlayer.getLocation()).toJSONString());
            }
        });
    }


}
