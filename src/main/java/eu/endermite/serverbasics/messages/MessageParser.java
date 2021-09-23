package eu.endermite.serverbasics.messages;

import eu.endermite.serverbasics.ServerBasics;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.markdown.DiscordFlavor;
import net.kyori.adventure.text.minimessage.transformation.TransformationType;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MessageParser {

    public static final MiniMessage miniMessage = MiniMessage.builder().build();
    public static final MiniMessage basicMiniMessage = MiniMessage.builder()
            .removeDefaultTransformations()
            .markdown()
            .markdownFlavor(DiscordFlavor.get())
            .transformation(TransformationType.COLOR)
            .transformation(TransformationType.DECORATION)
            .transformation(TransformationType.GRADIENT)
            .transformation(TransformationType.RESET)
            .transformation(TransformationType.RAINBOW)
            .transformation(TransformationType.PRE)
            .build();

    private static final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.builder().hexColors().build();

    /**
     * Parses message string into message and sends it to the recipent
     *
     * @param recipent Recipent of the message
     * @param message  String to parse into message
     */
    public static void sendMessage(CommandSender recipent, String message) {
        Component component = parseMessage(recipent, message);
        recipent.sendMessage(component);
    }

    public static void sendMessage(CommandSender recipent, String message, HashMap<String, Component> placeholders) {
        Component component = parseMessage(recipent, message, placeholders);
        recipent.sendMessage(component);
    }

    /**
     * Parses most of the legacy color codes and minimessage format.
     * @param sender For who to parse the placeholders if applicable.
     * @param message Message to parse.
     * @param placeholders HashMap of placeholder id and Components that should replace them in final message.
     * @return Parsed message Component.
     */
    public static Component parseMessage(CommandSender sender, String message, HashMap<String, Component> placeholders) {
        if (sender instanceof Player player && ServerBasics.getHooks().isHooked("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        message = makeColorsWork('&', message);
        Component minimsg = miniMessage.parse(message);

        if (placeholders != null && !placeholders.isEmpty()) {
            for (Map.Entry<String, Component> placeholder : placeholders.entrySet()) {
                TextReplacementConfig replacementConfig = TextReplacementConfig
                        .builder()
                        .match(placeholder.getKey())
                        .replacement(placeholder.getValue())
                        .build();
                minimsg = minimsg.replaceText(replacementConfig);
            }
        }
        return minimsg;
    }

    /**
     * Parses most of the legacy color codes and minimessage format.
     * @param sender For who to parse the placeholders if applicable.
     * @param message Message to parse.
     * @param placeholder Placeholder id.
     * @param toReplace Component to replace the id with.
     * @return Parsed message Component.
     */
    public static Component parseMessage(CommandSender sender, String message, String placeholder, Component toReplace) {
        if (sender instanceof Player player && ServerBasics.getHooks().isHooked("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        message = makeColorsWork('&', message);
        Component minimsg = miniMessage.parse(message);
        TextReplacementConfig replacementConfig = TextReplacementConfig
                .builder()
                .match(placeholder)
                .replacement(toReplace)
                .build();
        minimsg = minimsg.replaceText(replacementConfig);
        return minimsg;
    }

    /**
     * Parses most of the legacy color codes and minimessage format.
     * @param sender For who to parse the placeholders if applicable.
     * @param message Message to parse.
     * @return Parsed message Component.
     */
    public static Component parseMessage(CommandSender sender, String message) {
        return parseMessage(sender, message, null);
    }

    /**
     * Swaps most legacy color codes to adventure minimessage tags.
     * @param symbol Usually '&'.
     * @param string String to replace symbols in.
     * @return String with legacy color codes replaced with minimessage tags.
     */
    public static String makeColorsWork(Character symbol, String string) {
        // Adventure and ChatColor do not like each other, so this is a thing.
        string = string.replaceAll(symbol + "0", "<black>");
        string = string.replaceAll(symbol + "1", "<dark_blue>");
        string = string.replaceAll(symbol + "2", "<dark_green>");
        string = string.replaceAll(symbol + "3", "<dark_aqua>");
        string = string.replaceAll(symbol + "4", "<dark_red>");
        string = string.replaceAll(symbol + "5", "<dark_purple>");
        string = string.replaceAll(symbol + "6", "<gold>");
        string = string.replaceAll(symbol + "7", "<gray>");
        string = string.replaceAll(symbol + "8", "<dark_gray>");
        string = string.replaceAll(symbol + "9", "<blue>");
        string = string.replaceAll(symbol + "a", "<green>");
        string = string.replaceAll(symbol + "b", "<aqua>");
        string = string.replaceAll(symbol + "c", "<red>");
        string = string.replaceAll(symbol + "d", "<light_purple>");
        string = string.replaceAll(symbol + "e", "<yellow>");
        string = string.replaceAll(symbol + "f", "<white>");
        string = string.replaceAll(symbol + "k", "<obfuscated>");
        string = string.replaceAll(symbol + "l", "<bold>");
        string = string.replaceAll(symbol + "m", "<strikethrough>");
        string = string.replaceAll(symbol + "n", "<underlined>");
        string = string.replaceAll(symbol + "o", "<italic>");
        string = string.replaceAll(symbol + "r", "<reset>");
        return string;
    }

    /**
     * Gets the name of CommandSender.
     * @param sender Who's name to get.
     * @param locale Locale of console name if sender is not Player
     * @return Display name of player or localized console name.
     */
    public static Component getName(CommandSender sender, Locale locale) {
        if (sender instanceof Player player) {
            return player.displayName();
        }
        return miniMessage.parse(ServerBasics.getLang(locale).console_name);
    }

    /**
     * Sends "player haven't played on this server" message.
     */
    public static void sendHaventPlayedError(CommandSender sender) {
        String msg = ServerBasics.getLang(sender).havent_played;
        sendMessage(sender, msg);
    }

    /**
     * @param component Component to serialize.
     * @return Formatted legacy string.
     */
    public static String formattedStringFromMinimessage(Component component) {
        return legacyComponentSerializer.serialize(component);
    }

    /**
     * @param string String to parse.
     * @return Formatted legacy string.
     */
    public static String formattedStringFromMinimessage(String string) {
        string = MessageParser.makeColorsWork('&', string);
        Component component = MessageParser.basicMiniMessage.parse(string);
        return formattedStringFromMinimessage(component);
    }
}


