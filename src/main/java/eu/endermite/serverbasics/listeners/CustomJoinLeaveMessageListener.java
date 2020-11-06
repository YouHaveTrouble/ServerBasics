package eu.endermite.serverbasics.listeners;

import eu.endermite.serverbasics.ServerBasics;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class CustomJoinLeaveMessageListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {

        if (ServerBasics.getConfigCache().DISABLE_JOIN_MSG) {
            event.setJoinMessage("");
            return;
        }

        if (!ServerBasics.getConfigCache().CUSTOM_JOIN_MSG)
            return;

        event.setJoinMessage("");

        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
            String consoleMsg = ServerBasics.getInstance().getLang(ServerBasics.getConfigCache().DEFAULT_LANG).CUSTOM_JOIN_MSG;

            consoleMsg = consoleMsg.replace("%nickname%", player.getDisplayName());
            if (ServerBasics.isHooked("PlaceholderAPI")) {
                consoleMsg = PlaceholderAPI.setPlaceholders(player, consoleMsg);
            }
            consoleMsg = ChatColor.translateAlternateColorCodes('&', consoleMsg);
            Bukkit.getConsoleSender().sendMessage(consoleMsg);

            for (Player p : Bukkit.getOnlinePlayers()) {
                String msg = ServerBasics.getInstance().getLang(p.getLocale()).CUSTOM_JOIN_MSG;
                msg = msg.replace("%nickname%", player.getDisplayName());
                msg = msg.replace("%player_displayname%", player.getDisplayName());
                if (ServerBasics.isHooked("PlaceholderAPI")) {
                    msg = PlaceholderAPI.setPlaceholders(player, msg);
                }
                msg = ChatColor.translateAlternateColorCodes('&', msg);
                p.sendMessage(msg);
            }
        });
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeave(org.bukkit.event.player.PlayerQuitEvent event) {

        if (ServerBasics.getConfigCache().DISABLE_LEAVE_MSG) {
            event.setQuitMessage("");
            return;
        }

        if (!ServerBasics.getConfigCache().CUSTOM_LEAVE_MSG)
            return;

        event.setQuitMessage("");

        Player onlineplayer = event.getPlayer();
        UUID uuid = onlineplayer.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
            String consoleMsg = ServerBasics.getInstance().getLang(ServerBasics.getConfigCache().DEFAULT_LANG).CUSTOM_LEAVE_MSG;

            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            consoleMsg = consoleMsg.replace("%nickname%", onlineplayer.getDisplayName());
            consoleMsg = consoleMsg.replace("%player_displayname%", onlineplayer.getDisplayName());
            if (ServerBasics.isHooked("PlaceholderAPI")) {
                consoleMsg = PlaceholderAPI.setPlaceholders(player, consoleMsg);
            }
            consoleMsg = ChatColor.translateAlternateColorCodes('&', consoleMsg);
            Bukkit.getConsoleSender().sendMessage(consoleMsg);

            for (Player p : Bukkit.getOnlinePlayers()) {
                String msg = ServerBasics.getInstance().getLang(p.getLocale()).CUSTOM_LEAVE_MSG;
                msg = msg.replace("%nickname%", onlineplayer.getDisplayName());
                if (ServerBasics.isHooked("PlaceholderAPI")) {
                    msg = PlaceholderAPI.setPlaceholders(player, msg);
                }
                msg = ChatColor.translateAlternateColorCodes('&', msg);
                p.sendMessage(msg);
            }
        });

    }

}
