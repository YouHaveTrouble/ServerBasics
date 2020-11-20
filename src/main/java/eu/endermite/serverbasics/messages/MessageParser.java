package eu.endermite.serverbasics.messages;

import eu.endermite.serverbasics.ServerBasics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
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

        if (message.startsWith("!actionbar ")) {
            message = message.replaceFirst("!actionbar ", "");
            messageType = MessageType.ACTIONBAR;
        } else if (message.startsWith("!subtitle ")) {
            message = message.replaceFirst("!subtitle ", "");
            messageType = MessageType.SUBTITLE;
        }


        message = makeColorsWork('&', message);
        MiniMessage minimsg = MiniMessage.builder().markdown().build();
        Component component = minimsg.parse(message);


        if (!(recipent instanceof Player)) {
            ServerBasics.getCommandManager().bukkitAudiences.sender(recipent).sendMessage(component);
            return;
        }

        Player player = (Player) recipent;

        switch (messageType) {
            case ACTIONBAR:
                ServerBasics.getCommandManager().bukkitAudiences.player(player).sendActionBar(component);
                break;
            case SUBTITLE:
                Title title = Title.title(Component.empty(), component);
                ServerBasics.getCommandManager().bukkitAudiences.player(player).showTitle(title);
            case TEXT:
            default:
                ServerBasics.getCommandManager().bukkitAudiences.player(player).sendMessage(component);
                break;
        }
    }

    public static String makeColorsWork(Character symbol, String string) {

        // Adventure and ChatColor do not like each other, so this is a thing.

        string = string.replaceAll(symbol+"0", "<black>");
        string = string.replaceAll(symbol+"1", "<dark_blue>");
        string = string.replaceAll(symbol+"2", "<dark_green>");
        string = string.replaceAll(symbol+"3", "<dark_aqua>");
        string = string.replaceAll(symbol+"4", "<dark_red>");
        string = string.replaceAll(symbol+"5", "<dark_purple>");
        string = string.replaceAll(symbol+"6", "<gold>");
        string = string.replaceAll(symbol+"7", "<gray>");
        string = string.replaceAll(symbol+"8", "<dark_gray>");
        string = string.replaceAll(symbol+"9", "<blue>");
        string = string.replaceAll(symbol+"a", "<green>");
        string = string.replaceAll(symbol+"b", "<aqua>");
        string = string.replaceAll(symbol+"c", "<red>");
        string = string.replaceAll(symbol+"d", "<light_purple>");
        string = string.replaceAll(symbol+"e", "<yellow>");
        string = string.replaceAll(symbol+"f", "<white>");

        string = string.replaceAll(symbol+"k", "<obfuscated>");
        string = string.replaceAll(symbol+"l", "<bold>");
        string = string.replaceAll(symbol+"m", "<strikethrough>");
        string = string.replaceAll(symbol+"n", "<underlined>");
        string = string.replaceAll(symbol+"o", "<italic>");
        string = string.replaceAll(symbol+"r", "<reset>");

        return string;
    }

}


