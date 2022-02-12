package me.youhavetrouble.serverbasics.listeners;

import me.youhavetrouble.serverbasics.ServerBasics;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class HatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHatChange(org.bukkit.event.inventory.InventoryClickEvent event) {
        if (!event.getSlotType().equals(InventoryType.SlotType.ARMOR)) return;
        if (event.getClick().equals(ClickType.MIDDLE)) return;
        if (event.getSlot() != 39) return;
        if (!event.getWhoClicked().hasPermission("serverbasics.hat.equip")) return;
        if (event.getCursor() == null) return;
        Player player = (Player) event.getWhoClicked();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().containsEnchantment(Enchantment.BINDING_CURSE)) return;
        if (event.getAction().equals(InventoryAction.PLACE_ALL) || event.getAction().equals(InventoryAction.PLACE_ONE)) {
            event.setCancelled(true);
            ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> basicPlayer.setHat(event.getCursor()));
        } else if (event.getAction().equals(InventoryAction.NOTHING)) {
            event.setCancelled(true);
            if (event.getCursor().getAmount() == 1) {
                ItemStack helmet = player.getInventory().getHelmet();
                player.getInventory().setHelmet(event.getCursor());
                event.setCursor(helmet);
                player.updateInventory();
                return;
            }
            ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> {
                basicPlayer.setHat(event.getCursor());
                player.updateInventory();
            });
        }
    }
}



