package eu.endermite.serverbasics.messages;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageParser {

    public static void sendMessage(CommandSender recipent, String message) {

        MessageType messageType = MessageType.TEXT;

        message = ChatColor.translateAlternateColorCodes('&', message);

        //TODO different message types based on prefix

        //TODO placeholderAPI support

        if (!(recipent instanceof Player)) {
            recipent.sendMessage(message);
            return;
        }
        Player player = (Player) recipent;

        if (messageType.equals(MessageType.TEXT)) {
            player.sendMessage(message);
            return;
        }
        if (messageType.equals(MessageType.ACTIONBAR)) {
            player.sendActionBar(message);
            return;
        }
        if (messageType.equals(MessageType.SUBTITLE)) {
            player.sendTitle("",message, 5, 60, 5);
            return;
        }
    }

}


