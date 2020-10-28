package eu.endermite.serverbasics.config;

import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigCache {

    public String DEFAULT_LANG;
    public boolean AUTO_LANG, CUSTOM_JOIN_MSG, CUSTOM_LEAVE_MSG;

    public ConfigCache() {
        FileConfiguration config = ServerBasics.getInstance().getConfig();

        ConfigurationSection language = config.getConfigurationSection("language");

        this.DEFAULT_LANG = language.getString("default-language", "en_us");
        this.AUTO_LANG = language.getBoolean("auto-language", true);

        ConfigurationSection customMessages = config.getConfigurationSection("join-leave-messages");

        this.CUSTOM_JOIN_MSG = customMessages.getBoolean("custom-join-message", true);
        this.CUSTOM_LEAVE_MSG = customMessages.getBoolean("custom-leave-message", true);

    }

}
