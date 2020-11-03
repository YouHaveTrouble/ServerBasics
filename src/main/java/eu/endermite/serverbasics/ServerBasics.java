package eu.endermite.serverbasics;

import eu.endermite.serverbasics.config.ConfigCache;
import eu.endermite.serverbasics.config.LanguageCache;
import eu.endermite.serverbasics.config.LocationsCache;
import eu.endermite.serverbasics.listeners.CustomJoinLeaveMessageListener;
import eu.endermite.serverbasics.listeners.FlyListener;
import eu.endermite.serverbasics.listeners.PlayerCacheListener;
import eu.endermite.serverbasics.players.BasicPlayerCache;
import eu.endermite.serverbasics.storage.PlayerDatabase;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;

public final class ServerBasics extends JavaPlugin {

    private static ServerBasics plugin;
    private static ConfigCache configCache;
    private static LocationsCache locationsCache;
    private static CommandManager commandManager;
    private static HashMap<String, LanguageCache> languageCacheMap;
    private static BasicPlayerCache basicPlayers;

    @Override
    public void onEnable() {
        plugin = this;
        reloadConfigs();
        reloadLang();
        reloadLocations();
        commandManager = new CommandManager();
        commandManager.initCommands();

        PlayerDatabase.checkConnection();

        basicPlayers = new BasicPlayerCache();

        getServer().getPluginManager().registerEvents(new CustomJoinLeaveMessageListener(), this);
        getServer().getPluginManager().registerEvents(new FlyListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerCacheListener(), this);

    }

    public void reloadConfigs() {
        saveDefaultConfig();
        reloadConfig();
        configCache = new ConfigCache();
    }

    public void reloadLang() {
        languageCacheMap = new HashMap<>();
        try {
            //TODO scan through all **_** files in /lang directory
            LanguageCache en_us = new LanguageCache("en_us");
            languageCacheMap.put("en_us", en_us);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("Error loading default language file (en_us.yml)! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
        }
    }

    public void reloadLocations() {
        locationsCache = new LocationsCache();
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

    public static LanguageCache getLang(String lang) {
        LanguageCache cache;
         cache = languageCacheMap.get(lang);
        if (cache == null)
            cache = languageCacheMap.get(configCache.DEFAULT_LANG);
        return cache;
    }

    public static LocationsCache getLocationsCache() {
        return locationsCache;
    }

    public static BasicPlayerCache getBasicPlayers() {
        return basicPlayers;
    }
}