package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import eu.endermite.serverbasics.players.PlayerUtil;
import eu.endermite.serverbasics.ServerBasics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandRegistration
public class FlyCommand {

    @CommandMethod("fly")
    @CommandDescription("Toggle flight mode")
    @CommandPermission("serverbasics.command.fly")
    private void commandFly(
            final Player player
    ) {
        PlayerUtil.toggleFlight(player.getUniqueId());
    }

    @CommandMethod("fly <target>")
    @CommandDescription("Toggle flight mode")
    @CommandPermission("serverbasics.command.fly")
    private void commandFlyOther(
            final CommandSender sender,
            @Argument(value = "target", description = "Target")SinglePlayerSelector playerSelector
            ) {

        Player target = playerSelector.getPlayer();

        if (target == null) {
            final Component message = Component.translatable(
                    "argument.entity.notfound.entity",
                    NamedTextColor.WHITE);
            ServerBasics.getCommandManager().bukkitAudiences.sender(sender).sendMessage(message);
            return;
        }

        PlayerUtil.toggleFlight(target.getUniqueId());
    }

}
