package me.youhavetrouble.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.commands.registration.CommandRegistration;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
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
            MessageParser.sendMessage(sender, ServerBasics.getLang(sender).no_player_selected);
            return;
        }
        for (Player player : playerSelector.getPlayers()) {
            Component kickReason = Component.empty();
            for (String line : ServerBasics.getLang(player.locale()).kick_message) {
                line = line.replaceAll("%reason%", ServerBasics.getLang(player.locale()).kick_reason);
                kickReason = kickReason.append(MiniMessage.markdown().parse(line)).append(Component.newline());
            }
            Component finalKickReason = kickReason;
            Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> player.kick(finalKickReason));
        }
    }

    @CommandMethod("kick <player> <reason>")
    @CommandDescription("Kick player with provided reason")
    @CommandPermission("serverbasics.command.kick")
    private void commandKickWithReason(
            final CommandSender sender,
            @Argument(value = "player", description = "Player to kick") MultiplePlayerSelector playerSelector,
            final @Argument(value = "reason", description = "Reason for kick") @Greedy String[] reason
    ) {
        if (!playerSelector.hasAny()) {
            MessageParser.sendMessage(sender, ServerBasics.getLang(sender).no_player_selected);
            return;
        }
        String kickReason = StringUtils.join(reason, " ");
        kickReason = MessageParser.makeColorsWork('&', kickReason);
        String parsedKickReason = MessageParser.formattedStringFromMinimessage(kickReason);

        for (Player player : playerSelector.getPlayers()) {
            Component kickReasonBuilder = Component.empty();
            for (String line : ServerBasics.getLang(player).kick_message) {
                line = line.replaceAll("%reason%", parsedKickReason);
                kickReasonBuilder = kickReasonBuilder.append(MessageParser.miniMessage.parse(line)).append(Component.newline());
            }
            Component finalKickReason = kickReasonBuilder;
            Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> player.kick(finalKickReason));
        }
    }

}
