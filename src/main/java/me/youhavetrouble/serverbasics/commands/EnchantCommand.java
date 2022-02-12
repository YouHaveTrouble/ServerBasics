package me.youhavetrouble.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
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
public class EnchantCommand {

    @CommandMethod("enchant <target> <enchant> [level]")
    @CommandDescription("Enchant an item")
    @CommandPermission("serverbasics.command.enchant")
    private void commandEnchant(
            final Player player,
            final @Argument(value = "target") Player target,
            final @Argument(value = "enchant") Enchantment enchantment,
            final @Argument(value = "level", defaultValue = "1") int level
            )
    {
        ItemStack item = target.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            MessageParser.sendMessage(player, ServerBasics.getLang(player).no_item_to_enchant);
            return;
        }

        if (player.hasPermission("serverbasics.command.enchant.unsafe")) {
            item.addUnsafeEnchantment(enchantment, level);
        } else {
            try {
                item.addEnchantment(enchantment, level);
            } catch (IllegalArgumentException e) {
                MessageParser.sendMessage(player, ServerBasics.getLang(player).no_unsafe_enchant);
                return;
            }
        }
        MessageParser.sendMessage(player, ServerBasics.getLang(player).item_enchanted);
    }
}
