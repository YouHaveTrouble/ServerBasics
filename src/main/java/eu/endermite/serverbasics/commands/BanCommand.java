package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandRegistration
public class BanCommand {

    @CommandMethod("ban <player>")
    @CommandDescription("Ban player")
    @CommandPermission("serverbasics.command.ban")
    private void commandKick(
            final CommandSender sender,
            @Argument(value = "player", description = "Player to ban") SinglePlayerSelector playerSelector
    ) {

        OfflinePlayer target;

        if (!playerSelector.hasAny()) {
            target = Bukkit.getOfflinePlayerIfCached(playerSelector.getSelector());
            if (target == null) {
                MessageParser.sendHaventPlayedError(sender);
                return;
            }
        } else {
            target = playerSelector.getPlayer();
        }

        StringBuilder kickReasonBuilder = new StringBuilder();

        if (target.isOnline()) {
            Player player = target.getPlayer();
            for (String line : ServerBasics.getLang(player.locale()).ban_message) {
                line = line.replaceAll("%reason%", ServerBasics.getLang(player.locale()).ban_reason);
                kickReasonBuilder.append(line).append("\n");
            }
        } else {
            for (String line : ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).ban_message) {
                line = line.replaceAll("%reason%", ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).ban_reason);
                kickReasonBuilder.append(line).append("\n");
            }
        }

        String kickReasonParsed = kickReasonBuilder.toString();
        kickReasonParsed = MessageParser.makeColorsWorkButReverse(kickReasonParsed);
        kickReasonParsed = ChatColor.translateAlternateColorCodes('&', kickReasonParsed);

        String finalKickReasonParsed = kickReasonParsed;
        Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> {
            target.banPlayer(finalKickReasonParsed, sender.getName());
        });


    }

}
