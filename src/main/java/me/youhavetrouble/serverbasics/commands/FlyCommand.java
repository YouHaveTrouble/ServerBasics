package me.youhavetrouble.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.commands.registration.CommandRegistration;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import me.youhavetrouble.serverbasics.players.BasicPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandRegistration
public class FlyCommand {

    @CommandMethod("fly")
    @CommandDescription("Toggle flight mode")
    @CommandPermission("serverbasics.command.fly")
    private void commandFly(
            final Player player
    ) {
        BasicPlayer.fromPlayer(player).thenAccept(basicPlayer -> {
            if (basicPlayer.toggleFly()) {
                String msg = ServerBasics.getLang(player).started_flying;
                MessageParser.sendMessage(player, msg);
            } else {
                String msg = ServerBasics.getLang(player).stopped_flying;
                MessageParser.sendMessage(player, msg);
            }
        });
    }

    @CommandMethod("fly <target>")
    @CommandDescription("Toggle flight mode")
    @CommandPermission("serverbasics.command.fly.others")
    private void commandFlyOther(
            final CommandSender sender,
            @Argument(value = "target", description = "Target") SinglePlayerSelector playerSelector
            ) {
        Player target = playerSelector.getPlayer();
        UUID uuid;
        if (target == null) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerSelector.getSelector());
            if (!offlinePlayer.hasPlayedBefore()) {
                MessageParser.sendHaventPlayedError(sender);
                return;
            }
            uuid = offlinePlayer.getUniqueId();
        } else {
            uuid = target.getUniqueId();
        }
        BasicPlayer.fromUuid(uuid).thenAccept(basicPlayer -> {
            boolean flying = basicPlayer.toggleFly();
            senderFeedback(sender, flying, basicPlayer);
            if (!basicPlayer.isOnline()) return;
            if (flying) {
                MessageParser.sendMessage(target, ServerBasics.getLang(target).started_flying);
            } else {
                MessageParser.sendMessage(target, ServerBasics.getLang(target).stopped_flying);
            }
        });
    }

    private void senderFeedback(CommandSender sender, boolean flying, BasicPlayer basicPlayer) {
        String component;
        if (flying)
            component = ServerBasics.getLang(sender).started_flying_other;
        else
            component = ServerBasics.getLang(sender).stopped_flying_other;
        sender.sendMessage(MessageParser.parseMessage(sender, component, "%player%", basicPlayer.getDisplayName()));
    }

}
