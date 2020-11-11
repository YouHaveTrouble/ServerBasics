package eu.endermite.serverbasics.storage;

import eu.endermite.serverbasics.players.BasicPlayer;
import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerDatabase {

    private static final String url = ServerBasics.getConfigCache().getSqlPlayersConnectionString();
    private static final String table = ServerBasics.getConfigCache().getSqlPlayersTable();

    /**
     * Checks if plugin connected to database successfully and creates all tables
     * @return True if connected successfully, false if not
     */
    public static boolean checkConnection() {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn == null)
                return false;
            Statement statement = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS `"+table+"` (`" +
                    "player_uuid` varchar(36) UNIQUE PRIMARY KEY, " +
                    "`displayname` varchar(64), " +
                    "`fly` boolean DEFAULT false, " +
                    "`gamemode` varchar(10) " +
                    ");";
            statement.execute(sql);
            ServerBasics.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW+ "Playerdata database connected successfully");
            return true;

        } catch (SQLException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error initializing playerdata database");
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
            String sql = "SELECT * FROM `"+table+"` WHERE `player_uuid` = '" + uuid.toString() + "';";

            ResultSet result = statement.executeQuery(sql);

            BasicPlayer basicPlayer = BasicPlayer.builder()
                    .uuid(uuid)
                    .player(Bukkit.getOfflinePlayer(uuid))
                    .fly(result.getBoolean("fly"))
                    .displayName(result.getString("displayname"))
                    .gameMode(GameMode.valueOf(result.getString("gamemode")))
                    .build();

            return basicPlayer;

        } catch (SQLException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error while loading player data from database");
            return null;
        }
    }

    /**
     * @param basicPlayer BasicPlayer to save
     */
    public static void savePlayertoStorage(BasicPlayer basicPlayer) {

        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection == null)
                return;
            Statement statement = connection.createStatement();
            String sql = "UPDATE `"+table+"` SET " +
                    "fly = "+basicPlayer.canFly()+", " +
                    "displayname = '"+basicPlayer.getDisplayName()+"', " +
                    "gamemode = '"+basicPlayer.getGameMode().toString()+"' " +
                    "WHERE `player_uuid` = '"+basicPlayer.getUuid().toString()+"';";
            statement.execute(sql);

        } catch (SQLException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error while loading player data from database");
        }
    }

    /**
     * @param basicPlayer BasicPlayer to save
     */
    public static void createPlayerStorage(BasicPlayer basicPlayer) {

        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection == null)
                return;
            Statement statement = connection.createStatement();
            String sql = "INSERT INTO "+table+" (player_uuid, fly, displayname, gamemode) VALUES(" +
                    "'"+basicPlayer.getUuid().toString()+"', " +
                    ""+basicPlayer.canFly()+", " +
                    "'"+basicPlayer.getDisplayName()+"', " +
                    "'"+basicPlayer.getGameMode().toString()+"');";
            statement.execute(sql);

        } catch (SQLException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error while loading player data from database");
        }
    }

    public static void saveSingleOption(UUID uuid, String dbRow, Object value) {
        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection == null)
                return;

            DatabaseRow.valueOf(dbRow.toUpperCase());
            dbRow = dbRow.toLowerCase();

            if (value instanceof String)
                value = "'"+value+"'";

            Statement statement = connection.createStatement();
            String sql = "UPDATE "+table+" SET "+dbRow+" = "+ value +" " +
                    "WHERE `player_uuid` = '"+uuid.toString()+"';";
            statement.execute(sql);

        } catch (SQLException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error while updating player data in database");
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Provided player database row does not exist");
        }
    }
    public static void saveSingleOption(BasicPlayer basicPlayer, String dbRow, Object value) {
        UUID uuid = basicPlayer.getUuid();
        saveSingleOption(uuid, dbRow, value);
    }

    public static Object getSingleOption(UUID uuid, String dbRow) {
        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection == null)
                return null;

            DatabaseRow.valueOf(dbRow.toUpperCase());
            dbRow = dbRow.toLowerCase();

            Statement statement = connection.createStatement();
            String sql = "SELECT `"+dbRow+"` FROM "+table+" WHERE `player_uuid` = '"+uuid.toString()+"';";
            ResultSet rs = statement.executeQuery(sql);

            if(rs.next()) {
                return rs.getObject(dbRow);
            }
            return null;

        } catch (SQLException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error while updating player data in database");
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Provided player database row does not exist");
        }
        return null;
    }

    public static boolean playerExists(UUID uuid) {
        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection == null)
                return false;

            Statement statement = connection.createStatement();
            String sql = "SELECT `player_uuid` from "+table+" WHERE `player_uuid` = '"+uuid.toString()+"';";

            ResultSet rs = statement.executeQuery(sql);

            if(rs.next()) {
                if (rs.getString("player_uuid").equals(uuid.toString())) {
                    connection.close();
                    return true;
                }
            }
            return false;

        } catch (SQLException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error connecting to player database");
            return false;
        }
    }

    public enum DatabaseRow {
        FLY, DISPLAYNAME, GAMEMODE
    }

}



