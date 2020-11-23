package eu.endermite.serverbasics;

import eu.endermite.serverbasics.chat.ChatListener;
import eu.endermite.serverbasics.config.ConfigCache;
import eu.endermite.serverbasics.config.LanguageCache;
import eu.endermite.serverbasics.config.LocationsCache;
import eu.endermite.serverbasics.hooks.Hooks;
import eu.endermite.serverbasics.listeners.CustomJoinLeaveMessageListener;
import eu.endermite.serverbasics.listeners.FeatureListener;
import eu.endermite.serverbasics.listeners.HatListener;
import eu.endermite.serverbasics.players.BasicPlayerCache;
import eu.endermite.serverbasics.storage.PlayerDatabase;
import eu.endermite.serverbasics.storage.ServerDatabase;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServerBasics extends JavaPlugin {

    private static ServerBasics plugin;
    private static ConfigCache configCache;
    private static LocationsCache locationsCache;
    private static CommandManager commandManager;
    private static HashMap<String, LanguageCache> languageCacheMap;
    private static BasicPlayerCache basicPlayers;
    private static Hooks hooks;

    @Override
    public void onEnable() {
        plugin = this;
        hooks = new Hooks();
        reloadConfigs();
        reloadLang();
        commandManager = new CommandManager();
        commandManager.initCommands();

        PlayerDatabase.checkConnection();
        ServerDatabase.checkConnection();

        basicPlayers = new BasicPlayerCache();
        reloadLocations();


        getServer().getPluginManager().registerEvents(new CustomJoinLeaveMessageListener(), this);
        getServer().getPluginManager().registerEvents(new FeatureListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new HatListener(), this);


    }

    public void reloadConfigs() {
        saveDefaultConfig();
        reloadConfig();
        configCache = new ConfigCache();
    }

    public void reloadLang() {
        languageCacheMap = new HashMap<>();
        try {
            File langDirectory = new File(plugin.getDataFolder()+"/lang");
            Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);
            for (File langFile : langDirectory.listFiles()) {
                Matcher langMatcher = langPattern.matcher(langFile.getName());
                if (langMatcher.find()){
                    String localeString = langMatcher.group(1);
                    getLogger().info(String.format("Found language file for %s", localeString));
                    LanguageCache langCache = new LanguageCache(localeString);
                    languageCacheMap.put(localeString, langCache);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("Error loading language files! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
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
            cache = languageCacheMap.get(configCache.default_lang);
        return cache;
    }
    public static LocationsCache getLocationsCache() {
        return locationsCache;
    }
    public static BasicPlayerCache getBasicPlayers() {
        return basicPlayers;
    }

    public static Hooks getHooks() {
        return hooks;
    }

}