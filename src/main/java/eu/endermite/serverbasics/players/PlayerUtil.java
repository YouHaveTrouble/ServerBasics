package eu.endermite.serverbasics.players;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import io.papermc.lib.PaperLib;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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

    public void teleportPlayerToSpawn(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PaperLib.teleportAsync(player, ServerBasics.getLocationsCache().spawn.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                    if (result) {
                        MessageParser.sendMessage(player, ServerBasics.getLang(player.getLocale()).TPD_SPAWN);
                    } else {
                        MessageParser.sendMessage(player, ServerBasics.getLang(player.getLocale()).COULD_NOT_TP);
                    }
                });
            }
        }.runTask(ServerBasics.getInstance());
    }

    public ItemStack setHat(Player player, ItemStack itemStack) {

        ItemStack hatItem = itemStack.clone();
        hatItem.setAmount(1);

        itemStack.setAmount(itemStack.getAmount()-1);
        if (player.getInventory().getHelmet() != null) {
            player.getInventory().addItem(player.getInventory().getHelmet());
            player.getInventory().setHelmet(null);
        }
        player.getInventory().setHelmet(hatItem);

        return itemStack;
    }
}

