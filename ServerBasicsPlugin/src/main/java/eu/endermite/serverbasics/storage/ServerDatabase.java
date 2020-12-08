package eu.endermite.serverbasics.storage;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.locations.SBasicLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class ServerDatabase {

    private static final String url = ServerBasics.getConfigCache().getSqlPlayersConnectionString();

    /**
     * Checks if plugin connected to database successfully and creates all tables
     * @return True if connected successfully, false if not
     */
    public static boolean checkConnection() {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn == null)
                return false;
            Statement statement = conn.createStatement();
            String createSpawn = "CREATE TABLE IF NOT EXISTS `sbasics_spawns` (`" +
                    "server_uuid` varchar(36) UNIQUE PRIMARY KEY, " +
                    "`location` json " +
                    ");";
            statement.execute(createSpawn);
            String createWarp = "CREATE TABLE IF NOT EXISTS `sbasics_warps` (`" +
                    "server_uuid` varchar(36), " +
                    "`location` json, " +
                    "`name` varchar(64) " +
                    ");";
            statement.execute(createWarp);
            String createHome = "CREATE TABLE IF NOT EXISTS `sbasics_homes` (`" +
                    "server_uuid` varchar(36), " +
                    "`player_uuid` varchar(36), " +
                    "`location` json, " +
                    "`name` varchar(64) " +
                    ");";
            statement.execute(createHome);

            ServerBasics.getInstance().getLogger().log(Level.INFO, ChatColor.YELLOW+ "Serverdata database connected successfully");
            return true;

        } catch (SQLException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error initializing serverdata database");
            e.printStackTrace();
            return false;
        }
    }

    public static SBasicLocation getSpawn(String serveruuid) {
        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection == null)
                return null;
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM sbasics_spawns WHERE `server_uuid` = '"+serveruuid+"';";
            ResultSet rs = statement.executeQuery(sql);
            String locationJsonString = "";
            if(rs.next()) {
                locationJsonString = rs.getString("location");
            }
            if (locationJsonString.equals(""))
                return null;
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(locationJsonString);
            String worldString = (String) json.get("world");
            double x = (double) json.get("x");
            double y = (double) json.get("y");
            double z = (double) json.get("z");
            Double pitch = (Double) json.get("pitch");
            Double yaw = (Double) json.get("yaw");
            World world = Bukkit.getWorld(worldString);
            Location location = new Location(world, x, y, z, yaw.floatValue(), pitch.floatValue());
            SBasicLocation sBasicLocation = new SBasicLocation(location, "Spawn");
            return sBasicLocation;
        } catch (SQLException | ParseException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error while getting spawn data from database");
            e.printStackTrace();
            return null;
        }
    }

    public static void createSpawn(SBasicLocation location) {
        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection == null)
                return;
            String serverUuid = ServerBasics.getConfigCache().getServerUuid();
            JSONObject json = new JSONObject();
            json.put("world", location.getLocation().getWorld().getName());
            json.put("x", location.getLocation().getX());
            json.put("y", location.getLocation().getY());
            json.put("z", location.getLocation().getZ());
            json.put("pitch", location.getLocation().getPitch());
            json.put("yaw", location.getLocation().getYaw());
            Statement statement = connection.createStatement();
            String sql = "INSERT INTO `sbasics_spawns` (server_uuid, location) VALUES ('" + serverUuid + "', '" + json.toJSONString() + "');";
            statement.execute(sql);
        } catch (SQLException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error while updating spawn data in database");
            e.printStackTrace();
        }
    }

    public static void updateSpawn(SBasicLocation location) {

        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection == null)
                return;

            String serverUuid = ServerBasics.getConfigCache().getServerUuid();

            JSONObject json = new JSONObject();
            json.put("world", location.getLocation().getWorld().getName());
            json.put("x", location.getLocation().getX());
            json.put("y", location.getLocation().getY());
            json.put("z", location.getLocation().getZ());
            json.put("pitch", location.getLocation().getPitch());
            json.put("yaw", location.getLocation().getYaw());

            Statement statement = connection.createStatement();
            String sql = "UPDATE `sbasics_spawns` SET " +
                    "location = '"+json.toJSONString()+"' " +
                    "WHERE `server_uuid` = '"+serverUuid+"';";
            statement.execute(sql);

        } catch (SQLException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error while loading spawn data from database");
        }
    }

    public static boolean spawnExists() {
        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection == null)
                return false;
            String serverId = ServerBasics.getConfigCache().getServerUuid();
            Statement statement = connection.createStatement();
            String sql = "SELECT `server_uuid` from sbasics_spawns WHERE `server_uuid` = '"+serverId+"';";
            ResultSet rs = statement.executeQuery(sql);

            if(rs.next()) {
                if (rs.getString("server_uuid").equals(serverId)) {
                    connection.close();
                    return true;
                }
            }
            return false;

        } catch (SQLException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error connecting to serverdata database");
            return false;
        }
    }

    public static void saveSpawn(SBasicLocation location) {
        if (spawnExists()) {
            updateSpawn(location);
        } else {
            createSpawn(location);
        }
    }

    public static boolean homeExists(UUID playerUuid, String homeName) {
        try (Connection connection = DriverManager.getConnection(url)) {
            if (connection == null)
                return false;
            String serverId = ServerBasics.getConfigCache().getServerUuid();
            Statement statement = connection.createStatement();
            String sql = "SELECT `player_uuid` from sbasics_homes WHERE server_uuid = '"+serverId+"' AND player_uuid = '"+playerUuid.toString()+"' AND name = '"+homeName+"';";
            ResultSet rs = statement.executeQuery(sql);

            if(rs.next()) {
                if (rs.getString("player_uuid").equals(playerUuid.toString())) {
                    connection.close();
                    return true;
                }
            }
            return false;

        } catch (SQLException e) {
            ServerBasics.getInstance().getLogger().severe(ChatColor.RED + "Error connecting to serverdata database");
            return false;
        }
    }



}
