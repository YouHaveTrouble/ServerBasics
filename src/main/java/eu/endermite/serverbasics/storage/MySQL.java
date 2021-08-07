package eu.endermite.serverbasics.storage;

import eu.endermite.serverbasics.players.BasicPlayer;
import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

// TODO rewrite this using Database interface & merge with ServerDatabase

public class MySQL implements Database {

    private Connection connection;

    private final String url = ServerBasics.getConfigCache().getSqlPlayersConnectionString();
    private final String playerTable, warpTable, homesTable;

    public MySQL(String prefix) {
        this.playerTable = prefix+"players";
        this.warpTable = prefix+"warps";
        this.homesTable = prefix+"homes";
        createTables();
    }


    private void connect() {
        try {
            if (connection == null || connection.isClosed())
                connection = DriverManager.getConnection(url);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void createTables() {
        connect();
        try {
            if (connection != null) {
                Statement statement = connection.createStatement();
                String sql;
                sql = "CREATE TABLE IF NOT EXISTS `"+playerTable+"` (`player_uuid` varchar(36) PRIMARY KEY, `displayname` varchar(256);";
                statement.execute(sql);
                sql = "CREATE TABLE IF NOT EXISTS `"+warpTable+"` (`warp_id` varchar(32) PRIMARY KEY, `displayname` varchar(256), `world_uuid` varchar(36), `coords` varchar(256);";
                statement.execute(sql);
                sql = "CREATE TABLE IF NOT EXISTS `"+homesTable+"` (`home_id` varchar(32) , `player_uuid` varchar(36), `world_uuid` varchar(36), `coords` varchar(256), CONSTRAINT COMP_KEY PRIMARY KEY (home_id, player_uuid));";
                statement.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<BasicPlayer> getPlayer(UUID uuid) {
        return null;
    }

    @Override
    public CompletableFuture<Void> savePlayer(BasicPlayer basicPlayer) {
        return null;
    }

    @Override
    public CompletableFuture<Void> deletePlayer(UUID uuid) {
        return null;
    }

    @Override
    public CompletableFuture<Location> getSpawn() {
        return null;
    }

    @Override
    public CompletableFuture<Void> saveSpawn(Location location) {
        return null;
    }

    @Override
    public CompletableFuture<Void> deleteSpawn() {
        return null;
    }

    @Override
    public HashMap<String, Location> getWarps() {
        return null;
    }

    @Override
    public CompletableFuture<Void> saveWarp(Location location, String name) {
        return null;
    }

    @Override
    public CompletableFuture<Void> deleteWarp(String name) {
        return null;
    }

    @Override
    public CompletableFuture<HashMap<String, Location>> getPlayerHomes(UUID uuid) {
        return null;
    }

    @Override
    public CompletableFuture<Void> savePlayerHomes(HashMap<String, Location> homes) {
        return null;
    }

    @Override
    public CompletableFuture<Void> deletePlayerHome(UUID uuid, String name) {
        return null;
    }
}



