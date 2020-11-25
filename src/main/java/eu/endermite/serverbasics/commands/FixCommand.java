package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

@CommandRegistration
public class FixCommand {

    @CommandMethod("fix")
    @CommandDescription("Fix item")
    @CommandPermission("serverbasics.command.fix.hand")
    private void commandFix(
            final Player player
    ) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            String msg = ServerBasics.getLang(player.getLocale()).HAVE_TO_HOLD_ITEM;
            MessageParser.sendMessage(player, msg);
            return;
        }
        fixItem(itemStack);

        MessageParser.sendMessage(player, ServerBasics.getLang(player.getLocale()).fixed_hand);
    }

    @CommandMethod("fix hand")
    @CommandDescription("Fix item")
    @CommandPermission("serverbasics.command.fix.hand")
    private void commandFixHand(
            final Player player
    ) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            String msg = ServerBasics.getLang(player.getLocale()).HAVE_TO_HOLD_ITEM;
            MessageParser.sendMessage(player, msg);
            return;
        }
        fixItem(itemStack);

        MessageParser.sendMessage(player, ServerBasics.getLang(player.getLocale()).fixed_hand);
    }

    @CommandMethod("fix all")
    @CommandDescription("Fix all items")
    @CommandPermission("serverbasics.command.fix.all")
    private void commandFixAll(
            final Player player
    ) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getItemMeta() == null)
                continue;
            if (item.getItemMeta() instanceof Damageable)
                fixItem(item);
        }

        MessageParser.sendMessage(player, ServerBasics.getLang(player.getLocale()).fixed_inventory);
    }

    private void fixItem(ItemStack itemStack) {
        Damageable damagable = (Damageable) itemStack.getItemMeta();
        damagable.setDamage(0);
        itemStack.setItemMeta((ItemMeta) damagable);
    }

}
