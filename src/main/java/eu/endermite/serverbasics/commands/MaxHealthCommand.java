package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Range;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

@CommandRegistration
public class MaxHealthCommand {

    @CommandMethod("maxhp <amount>")
    @CommandDescription("Set your max hp")
    @CommandPermission("serverbasics.command.maxhp")
    private void commandGamemode(
            final Player player,
            final @Argument(value = "amount") @Range(min = "0", max = "2048") double amount
    ) {
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(amount);
        player.setHealth(amount);
    }

}
