package eu.endermite.serverbasics.messages;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageParser {

    /**
     * Parses message string into message
     *
     * @param recipent Recipent of the message
     * @param message  String to parse into message
     */
    public static void sendMessage(CommandSender recipent, String message) {

        MessageType messageType = MessageType.TEXT;

        //TODO different message types based on string prefix

        message = ChatColor.translateAlternateColorCodes('&', message);

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
            player.sendTitle("", message, 5, 60, 5);
            return;
        }
    }

}


