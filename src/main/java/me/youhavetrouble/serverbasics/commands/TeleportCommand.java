package me.youhavetrouble.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SingleEntitySelector;
import me.youhavetrouble.serverbasics.NMSHandler;
import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.commands.registration.CommandRegistration;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import me.youhavetrouble.serverbasics.util.BasicUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

@CommandRegistration
public class TeleportCommand {

    @CommandMethod("tpo <target>")
    @CommandDescription("Teleport to entity")
    @CommandPermission("serverbasics.command.tpo")
    private void commandTpo(
            final Player player,
            final @Argument(value = "target") SingleEntitySelector entitySelector
    ) {
        if (!entitySelector.hasAny()) {
            Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
                OfflinePlayer target = Bukkit.getOfflinePlayer(entitySelector.getSelector());
                if (!target.hasPlayedBefore()) {
                    MessageParser.sendHaventPlayedError(player);
                    return;
                }
                if (!player.hasPermission("serverbasics.teleportoffline")) {
                    player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player).cant_tp_to_offline));
                    return;
                }
                ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> {
                    Location location = NMSHandler.getOfflinePlayerPosition(target);
                    basicPlayer.teleportPlayer(location);
                });
            });
            return;
        }

        Entity entity = entitySelector.getEntity();
        Component name = BasicUtil.entityName(entity);
        ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> {
            Location location = entitySelector.getEntity().getLocation();
            String msg = ServerBasics.getLang(player).teleported_self;
            basicPlayer.teleportPlayer(location, MessageParser.parseMessage(player, msg, "%entity%", name));
        });
    }

    @CommandMethod("tpohere <target>")
    @CommandDescription("Teleport entity to you")
    @CommandPermission("serverbasics.command.tpohere")
    private void commandTpoHere(
            final Player player,
            final @Argument(value = "target") SingleEntitySelector singleEntitySelector
    ) {
        if (!singleEntitySelector.hasAny()) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(singleEntitySelector.getSelector());
            if (!target.hasPlayedBefore()) {
                MessageParser.sendHaventPlayedError(player);
                return;
            }
            if (!player.hasPermission("serverbasics.teleportoffline")) {
                player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player).cant_tp_to_offline));
                return;
            }
            ServerBasics.getBasicPlayers().getBasicPlayer(target.getUniqueId()).thenAccept(basicPlayer -> {
                basicPlayer.teleportPlayer(player.getLocation());
                HashMap<String, Component> placeholders = new HashMap<>();
                placeholders.put("%name%", basicPlayer.getDisplayName());
                MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).teleported_to_self, placeholders);
            });
            return;
        }

        Entity entity = singleEntitySelector.getEntity();

        Bukkit.getScheduler().runTask(ServerBasics.getInstance(),
                () -> entity.teleportAsync(player.getLocation()).thenRun(
                        () -> player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player.locale()).teleported_to_self, "%name%", BasicUtil.entityName(entity)))));
    }


}
