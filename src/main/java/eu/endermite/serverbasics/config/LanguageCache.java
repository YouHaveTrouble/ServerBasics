package eu.endermite.serverbasics.config;

import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class LanguageCache {

    public String ALL_CONFIG_RELOADED, CONFIG_RELOADED, LANG_RELOADED, LOCATIONS_RELOADED, HAVE_TO_HOLD_ITEM,
            HEALED, HEALED_BY_OTHER, HEALED_OTHER, HEALED_MANY, HEALED_NOONE, FED, FED_BY_OTHER, FED_OTHER,
            FED_MANY, FED_NOONE, ITEM_NAME_CHANGED, ITEM_LORE_CHANGED, CUSTOM_JOIN_MSG, CUSTOM_LEAVE_MSG,
            TPD_SPAWN, TPD_SPAWN_OTHER, TPD_SPAWN_BY_OTHER, SPAWN_SET, SPAWN_NOT_SET, COULD_NOT_TP, NO_PERMISSION,
            STARTED_FLYING, STOPPED_FLYING, GAMEMODE_SET_MANY, HAVENT_PLAYED;

    public LanguageCache(String lang) {

        FileConfiguration fileConfiguration;

        ServerBasics plugin = ServerBasics.getInstance();

        File langFile = new File(plugin.getDataFolder()+"/lang", lang+".yml");
        fileConfiguration = new YamlConfiguration();

        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            plugin.saveResource("lang/"+lang+".yml", false);
        }
        try {
            fileConfiguration.load(langFile);

            String defaultMessage = "Message missing";

            this.ALL_CONFIG_RELOADED = fileConfiguration.getString("commands.misc.all-reloaded", defaultMessage);
            this.CONFIG_RELOADED = fileConfiguration.getString("commands.misc.config-reloaded", defaultMessage);
            this.LANG_RELOADED = fileConfiguration.getString("commands.misc.lang-reloaded", defaultMessage);
            this.LOCATIONS_RELOADED = fileConfiguration.getString("commands.misc.locations-reloaded", defaultMessage);
            this.COULD_NOT_TP = fileConfiguration.getString("commands.misc.could-not-teleport", defaultMessage);

            this.HAVE_TO_HOLD_ITEM = fileConfiguration.getString("commands.misc.have-to-hold-item", defaultMessage);
            this.NO_PERMISSION = fileConfiguration.getString("commands.misc.no-permission", defaultMessage);
            this.HAVENT_PLAYED = fileConfiguration.getString("commands.misc.havent-played", defaultMessage);

            this.TPD_SPAWN = fileConfiguration.getString("commands.spawn.teleported", defaultMessage);
            this.TPD_SPAWN_OTHER = fileConfiguration.getString("commands.spawn.teleported-other", defaultMessage);
            this.TPD_SPAWN_BY_OTHER = fileConfiguration.getString("commands.spawn.teleported-by-other", defaultMessage);
            this.SPAWN_SET = fileConfiguration.getString("commands.spawn.set", defaultMessage);
            this.SPAWN_NOT_SET = fileConfiguration.getString("commands.spawn.not-set", defaultMessage);

            this.HEALED = fileConfiguration.getString("commands.heal.healed", defaultMessage);
            this.HEALED_BY_OTHER = fileConfiguration.getString("commands.heal.healed-by-other", defaultMessage);
            this.HEALED_OTHER = fileConfiguration.getString("commands.heal.healed-other", defaultMessage);
            this.HEALED_MANY = fileConfiguration.getString("commands.heal.healed-many", defaultMessage);
            this.HEALED_NOONE = fileConfiguration.getString("commands.heal.healed-noone", defaultMessage);

            this.FED = fileConfiguration.getString("commands.feed.fed", defaultMessage);
            this.FED_BY_OTHER = fileConfiguration.getString("commands.feed.fed-by-other", defaultMessage);
            this.FED_OTHER = fileConfiguration.getString("commands.feed.fed-other", defaultMessage);
            this.FED_MANY = fileConfiguration.getString("commands.feed.fed-many", defaultMessage);
            this.FED_NOONE = fileConfiguration.getString("commands.feed.fed-noone", defaultMessage);

            this.ITEM_NAME_CHANGED = fileConfiguration.getString("commands.itemname.name-changed", defaultMessage);
            this.ITEM_LORE_CHANGED = fileConfiguration.getString("commands.itemlore.lore-changed", defaultMessage);

            this.STARTED_FLYING = fileConfiguration.getString("commands.fly.flight-on", defaultMessage);
            this.STOPPED_FLYING = fileConfiguration.getString("commands.fly.flight-off", defaultMessage);

            this.GAMEMODE_SET_MANY = fileConfiguration.getString("commands.gamemode.set_many", defaultMessage);


            this.CUSTOM_JOIN_MSG = fileConfiguration.getString("custom-join-leave-messages.join", defaultMessage);
            this.CUSTOM_LEAVE_MSG = fileConfiguration.getString("custom-join-leave-messages.leave", defaultMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
