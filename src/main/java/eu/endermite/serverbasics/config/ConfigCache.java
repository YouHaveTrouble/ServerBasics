package eu.endermite.serverbasics.config;

import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigCache {

    public String DEFAULT_LANG, CHAT_FORMAT, STAFFCHAT_FORMAT;
    public boolean AUTO_LANG, CUSTOM_JOIN_MSG, CUSTOM_LEAVE_MSG, DISABLE_JOIN_MSG, DISABLE_LEAVE_MSG, CHAT_FORMAT_ENABLED, STAFFCHAT_ENABLED;
    private final String SQL_PLAYERS_CONNECTION_STRING, SQL_PLAYERS_TABLE, SQL_SERVER_CONNECTION_STRING, SQL_SERVER_TABLE;

    public ConfigCache() {
        FileConfiguration config = ServerBasics.getInstance().getConfig();

        this.DEFAULT_LANG = config.getString("language.default-language", "en_us");
        this.AUTO_LANG = config.getBoolean("language.auto-language", true);

        String playerdbType = config.getString("storage.players.type", "sqlite");
        assert playerdbType != null;
        playerdbType = playerdbType.toLowerCase();

        switch (playerdbType) {
            case "mysql":
                String host = config.getString("storage.players.host", "localhost");
                int port = config.getInt("storage.players.port", 3306);
                String database = config.getString("storage.players.database");
                String user = config.getString("storage.players.username");
                String password = config.getString("storage.players.password");
                String connString = "jdbc:mysql://" + host + ":" + port + "/"+database+"?user=" + user + "&password=" + password;
                boolean ssl = config.getBoolean("storage.players.ssl", true);
                if (ssl) {
                    connString = connString + "&useSSL=true";
                } else {
                    connString = connString + "&useSSL=false";
                }
                boolean verify = config.getBoolean("storage.players.verifycertificate", true);
                if (verify) {
                    connString = connString + "&verifyServerCertificate=true";
                } else {
                    connString = connString + "&verifyServerCertificate=false";
                }
                this.SQL_PLAYERS_CONNECTION_STRING = connString;
                break;
            default:
                this.SQL_PLAYERS_CONNECTION_STRING = "jdbc:sqlite:plugins/ServerBasics/users.db";
                break;
        }

        this.SQL_PLAYERS_TABLE = config.getString("storage.players.table", "sbasics_players");

        String serverdbType = config.getString("storage.serverdata.type", "sqlite");
        assert serverdbType != null;
        serverdbType = serverdbType.toLowerCase();

        switch (serverdbType) {
            case "mysql":
                String host = config.getString("storage.serverdata.host", "localhost");
                int port = config.getInt("storage.serverdata.port", 3306);
                String database = config.getString("storage.serverdata.database");
                String user = config.getString("storage.serverdata.username");
                String password = config.getString("storage.serverdata.password");
                String connString = "jdbc:mysql://" + host + ":" + port + "/"+database+"?user=" + user + "&password=" + password;
                boolean ssl = config.getBoolean("storage.serverdata.ssl", true);
                if (ssl) {
                    connString = connString + "&useSSL=true";
                } else {
                    connString = connString + "&useSSL=false";
                }
                boolean verify = config.getBoolean("storage.serverdata.verifycertificate", true);
                if (verify) {
                    connString = connString + "&verifyServerCertificate=true";
                } else {
                    connString = connString + "&verifyServerCertificate=false";
                }
                this.SQL_SERVER_CONNECTION_STRING = connString;
                break;
            default:
                this.SQL_SERVER_CONNECTION_STRING = "jdbc:sqlite:plugins/ServerBasics/serverdata.db";
                break;
        }

        this.SQL_SERVER_TABLE = config.getString("storage.serverdata.table", "sbasics_players");

        this.DISABLE_JOIN_MSG = config.getBoolean("join-leave-messages.disable-join", false);
        this.DISABLE_LEAVE_MSG = config.getBoolean("join-leave-messages.disable-leave", false);

        this.CUSTOM_JOIN_MSG = config.getBoolean("join-leave-messages.custom-join-message", true);
        this.CUSTOM_LEAVE_MSG = config.getBoolean("join-leave-messages.custom-leave-message", true);

        this.CHAT_FORMAT_ENABLED = config.getBoolean("chat.format-enabled", true);
        this.STAFFCHAT_ENABLED = config.getBoolean("chat.staffchat-enabled", true);

        this.CHAT_FORMAT = config.getString("chat.format", "&f<%nickname%&f> %message%");
        this.STAFFCHAT_FORMAT = config.getString("chat.staffchat-format", "&f<%nickname%&f> %message%");

    }

    public String getSqlPlayersConnectionString() {
        return SQL_PLAYERS_CONNECTION_STRING;
    }
    public String getSqlServerConnectionString() {
        return SQL_SERVER_CONNECTION_STRING;
    }
    public String getSqlPlayersTable() {
        return SQL_PLAYERS_TABLE;
    }
    public String getSqlServerTable() {
        return SQL_SERVER_TABLE;
    }
}
