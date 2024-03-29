package me.youhavetrouble.serverbasics.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.commands.registration.CommandRegistration;
import me.youhavetrouble.serverbasics.messages.MessageParser;
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
            String msg = ServerBasics.getLang(player).have_to_hold_item;
            MessageParser.sendMessage(player, msg);
            return;
        }
        if (helmet != null && helmet.containsEnchantment(Enchantment.BINDING_CURSE)) {
            MessageParser.sendMessage(player, ServerBasics.getLang(player).hat_curse);
            return;
        }
        ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> {
            basicPlayer.setHat(itemStack);
            MessageParser.sendMessage(player, ServerBasics.getLang(player).hat_set);
        });

    }

}
