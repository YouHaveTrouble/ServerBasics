package eu.endermite.serverbasics;

import eu.endermite.serverbasics.listeners.CustomJoinLeaveMessageListener;
import eu.endermite.serverbasics.listeners.FeatureListener;
import eu.endermite.serverbasics.players.BasicPlayerCache;
import eu.endermite.serverbasics.storage.Database;
import eu.endermite.serverbasics.storage.MySQL;
import eu.endermite.serverbasics.chat.ChatListener;
import eu.endermite.serverbasics.config.ConfigCache;
import eu.endermite.serverbasics.config.LanguageCache;
import eu.endermite.serverbasics.config.LocationsCache;
import eu.endermite.serverbasics.hooks.Hooks;
import eu.endermite.serverbasics.listeners.HatListener;
import eu.endermite.serverbasics.storage.SQLite;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;


import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServerBasics extends JavaPlugin {

    private static ServerBasics instance;
    private static ConfigCache configCache;
    private static LocationsCache locationsCache;
    private static CommandManager commandManager;
    private static HashMap<String, LanguageCache> languageCacheMap;
    private static BasicPlayerCache basicPlayers;
    private static Hooks hooks;
    private Database database;

    @Override
    public void onEnable() {

        String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);

        // nms is a bitch and I don't care enough to support multiple versions
        if (!version.equals("v1_17_R1")) {
            this.getLogger().severe("Could not find support for server version "+version);
            this.setEnabled(false);
            return;
        }

        instance = this;
        reloadConfigs();
        reloadLang();
        hooks = new Hooks();
        commandManager = new CommandManager();
        commandManager.initCommands();

        String tablePrefix = configCache.getDatabaseTablePrefix();
        switch (configCache.databaseType) {
            case MYSQL:
                database = new MySQL(tablePrefix);
                break;
            case SQLITE:
                database = new SQLite(tablePrefix);
                break;
        }

        basicPlayers = new BasicPlayerCache();
        reloadLocations();

        //getServer().getPluginManager().registerEvents(new CustomJoinLeaveMessageListener(), this);
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
            File langDirectory = new File(instance.getDataFolder()+ "/lang");
            Files.createDirectories(langDirectory.toPath());
            getDefaultLanguageFiles().forEach((fileName)->{ // ensure default files first (or else we get errors second)
                String localeString = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
                getLogger().info(String.format("Found language file for %s", localeString));
                LanguageCache langCache = new LanguageCache(localeString);
                languageCacheMap.put(localeString, langCache);
            });
            Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);
            for (File langFile : langDirectory.listFiles()) {
                Matcher langMatcher = langPattern.matcher(langFile.getName());
                if (langMatcher.find()) {
                    String localeString = langMatcher.group(1).toLowerCase();
                    if(!languageCacheMap.containsKey(localeString)) { // make sure it wasn't a default file that we already loaded
                        getLogger().info(String.format("Found language file for %s", localeString));
                        LanguageCache langCache = new LanguageCache(localeString);
                        languageCacheMap.put(localeString, langCache);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().severe("Error loading language files! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
        }
    }

    private Set<String> getDefaultLanguageFiles(){
        Reflections reflections = new Reflections("lang", new ResourcesScanner());
        return reflections.getResources(Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)"));
    }

    public void reloadLocations() {
        locationsCache = new LocationsCache();
    }

    private static LanguageCache getLang(String lang) {
        LanguageCache cache;
         cache = languageCacheMap.get(lang);
        if (cache == null)
            cache = languageCacheMap.get(configCache.default_lang);
        return cache;
    }

    public static LanguageCache getLang(Locale locale) {
        return getLang(locale.toString().toLowerCase());
    }

    public static ServerBasics getInstance() {
        return instance;
    }

    public static ConfigCache getConfigCache() {
        return configCache;
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

    public Database getDatabase() {
        return database;
    }

}