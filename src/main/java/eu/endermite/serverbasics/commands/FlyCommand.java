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
            @Argument(value = "target", description = "Target")SinglePlayerSelector playerSelector
            ) {

        Player target = playerSelector.getPlayer();

        if (target == null) {
            final Component message = Component.translatable(
                    "argument.entity.notfound.entity",
                    NamedTextColor.WHITE);
            sender.sendMessage(message);
            return;
        }

        BasicPlayer.fromPlayer(target).thenAccept(basicPlayer -> {
            if (basicPlayer.toggleFly()) {
                String msg = ServerBasics.getLang(target.locale()).started_flying;
                MessageParser.sendMessage(target, msg);
            } else {
                String msg = ServerBasics.getLang(target.locale()).stopped_flying;
                MessageParser.sendMessage(target, msg);
            }
        });
    }

}
