package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
import eu.endermite.serverbasics.players.BasicPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
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
                String msg = ServerBasics.getLang(player.locale()).started_flying;
                MessageParser.sendMessage(player, msg);
            } else {
                String msg = ServerBasics.getLang(player.locale()).stopped_flying;
                MessageParser.sendMessage(player, msg);
            }
        });
    }

    @CommandMethod("fly <target>")
    @CommandDescription("Toggle flight mode")
    @CommandPermission("serverbasics.command.fly")
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
            String msg;
            boolean flying = basicPlayer.toggleFly();
            senderFeedback(sender, flying, basicPlayer);
            if (!basicPlayer.isOnline()) return;
            if (flying) {
                msg = ServerBasics.getLang(target.locale()).started_flying;
            } else {
                msg = ServerBasics.getLang(target.locale()).stopped_flying;
            }
            MessageParser.sendMessage(target.getPlayer(), msg);
        });
    }

    private void senderFeedback(CommandSender sender, boolean flying, BasicPlayer basicPlayer) {
        String component;
        if (sender instanceof Player player) {
            if (flying)
                component = ServerBasics.getLang(player.locale()).started_flying_other;
            else
                component = ServerBasics.getLang(player.locale()).stopped_flying_other;
        } else {
            if (flying)
                component = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).started_flying_other;
            else
                component = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).stopped_flying_other;
        }
        HashMap<String, Component> placeholders = new HashMap<>();
        placeholders.put("%player%", basicPlayer.getDisplayName());
        sender.sendMessage(MessageParser.parseMessage(sender, component, placeholders));
    }

}
