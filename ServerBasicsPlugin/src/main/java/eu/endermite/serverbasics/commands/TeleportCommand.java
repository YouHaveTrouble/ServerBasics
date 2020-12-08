package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.MultipleEntitySelector;
import cloud.commandframework.bukkit.arguments.selector.SingleEntitySelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@CommandRegistration
public class TeleportCommand {

    @CommandMethod("tp <target>")
    @CommandDescription("Teleport to entity")
    @CommandPermission("serverbasics.command.tp")
    private void commandTp(
            final Player player,
            final @Argument(value = "target") SingleEntitySelector entitySelector
    ) {
        if (!entitySelector.hasAny()) {
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(entitySelector.getSelector());
            if (target == null) {
                MessageParser.sendHaventPlayedError(player);
            } else {
                Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> {
                    PaperLib.teleportAsync(player, ServerBasics.getNmsHandler().getOfflinePlayerPostition(target));
                });
            }
            return;
        }
        Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> {
            PaperLib.teleportAsync(player, entitySelector.getEntity().getLocation());
        });
    }

    @CommandMethod("tp <entities> <target>")
    @CommandDescription("Teleport entity to entity")
    @CommandPermission("serverbasics.command.tp")
    private void commandTpOthers(
            final CommandSender sender,
            final @Argument(value = "entities") MultipleEntitySelector entitySelector,
            final @Argument(value = "target") SingleEntitySelector targetSelector
    ) {

        if (!entitySelector.hasAny()) {
            if (!targetSelector.hasAny()) {
                Component message = Component.translatable(
                        "argument.entity.notfound.entity",
                        NamedTextColor.WHITE);
                ServerBasics.getCommandManager().bukkitAudiences.sender(sender).sendMessage(message);
            }
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(entitySelector.getSelector());
            if (target == null) {
                MessageParser.sendHaventPlayedError(sender);
            } else {
                Location targetLocation = ServerBasics.getNmsHandler().getOfflinePlayerPostition(target);

                Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> {
                    for (Entity entity : targetSelector.getEntities()) {
                        PaperLib.teleportAsync(entity, targetLocation);
                    }
                });
            }
            return;
        }

        Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> {

        });



    }

}
