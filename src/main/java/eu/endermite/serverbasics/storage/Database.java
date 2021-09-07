package eu.endermite.serverbasics.storage;

import eu.endermite.serverbasics.players.BasicPlayer;
import eu.endermite.serverbasics.util.BasicWarp;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Database {

    void createTables();

    // Player data
    CompletableFuture<BasicPlayer> getPlayer(UUID uuid);
    CompletableFuture<Void> savePlayerDisplayName(UUID uuid, String displayName);
    CompletableFuture<Void> savePlayerLastSeen(UUID uuid, long lastSeen);
    CompletableFuture<Void> deletePlayer(UUID uuid);

    // Spawn data
    CompletableFuture<BasicWarp> getSpawn();
    CompletableFuture<Void> saveSpawn(BasicWarp basicWarp);
    CompletableFuture<Void> deleteSpawn();

    // Warp data
    CompletableFuture<HashMap<String, BasicWarp>> getWarps();
    CompletableFuture<Void> saveWarp(BasicWarp basicWarp);
    CompletableFuture<Void> deleteWarp(String name);

    // Home data
    CompletableFuture<HashMap<String, BasicWarp>> getPlayerHomes(UUID uuid);
    CompletableFuture<Void> savePlayerHome(BasicWarp home, UUID uuid);
    CompletableFuture<Void> deletePlayerHome(UUID uuid, String name);

    // Economy data
    CompletableFuture<Double> getBalance(UUID uuid);
    CompletableFuture<Void> saveBalance(UUID uuid, double balance);
    CompletableFuture<Void> removeBalance(UUID uuid);
    CompletableFuture<HashMap<UUID, Double>> getBaltop(int limit);


}
