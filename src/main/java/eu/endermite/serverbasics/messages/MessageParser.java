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
            recipent.sendMessage(component);
            return;
        }

        Player player = (Player) recipent;
        switch (messageType) {
            case ACTIONBAR:
                player.sendActionBar(component);
                break;
            case SUBTITLE:
                Title title = Title.title(Component.empty(), component);
                player.showTitle(title);
            case TEXT:
            default:
                player.sendMessage(component);
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

    public static String makeColorsWorkButReverse(String string) {

        string = string.replaceAll("<black>", "&0");
        string = string.replaceAll("<dark_blue>", "&1");
        string = string.replaceAll("<dark_green>", "&2");
        string = string.replaceAll("<dark_aqua>", "&3");
        string = string.replaceAll("<dark_red>", "&4");
        string = string.replaceAll("<dark_purple>", "&5");
        string = string.replaceAll("<gold>", "&6");
        string = string.replaceAll("<gray>", "&7");
        string = string.replaceAll("<dark_gray>", "&8");
        string = string.replaceAll("<blue>", "&9");
        string = string.replaceAll("<green>", "&a");
        string = string.replaceAll("<aqua>", "&b");
        string = string.replaceAll("<red>", "&c");
        string = string.replaceAll("<light_purple>", "&d");
        string = string.replaceAll("<yellow>", "&e");
        string = string.replaceAll("<white>", "&f");

        string = string.replaceAll("<obfuscated>", "&k");
        string = string.replaceAll("<bold>", "&l");
        string = string.replaceAll("<strikethrough>", "&m");
        string = string.replaceAll("<underlined>", "&n");
        string = string.replaceAll("<italic>", "&o");
        string = string.replaceAll("<reset>", "&r");

        return string;
    }

    public static void sendHaventPlayedError(CommandSender sender) {

        if (sender instanceof Player player) {
            String msg = ServerBasics.getLang(player.locale()).havent_played;
            sendMessage(player, msg);
        } else {
            String msg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).havent_played;
            sendMessage(sender, msg);
        }
    }

}


