package eu.endermite.serverbasics;

import eu.endermite.serverbasics.commands.FlyCommand;
import eu.endermite.serverbasics.messages.MessageParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;


public class PlayerUtil {

    /**
     * @param player
     * @return new flight state
     */
    //TODO move this to BasicPlayer
    public static boolean toggleFlight(Player player) {
        String flying = player.getPersistentDataContainer().get(FlyCommand.flyKey, PersistentDataType.STRING);

        if (flying != null && flying.equals("true")) {
            player.setFlying(false);
            player.setAllowFlight(false);
            player.getPersistentDataContainer().set(FlyCommand.flyKey, PersistentDataType.STRING, "false");
            String msg = ServerBasics.getLang(player.getLocale()).STOPPED_FLYING;
            MessageParser.sendMessage(player, msg);
            return false;
        } else {
            player.setAllowFlight(true);
            Bukkit.getScheduler().runTask(ServerBasics.getInstance(), task -> {
                player.teleport(player.getLocation().add(0, 0.1, 0));
                player.setFlying(true);
            });
            player.getPersistentDataContainer().set(FlyCommand.flyKey, PersistentDataType.STRING, "true");
            String msg = ServerBasics.getLang(player.getLocale()).STARTED_FLYING;
            MessageParser.sendMessage(player, msg);
            return true;
        }

    }
}

