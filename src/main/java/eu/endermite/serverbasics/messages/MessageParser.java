package eu.endermite.serverbasics.messages;

import eu.endermite.serverbasics.ServerBasics;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageParser {

    /**
     * Parses message string into message
     * @param recipent Recipent of the message
     * @param message String to parse into message
     */
    public static void sendMessage(CommandSender recipent, String message) {

        MessageType messageType = MessageType.TEXT;

        message = ChatColor.translateAlternateColorCodes('&', message);

        //TODO different message types based on string prefix

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

    /**
     * For sending TranslatableComponent errors that already exist in the client
     * @param player
     * @param translationString
     * @param color
     */
    public static void sendDefaultTranslatedError(CommandSender player, String translationString, TextColor color) {
        Component component = Component.translatable().key(translationString).colorIfAbsent(color).build();
        BukkitAudiences bukkitAudiences = ServerBasics.getCommandManager().bukkitAudiences;
        bukkitAudiences.sender(player).sendMessage(component);

    }

    public static void sendDefaultTranslatedError(CommandSender player, String translationString, String suffix, TextColor color) {
        net.kyori.adventure.text.TranslatableComponent.Builder component = Component.translatable().key(translationString).colorIfAbsent(color);
        Component component1 = Component.translatable().key(suffix).colorIfAbsent(color).build();
        component.append(component1);
        BukkitAudiences bukkitAudiences = ServerBasics.getCommandManager().bukkitAudiences;
        bukkitAudiences.sender(player).sendMessage(component.build());
    }


}


