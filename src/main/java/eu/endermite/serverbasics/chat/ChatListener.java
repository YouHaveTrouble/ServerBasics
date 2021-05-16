package eu.endermite.serverbasics.chat;

import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();

        // Parse colors in message if player has permission for it
        if (player.hasPermission("serverbasics.chat.color")) {
            event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
        }

        // Staffchat
        if (event.getMessage().startsWith("!") && ServerBasics.getConfigCache().staffchat_enabled && player.hasPermission("serverbasics.staffchat")) {
            // Make sure only players with staffchat perms get the message
            event.getRecipients().removeIf(recipent -> !recipent.hasPermission("serverbasics.chat.staffchat"));

            // Remove the staffchat symbol
            String message = event.getMessage();
            message = message.substring(1);
            event.setMessage(message);

            // Set staffchat format
            String format = ServerBasics.getConfigCache().staffchat_format;
            format = format.replaceAll("%nickname%", player.getDisplayName());
            format = format+"%2$s";
            format = ChatColor.translateAlternateColorCodes('&', format);
            event.setFormat(format);
            return;
        }

        // Chat format
        if (!ServerBasics.getConfigCache().chat_format_enabled)
            return;

        String format = ServerBasics.getConfigCache().chat_format;
        format = format.replaceAll("%nickname%", player.getDisplayName());
        format = format+"%2$s";
        format = ChatColor.translateAlternateColorCodes('&', format);
        event.setFormat(format);

    }
}
