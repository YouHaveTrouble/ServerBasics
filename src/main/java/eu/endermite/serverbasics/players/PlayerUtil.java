package eu.endermite.serverbasics.players;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.FlyCommand;
import eu.endermite.serverbasics.messages.MessageParser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class PlayerUtil {



    /**
     * Toggles players flight status. Requires check if player played before.
     * @param uuid
     * @return new flight state
     */
    // Decided to not move it to BasicPlayer for more convinient offline player support
    public static boolean toggleFlight(UUID uuid) {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();
            boolean flying = ServerBasics.getBasicPlayers().getBasicPlayer(uuid).canFly();

            if (flying) {
                player.setFlying(false);
                player.setAllowFlight(false);
                ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).setFly(false);
                String msg = ServerBasics.getLang(player.getLocale()).STOPPED_FLYING;
                MessageParser.sendMessage(player, msg);
                return false;
            } else {
                player.setAllowFlight(true);
                Bukkit.getScheduler().runTask(ServerBasics.getInstance(), task -> {
                    player.teleport(player.getLocation().add(0, 0.1, 0));
                    player.setFlying(true);
                });
                ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).setFly(true);
                String msg = ServerBasics.getLang(player.getLocale()).STARTED_FLYING;
                MessageParser.sendMessage(player, msg);
                return true;
            }
        } else {
            return false;
        }


    }
}

