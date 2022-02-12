package me.youhavetrouble.serverbasics.config;

import me.youhavetrouble.serverbasics.ServerBasics;
import org.bukkit.GameMode;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class LanguageCache {

    private final FileConfiguration fileConfiguration;
    boolean addedMissing = false;

    public String all_configs_reloaded, config_reloaded, lang_reloaded, locations_reloaded, have_to_hold_item,
            healed, healed_by_other, healed_other, healed_many, healed_noone, fed, fed_by_other, fed_other,
            fed_many, fed_noone, item_name_changed, item_lore_changed, custom_join_message, custom_leave_message,
            teleported_spawn, teleported_spawn_other, teleported_spawn_by_other, spawn_set, spawn_not_set, spawn_unset,
            could_not_tp, no_permission, started_flying, stopped_flying, gamemode_set_many, gamemode_changed,
            gamemode_changed_self, gamemode_changed_other, gamemode_no_perms, gamemode_no_perms_to_set, havent_played,
            no_player_selected, hat_set, hat_curse, fixed_hand, fixed_hand_other, fixed_inventory, fixed_inventory_other,
            kick_reason, ban_reason, gamemode_survival, gamemode_creative, gamemode_adventure, gamemode_spectator, hooks,
            hook_inactive, hook_fix, hooks_vault, hooks_placeholderapi, tp_noone_to_tp, teleported_self,
            teleported_to_self, teleported_coords, teleported_by_other, invalid_syntax, failed_argument_parse,
            unknown_player, started_flying_other, stopped_flying_other, console_name, nick_self, nick_other,
            nick_changed_by_other, nick_only_same_as_name, warped, warp_cant_use_name, warp_set, warp_exists,
            warp_doesnt_exist, warp_displayname_set, warp_removed, warp_perm_on, warp_perm_off, econ_disabled, balance,
            balance_other, balance_set, balance_add, balance_got, balance_deducted, negative_value, baltop_title,
            baltop_format, baltop_empty, cant_tp_to_offline, item_enchanted, no_item_to_enchant, no_unsafe_enchant,
            cannot_execute_as, executed_command_as;

    public List<String> kick_message, ban_message;

    public LanguageCache(String lang) {
        ServerBasics plugin = ServerBasics.getInstance();
        File langFile = new File(plugin.getDataFolder() + "/lang", lang + ".yml");
        fileConfiguration = new YamlConfiguration();

        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            plugin.saveResource("lang/" + lang + ".yml", false);
        }
        try {
            fileConfiguration.load(langFile);
            String defaultMessage = "Message missing";

            this.all_configs_reloaded = getStringTranslation("commands.misc.all-reloaded", "All configurations reloaded");
            this.config_reloaded = getStringTranslation("commands.misc.config-reloaded", "Configuration reloaded");
            this.lang_reloaded = getStringTranslation("commands.misc.lang-reloaded", "Language files reloaded");
            this.locations_reloaded = getStringTranslation("commands.misc.locations-reloaded", "Locations reloaded");
            this.could_not_tp = getStringTranslation("commands.misc.could-not-teleport", "Could not teleport");
            this.no_player_selected = getStringTranslation("command.misc.no-player-selected", "No player selected");
            this.unknown_player = getStringTranslation("command.misc.unknown-player", "Unknown Player");
            this.console_name = getStringTranslation("command.misc.console-name", "Console");

            this.have_to_hold_item = getStringTranslation("commands.misc.have-to-hold-item", "You have to hold an item in your hand.");
            this.no_permission = getStringTranslation("commands.misc.no-permission", "You do not have permission to do this");
            this.invalid_syntax = getStringTranslation("commands.misc.invalid-syntax", "<red>Invalid command syntax. Correct syntax: <gray>%s");
            this.failed_argument_parse = getStringTranslation("commands.misc.failed-argument-parse", "<red>Failed to parse command arguments");
            this.havent_played = getStringTranslation("commands.misc.havent-played", "Player with that name hasn't played here");

            this.econ_disabled = getStringTranslation("commands.money.economy-disabled", "<red>Economy is disabled!");
            this.balance = getStringTranslation("commands.money.balance", "Your balance is %balance%");
            this.balance_other = getStringTranslation("commands.money.balance-other", "Balance of %player% is %balance%");
            this.balance_set = getStringTranslation("commands.money.balance-set", "Balance of %player% set to %amount%");
            this.balance_add = getStringTranslation("commands.money.balance-added", "Added %amount% to %player%'s balance");
            this.balance_got = getStringTranslation("commands.money.balance-got", "You got %amount%");
            this.balance_deducted = getStringTranslation("commands.money.balance-deducted", "%amount% was deducted from your account");
            this.negative_value = getStringTranslation("commands.money.negative-value", "<red>You cannot use negative values here");

            this.baltop_title = getStringTranslation("commands.baltop.title", "---------- Baltop ----------");
            this.baltop_format = getStringTranslation("commands.baltop.format", "<white>%place%. %name% <white>- %balance%");
            this.baltop_empty = getStringTranslation("commands.baltop.empty", "Nothing here.");

            this.gamemode_survival = getStringTranslation("gamemodes.survival", "Survival Mode");
            this.gamemode_creative = getStringTranslation("gamemodes.creative", "Creative Mode");
            this.gamemode_adventure = getStringTranslation("gamemodes.adventure", "Adventure Mode");
            this.gamemode_spectator = getStringTranslation("gamemodes.spectator", "Spectator Mode");

            this.teleported_spawn = getStringTranslation("commands.spawn.teleported", "Teleported to spawn");
            this.teleported_spawn_other = getStringTranslation("commands.spawn.teleported-other", "You teleported %s to spawn");
            this.teleported_spawn_by_other = getStringTranslation("commands.spawn.teleported-by-other", "You have been sent to spawn by %s");
            this.spawn_set = getStringTranslation("commands.spawn.set", "Spawn has been set at your location");
            this.spawn_not_set = getStringTranslation("commands.spawn.not-set", "Spawn not set! Ask server administrators to do that!");
            this.spawn_unset = getStringTranslation("commands.spawn.unset", "Spawn not set!");

            this.warped = getStringTranslation("commands.warp.warped", "You warped to %warp%");
            this.warp_cant_use_name = getStringTranslation("commands.warp.cannot-use-name", "\"Spawn\" is a reserved name and you cannot use it as warp name. Use /setspawn instead");
            this.warp_set = getStringTranslation("commands.warp.set", "Warp %warp% created at your location");
            this.warp_exists = getStringTranslation("commands.warp.already-exists", "Warp with id \"%warp%\" already exists. Use /editwarp to edit or remove it");
            this.warp_doesnt_exist = getStringTranslation("commands.warp.doesnt-exist", "Warp %warp% doesn't exist");
            this.warp_displayname_set = getStringTranslation("commands.warp.displayname-set", "Set %warp%'s displayname to %displayname%");
            this.warp_removed = getStringTranslation("comands.warp.removed", "Warp %warp% removed");
            this.warp_perm_on = getStringTranslation("commands.warp.requires-permission-true", "Warp %warp% now requires permission to access");
            this.warp_perm_off = getStringTranslation("commands.warp.requires-permission-false", "Warp %warp% doesn't require permission to access now");

            this.healed = getStringTranslation("commands.heal.healed", "You have been healed");
            this.healed_by_other = getStringTranslation("commands.heal.healed-by-other", "You have been healed by %player%");
            this.healed_other = getStringTranslation("commands.heal.healed-other", "You healed %player%");
            this.healed_many = getStringTranslation("commands.heal.healed-many", "You healed %amount% players");
            this.healed_noone = getStringTranslation("commands.heal.healed-noone", "You healed noone");

            this.fed = getStringTranslation("commands.feed.fed", "You have been fed");
            this.fed_by_other = getStringTranslation("commands.feed.fed-by-other", "You have been fed by %player%");
            this.fed_other = getStringTranslation("commands.feed.fed-other", "You fed %player%");
            this.fed_many = getStringTranslation("commands.feed.fed-many", "You fed %amount% players");
            this.fed_noone = getStringTranslation("commands.feed.fed-noone", "You fed noone");

            this.item_name_changed = getStringTranslation("commands.itemname.name-changed", "You changed the name of the the item you're holding");
            this.item_lore_changed = getStringTranslation("commands.itemlore.lore-changed", "You changed the lore of the item you're holding");

            this.nick_self = getStringTranslation("commands.nick.changed-self", "You changed your nickname to %nickname%");
            this.nick_other = getStringTranslation("commands.nick.changed-other", "You changed %oldnickname%'s nickname to %newnickname%");
            this.nick_changed_by_other = getStringTranslation("commands.nick.changed-by-other", "Your nickname was changed to %nickname%");
            this.nick_only_same_as_name = getStringTranslation("commands.nick.only-colors", "You are only allowed to add colors to your name");

            this.started_flying = getStringTranslation("commands.fly.flight-on", "You toggled fly on");
            this.stopped_flying = getStringTranslation("commands.fly.flight-off", "You toggled fly off");
            this.started_flying_other = getStringTranslation("commands.fly.flight-on-other", "You toggled fly on for %player%");
            this.stopped_flying_other = getStringTranslation("commands.fly.flight-off-other", "You toggled fly off for %player%");

            this.hat_set = getStringTranslation("commands.hat.hat-set", "You changed your hat!");
            this.hat_curse = getStringTranslation("commands.hat.binding-curse", "Your current hat has curse of binding!");

            this.fixed_hand = getStringTranslation("commands.fix.item-fixed", "Held item has been fixed");
            this.fixed_hand_other = getStringTranslation("commands.fix.item-fixed-other", "Fixed item in %s's hand");
            this.fixed_inventory = getStringTranslation("commands.fix.all-fixed", "All items have been fixed");
            this.fixed_inventory_other = getStringTranslation("commands.fix.all-fixed-other", "Fixed all items in %s's inventory");

            this.gamemode_set_many = getStringTranslation("commands.gamemode.set_many", "Set %number% players gamemode to %gamemode%");
            this.gamemode_changed = getStringTranslation("commands.gamemode.changed", "Your gamemode was set to %gamemode%");
            this.gamemode_changed_self = getStringTranslation("commands.gamemode.changed-self", "You set your own gamemode to %gamemode%");
            this.gamemode_changed_other = getStringTranslation("commands.gamemode.changed-other", "You set %name%'s gamemode to %gamemode%");
            this.gamemode_no_perms = getStringTranslation("commands.gamemode.no-perms", "You don't have permission to use this gamemode");
            this.gamemode_no_perms_to_set = getStringTranslation("commands.gamemode.no-perms-to-set", "You don't have permission to set someone to this gamemode");

            this.tp_noone_to_tp = getStringTranslation("commands.teleport.noone-to-teleport", "No entities to teleport");
            this.teleported_self = getStringTranslation("commands.teleport.teleported-self", "You teleported to %entity%'s location");
            this.teleported_to_self = getStringTranslation("commands.teleport.teleported-to-self", "You teleported %name% to your location");
            this.teleported_coords = getStringTranslation("commands.teleport.teleported-coords", "You teleported to %coords%");
            this.teleported_by_other = getStringTranslation("commands.teleport.teleported-by-other", "You have been teleported");
            this.cant_tp_to_offline = getStringTranslation("commands.teleport.cant-tp-to-offline", "Player is not online");

            this.item_enchanted = getStringTranslation("commands.enchant.item-enchanted", "Item enchanted");
            this.no_item_to_enchant = getStringTranslation("commands.enchant.no-item", "No item to enchant");
            this.no_unsafe_enchant = getStringTranslation("commands.enchant.no-unsafe", "You cannot apply unsafe enchants");

            this.cannot_execute_as = getStringTranslation("commands.execute.cannot-as", "Cannot execute commands as this player");
            this.executed_command_as = getStringTranslation("commands.execute.executed-as", "Executed %command% as %player%");

            this.kick_message = getStringListTranslation("commands.kick.kick-message", List.of("You have been kicked from the server", "Reason: %reason%"));
            this.kick_reason = getStringTranslation("commands.kick.default-reason", "Kicked by a moderator");

            this.ban_message = getStringListTranslation("commands.ban.ban-message", List.of("%reason%"));
            this.ban_reason = getStringTranslation("commands.ban.default-reason", "Banned by a moderator");

            this.custom_join_message = getStringTranslation("custom-join-leave-messages.join", "<yellow>%nickname% <yellow>joined the game");
            this.custom_leave_message = getStringTranslation("custom-join-leave-messages.leave", "<yellow>%nickname% <yellow>left the game");

            this.hooks = getStringTranslation("debug.hooks.hooks", "Hooks");
            this.hook_inactive = getStringTranslation("debug.hooks.inactive", "<red>Inactive. Install %s to benefit from this");
            this.hook_fix = getStringTranslation("debug.hooks.fix", defaultMessage);
            this.hooks_placeholderapi = getStringTranslation("debug.hooks.placeholderapi", "Parsing placeholders in messages");
            this.hooks_vault = getStringTranslation("debug.hooks.vault", "Economy support");

            if (addedMissing) fileConfiguration.save(langFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            ServerBasics.getInstance().getLogger().warning("Translation file " + langFile + " is not formatted properly. Skipping it.");
        }
    }

    public String getStringTranslation(String path, String defaultTranslation) {
        String translation = fileConfiguration.getString(path);
        if (translation == null) {
            fileConfiguration.set(path, defaultTranslation);
            addedMissing = true;
            return defaultTranslation;
        }
        return translation;
    }

    public List<String> getStringListTranslation(String path, List<String> defaultTranslation) {
        List<String> translation = fileConfiguration.getStringList(path);
        if (translation.isEmpty()) {
            fileConfiguration.set(path, defaultTranslation);
            addedMissing = true;
            return defaultTranslation;
        }
        return translation;
    }

    public String getGamemode(GameMode gamemode) {
        return switch (gamemode) {
            case SURVIVAL -> gamemode_survival;
            case CREATIVE -> gamemode_creative;
            case ADVENTURE -> gamemode_adventure;
            case SPECTATOR -> gamemode_spectator;
        };
    }

    public String getHookDesc(String string) {
        if ("PlaceholderAPI".equals(string)) {
            return hooks_placeholderapi;
        }
        if ("Vault".equals(string)) {
            return hooks_vault;
        }
        return "";
    }

}
