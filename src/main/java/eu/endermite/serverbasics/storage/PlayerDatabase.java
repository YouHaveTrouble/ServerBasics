package eu.endermite.serverbasics.storage;

import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class PlayerDatabase {

    private static String url = ServerBasics.getConfigCache().getSqlConnectionString();

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

}



