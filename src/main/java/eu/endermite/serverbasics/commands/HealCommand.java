package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandRegistration
public class HealCommand {

    @CommandMethod("heal")
    @CommandDescription("Heal yourself")
    @CommandPermission("serverbasics.command.heal")
    private void commandHeal(
            final Player player
    ) {
        AttributeInstance maxHpAttr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double maxHp;
        try {
            maxHp = maxHpAttr.getValue();
        } catch (NullPointerException e) {
            maxHp = maxHpAttr.getDefaultValue();
        }
        player.setHealth(maxHp);
        MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).healed);
    }

    @CommandMethod("heal <target>")
    @CommandDescription("Heal yourself or other player")
    @CommandPermission("serverbasics.command.heal.others")
    private void commandHealOther(
            final CommandSender sender,
            final @Argument(value = "target", description = "Player to heal") MultiplePlayerSelector targetPlayer
    ) {
        int amountHealed = targetPlayer.getPlayers().size();
        Player lastPlayer = null;

        for (Player target : targetPlayer.getPlayers()) {
            lastPlayer = target;
            AttributeInstance maxHpAttr = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            double maxHp;
            try {
                maxHp = maxHpAttr.getValue();
            } catch (NullPointerException e) {
                maxHp = maxHpAttr.getDefaultValue();
            }
            target.setHealth(maxHp);
            String msg;
            if (sender != target) {
                msg = String.format(ServerBasics.getLang(target.locale()).healed_by_other, sender.getName());
            } else {
                msg = ServerBasics.getLang(target.locale()).healed;
            }
            MessageParser.sendMessage(target, msg);
        }
        if (sender instanceof Player player) {
            String msg;

            if (amountHealed == 1)
                msg = String.format(ServerBasics.getLang(player.locale()).healed_by_other, lastPlayer.getDisplayName());
            else if (amountHealed > 1)
                msg = String.format(ServerBasics.getLang(player.locale()).healed_many, amountHealed);
            else
                msg = ServerBasics.getLang(player.locale()).healed_noone;
            MessageParser.sendMessage(player, msg);
        } else {
            String msg;
            if (amountHealed == 1)
                msg = String.format(ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).healed_by_other, lastPlayer.getDisplayName());
            else if (amountHealed > 1)
                msg = String.format(ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).healed_many, amountHealed);
            else
                msg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).healed_noone;
            MessageParser.sendMessage(sender, msg);
        }

    }
}
