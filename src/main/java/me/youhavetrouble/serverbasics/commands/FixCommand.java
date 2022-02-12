package me.youhavetrouble.serverbasics.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.commands.registration.CommandRegistration;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

@CommandRegistration
public class FixCommand {

    @CommandMethod("fix")
    @CommandDescription("Fix item")
    @CommandPermission("serverbasics.command.fix")
    private void commandFix(
            final Player player
    ) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            String msg = ServerBasics.getLang(player.locale()).have_to_hold_item;
            MessageParser.sendMessage(player, msg);
            return;
        }
        fixItem(itemStack);
        MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).fixed_hand);
    }

    @CommandMethod("fix hand")
    @CommandDescription("Fix item")
    @CommandPermission("serverbasics.command.fix.hand")
    private void commandFixHand(
            final Player player
    ) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            String msg = ServerBasics.getLang(player.locale()).have_to_hold_item;
            MessageParser.sendMessage(player, msg);
            return;
        }
        fixItem(itemStack);
        MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).fixed_hand);
    }

    @CommandMethod("fix all")
    @CommandDescription("Fix all items")
    @CommandPermission("serverbasics.command.fix.all")
    private void commandFixAll(
            final Player player
    ) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getItemMeta() == null) continue;
            if (item.getItemMeta() instanceof Damageable)
                fixItem(item);
        }
        MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).fixed_inventory);
    }

    private void fixItem(ItemStack itemStack) {
        Damageable damagable = (Damageable) itemStack.getItemMeta();
        damagable.setDamage(0);
        itemStack.setItemMeta(damagable);
    }

}
