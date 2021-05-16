package eu.endermite.serverbasics.listeners;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class CustomJoinLeaveMessageListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {

        if (ServerBasics.getConfigCache().disable_join_msg) {
            event.joinMessage(null);
            return;
        }

        if (!ServerBasics.getConfigCache().custom_join_msg)
            return;

        event.joinMessage(null);

        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
            String consoleMsg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).CUSTOM_JOIN_MSG;

            consoleMsg = consoleMsg.replace("%nickname%", player.getDisplayName());
            consoleMsg = consoleMsg.replace("%player_displayname%", player.getDisplayName());
            if (ServerBasics.getHooks().isHooked("PlaceholderAPI")) {
                consoleMsg = PlaceholderAPI.setPlaceholders(player, consoleMsg);
            }
            consoleMsg = MessageParser.makeColorsWork('&', consoleMsg);
            MiniMessage minimsg = MiniMessage.builder().markdown().build();
            Component component = minimsg.parse(consoleMsg);
            ServerBasics.getInstance().getServer().getConsoleSender().sendMessage(component);

            for (Player p : Bukkit.getOnlinePlayers()) {
                String msg = ServerBasics.getLang(p.getLocale()).CUSTOM_JOIN_MSG;
                msg = msg.replace("%nickname%", player.getDisplayName());
                msg = msg.replace("%player_displayname%", player.getDisplayName());
                if (ServerBasics.getHooks().isHooked("PlaceholderAPI")) {
                    msg = PlaceholderAPI.setPlaceholders(player, msg);
                }
                msg = MessageParser.makeColorsWork('&', msg);
                Component component2 = minimsg.parse(msg);
                p.sendMessage(component2);
            }
        });
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeave(org.bukkit.event.player.PlayerQuitEvent event) {

        if (ServerBasics.getConfigCache().disable_leave_msg) {
            event.quitMessage(null);
            return;
        }

        if (!ServerBasics.getConfigCache().custom_leave_msg)
            return;

        event.quitMessage(null);

        Player onlineplayer = event.getPlayer();
        UUID uuid = onlineplayer.getUniqueId();

        Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
            String consoleMsg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).CUSTOM_LEAVE_MSG;

            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            consoleMsg = consoleMsg.replace("%nickname%", onlineplayer.getDisplayName());
            consoleMsg = consoleMsg.replace("%player_displayname%", onlineplayer.getDisplayName());
            if (ServerBasics.getHooks().isHooked("PlaceholderAPI"))
                consoleMsg = PlaceholderAPI.setPlaceholders(player, consoleMsg);
            consoleMsg = MessageParser.makeColorsWork('&', consoleMsg);
            MiniMessage minimsg = MiniMessage.builder().markdown().build();
            Component component = minimsg.parse(consoleMsg);
            ServerBasics.getInstance().getServer().getConsoleSender().sendMessage(component);

            for (Player p : Bukkit.getOnlinePlayers()) {
                String msg = ServerBasics.getLang(p.locale()).CUSTOM_LEAVE_MSG;
                msg = msg.replace("%nickname%", onlineplayer.getDisplayName());
                msg = msg.replace("%player_displayname%", onlineplayer.getDisplayName());
                if (ServerBasics.getHooks().isHooked("PlaceholderAPI")) {
                    msg = PlaceholderAPI.setPlaceholders(player, msg);
                }
                msg = MessageParser.makeColorsWork('&', msg);
                Component component2 = minimsg.parse(msg);
                p.sendMessage(component2);
            }
        });

    }

}
