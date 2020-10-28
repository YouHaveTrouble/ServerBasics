package eu.endermite.serverbasics.config;

import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LanguageCache {

    public String CONFIG_RELOADED, LANG_RELOADED, HAVE_TO_HOLD_ITEM, HEALED, HEALED_BY_OTHER, HEALED_OTHER, HEALED_MANY, HEALED_NOONE, FED, FED_BY_OTHER, FED_OTHER,
            FED_MANY, FED_NOONE, ITEM_NAME_CHANGED, ITEM_LORE_CHANGED, CUSTOM_JOIN_MSG, CUSTOM_LEAVE_MSG;

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

            this.CONFIG_RELOADED = fileConfiguration.getString("commands.misc.config-reloaded");
            this.LANG_RELOADED = fileConfiguration.getString("commands.misc.lang-reloaded");

            this.HAVE_TO_HOLD_ITEM = fileConfiguration.getString("commands.misc.have-to-hold-item");

            this.HEALED = fileConfiguration.getString("commands.heal.healed");
            this.HEALED_BY_OTHER = fileConfiguration.getString("commands.heal.healed-by-other");
            this.HEALED_OTHER = fileConfiguration.getString("commands.heal.healed-other");
            this.HEALED_MANY = fileConfiguration.getString("commands.heal.healed-many");
            this.HEALED_NOONE = fileConfiguration.getString("commands.heal.healed-noone");

            this.FED = fileConfiguration.getString("commands.feed.fed");
            this.FED_BY_OTHER = fileConfiguration.getString("commands.feed.fed-by-other");
            this.FED_OTHER = fileConfiguration.getString("commands.feed.fed-other");
            this.FED_MANY = fileConfiguration.getString("commands.feed.fed-many");
            this.FED_NOONE = fileConfiguration.getString("commands.feed.fed-noone");

            this.ITEM_NAME_CHANGED = fileConfiguration.getString("commands.itemname.name-changed");
            this.ITEM_LORE_CHANGED = fileConfiguration.getString("commands.itemlore.lore-changed");

            this.CUSTOM_JOIN_MSG = fileConfiguration.getString("custom-join-leave-messages.join");
            this.CUSTOM_LEAVE_MSG = fileConfiguration.getString("custom-join-leave-messages.leave");

        } catch (Exception e) {
            e.printStackTrace();
        }



    }


}
