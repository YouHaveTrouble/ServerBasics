package me.youhavetrouble.serverbasics.players;

import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import me.youhavetrouble.serverbasics.NMSHandler;
import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import lombok.Builder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * All player data that needs to be tracked in a plugin and convienience methods
 */

public class BasicPlayer {

    private final UUID uuid;
    private Component displayName;
    private String rawDisplayName;
    private final long lastSeen;

    public BasicPlayer(UUID id, String rawDisplayName, long lastSeen) {
        this.uuid = id;
        this.rawDisplayName = rawDisplayName;
        this.displayName = rawDisplayName == null ? null : MessageParser.miniMessage.deserialize(rawDisplayName);
        this.lastSeen = lastSeen;
    }

    public UUID getUuid() {
        return uuid;
    }

    /**
     * Check if player is allowed to fly
     */
    public boolean canFly() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return false;
        if (offlinePlayer.isOnline()) return offlinePlayer.getPlayer().getAllowFlight();
        return NMSHandler.getOfflinePlayerCanFly(offlinePlayer);
    }

    /**
     * Set player's fly state.
     *
     * @param state State to set.
     * @return true if set successfully, false if not
     */
    public boolean setFly(boolean state) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return false;
        if (offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();
            Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> player.setAllowFlight(state));
            if (!state) {
                Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> {
                    player.setAllowFlight(false);
                    player.setFallDistance(Float.MIN_VALUE);
                });
            }
            return true;
        }
        NMSHandler.setOfflinePlayerCanFly(offlinePlayer, state);
        if (state)
            NMSHandler.setOfflinePlayerFallDistance(offlinePlayer, Float.MIN_VALUE);
        return true;
    }

    /**
     * Toggle player's fly mode.
     *
     * @return New fly state.
     */
    public boolean toggleFly() {
        if (canFly()) {
            setFly(false);
            return false;
        } else {
            setFly(true);
            return true;
        }
    }

    public void sendMessage(String message) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        if (!player.isOnline()) return;
        MessageParser.sendMessage(player, message);
    }

    public void sendMessage(String message, HashMap<String, Component> placeholders) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        if (!player.isOnline()) return;
        MessageParser.sendMessage(player, message, placeholders);
    }

    public void sendMessage(Component message) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        if (!player.isOnline()) return;
        player.sendMessage(message);
    }

    public Locale getLocale() {
        Locale def = ServerBasics.getConfigCache().default_lang;
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return def;
        if (!player.isOnline()) return def;
        return player.locale();
    }

    public String getRawDisplayName() {
        return rawDisplayName;
    }

    public Component getDisplayName() {
        if (displayName != null) return displayName;
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.getName() == null) return Component.empty();
        if (offlinePlayer.isOnline()) return offlinePlayer.getPlayer().displayName();
        return Component.text(offlinePlayer.getName());
    }

    public void setDisplayName(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return;
        this.rawDisplayName = name;
        if (offlinePlayer.isOnline()) {
            this.displayName = name == null ? null : MessageParser.miniMessage.deserialize(name);
            offlinePlayer.getPlayer().displayName(this.displayName);
        }
    }

    public GameMode getGameMode() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return null;
        if (offlinePlayer.isOnline()) return offlinePlayer.getPlayer().getGameMode();
        return NMSHandler.getOfflinePlayerGamemode(offlinePlayer);
    }

    public void setGameMode(GameMode gamemode) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return;
        if (offlinePlayer.isOnline()) Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> offlinePlayer.getPlayer().setGameMode(gamemode));
        NMSHandler.setOfflinePlayerGamemode(offlinePlayer, gamemode);
    }

    public void setHat(ItemStack itemStack) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) return;
        ItemStack hatItem = itemStack.clone();
        hatItem.setAmount(1);
        itemStack.setAmount(itemStack.getAmount() - 1);
        if (player.getInventory().getHelmet() != null) {
            player.getInventory().addItem(player.getInventory().getHelmet());
            player.getInventory().setHelmet(null);
        }
        player.getInventory().setHelmet(hatItem);
    }

    public boolean teleportPlayer(Location location) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return false;
        if (offlinePlayer.isOnline()) {
            Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> offlinePlayer.getPlayer().teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN));
        } else
            CompletableFuture.runAsync(() -> NMSHandler.setOfflinePlayerPosition(offlinePlayer, location));
        return true;
    }

    public boolean teleportPlayer(Location location, Component feedback) {
        boolean result = teleportPlayer(location);
        if (result) sendMessage(feedback);
        return result;
    }

    public void teleportToSpawn() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        teleportPlayer(ServerBasics.getLocationsCache().spawn.getLocation());
        if (!offlinePlayer.isOnline()) return;
        Player player = offlinePlayer.getPlayer();
        MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).teleported_spawn);
    }

    public boolean isOnline() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return false;
        return player.isOnline();
    }

    /**
     * @return Epoch second timestamp of last logout
     */
    public long getLastSeen() {
        return lastSeen;
    }

    public static CompletableFuture<BasicPlayer> fromSinglePlayerSelector(SinglePlayerSelector selector) {
        if (!selector.hasAny()) {
            String name = selector.getSelector();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            if (!offlinePlayer.hasPlayedBefore()) {
                return null;
            }
            return ServerBasics.getBasicPlayers().getBasicPlayer(offlinePlayer.getUniqueId());
        }
        return ServerBasics.getBasicPlayers().getBasicPlayer(selector.getPlayer().getUniqueId());

    }


    /**
     * Get player data from cache if the player is online or from database when they're not.
     */
    public static CompletableFuture<BasicPlayer> fromPlayer(Player player) {
        return ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId());
    }

    /**
     * Get player data from cache if the player is online or from database when they're not.
     */
    public static CompletableFuture<BasicPlayer> fromOfflinePlayer(OfflinePlayer offlinePlayer) {
        return ServerBasics.getBasicPlayers().getBasicPlayer(offlinePlayer.getUniqueId());
    }

    /**
     * Get player data from cache if the player is online or from database when they're not.
     */
    public static CompletableFuture<BasicPlayer> fromUuid(UUID uuid) {
        return ServerBasics.getBasicPlayers().getBasicPlayer(uuid);
    }

    /**
     * Get player data directly from the database. This method will NOT check for online player cache first.
     *
     * @return CompletableFuture of BasicPlayer instance. Can be null if player does not exist in the database.
     */
    public static CompletableFuture<BasicPlayer> fromDatabase(UUID uuid) {
        return ServerBasics.getInstance().getDatabase().getPlayerData().loadBasicPlayer(uuid);
    }

    public static BasicPlayer empty(UUID uuid) {
        return new BasicPlayer(uuid, null, 0L);
    }

}


