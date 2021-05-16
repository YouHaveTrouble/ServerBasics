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
        String playerLang = player.getLocale();
        MessageParser.sendMessage(player, ServerBasics.getLang(playerLang).healed);
    }

    @CommandMethod("heal <target>")
    @CommandDescription("Heal yourself or other player")
    @CommandPermission("serverbasics.command.heal.others")
    private void commandHealOther(
            final CommandSender player,
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
            String playerLang = target.getLocale();
            String msg;
            if (player != target) {
                msg = String.format(ServerBasics.getLang(playerLang).healed_by_other, player.getName());
            } else {
                msg = ServerBasics.getLang(playerLang).healed;
            }
            MessageParser.sendMessage(target, msg);
        }
        if (player instanceof Player) {
            String msg;
            String playerLang = ((Player) player).getLocale();
            if (amountHealed == 1)
                msg = String.format(ServerBasics.getLang(playerLang).healed_by_other, lastPlayer.getDisplayName());
            else if (amountHealed > 1)
                msg = String.format(ServerBasics.getLang(playerLang).healed_many, amountHealed);
            else
                msg = ServerBasics.getLang(playerLang).healed_noone;
            MessageParser.sendMessage(player, msg);
        } else {
            String msg;
            if (amountHealed == 1)
                msg = String.format(ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).healed_by_other, lastPlayer.getDisplayName());
            else if (amountHealed > 1)
                msg = String.format(ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).healed_many, amountHealed);
            else
                msg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).healed_noone;
            MessageParser.sendMessage(player, msg);
        }

    }
}
