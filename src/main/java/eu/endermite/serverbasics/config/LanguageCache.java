package eu.endermite.serverbasics.config;

import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.List;

public class LanguageCache {

    public String all_configs_reloaded, config_reloaded, lang_reloaded, locations_reloaded, have_to_hold_item,
            healed, healed_by_other, healed_other, healed_many, healed_noone, fed, fed_by_other, fed_other,
            fed_many, fed_noone, item_name_changed, item_lore_changed, custom_join_message, custom_leave_message,
            teleported_spawn, teleported_spawn_other, teleported_spawn_by_other, spawn_set, spawn_not_set, could_not_tp, no_permission,
            started_flying, stopped_flying, gamemode_set_many, gamemode_changed, gamemode_changed_self, gamemode_changed_other, gamemode_no_perms,
            gamemode_no_perms_to_set, havent_played, no_player_selected, hat_set, hat_curse, fixed_hand,
            fixed_hand_other, fixed_inventory, fixed_inventory_other, kick_reason, ban_reason, gamemode_survival,
            gamemode_creative, gamemode_adventure, gamemode_spectator, hooks, hook_inactive, hooks_paper, hook_fix,
            hooks_placeholderapi, tp_noone_to_tp, teleported_self, teleported_to_self, teleported_coords, teleported_by_other,
            invalid_syntax, failed_argument_parse;

    public List<String> kick_message, ban_message, tempban_message;

