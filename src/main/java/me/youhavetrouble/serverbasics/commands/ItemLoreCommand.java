package me.youhavetrouble.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.commands.registration.CommandRegistration;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@CommandRegistration
public class ItemLoreCommand {

    @CommandMethod("itemlore <lore>")
    @CommandDescription("Change the lore of the held item")
    @CommandPermission("serverbasics.command.itemlore")
    private void commandItemLore(
            final Player player,
            final @Argument(value = "lore", description = "new lore for the item") @Greedy String[] newName
    ) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() == Material.AIR) {
            String msg = ServerBasics.getLang(player).have_to_hold_item;
            MessageParser.sendMessage(player, msg);
            return;
        }
        String raw = StringUtils.join(newName, " ");
        raw = MessageParser.makeColorsWork('&', raw);

        String[] lines = raw.split("\\\\n");
        List<Component> lore = new ArrayList<>();
        for (String line : lines) {
            lore.add(MessageParser.miniMessage.deserialize(line));
        }
        ItemMeta meta = itemStack.getItemMeta();
        meta.lore(lore);
        itemStack.setItemMeta(meta);
        player.getInventory().setItemInMainHand(itemStack);
        String msg = ServerBasics.getLang(player).item_lore_changed;
        MessageParser.sendMessage(player, msg);
    }

}
