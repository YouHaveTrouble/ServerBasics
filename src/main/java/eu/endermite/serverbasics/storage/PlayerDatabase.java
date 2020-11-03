package eu.endermite.serverbasics.storage;

import eu.endermite.serverbasics.BasicPlayer;
import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.ChatColor;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerDatabase {

    private static String url = ServerBasics.getConfigCache().getSqlConnectionString();

    /**
     * Checks if plugin connected to database successfully and creates all tables
     * @return True if connected successfully, false if not
     */
    public static boolean checkConnection() {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn == null)
                return false;
            Statement statement = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS `players` (`" +
                    "player_uuid` varchar(36) UNIQUE PRIMARY KEY, " +
                    "`displayname` varchar(64), " +
                    "`fly` boolean DEFAULT false " +
                    ");";
            statement.execute(sql);
            ServerBasics.getInstance().getLogger().log(Level.FINE, "Database connected successfully");
            return true;

        } catch (SQLException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error initializing database");
            return false;
        }
    }

    /**
     * @param uuid UUID of player of whose data to retrieve
     * @return Player data from database
     */
    public static BasicPlayer getPlayerfromStorage(UUID uuid) {

        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection == null)
                return null;
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM `players` WHERE `player_uuid` = '" + uuid.toString() + "';";
            statement.executeQuery(sql);
            ResultSet result = statement.getResultSet();

            BasicPlayer basicPlayer = BasicPlayer.builder()
                    .fly(result.getBoolean("fly"))
                    .displayName(result.getString("displayname"))
                    .build();

            return basicPlayer;

        } catch (SQLException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error while loading player data from database");
            return null;
        }
    }

}



