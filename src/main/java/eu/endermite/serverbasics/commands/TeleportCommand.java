package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SingleEntitySelector;
import eu.endermite.serverbasics.NMSHandler;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.SyncCommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

@SyncCommandRegistration
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
                ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> {
                    Location location = NMSHandler.getOfflinePlayerPosition(target);
                    basicPlayer.teleportPlayer(location);
                });
            });
            return;
        }
        Entity entity = entitySelector.getEntity();
        String msg = String.format(ServerBasics.getLang(player.locale()).teleported_self, entity.getName());
        player.teleportAsync(entitySelector.getEntity().getLocation()).thenRunAsync(() -> MessageParser.sendMessage(player, msg));
    }

    @CommandMethod("tpohere <target>")
    @CommandDescription("Teleport entity to you")
    @CommandPermission("serverbasics.command.tpohere")
    private void commandTpoHere(
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
                NMSHandler.setOfflinePlayerPosition(target, player.getLocation());
                String msg = String.format(ServerBasics.getLang(player.locale()).teleported_to_self, target.getName());
                MessageParser.sendMessage(player, msg);

            });
            return;
        }
        Entity entity = entitySelector.getEntity();
        String msg = String.format(ServerBasics.getLang(player.locale()).teleported_to_self, entity.getName());
        entity.teleportAsync(player.getLocation()).thenRun(() -> MessageParser.sendMessage(player, msg));
    }
}
