package eu.endermite.serverbasics.messages;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
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

    public static void sendDefaultTranslatedError(CommandSender player, String translationString, net.md_5.bungee.api.ChatColor color) {
        BaseComponent baseComponent = new TranslatableComponent(translationString);
        baseComponent.setColor(color);
        player.sendMessage(baseComponent);
    }

}


