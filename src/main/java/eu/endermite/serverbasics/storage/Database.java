package eu.endermite.serverbasics.storage;

import eu.endermite.serverbasics.players.BasicPlayer;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Database {

    void createTables();
    CompletableFuture<BasicPlayer> getPlayer(UUID uuid);
    CompletableFuture<Void> savePlayer(BasicPlayer basicPlayer);
    CompletableFuture<Void> deletePlayer(UUID uuid);

    CompletableFuture<Location> getSpawn();
    CompletableFuture<Void> saveSpawn(Location location);
    CompletableFuture<Void> deleteSpawn();

    HashMap<String, Location> getWarps();
    CompletableFuture<Void> saveWarp(Location location, String name);
    CompletableFuture<Void> deleteWarp(String name);

    CompletableFuture<HashMap<String, Location>> getPlayerHomes(UUID uuid);
    CompletableFuture<Void> savePlayerHomes(HashMap<String, Location> homes);
    CompletableFuture<Void> deletePlayerHome(UUID uuid, String name);


}
