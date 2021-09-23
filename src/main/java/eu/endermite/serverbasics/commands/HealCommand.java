package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
import net.kyori.adventure.text.Component;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

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
        MessageParser.sendMessage(player, ServerBasics.getLang(player).healed);
    }

    @CommandMethod("heal <target>")
    @CommandDescription("Heal yourself or other player")
    @CommandPermission("serverbasics.command.heal.others")
    private void commandHealOther(
            final CommandSender sender,
            final @Argument(value = "target", description = "Player to heal") MultiplePlayerSelector targetPlayer
    ) {
        int amountHealed = targetPlayer.getPlayers().size();
        for (Player target : targetPlayer.getPlayers()) {
            target.setHealth(Objects.requireNonNull(target.getAttribute(Attribute.GENERIC_MAX_HEALTH), "Player must have max health").getValue());
            if (sender != target) {
                String msg = ServerBasics.getLang(target).healed_by_other;
                sender.sendMessage(MessageParser.parseMessage(sender, msg, "%player%", MessageParser.getName(sender, target.locale())));
            }
        }

        Locale locale;
        if (sender instanceof Player player)
            locale = player.locale();
        else
            locale = ServerBasics.getConfigCache().default_lang;

        String msg;
        if (amountHealed == 1) {
            msg = ServerBasics.getLang(locale).healed_other;
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%player%", MessageParser.getName(sender, locale));
            sender.sendMessage(MessageParser.parseMessage(sender, msg, placeholders));
        } else if (amountHealed > 1) {
            msg = ServerBasics.getLang(locale).healed_many;
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%amount%", MessageParser.getName(sender, locale));
            sender.sendMessage(MessageParser.parseMessage(sender, msg, placeholders));
        } else {
            msg = ServerBasics.getLang(locale).healed_noone;
            sender.sendMessage(MessageParser.parseMessage(sender, msg));
        }
    }
}
