package eu.endermite.serverbasics;

import eu.endermite.serverbasics.config.ConfigCache;
import eu.endermite.serverbasics.config.LanguageCache;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class ServerBasics extends JavaPlugin {

    private static ServerBasics plugin;
    private static ConfigCache configCache;
    private static CommandManager commandManager;
    private static HashMap<String, LanguageCache> languageCacheMap;

    @Override
    public void onEnable() {
        plugin = this;
        reloadConfigs();
        commandManager = new CommandManager();
        commandManager.initCommands();
    }

    private void reloadConfigs() {
        saveDefaultConfig();
        reloadConfig();
        configCache = new ConfigCache();
        languageCacheMap = new HashMap<>();
        try {
            LanguageCache en_us = new LanguageCache("en_us");
            languageCacheMap.put("en_us", en_us);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("Error loading default language file (en_us.yml)! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
        }
    }

    public void reloadConfigs(CommandSender sender) {

    }

    public static ServerBasics getInstance() {
        return plugin;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static ConfigCache getConfigCache() {
        return configCache;
    }

    public LanguageCache getLang(String lang) {
        LanguageCache cache;
         cache = languageCacheMap.get(lang);
        if (cache == null)
            cache = languageCacheMap.get("en_us");
        return cache;
    }
}
