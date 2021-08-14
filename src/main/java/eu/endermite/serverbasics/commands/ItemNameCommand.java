package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
            String msg = ServerBasics.getLang(player.locale()).have_to_hold_item;
            MessageParser.sendMessage(player, msg);
            return;
        }
        String name = StringUtils.join(newName, " ");
        ItemMeta meta = itemStack.getItemMeta();
        meta.displayName(MiniMessage.markdown().parse(name));
        itemStack.setItemMeta(meta);
        player.getInventory().setItemInMainHand(itemStack);
        String msg = ServerBasics.getLang(player.locale()).item_name_changed;
        MessageParser.sendMessage(player, msg);
    }
}
