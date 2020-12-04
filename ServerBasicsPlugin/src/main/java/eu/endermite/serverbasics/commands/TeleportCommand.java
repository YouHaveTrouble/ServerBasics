package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SingleEntitySelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandRegistration
public class TeleportCommand {

    @CommandMethod("tp <target>")
    @CommandDescription("Teleport to entity")
    @CommandPermission("serverbasics.command.serverbasics")
    private void commandServerBasicsDebug(
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

}
