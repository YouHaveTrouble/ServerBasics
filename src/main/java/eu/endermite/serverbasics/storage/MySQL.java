package eu.endermite.serverbasics.storage;

import eu.endermite.serverbasics.players.BasicPlayer;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.util.BasicWarp;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;

import java.sql.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQL implements Database {

    private Connection connection;

    private final String url = ServerBasics.getConfigCache().getSqlPlayersConnectionString();
    private final String playerTable, warpTable, homesTable;
    private final String loadPlayer, savePlayerDisplayName, savePlayerGodMode, savePlayerLastSeen, getSpawn;

    public MySQL(String prefix) {
        this.playerTable = prefix+"players";
        this.warpTable = prefix+"warps";
        this.homesTable = prefix+"homes";
        createTables();

        loadPlayer = "SELECT * FROM `"+playerTable+"` WHERE `player_uuid` = ?;";
        savePlayerDisplayName = "INSERT INTO `"+playerTable+"` (player_uuid, displayname) VALUES (?, ?) ON DUPLICATE KEY UPDATE displayname = ?;";
        savePlayerGodMode = "INSERT INTO `"+playerTable+"` (player_uuid, godmode) VALUES (?, ?) ON DUPLICATE KEY UPDATE godmode = ?;";
        savePlayerLastSeen = "INSERT INTO `"+playerTable+"` (player_uuid, lastseen) VALUES (?, ?) ON DUPLICATE KEY UPDATE lastseen = ?;";
        getSpawn = "SELECT * FROM `"+warpTable+"` WHERE `warp_id` = ?;";
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
                sql = "CREATE TABLE IF NOT EXISTS `"+playerTable+"` (`player_uuid` varchar(36) NOT NULL PRIMARY KEY, `displayname` varchar(256), `godmode` BOOLEAN DEFAULT FALSE, `lastseen` long);";
                statement.execute(sql);
                sql = "CREATE TABLE IF NOT EXISTS `"+warpTable+"` (`warp_id` varchar(32) UNIQUE PRIMARY KEY, `displayname` varchar(256), `world_uuid` varchar(36), `coords` varchar(256));";
                statement.execute(sql);
                sql = "CREATE TABLE IF NOT EXISTS `"+homesTable+"` (`home_id` varchar(32), `player_uuid` varchar(36), `world_uuid` varchar(36), `coords` varchar(256), CONSTRAINT COMP_KEY PRIMARY KEY (home_id, player_uuid));";
                statement.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ServerBasics.getInstance().getServer().getPluginManager().disablePlugin(ServerBasics.getInstance());
        }
    }

    @Override
    public CompletableFuture<BasicPlayer> getPlayer(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            connect();
            try (PreparedStatement statement = connection.prepareStatement(loadPlayer)) {
                statement.setString(1, uuid.toString());
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    String displayName = result.getString("displayname");
                    boolean godMode = result.getBoolean("godmode");
                    long lastSeen = result.getLong("lastseen");
                    return BasicPlayer.builder()
                            .uuid(uuid)
                            .godMode(godMode)
                            .displayName(MiniMessage.markdown().parse(displayName))
                            .lastSeen(lastSeen)
                            .build();
                } else
                    return BasicPlayer.empty(uuid);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Void> savePlayerDisplayName(UUID uuid, String displayName) {
        return CompletableFuture.runAsync(() -> {
            connect();
            try (PreparedStatement statement = connection.prepareStatement(savePlayerDisplayName)) {
                statement.setString(1, uuid.toString());
                statement.setString(2, displayName);
                statement.setString(3, displayName);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> savePlayerGodMode(UUID uuid, boolean godmode) {
        return CompletableFuture.runAsync(() -> {
            connect();
            try (PreparedStatement statement = connection.prepareStatement(savePlayerGodMode)) {
                statement.setString(1, uuid.toString());
                statement.setBoolean(2, godmode);
                statement.setBoolean(3, godmode);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> savePlayerLastSeen(UUID uuid, long lastSeen) {
        return CompletableFuture.runAsync(() -> {
            connect();
            try (PreparedStatement statement = connection.prepareStatement(savePlayerLastSeen)) {
                statement.setString(1, uuid.toString());
                statement.setLong(2, lastSeen);
                statement.setLong(3, lastSeen);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> deletePlayer(UUID uuid) {
        return null;
    }

    @Override
    public CompletableFuture<BasicWarp> getSpawn() {
        return CompletableFuture.supplyAsync(() -> {
            connect();
            try (PreparedStatement statement = connection.prepareStatement(getSpawn)) {
                statement.setString(1, "spawn");
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    String displayName = result.getString("displayname");
                    String worldUuid = result.getString("world_uuid");
                    String[] coords = result.getString("coords").split(";");
                    return BasicWarp.builder()
                            .location(new Location(Bukkit.getWorld(UUID.fromString(worldUuid)), Double.parseDouble(coords[0]) , Double.parseDouble(coords[1]), Double.parseDouble(coords[2])))
                            .displayName(MiniMessage.markdown().parse(displayName))
                            .warpId("spawn")
                            .build();
                } else
                    return null;
            } catch (SQLException | NumberFormatException throwables) {
                throwables.printStackTrace();
                return null;
            }
        });
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
    public HashMap<String, BasicWarp> getWarps() {
        return null;
    }

    @Override
    public CompletableFuture<Void> saveWarp(BasicWarp basicWarp) {
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



