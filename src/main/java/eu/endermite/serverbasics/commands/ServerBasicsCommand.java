package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerBasicsCommand {

    public void constructCommand() {
        ServerBasics.getCommandManager().getAnnotationParser().parse(this);
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

    @CommandMethod("serverbasics reload")
    @CommandDescription("Reload all ServerBasics configurations")
    @CommandPermission("serverbasics.command.serverbasics.reload")
    private void commandServerBasicsReloadAll(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.reloadConfigs();
                plugin.reloadLang();
                plugin.reloadLocations();
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    MessageParser.sendMessage(player, ServerBasics.getInstance().getLang(player.getLocale()).ALL_CONFIG_RELOADED);
                } else {
                    MessageParser.sendMessage(sender, ServerBasics.getInstance().getLang(ServerBasics.getConfigCache().DEFAULT_LANG).ALL_CONFIG_RELOADED);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @CommandMethod("serverbasics reload config")
    @CommandDescription("Reload ServerBasics configuration")
    @CommandPermission("serverbasics.command.serverbasics.reload")
    private void commandServerBasicsReloadConf(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.reloadConfigs();
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    MessageParser.sendMessage(player, ServerBasics.getInstance().getLang(player.getLocale()).CONFIG_RELOADED);
                } else {
                    MessageParser.sendMessage(sender, ServerBasics.getInstance().getLang(ServerBasics.getConfigCache().DEFAULT_LANG).CONFIG_RELOADED);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @CommandMethod("serverbasics reload language")
    @CommandDescription("Reload ServerBasics language files")
    @CommandPermission("serverbasics.command.serverbasics.reload")
    private void commandServerBasicsReloadLang(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.reloadLang();
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    MessageParser.sendMessage(player, ServerBasics.getInstance().getLang(player.getLocale()).LANG_RELOADED);
                } else {
                    MessageParser.sendMessage(sender, ServerBasics.getInstance().getLang(ServerBasics.getConfigCache().DEFAULT_LANG).LANG_RELOADED);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @CommandMethod("serverbasics reload locations")
    @CommandDescription("Reload ServerBasics location files")
    @CommandPermission("serverbasics.command.serverbasics.reload")
    private void commandServerBasicsReloadLocations(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.reloadLocations();
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    MessageParser.sendMessage(player, ServerBasics.getInstance().getLang(player.getLocale()).LOCATIONS_RELOADED);
                } else {
                    MessageParser.sendMessage(sender, ServerBasics.getInstance().getLang(ServerBasics.getConfigCache().DEFAULT_LANG).LOCATIONS_RELOADED);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

}
