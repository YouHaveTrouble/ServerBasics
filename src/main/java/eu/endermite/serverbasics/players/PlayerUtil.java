package eu.endermite.serverbasics.players;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@UtilityClass
public class PlayerUtil {

    /**
     * Toggles players flight status. Requires check if player played before.
     * @param uuid
     * @return new flight state
     */
    // Decided to not move it to BasicPlayer for more convinient offline player support
    public boolean toggleFlight(UUID uuid) {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();
            boolean flying = ServerBasics.getBasicPlayers().getBasicPlayer(uuid).canFly();

            if (flying) {
                player.setFlying(false);
                player.setAllowFlight(false);
                ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).setFly(false);
                String msg = ServerBasics.getLang(player.getLocale()).stopped_flying;
                MessageParser.sendMessage(player, msg);
                return false;
            } else {
                player.setAllowFlight(true);
                Bukkit.getScheduler().runTask(ServerBasics.getInstance(), task -> {
                    player.teleport(player.getLocation().add(0, 0.1, 0));
                    player.setFlying(true);
                });
                ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).setFly(true);
                String msg = ServerBasics.getLang(player.getLocale()).started_flying;
                MessageParser.sendMessage(player, msg);
                return true;
            }
        } else {
            return false;
        }
    }

    public void teleportPlayerToSpawn(Player player) {
        Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> player.teleportAsync(ServerBasics.getLocationsCache().spawn.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
            if (result) {
                MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).TPD_SPAWN);
            } else {
                MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).could_not_tp);
            }
        }));
    }

    public void setHat(Player player, ItemStack itemStack) {
        ItemStack hatItem = itemStack.clone();
        hatItem.setAmount(1);
        itemStack.setAmount(itemStack.getAmount()-1);
        if (player.getInventory().getHelmet() != null) {
            player.getInventory().addItem(player.getInventory().getHelmet());
            player.getInventory().setHelmet(null);
        }
        player.getInventory().setHelmet(hatItem);
    }
}

