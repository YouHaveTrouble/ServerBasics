package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
import eu.endermite.serverbasics.players.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandRegistration
public class HatCommand {

    @CommandMethod("hat")
    @CommandDescription("Set your hat")
    @CommandPermission("serverbasics.command.hat")
    private void commandGamemode(
            final Player player
    ) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        ItemStack helmet = player.getInventory().getHelmet();

        if (itemStack.getType() == Material.AIR) {
            String msg = ServerBasics.getLang(player.locale()).have_to_hold_item;
            MessageParser.sendMessage(player, msg);
            return;
        }
        if (helmet != null && helmet.containsEnchantment(Enchantment.BINDING_CURSE)) {
            String msg = ServerBasics.getLang(player.locale()).hat_curse;
            MessageParser.sendMessage(player, msg);
            return;
        }

        PlayerUtil.setHat(player, itemStack);
        String msg = ServerBasics.getLang(player.locale()).hat_set;
        MessageParser.sendMessage(player, msg);
    }

}
