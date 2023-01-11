package me.youhavetrouble.serverbasics;

import me.youhavetrouble.serverbasics.economy.BasicEconomy;
import me.youhavetrouble.serverbasics.economy.BasicVaultHandler;
import me.youhavetrouble.serverbasics.hooks.PlaceholderAPIHook;
import me.youhavetrouble.serverbasics.listeners.FeatureListener;
import me.youhavetrouble.serverbasics.listeners.SpawnListener;
import me.youhavetrouble.serverbasics.players.BasicPlayerCache;
import me.youhavetrouble.serverbasics.storage.Database;
import me.youhavetrouble.serverbasics.storage.mysql.MySQL;
import me.youhavetrouble.serverbasics.chat.ChatListener;
import me.youhavetrouble.serverbasics.config.ConfigCache;
import me.youhavetrouble.serverbasics.config.LanguageCache;
import me.youhavetrouble.serverbasics.config.LocationsCache;
import me.youhavetrouble.serverbasics.hooks.Hooks;
import me.youhavetrouble.serverbasics.listeners.HatListener;
import me.youhavetrouble.serverbasics.storage.SQLite;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.File;
import java.io.IOException;
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
    private static HashMap<String, LanguageCache> languageCacheMap;
    private static BasicPlayerCache basicPlayers;
    private static BasicEconomy basicEconomy;
    private static Hooks hooks;
    private Database database;

    @Override
    public void onEnable() {

        String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);

        // nms is a bitch and I don't care enough to support multiple versions
        if (!version.equals("v1_19_R1")) {
            this.getLogger().severe("Could not find support for server version "+version);
            this.setEnabled(false);
            return;
        }

        instance = this;
        reloadConfigs();
        reloadLang();
        hooks = new Hooks();
        CommandManager commandManager = new CommandManager();
        commandManager.initCommands();

        String playerPrefix = configCache.getDatabasePlayerTablePrefix();
        String locationsPrefix = configCache.getDatabaseLocationsTablePrefix();

        switch (configCache.databaseType) {
            case MYSQL -> database = new MySQL(playerPrefix, locationsPrefix);
            case SQLITE -> database = new SQLite(playerPrefix, locationsPrefix);
        }

        basicPlayers = new BasicPlayerCache();
        reloadLocations();

        getServer().getPluginManager().registerEvents(new FeatureListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new HatListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(), this);

        if (hooks.isHooked("PlaceholderAPI")) {
            new PlaceholderAPIHook().register();
        }
        if (hooks.isHooked("Vault")) {
            basicEconomy = new BasicEconomy(this);
            getServer().getServicesManager().register(Economy.class, new BasicVaultHandler(), this, ServicePriority.Lowest);
            basicEconomy.activateEconomy();
            getLogger().info("Vault economy support enabled!");
        }

    }

    @Override
    public void onDisable() {
        if (basicEconomy == null || !basicEconomy.isBasicEconomy()) return;
        // save the balances before shutdown
        basicEconomy.getAccounts().forEach(basicEconomyAccount -> database.saveBalance(basicEconomyAccount.getUuid(), basicEconomyAccount.getBalance()));
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
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().severe("Error loading language files! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
        }
    }

    private Set<String> getDefaultLanguageFiles(){
        Reflections reflections = new Reflections("lang", Scanners.Resources);
        return reflections.getResources(Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)"));
    }

    public void reloadLocations() {
        locationsCache = new LocationsCache();
    }

    public static LanguageCache getLang(String lang) {
        lang = lang.replace("-", "_");
        if (configCache.auto_lang) {
            return languageCacheMap.getOrDefault(lang, languageCacheMap.get(configCache.default_lang.toString().toLowerCase()));
        } else {
            return languageCacheMap.get(configCache.default_lang.toString().toLowerCase());
        }
    }

    public static LanguageCache getLang(Locale locale) {
        return getLang(locale.toString().toLowerCase());
    }

    public static LanguageCache getLang(CommandSender commandSender) {
        if (commandSender instanceof Player player) {
            return getLang(player.locale());
        } else {
            return getLang(configCache.default_lang);
        }
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

    public static BasicEconomy getBasicEconomy() {
        return basicEconomy;
    }
    public static Hooks getHooks() {
        return hooks;
    }

    public Database getDatabase() {
        return database;
    }

}