package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import eu.endermite.serverbasics.NMSHandler;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.storage.PlayerDatabase;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandRegistration
public class GamemodeCommand {

    @CommandMethod("gamemode <gm>")
    @CommandDescription("Set your gamemode")
    @CommandPermission("serverbasics.command.gamemode.self")
    private void commandGamemode(
            final Player player,
            final @Argument(value = "gm") GameMode gamemode
    ) {

        if (!player.hasPermission("serverbasics.gamemode.self." + gamemode.toString().toLowerCase())
                && !player.hasPermission("serverbasics.gamemode.*")
                && !player.hasPermission("serverbasics.gamemode.self.*")
        ) {
            MessageParser.sendMessage(player, ServerBasics.getLang(player.getLocale()).gamemode_no_perms);
        }

        Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> player.setGameMode(gamemode));

        String msg = ServerBasics.getLang(player.getLocale()).gamemode_changed_self;

        msg = String.format(msg, ServerBasics.getLang(player.getLocale()).getGamemode(gamemode));
        MessageParser.sendMessage(player, msg);
    }

    @CommandMethod("gamemode <gm> <target>")
    @CommandDescription("Set someones gamemode")
    @CommandPermission("serverbasics.command.gamemode.others")
    private void commandGamemodeOthers(
            final CommandSender sender,
            final @Argument(value = "gm") GameMode gamemode,
            final @Argument(value = "target") MultiplePlayerSelector players
    ) {

        if (!sender.hasPermission("serverbasics.gamemode.others." + gamemode.toString().toLowerCase())
                && !sender.hasPermission("serverbasics.gamemode.*")
                && !sender.hasPermission("serverbasics.gamemode.others.*")) {
            String msg = "";
            if (sender instanceof Player) {
                Player player = (Player) sender;
                msg = ServerBasics.getLang(player.getLocale()).gamemode_no_perms_to_set;
            } else {
                msg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).gamemode_no_perms_to_set;
            }
            msg = String.format(msg, gamemode);
            MessageParser.sendMessage(sender, msg);
        }

        if (!players.hasAny()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(players.getSelector());
            if (offlinePlayer == null) {
                MessageParser.sendHaventPlayedError(sender);
                return;
            }
            String offlineName = (String) PlayerDatabase.getSingleOption(offlinePlayer.getUniqueId(), "displayname");
            NMSHandler.setOfflinePlayerGamemode(offlinePlayer, gamemode);

            String msg;
            if (sender instanceof Player) {
                Player player = (Player) sender;
                msg = ServerBasics.getLang(player.getLocale()).gamemode_changed_other;
                msg = String.format(
                        msg,
                        ChatColor.translateAlternateColorCodes('&', offlineName),
                        ServerBasics.getLang(player.getLocale()).getGamemode(gamemode)
                );
            } else {
                msg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).gamemode_changed_other;
                msg = String.format(
                        msg,
                        ChatColor.translateAlternateColorCodes('&', offlineName),
                        ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).getGamemode(gamemode)
                );
            }
            MessageParser.sendMessage(sender, msg);
            return;
        }

        for (Player player : players.getPlayers()) {
            Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> player.setGameMode(gamemode));

            if (player == sender) {
                String msg = ServerBasics.getLang(player.getLocale()).gamemode_changed_self;
                msg = String.format(msg, ServerBasics.getLang(player.getLocale()).getGamemode(gamemode));
                MessageParser.sendMessage(player, msg);
                continue;
            }

            String msg = ServerBasics.getLang(player.getLocale()).gamemode_changed;
            msg = String.format(msg, ServerBasics.getLang(player.getLocale()).getGamemode(gamemode));
            MessageParser.sendMessage(player, msg);

        }

        if (players.getPlayers().size() == 1) {

            Player player = players.getPlayers().get(0);
            String msg;

            if (player != sender) {
                if (sender instanceof Player) {
                    msg = ServerBasics.getLang(player.getLocale()).gamemode_changed_other;
                    msg = String.format(
                            msg,
                            ChatColor.translateAlternateColorCodes('&', player.getDisplayName()),
                            ServerBasics.getLang(player.getLocale()).getGamemode(gamemode)
                    );
                    MessageParser.sendMessage(sender, msg);
                } else {
                    msg = ServerBasics.getLang(player.getLocale()).gamemode_changed_other;
                    msg = String.format(
                            msg,
                            ChatColor.translateAlternateColorCodes('&', player.getDisplayName()),
                            ServerBasics.getLang(player.getLocale()).getGamemode(gamemode)
                    );
                }
                MessageParser.sendMessage(sender, msg);
            }
        } else {
            String msg;
            if (sender instanceof Player) {
                Player player = (Player) sender;
                msg = ServerBasics.getLang(player.getLocale()).gamemode_set_many;
                msg = String.format(msg, players.getPlayers().size(), ServerBasics.getLang(player.getLocale()).getGamemode(gamemode));
            } else {
                msg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).gamemode_set_many;
                msg = String.format(msg, players.getPlayers().size(), ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).getGamemode(gamemode));
            }
            MessageParser.sendMessage(sender, msg);
        }
    }
}
