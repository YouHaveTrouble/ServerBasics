package eu.endermite.serverbasics.players;

import eu.endermite.serverbasics.NMSHandler;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import lombok.Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * All player data that needs to be tracked in a plugin and convienience methods
 */
@Builder
public class BasicPlayer {

    private final UUID uuid;
    private boolean godMode = false;
    private Component displayName;
    private long lastSeen;

    public UUID getUuid() {
        return uuid;
    }

    /**
     * Check if player is allowed to fly
     * @return
     */
    public boolean canFly() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return false;
        if (offlinePlayer.isOnline())
            return offlinePlayer.getPlayer().getAllowFlight();
        else
            return NMSHandler.getOfflinePlayerCanFly(offlinePlayer);
    }

    /**
     * Set player's fly state.
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

    public Component getDisplayName() {
        if (displayName != null) return displayName;
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return Component.empty();
        if (offlinePlayer.isOnline())
            return offlinePlayer.getPlayer().displayName();
        else
            return Component.text(offlinePlayer.getName());
    }
    public boolean setDisplayName(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return false;
        if (offlinePlayer.isOnline()) {
            displayName = MiniMessage.markdown().parse(name);
            offlinePlayer.getPlayer().displayName(MiniMessage.markdown().parse(name));
        }
        ServerBasics.getInstance().getDatabase().savePlayerDisplayName(uuid, name);
        return true;
    }

    public GameMode getGameMode() {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return null;
        if (offlinePlayer.isOnline())
            return offlinePlayer.getPlayer().getGameMode();
        else
            return NMSHandler.getOfflinePlayerGamemode(offlinePlayer);
    }

    public boolean setGameMode(GameMode gamemode) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return false;
        if (offlinePlayer.isOnline())
            Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> offlinePlayer.getPlayer().setGameMode(gamemode)) ;
        else
            NMSHandler.setOfflinePlayerGamemode(offlinePlayer, gamemode);
        return true;
    }

    public void setHat(ItemStack itemStack) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline()) return;
        ItemStack hatItem = itemStack.clone();
        hatItem.setAmount(1);
        itemStack.setAmount(itemStack.getAmount()-1);
        if (player.getInventory().getHelmet() != null) {
            player.getInventory().addItem(player.getInventory().getHelmet());
            player.getInventory().setHelmet(null);
        }
        player.getInventory().setHelmet(hatItem);
    }

    public CompletableFuture<Boolean> teleportPlayer(Location location) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.hasPlayedBefore()) return CompletableFuture.completedFuture(false);
        if (offlinePlayer.isOnline())
            return offlinePlayer.getPlayer().teleportAsync(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        else {
            return CompletableFuture.supplyAsync(() -> {
                NMSHandler.setOfflinePlayerPosition(offlinePlayer, location);
                return true;
            });
        }
    }

    public void teleportToSpawn() {
        OfflinePlayer offlinePlayer = Bukkit.getPlayer(uuid);
        Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> teleportPlayer(ServerBasics.getLocationsCache().spawn.getLocation()).thenAccept(result -> {
            if (!offlinePlayer.isOnline()) return;
            Player player = offlinePlayer.getPlayer();
            if (result) {
                MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).teleported_spawn);
            } else {
                MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).could_not_tp);
            }
        }));
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
     * @return CompletableFuture of BasicPlayer instance. Can be null if player does not exist in the database.
     */
    public static CompletableFuture<BasicPlayer> fromDatabase(UUID uuid) {
        return ServerBasics.getInstance().getDatabase().getPlayer(uuid);
    }

    public static BasicPlayer empty(UUID uuid) {
        return builder()
                .uuid(uuid)
                .godMode(false)
                .lastSeen(0L)
                .displayName(MiniMessage.markdown().parse(ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).unknown_player))
                .build();
    }

}


