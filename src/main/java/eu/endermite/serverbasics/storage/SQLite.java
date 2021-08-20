package eu.endermite.serverbasics.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.players.BasicPlayer;
import eu.endermite.serverbasics.util.BasicUtil;
import eu.endermite.serverbasics.util.BasicWarp;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLite implements Database {

    DataSource dataSource;

    private final String playerTable, warpTable, homesTable;
    private final String loadPlayer, savePlayerDisplayName, savePlayerLastSeen, getSpawn, saveWarp,
            getWarps, getHomes, saveHome, deleteWarp, deleteHome, deletePlayer;

    public SQLite(String playerPrefix, String locationsPrefix) {
        HikariConfig config = new HikariConfig();
        String url = ServerBasics.getConfigCache().getSqlPlayersConnectionString();
        config.setJdbcUrl(url);
        config.setMaximumPoolSize(10);

        dataSource = new HikariDataSource(config);
        this.playerTable = playerPrefix + "players";
        this.warpTable = locationsPrefix + "warps";
        this.homesTable = locationsPrefix + "homes";
        createTables();

        loadPlayer = "SELECT * FROM `" + playerTable + "` WHERE `player_uuid` = ?;";
        savePlayerDisplayName = "INSERT INTO `" + playerTable + "` (player_uuid, displayname) VALUES (?, ?) ON CONFLICT(player_uuid) DO UPDATE SET displayname = ?;";
        savePlayerLastSeen = "INSERT INTO `" + playerTable + "` (player_uuid, lastseen) VALUES (?, ?) ON CONFLICT(player_uuid) DO UPDATE SET lastseen = ?;";
        getSpawn = "SELECT * FROM `" + warpTable + "` WHERE `warp_id` = ?;";
        saveWarp = "INSERT INTO `" + warpTable + "` (warp_id, displayname, location, requires_permission) VALUES (?, ?, ?, ?) ON CONFLICT(warp_id) DO UPDATE SET displayname = ?, location = ?, requires_permission = ?;";
        getWarps = "SELECT * FROM `" + warpTable + "`;";
        getHomes = "SELECT * FROM `" + homesTable + "` WHERE `player_uuid` = ?;";
        saveHome = "INSERT INTO `" + homesTable + "` (home_id, player_uuid, displayname, location) VALUES (?, ?, ?, ?) ON CONFLICT(player_uuid) DO UPDATE SET displayname = ?, location = ?;";
        deleteWarp = "DELETE FROM `" + warpTable + "` WHERE warp_id = ?;";
        deleteHome = "DELETE FROM " + homesTable + "` WHERE player_uuid = ?, WHERE home_id = ?;";
        deletePlayer = "DELETE FROM `" + playerTable + "` WHERE player_uuid = ?;";
    }

    public void createTables() {
        try {
            Statement statement = dataSource.getConnection().createStatement();
            String sql;
            sql = "CREATE TABLE IF NOT EXISTS `" + playerTable + "` (`player_uuid` varchar(36) NOT NULL PRIMARY KEY, `displayname` varchar(256), `lastseen` long, `balance` DOUBLE);";
            statement.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS `" + warpTable + "` (`warp_id` varchar(32) UNIQUE PRIMARY KEY, `displayname` varchar(256), `location` varchar(256), `requires_permission` boolean DEFALUT FALSE);";
            statement.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS `" + homesTable + "` (`home_id` varchar(32), `player_uuid` varchar(36), `location` varchar(256), CONSTRAINT COMP_KEY PRIMARY KEY (home_id, player_uuid));";
            statement.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
            ServerBasics.getInstance().getServer().getPluginManager().disablePlugin(ServerBasics.getInstance());
        }
    }

    @Override
    public CompletableFuture<BasicPlayer> getPlayer(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(loadPlayer)) {
                statement.setString(1, uuid.toString());
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    String displayName = result.getString("displayname");
                    long lastSeen = result.getLong("lastseen");
                    if (displayName == null) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                        if (offlinePlayer.hasPlayedBefore())
                            displayName = offlinePlayer.getName();
                        else
                            displayName = "Unknown Player";
                    }

                    return BasicPlayer.builder()
                            .uuid(uuid)
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
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(savePlayerDisplayName)) {
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
    public CompletableFuture<Void> savePlayerLastSeen(UUID uuid, long lastSeen) {
        return CompletableFuture.runAsync(() -> {

            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(savePlayerLastSeen)) {
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
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(deletePlayer)) {
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<BasicWarp> getSpawn() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(getSpawn)) {
                statement.setString(1, "spawn");
                ResultSet result = statement.executeQuery();
                if (result.next()) {
                    String displayName = result.getString("displayname");

                    Location location = BasicUtil.locationFromJson(result.getString("location"));
                    return BasicWarp.builder()
                            .location(location)
                            .displayName(displayName)
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
    public CompletableFuture<Void> saveSpawn(BasicWarp basicWarp) {
        return saveWarp(basicWarp);
    }

    @Override
    public CompletableFuture<Void> deleteSpawn() {
        return deleteWarp("spawn");
    }

    @Override
    public CompletableFuture<HashMap<String, BasicWarp>> getWarps() {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection(); PreparedStatement loadPlayerStatement = connection.prepareStatement(getWarps)) {
                HashMap<String, BasicWarp> warps = new HashMap<>();
                ResultSet result = loadPlayerStatement.executeQuery();
                while (result.next()) {
                    String id = result.getString("warp_id");
                    if (id.equals("spawn")) continue;
                    String displayName = result.getString("displayname");
                    Location location = BasicUtil.locationFromJson(result.getString("location"));
                    boolean requiresPermission = result.getBoolean("requires_permission");
                    BasicWarp basicWarp = BasicWarp.builder()
                            .warpId(id)
                            .location(location)
                            .displayName(displayName)
                            .warpId(id)
                            .requiresPermission(requiresPermission)
                            .build();
                    warps.put(id, basicWarp);
                }
                return warps;
            } catch (SQLException e) {
                e.printStackTrace();
                return new HashMap<>();
            }
        });
    }

    @Override
    public CompletableFuture<Void> saveWarp(BasicWarp basicWarp) {
        return CompletableFuture.runAsync(() -> {
            Location location = basicWarp.getLocation();
            String locationString = BasicUtil.jsonFromLocation(location).toJSONString();
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(saveWarp)) {
                statement.setString(1, basicWarp.getWarpId());
                statement.setString(2, basicWarp.getRawDisplayName());
                statement.setString(3, locationString);
                statement.setBoolean(4, basicWarp.requiresPermission());
                statement.setString(5, basicWarp.getRawDisplayName());
                statement.setString(6, locationString);
                statement.setBoolean(7, basicWarp.requiresPermission());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteWarp(String warpId) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(deleteWarp)) {
                statement.setString(1, warpId);
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<HashMap<String, BasicWarp>> getPlayerHomes(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = dataSource.getConnection(); PreparedStatement loadPlayerStatement = connection.prepareStatement(getHomes)) {
                HashMap<String, BasicWarp> warps = new HashMap<>();
                ResultSet result = loadPlayerStatement.executeQuery();
                while (result.next()) {
                    String id = result.getString("home_id");
                    String displayName = result.getString("displayname");
                    Location location = BasicUtil.locationFromJson(result.getString("location"));
                    BasicWarp basicWarp = BasicWarp.builder()
                            .warpId(id)
                            .location(location)
                            .displayName(displayName)
                            .warpId("spawn")
                            .build();
                    warps.put(id, basicWarp);
                }
                return warps;
            } catch (SQLException e) {
                e.printStackTrace();
                return new HashMap<>();
            }
        });
    }

    @Override
    public CompletableFuture<Void> savePlayerHome(BasicWarp basicWarp, UUID uuid) {
        return CompletableFuture.runAsync(() -> {
            Location location = basicWarp.getLocation();
            String locationString = BasicUtil.jsonFromLocation(location).toJSONString();
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(saveHome)) {
                statement.setString(1, basicWarp.getWarpId());
                statement.setString(2, basicWarp.getRawDisplayName());
                statement.setString(3, locationString);
                statement.setString(4, basicWarp.getRawDisplayName());
                statement.setString(5, locationString);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> deletePlayerHome(UUID uuid, String homeId) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(deleteHome)) {
                statement.setString(1, uuid.toString());
                statement.setString(2, homeId);
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }
}
