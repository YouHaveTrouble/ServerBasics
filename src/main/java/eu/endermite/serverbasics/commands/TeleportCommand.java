package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SingleEntitySelector;
import eu.endermite.serverbasics.NMSHandler;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
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
                ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> {
                    Location location = NMSHandler.getOfflinePlayerPosition(target);
                    basicPlayer.teleportPlayer(location);
                });
            });
            return;
        }

        ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> {
            Location location = entitySelector.getEntity().getLocation();
            basicPlayer.teleportPlayer(location);
        });


        Entity entity = entitySelector.getEntity();
        String msg = String.format(ServerBasics.getLang(player.locale()).teleported_self, entity.getName());
        player.teleportAsync(entitySelector.getEntity().getLocation()).thenRunAsync(() -> MessageParser.sendMessage(player, msg));
    }

    @CommandMethod("tpohere <target>")
    @CommandDescription("Teleport entity to you")
    @CommandPermission("serverbasics.command.tpohere")
    private void commandTpoHere(
            final Player player,
            final @Argument(value = "target") SingleEntitySelector singleEntitySelector
    ) {
        if (!singleEntitySelector.hasAny()) {
            Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
                OfflinePlayer target = Bukkit.getOfflinePlayer(singleEntitySelector.getSelector());
                if (!target.hasPlayedBefore()) {
                    MessageParser.sendHaventPlayedError(player);
                    return;
                }
                ServerBasics.getBasicPlayers().getBasicPlayer(target.getUniqueId()).thenAccept(basicPlayer -> {
                    basicPlayer.teleportPlayer(player.getLocation());
                    HashMap<String, Component> placeholders = new HashMap<>();
                    placeholders.put("%name%", basicPlayer.getDisplayName());
                    MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).teleported_to_self, placeholders);
                });
            });
            return;
        }

        Entity entity = singleEntitySelector.getEntity();
        Component name;
        if (entity instanceof Player playerEntity)
            name = playerEntity.displayName();
        else
            name = entity.customName();

        HashMap<String, Component> placeholders = new HashMap<>();
        placeholders.put("%name%", name);
        entity.teleportAsync(player.getLocation()).thenRun(() -> player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player.locale()).teleported_to_self, placeholders)));
    }


}
