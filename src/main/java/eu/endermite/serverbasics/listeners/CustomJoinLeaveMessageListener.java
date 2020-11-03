package eu.endermite.serverbasics.listeners;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CustomJoinLeaveMessageListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {

        if (!ServerBasics.getConfigCache().CUSTOM_JOIN_MSG)
            return;

        event.setJoinMessage("");

        Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
            String consoleMsg = ServerBasics.getInstance().getLang(ServerBasics.getConfigCache().DEFAULT_LANG).CUSTOM_JOIN_MSG;

            if (!consoleMsg.equals("")) {
                consoleMsg = String.format(consoleMsg, event.getPlayer().getDisplayName());
                consoleMsg = ChatColor.translateAlternateColorCodes('&', consoleMsg);
                System.out.println(consoleMsg);
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                String msg = ServerBasics.getInstance().getLang(player.getLocale()).CUSTOM_JOIN_MSG;
                if (msg.equals(""))
                    continue;
                msg = String.format(msg, event.getPlayer().getDisplayName());
                MessageParser.sendMessage(player, msg);
            }
        });
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeave(org.bukkit.event.player.PlayerQuitEvent event) {

        if (!ServerBasics.getConfigCache().CUSTOM_LEAVE_MSG)
            return;

        event.setQuitMessage("");

        Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
            String consoleMsg = ServerBasics.getInstance().getLang(ServerBasics.getConfigCache().DEFAULT_LANG).CUSTOM_LEAVE_MSG;

            if (!consoleMsg.equals("")) {
                consoleMsg = String.format(consoleMsg, event.getPlayer().getDisplayName());
                consoleMsg = ChatColor.translateAlternateColorCodes('&', consoleMsg);
                System.out.println(consoleMsg);
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                String msg = ServerBasics.getInstance().getLang(player.getLocale()).CUSTOM_LEAVE_MSG;
                if (msg.equals(""))
                    continue;
                msg = String.format(msg, event.getPlayer().getDisplayName());
                MessageParser.sendMessage(player, msg);
            }
        });

    }

}
