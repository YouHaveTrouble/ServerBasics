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
                OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(entitySelector.getSelector());
                if (target == null) {
                    MessageParser.sendHaventPlayedError(player);
                } else {
                    Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> {
                        Location location = NMSHandler.getOfflinePlayerPostition(target);
                       player.teleportAsync(location).thenRun(() -> {
                            String msg = String.format(ServerBasics.getLang(player.getLocale()).teleported_self, target.getName());
                            MessageParser.sendMessage(player, msg);
                        });
                    });
                }
            });
            return;
        }
        Entity entity = entitySelector.getEntity();
        String msg = String.format(ServerBasics.getLang(player.locale()).teleported_self, entity.getName());
        player.teleportAsync(entitySelector.getEntity().getLocation()).thenRunAsync( () -> MessageParser.sendMessage(player, msg));
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
                OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(entitySelector.getSelector());
                if (target == null) {
                    MessageParser.sendHaventPlayedError(player);
                } else {
                    NMSHandler.setOfflinePlayerPosition(target, player.getLocation());
                    String msg = String.format(ServerBasics.getLang(player.locale()).teleported_to_self, target.getName());
                    MessageParser.sendMessage(player, msg);
                }
            });
            return;
        }
        Entity entity = entitySelector.getEntity();
        String msg = String.format(ServerBasics.getLang(player.locale()).teleported_to_self, entity.getName());
        entity.teleportAsync(player.getLocation()).thenRun(() -> MessageParser.sendMessage(player, msg));
    }

    private void tpMultiple(List<Entity> entitites, Location location, CommandSender sender) {
        for (Entity entity : entitites) {
            entity.teleportAsync(location)
                    .thenRun(() -> {
                        if (entity instanceof Player) {
                            Player player = (Player) entity;
                            String msg = ServerBasics.getLang(player.locale()).teleported_by_other;
                            MessageParser.sendMessage(player, msg);
                        }
                    });
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            //String msg = String.format(ServerBasics.getLang(player.getLocale()).teleported_many, entitites.size()) ;
            //MessageParser.sendMessage(player, msg);
        }

    }


}
