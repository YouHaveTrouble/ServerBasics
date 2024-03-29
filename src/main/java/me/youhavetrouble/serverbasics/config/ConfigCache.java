package me.youhavetrouble.serverbasics.config;

import me.youhavetrouble.serverbasics.ServerBasics;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

public class ConfigCache {

    public final String chat_format, staffchat_format, currencySymbol;
    public final Locale default_lang;
    public final boolean auto_lang, custom_join_msg, custom_leave_msg, disable_join_msg, disable_leave_msg,
            chat_format_enabled, staffchat_enabled, spawn_on_join;
    private final String sql_connection_string, database_player_table_prefix, database_server_table_prefix;
    public final DatabaseType databaseType;
    public final long economySaveInterval;
    public final int baltopSize, baltopRefreshInterval, fractionalDigits;

    public ConfigCache() {
        FileConfiguration config = ServerBasics.getInstance().getConfig();

        this.default_lang = Locale.forLanguageTag(config.getString("language.default-language", "en-us").replace("_", "-"));
        this.auto_lang = config.getBoolean("language.auto-language", true);

        String playerdbType = config.getString("storage.type", "sqlite");

        playerdbType = playerdbType.toUpperCase();
        DatabaseType databaseType;
        try {
            databaseType = DatabaseType.valueOf(playerdbType);
        } catch (IllegalArgumentException e) {
            databaseType = DatabaseType.SQLITE;
        }
        this.databaseType = databaseType;

        switch (databaseType) {
            case MYSQL -> {
                String host = config.getString("storage.host", "localhost");
                int port = config.getInt("storage.port", 3306);
                String database = config.getString("storage.database");
                String user = config.getString("storage.username");
                String password = config.getString("storage.password");
                String connString = "jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + user + "&password=" + password;
                boolean ssl = config.getBoolean("storage.ssl", true);
                connString = connString + "&useSSL=" + ssl;
                boolean verify = config.getBoolean("storage.players.verifycertificate", true);
                connString = connString + "&verifyServerCertificate=" + verify;
                this.sql_connection_string = connString;
            }
            default -> this.sql_connection_string = "jdbc:sqlite:plugins/ServerBasics/data.db";
        }

        this.database_player_table_prefix = config.getString("storage.player_table_prefix", "sbasics_");
        this.database_server_table_prefix = config.getString("storage.server_table_prefix", "sbasics_");

        this.disable_join_msg = config.getBoolean("join-leave-messages.disable-join", false);
        this.disable_leave_msg = config.getBoolean("join-leave-messages.disable-leave", false);

        this.custom_join_msg = config.getBoolean("join-leave-messages.custom-join-message", true);
        this.custom_leave_msg = config.getBoolean("join-leave-messages.custom-leave-message", true);

        this.chat_format_enabled = config.getBoolean("chat.format-enabled", true);
        this.staffchat_enabled = config.getBoolean("chat.staffchat-enabled", true);

        this.chat_format = config.getString("chat.format", "&f<%nickname%&f> %message%");
        this.staffchat_format = config.getString("chat.staffchat-format", "&f<%nickname%&f> %message%");

        this.spawn_on_join = config.getBoolean("spawn.players-always-join-spawn", false);

        this.economySaveInterval = config.getLong("economy.save-interval", 60);
        this.baltopSize = config.getInt("economy.baltop.size", 10);
        this.baltopRefreshInterval = config.getInt("economy.baltop.refresh", 30);
        this.currencySymbol = config.getString("economy.currency.symbol", "$");
        this.fractionalDigits = config.getInt("economy.corrency.fractional-digits", 2);

    }

    public String getSqlPlayersConnectionString() {
        return sql_connection_string;
    }

    public String getDatabasePlayerTablePrefix() {
        return database_player_table_prefix;
    }

    public String getDatabaseLocationsTablePrefix() {
        return database_server_table_prefix;
    }

    public enum DatabaseType {
        MYSQL, SQLITE
    }
}
