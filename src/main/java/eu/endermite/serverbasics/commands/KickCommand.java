package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandRegistration
public class KickCommand {

    @CommandMethod("kick <player>")
    @CommandDescription("Kick player")
    @CommandPermission("serverbasics.command.kick")
    private void commandKick(
            final CommandSender sender,
            @Argument(value = "player", description = "Player to kick") MultiplePlayerSelector playerSelector
    ) {
        if (!playerSelector.hasAny()) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                MessageParser.sendMessage(sender, ServerBasics.getLang(player.getLocale()).no_player_selected);
            } else {
                MessageParser.sendMessage(sender, ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).no_player_selected);
            }
            return;
        }

        Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> {
            for (Player player : playerSelector.getPlayers()) {
                String kickReason = ServerBasics.getLang(player.getLocale()).kick_default;
                kickReason = MessageParser.makeColorsWorkButReverse(kickReason);
                String finalKickReason = ChatColor.translateAlternateColorCodes('&', kickReason);
                player.kickPlayer(finalKickReason);
            }
        });
    }

    @CommandMethod("kick <player> <reason>")
    @CommandDescription("Kick player")
    @CommandPermission("serverbasics.command.kick")
    private void commandKickWithReason(
            final CommandSender sender,
            @Argument(value = "player", description = "Player to kick") MultiplePlayerSelector playerSelector,
            final @Argument(value = "reason", description = "Reason or kick") @Greedy String[] reason
    ) {
        if (!playerSelector.hasAny()) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                MessageParser.sendMessage(sender, ServerBasics.getLang(player.getLocale()).no_player_selected);
            } else {
                MessageParser.sendMessage(sender, ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).no_player_selected);
            }
            return;
        }
        String kickReason = StringUtils.join(reason, " ");

        Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> {
            for (Player player : playerSelector.getPlayers()) {
                String personalizedFinalKickReason =
                        ServerBasics.getLang(player.getLocale()).kick_default+"\n"
                        +ServerBasics.getLang(player.getLocale()).kick_reason+"\n"
                        + kickReason;
                personalizedFinalKickReason = MessageParser.makeColorsWorkButReverse(personalizedFinalKickReason);
                personalizedFinalKickReason = ChatColor.translateAlternateColorCodes('&', personalizedFinalKickReason);
                player.kickPlayer(personalizedFinalKickReason);
            }
        });


    }

}
