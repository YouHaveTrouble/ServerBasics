package me.youhavetrouble.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.commands.registration.CommandRegistration;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@CommandRegistration
public class ItemNameCommand {

    @CommandMethod("itemname <name>")
    @CommandDescription("Change the name of the held item")
    @CommandPermission("serverbasics.command.itemname")
    private void commandItemName(
            final Player player,
            final @Argument(value = "name", description = "new name for the item") @Greedy String[] newName
    ) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            String msg = ServerBasics.getLang(player).have_to_hold_item;
            MessageParser.sendMessage(player, msg);
            return;
        }
        String name = StringUtils.join(newName, " ");
        name = MessageParser.makeColorsWork('&', name);
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(MessageParser.miniMessage.deserialize(name));
        itemStack.setItemMeta(meta);
        player.getInventory().setItemInMainHand(itemStack);
        MessageParser.sendMessage(player, ServerBasics.getLang(player).item_name_changed);
    }
}
