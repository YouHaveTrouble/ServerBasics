package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import eu.endermite.serverbasics.PlayerUtil;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand {

    public void constructCommand() {
        ServerBasics.getCommandManager().getAnnotationParser().parse(this);
    }

    public static final NamespacedKey flyKey = new NamespacedKey(ServerBasics.getInstance(), "flying");

    @CommandMethod("fly")
    @CommandDescription("Toggle flight mode")
    @CommandPermission("serverbasics.command.fly")
    private void commandFly(
            final Player player
    ) {
        PlayerUtil.toggleFlight(player);
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
            MessageParser.sendDefaultTranslatedError(sender, "argument.entity.notfound.entity", ChatColor.RED);
            return;
        }

        PlayerUtil.toggleFlight(target);
    }

}
