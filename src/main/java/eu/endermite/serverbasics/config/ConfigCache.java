package eu.endermite.serverbasics.config;

import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigCache {

    public String DEFAULT_LANG;
    public boolean AUTO_LANG, CUSTOM_JOIN_MSG, CUSTOM_LEAVE_MSG;
    private String SQL_CONNECTION_STRING;

    public ConfigCache() {
        FileConfiguration config = ServerBasics.getInstance().getConfig();

        this.DEFAULT_LANG = config.getString("language.default-language", "en_us");
        this.AUTO_LANG = config.getBoolean("language.auto-language", true);

        String dbType = config.getString("storage.type", "sqlite");
        assert dbType != null;
        dbType = dbType.toLowerCase();

        switch (dbType) {
            case "mysql":
                String host = config.getString("storage.host", "localhost");
                int port = config.getInt("storage.port", 3306);
                String user = config.getString("storage.username");
                String password = config.getString("storage.password");
                String connString = "jdbc:mysql://" + host + ":" + port + "/database?user=" + user + "&password=" + password;
                boolean ssl = config.getBoolean("storage.ssl", true);
                if (ssl) {
                    connString = connString + "&useSSL=true";
                }
                this.SQL_CONNECTION_STRING = connString;
                break;
            default:
                this.SQL_CONNECTION_STRING = "jdbc:sqlite:plugins/ServerBasics/users.db";
                break;
        }

        this.CUSTOM_JOIN_MSG = config.getBoolean("join-leave-messages.custom-join-message", true);
        this.CUSTOM_LEAVE_MSG = config.getBoolean("join-leave-messages.custom-leave-message", true);
    }

    public String getSqlConnectionString() {
        return SQL_CONNECTION_STRING;
    }

}
