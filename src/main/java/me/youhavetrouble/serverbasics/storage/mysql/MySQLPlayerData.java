package me.youhavetrouble.serverbasics.storage.mysql;

import me.youhavetrouble.serverbasics.messages.MessageParser;
import me.youhavetrouble.serverbasics.players.BasicPlayer;
import me.youhavetrouble.serverbasics.storage.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLPlayerData implements PlayerData {

    private final DataSource dataSource;
    private final String loadBasicPlayer, saveBasicPlayer;

    protected MySQLPlayerData(DataSource dataSource, String playerTable) {
        this.dataSource = dataSource;
        this.loadBasicPlayer = "SELECT * FROM ` " + playerTable + "` WHERE `player_uuid` = ?;";
        this.saveBasicPlayer = "INSERT INTO `" + playerTable + "` (player_uuid, displayname, lastseen) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE displayname = ?, lastseen = ?;";
    }

    @Override
    public CompletableFuture<BasicPlayer> loadBasicPlayer(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (
                    Connection connection = dataSource.getConnection();
                    PreparedStatement statement = connection.prepareStatement(loadBasicPlayer)
            ) {
                statement.setString(1, uuid.toString());
                ResultSet result = statement.executeQuery();
                if (!result.next()) {
                    return BasicPlayer.empty(uuid);
                }
                String displayName = result.getString("displayname");
                long lastSeen = result.getLong("lastseen");
                if (displayName == null) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    if (offlinePlayer.hasPlayedBefore() && offlinePlayer.getName() != null)
                        displayName = offlinePlayer.getName();
                    else
                        displayName = "Unknown Player";
                }
                return new BasicPlayer(uuid, displayName, lastSeen);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Void> saveBasicPlayer(BasicPlayer basicPlayer) {
        return CompletableFuture.runAsync(() -> {
            try (
                    Connection connection = dataSource.getConnection();
                    PreparedStatement statement = connection.prepareStatement(saveBasicPlayer)
            ) {
                statement.setString(1, basicPlayer.getUuid().toString());
                statement.setString(2, basicPlayer.getRawDisplayName());
                statement.setDouble(3, basicPlayer.getLastSeen());
                statement.setString(4, basicPlayer.getRawDisplayName());
                statement.setDouble(5, basicPlayer.getLastSeen());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
