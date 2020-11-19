package eu.endermite.serverbasics.config;

import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ConfigCache {

    public String DEFAULT_LANG, CHAT_FORMAT, STAFFCHAT_FORMAT;
    public boolean AUTO_LANG, CUSTOM_JOIN_MSG, CUSTOM_LEAVE_MSG, DISABLE_JOIN_MSG, DISABLE_LEAVE_MSG, CHAT_FORMAT_ENABLED, STAFFCHAT_ENABLED;
    private final String SQL_CONNECTION_STRING;
    private String server_uuid;

    public ConfigCache() {
        FileConfiguration config = ServerBasics.getInstance().getConfig();

        this.DEFAULT_LANG = config.getString("language.default-language", "en_us");
        this.AUTO_LANG = config.getBoolean("language.auto-language", true);

        String playerdbType = config.getString("storage.type", "sqlite");

        playerdbType = playerdbType.toLowerCase();

        switch (playerdbType) {
            case "mysql":
                String host = config.getString("storage.host", "localhost");
                int port = config.getInt("storage.port", 3306);
                String database = config.getString("storage.database");
                String user = config.getString("storage.username");
                String password = config.getString("storage.password");
                String connString = "jdbc:mysql://" + host + ":" + port + "/"+database+"?user=" + user + "&password=" + password;
                boolean ssl = config.getBoolean("storage.ssl", true);
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
                this.SQL_CONNECTION_STRING = connString;
                break;
            default:
                this.SQL_CONNECTION_STRING = "jdbc:sqlite:plugins/ServerBasics/data.db";
                break;
        }

        this.DISABLE_JOIN_MSG = config.getBoolean("join-leave-messages.disable-join", false);
        this.DISABLE_LEAVE_MSG = config.getBoolean("join-leave-messages.disable-leave", false);

        this.CUSTOM_JOIN_MSG = config.getBoolean("join-leave-messages.custom-join-message", true);
        this.CUSTOM_LEAVE_MSG = config.getBoolean("join-leave-messages.custom-leave-message", true);

        this.CHAT_FORMAT_ENABLED = config.getBoolean("chat.format-enabled", true);
        this.STAFFCHAT_ENABLED = config.getBoolean("chat.staffchat-enabled", true);

        this.CHAT_FORMAT = config.getString("chat.format", "&f<%nickname%&f> %message%");
        this.STAFFCHAT_FORMAT = config.getString("chat.staffchat-format", "&f<%nickname%&f> %message%");

        this.server_uuid = config.getString("server-uuid", "<this should generate automatically>");
        if (server_uuid.equals("<this should generate automatically>")) {
            try {
                server_uuid = UUID.randomUUID().toString();
                config.set("server-uuid", server_uuid);
                config.save(new File("/ServerBasics/config"));
            } catch (IOException e) {
                ServerBasics.getInstance().getLogger().severe("Could not save generated server UUID");
            }
        }

    }

    public String getSqlPlayersConnectionString() {
        return SQL_CONNECTION_STRING;
    }
    public String getServerUuid() {
        return server_uuid;
    }
}
