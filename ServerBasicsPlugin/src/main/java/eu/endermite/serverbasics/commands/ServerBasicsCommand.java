package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.hooks.Hook;
import eu.endermite.serverbasics.messages.MessageParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

@CommandRegistration
public class ServerBasicsCommand {

    @CommandMethod("serverbasics version")
    @CommandDescription("Display ServerBasics version")
    @CommandPermission("serverbasics.command.serverbasics")
    private void commandServerBasicsVersion(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        String msg = "&fServerBasics " + plugin.getDescription().getVersion();
        MessageParser.sendMessage(sender, msg);
    }

    @CommandMethod("serverbasics debug")
    @CommandDescription("Display ServerBasics version")
    @CommandPermission("serverbasics.command.serverbasics")
    private void commandServerBasicsDebug(
            final CommandSender sender
    ) {

        MessageParser.sendMessage(sender, "Version: " + Bukkit.getVersion());
        MessageParser.sendMessage(sender, "NMS version: " + Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit", "").replace(".", ""));

        StringBuilder base = new StringBuilder();

        base.append("Hooks (%s): ");

        int hooks = 0;
        for (Map.Entry<String, Hook> e : ServerBasics.getHooks().getSoftwareHooks().entrySet()) {
            if (hooks > 0 && e.getValue().classExists()) {
                base.append(ChatColor.WHITE).append(", ");
            }

            if (e.getValue().classExists()) {
                base.append(ChatColor.GREEN).append(e.getKey());
            } else {
                base.append(ChatColor.RED).append(e.getKey());
            }
            hooks++;

        }
        for (Map.Entry<String, Hook> e : ServerBasics.getHooks().getPluginHooks().entrySet()) {
            if (hooks > 0) {
                base.append(ChatColor.WHITE).append(", ");
            }

            if (e.getValue().pluginEnabled()) {
                base.append(ChatColor.GREEN).append(e.getKey());
            } else {
                base.append(ChatColor.RED).append(e.getKey());
            }
            hooks++;

        }
        String msg = base.toString();
        msg = String.format(msg, hooks);
        MessageParser.sendMessage(sender, msg);
    }

    @CommandMethod("serverbasics reload")
    @CommandDescription("Reload all ServerBasics configurations")
    @CommandPermission("serverbasics.command.serverbasics.reload")
    private void commandServerBasicsReloadAll(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
            plugin.reloadConfigs();
            plugin.reloadLang();
            plugin.reloadLocations();
            if (sender instanceof Player) {
                Player player = (Player) sender;
                MessageParser.sendMessage(player, ServerBasics.getLang(player.getLocale()).all_configs_reloaded);
            } else {
                MessageParser.sendMessage(sender, ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).all_configs_reloaded);
            }
        });
    }

    @CommandMethod("serverbasics reload config")
    @CommandDescription("Reload ServerBasics configuration")
    @CommandPermission("serverbasics.command.serverbasics.reload")
    private void commandServerBasicsReloadConf(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
            plugin.reloadConfigs();
            if (sender instanceof Player) {
                Player player = (Player) sender;
                MessageParser.sendMessage(player, ServerBasics.getLang(player.getLocale()).config_reloaded);
            } else {
                MessageParser.sendMessage(sender, ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).config_reloaded);
            }
        });
    }

    @CommandMethod("serverbasics reload language")
    @CommandDescription("Reload ServerBasics language files")
    @CommandPermission("serverbasics.command.serverbasics.reload")
    private void commandServerBasicsReloadLang(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
            plugin.reloadLang();
            if (sender instanceof Player) {
                Player player = (Player) sender;
                MessageParser.sendMessage(player, ServerBasics.getLang(player.getLocale()).lang_reloaded);
            } else {
                MessageParser.sendMessage(sender, ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).lang_reloaded);
            }

        });
    }

    @CommandMethod("serverbasics reload locations")
    @CommandDescription("Reload ServerBasics location files")
    @CommandPermission("serverbasics.command.serverbasics.reload")
    private void commandServerBasicsReloadLocations(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
            plugin.reloadLocations();
            if (sender instanceof Player) {
                Player player = (Player) sender;
                MessageParser.sendMessage(player, ServerBasics.getLang(player.getLocale()).locations_reloaded);
            } else {
                MessageParser.sendMessage(sender, ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).locations_reloaded);
            }
        });
    }

    @CommandMethod("serverbasics")
    @CommandDescription("Main ServerBasics command")
    @CommandPermission("serverbasics.command.serverbasics")
    private void commandServerBasics(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        String msg = "&fServerBasics " + plugin.getDescription().getVersion();
        MessageParser.sendMessage(sender, msg);
    }

}
