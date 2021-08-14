package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;

@CommandRegistration
public class FeedCommand {

    @CommandMethod("feed")
    @CommandDescription("Feed yourself")
    @CommandPermission("serverbasics.command.feed")
    private void commandFeed(
            final Player player
    ) {
        player.setFoodLevel(20);
        player.setSaturation(0);
        String msg = ServerBasics.getLang(player.getLocale()).fed;
        MessageParser.sendMessage(player, msg);
    }

    @CommandMethod("feed <target>")
    @CommandDescription("Feed yourself or other player")
    @CommandPermission("serverbasics.command.feed.others")
    private void commandFeedOther(
            final CommandSender player,
            final @Argument(value = "target", description = "Player to feed") MultiplePlayerSelector targetPlayer
    ) {
        int amountFed = targetPlayer.getPlayers().size();
        Player lastPlayer = null;

        for (Player target : targetPlayer.getPlayers()) {
            lastPlayer = target;

            target.setFoodLevel(20);
            target.setSaturation(0);

            String playerLang = target.getLocale();
            String msg;
            if (player != target) {
                msg = ServerBasics.getLang(playerLang).fed_by_other;
                HashMap<String, Component> placeholders = new HashMap<>();
                placeholders.put("%player%", lastPlayer.displayName());
                player.sendMessage(MessageParser.parseMessage(player, msg, placeholders));
            }
            else {
                msg = ServerBasics.getLang(playerLang).fed;
                MessageParser.sendMessage(target, msg);
            }
        }
        if (player instanceof Player) {
            String msg;
            Locale playerLang = ((Player) player).locale();
            HashMap<String, Component> placeholders = new HashMap<>();
            if (amountFed == 1) {
                msg = ServerBasics.getLang(playerLang).fed_by_other;
                placeholders.put("%player%", lastPlayer.displayName());
            } else if (amountFed > 1) {
                msg = String.format(ServerBasics.getLang(playerLang).fed_many, amountFed);
                placeholders.put("%amount%", Component.text(amountFed));
            } else {
                msg = ServerBasics.getLang(playerLang).fed_noone;
            }
            player.sendMessage(MessageParser.parseMessage(player, msg, placeholders));
        } else {
            String msg;
            HashMap<String, Component> placeholders = new HashMap<>();
            if (amountFed == 1) {
                msg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).fed_by_other;
                placeholders.put("%player%", lastPlayer.displayName());
            }
            else if (amountFed > 1) {
                msg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).healed_many;
                placeholders.put("%amount%", Component.text(amountFed));
            } else {
                msg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).healed_noone;
            }
            player.sendMessage(MessageParser.parseMessage(player, msg, placeholders));
        }
    }

}