    public LanguageCache(String lang) {

        FileConfiguration fileConfiguration;

        ServerBasics plugin = ServerBasics.getInstance();

        File langFile = new File(plugin.getDataFolder()+ "/lang", lang+".yml");
        fileConfiguration = new YamlConfiguration();

        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            plugin.saveResource("lang/"+lang+".yml", false);
        }
        try {
            fileConfiguration.load(langFile);

            String defaultMessage = "Message missing";

            this.all_configs_reloaded = fileConfiguration.getString("commands.misc.all-reloaded", defaultMessage);
            this.config_reloaded = fileConfiguration.getString("commands.misc.config-reloaded", defaultMessage);
            this.lang_reloaded = fileConfiguration.getString("commands.misc.lang-reloaded", defaultMessage);
            this.locations_reloaded = fileConfiguration.getString("commands.misc.locations-reloaded", defaultMessage);
            this.could_not_tp = fileConfiguration.getString("commands.misc.could-not-teleport", defaultMessage);
            this.no_player_selected = fileConfiguration.getString("command.misc.no-player-selected", defaultMessage);

            this.have_to_hold_item = fileConfiguration.getString("commands.misc.have-to-hold-item", defaultMessage);
            this.no_permission = fileConfiguration.getString("commands.misc.no-permission", defaultMessage);
            this.invalid_syntax = fileConfiguration.getString("commands.misc.invalid-syntax", defaultMessage);
            this.failed_argument_parse = fileConfiguration.getString("commands.misc.failed-argument-parse", defaultMessage);
            this.havent_played = fileConfiguration.getString("commands.misc.havent-played", defaultMessage);

            this.gamemode_survival = fileConfiguration.getString("gamemodes.survival", "Survival");
            this.gamemode_creative = fileConfiguration.getString("gamemodes.creative", "Creative");
            this.gamemode_adventure = fileConfiguration.getString("gamemodes.adventure", "Adventure");
            this.gamemode_spectator = fileConfiguration.getString("gamemodes.spectator", "Spectator");

            this.teleported_spawn = fileConfiguration.getString("commands.spawn.teleported", defaultMessage);
            this.teleported_spawn_other = fileConfiguration.getString("commands.spawn.teleported-other", defaultMessage);
            this.teleported_spawn_by_other = fileConfiguration.getString("commands.spawn.teleported-by-other", defaultMessage);
            this.spawn_set = fileConfiguration.getString("commands.spawn.set", defaultMessage);
            this.spawn_not_set = fileConfiguration.getString("commands.spawn.not-set", defaultMessage);

            this.healed = fileConfiguration.getString("commands.heal.healed", defaultMessage);
            this.healed_by_other = fileConfiguration.getString("commands.heal.healed-by-other", defaultMessage);
            this.healed_other = fileConfiguration.getString("commands.heal.healed-other", defaultMessage);
            this.healed_many = fileConfiguration.getString("commands.heal.healed-many", defaultMessage);
            this.healed_noone = fileConfiguration.getString("commands.heal.healed-noone", defaultMessage);

            this.fed = fileConfiguration.getString("commands.feed.fed", defaultMessage);
            this.fed_by_other = fileConfiguration.getString("commands.feed.fed-by-other", defaultMessage);
            this.fed_other = fileConfiguration.getString("commands.feed.fed-other", defaultMessage);
            this.fed_many = fileConfiguration.getString("commands.feed.fed-many", defaultMessage);
            this.fed_noone = fileConfiguration.getString("commands.feed.fed-noone", defaultMessage);

            this.item_name_changed = fileConfiguration.getString("commands.itemname.name-changed", defaultMessage);
            this.item_lore_changed = fileConfiguration.getString("commands.itemlore.lore-changed", defaultMessage);

            this.started_flying = fileConfiguration.getString("commands.fly.flight-on", defaultMessage);
            this.stopped_flying = fileConfiguration.getString("commands.fly.flight-off", defaultMessage);

            this.hat_set = fileConfiguration.getString("commands.hat.hat-set", defaultMessage);
            this.hat_curse = fileConfiguration.getString("commands.hat.binding-curse", defaultMessage);

            this.fixed_hand = fileConfiguration.getString("commands.fix.item-fixed", defaultMessage);
            this.fixed_hand_other = fileConfiguration.getString("commands.fix.item-fixed-other", defaultMessage);
            this.fixed_inventory = fileConfiguration.getString("commands.fix.all-fixed", defaultMessage);
            this.fixed_inventory_other = fileConfiguration.getString("commands.fix.all-fixed-other", defaultMessage);

            this.gamemode_set_many = fileConfiguration.getString("commands.gamemode.set_many", defaultMessage);
            this.gamemode_changed = fileConfiguration.getString("commands.gamemode.changed", defaultMessage);
            this.gamemode_changed_self = fileConfiguration.getString("commands.gamemode.changed-self", defaultMessage);
            this.gamemode_changed_other = fileConfiguration.getString("commands.gamemode.changed-other", defaultMessage);
            this.gamemode_no_perms = fileConfiguration.getString("commands.gamemode.no-perms", defaultMessage);
            this.gamemode_no_perms_to_set = fileConfiguration.getString("commands.gamemode.no-perms-to-set", defaultMessage);

            this.tp_noone_to_tp = fileConfiguration.getString("commands.teleport.noone-to-teleport", defaultMessage);
            this.teleported_self = fileConfiguration.getString("commands.teleport.teleported-self", defaultMessage);
            this.teleported_to_self = fileConfiguration.getString("commands.teleport.teleported-to-self", defaultMessage);
            this.teleported_coords = fileConfiguration.getString("commands.teleport.teleported-coords", defaultMessage);
            this.teleported_by_other = fileConfiguration.getString("commands.teleport.teleported-by-other", defaultMessage);

            this.kick_message = fileConfiguration.getStringList("commands.kick.kick-message");
            this.kick_reason = fileConfiguration.getString("commands.kick.default-reason", defaultMessage);

            this.ban_message = fileConfiguration.getStringList("commands.ban.ban-message");
            this.tempban_message = fileConfiguration.getStringList("commands.ban.tempban-message");
            this.ban_reason = fileConfiguration.getString("commands.ban.default-reason", defaultMessage);

            this.custom_join_message = fileConfiguration.getString("custom-join-leave-messages.join", defaultMessage);
            this.custom_leave_message = fileConfiguration.getString("custom-join-leave-messages.leave", defaultMessage);

            this.hooks = fileConfiguration.getString("debug.hooks.hooks", defaultMessage);
            this.hook_inactive = fileConfiguration.getString("debug.hooks.inactive", defaultMessage);
            this.hook_fix = fileConfiguration.getString("debug.hooks.fix", defaultMessage);
            this.hooks_paper = fileConfiguration.getString("debug.hooks.paper", defaultMessage);
            this.hooks_placeholderapi = fileConfiguration.getString("debug.hooks.placeholderapi", defaultMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getGamemode(GameMode gamemode) {
        switch (gamemode) {
            case SURVIVAL:
                return gamemode_survival;
            case CREATIVE:
                return gamemode_creative;
            case ADVENTURE:
                return gamemode_adventure;
            case SPECTATOR:
                return gamemode_spectator;
            default:
                return "";
        }
    }

    public String getHookDesc(String string) {
        switch (string) {
            default:
                return "";
            case "PlaceholderAPI":
                return hooks_placeholderapi;
        }
    }


}
