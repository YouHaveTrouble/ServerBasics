package eu.endermite.serverbasics.storage;

import eu.endermite.serverbasics.players.BasicPlayer;
import eu.endermite.serverbasics.util.BasicWarp;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Database {

    void createTables();

    // Player data

    /**
     * Get complete player data object from database.
     * @param uuid Player's UUID
     * @return CompletableFuture of BasicPlayer when possible. Null when uuid not present in the database.
     */
    CompletableFuture<BasicPlayer> getPlayer(UUID uuid);

    CompletableFuture<Void> savePlayerDisplayName(UUID uuid, String displayName);
    CompletableFuture<Void> savePlayerGodMode(UUID uuid, boolean godmode);
    CompletableFuture<Void> savePlayerLastSeen(UUID uuid, long lastSeen);
    CompletableFuture<Void> deletePlayer(UUID uuid);

    CompletableFuture<BasicWarp> getSpawn();
    CompletableFuture<Void> saveSpawn(Location location);
    CompletableFuture<Void> deleteSpawn();

    // Warp data
    HashMap<String, BasicWarp> getWarps();
    CompletableFuture<Void> saveWarp(BasicWarp basicWarp);
    CompletableFuture<Void> deleteWarp(String name);

    // Home data
    CompletableFuture<HashMap<String, Location>> getPlayerHomes(UUID uuid);
    CompletableFuture<Void> savePlayerHomes(HashMap<String, Location> homes);
    CompletableFuture<Void> deletePlayerHome(UUID uuid, String name);


}
