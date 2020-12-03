package eu.endermite.serverbasics;

import eu.endermite.serverbasics.api.NMS;
import eu.endermite.serverbasics.listeners.CustomJoinLeaveMessageListener;
import eu.endermite.serverbasics.listeners.FeatureListener;
import eu.endermite.serverbasics.listeners.WorldSaveListener;
import eu.endermite.serverbasics.players.BasicPlayerCache;
import eu.endermite.serverbasics.storage.PlayerDatabase;
import eu.endermite.serverbasics.chat.ChatListener;
import eu.endermite.serverbasics.config.ConfigCache;
import eu.endermite.serverbasics.config.LanguageCache;
import eu.endermite.serverbasics.config.LocationsCache;
import eu.endermite.serverbasics.hooks.Hooks;
import eu.endermite.serverbasics.listeners.HatListener;
import eu.endermite.serverbasics.storage.ServerDatabase;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ServerBasics extends JavaPlugin {

    private NMS nmsHandler;

    @Getter private static ServerBasics instance;
    @Getter private static ConfigCache configCache;
    @Getter private static LocationsCache locationsCache;
    @Getter private static CommandManager commandManager;
    private static HashMap<String, LanguageCache> languageCacheMap;
    @Getter private static BasicPlayerCache basicPlayers;
    @Getter private static Hooks hooks;

    @Override
    public void onEnable() {

        instance = this;
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
        getServer().getPluginManager().registerEvents(new WorldSaveListener(), this);

    }

    public void reloadConfigs() {
        saveDefaultConfig();
        reloadConfig();
        configCache = new ConfigCache();
    }

    public void reloadLang() {
        languageCacheMap = new HashMap<>();
        try {
            File langDirectory = new File(instance.getDataFolder()+"/lang");
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
                if (langMatcher.find()){
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

    public static LanguageCache getLang(String lang) {
        LanguageCache cache;
         cache = languageCacheMap.get(lang);
        if (cache == null)
            cache = languageCacheMap.get(configCache.default_lang);
        return cache;
    }


}