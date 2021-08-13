package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.config.LanguageCache;
import eu.endermite.serverbasics.messages.MessageParser;
import eu.endermite.serverbasics.players.BasicPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

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
            MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).gamemode_no_perms);
            return;
        }

        ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> {
            basicPlayer.setGameMode(gamemode);
            gamemodeChangedSelf(player, gamemode);
        });
    }

    @CommandMethod("gamemode <gm> <target>")
    @CommandDescription("Set someones gamemode")
    @CommandPermission("serverbasics.command.gamemode.others")
    private void commandGamemodeOthers(
            final CommandSender sender,
            final @Argument(value = "gm") GameMode gamemode,
            final @Argument(value = "target") MultiplePlayerSelector players
    ) {

        // Permission check
        if (!sender.hasPermission("serverbasics.gamemode.others." + gamemode.toString().toLowerCase())
                && !sender.hasPermission("serverbasics.gamemode.*")
                && !sender.hasPermission("serverbasics.gamemode.others.*")) {
            String msg;
            if (sender instanceof Player player) {
                msg = ServerBasics.getLang(player.locale()).gamemode_no_perms_to_set;
            } else {
                msg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).gamemode_no_perms_to_set;
            }
            msg = String.format(msg, gamemode);
            MessageParser.sendMessage(sender, msg);
        }

        // When no players selected, check for name
        if (!players.hasAny()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(players.getSelector());
            if (!offlinePlayer.hasPlayedBefore()) {
                MessageParser.sendHaventPlayedError(sender);
                return;
            }
            BasicPlayer.fromDatabase(offlinePlayer.getUniqueId()).thenAccept(basicPlayer -> {
                basicPlayer.setGameMode(gamemode);
                gamemodeChangedOtherSender(sender, basicPlayer, gamemode);
            });
            return;
        }

        // When players selected, loop throught them
        for (Player player : players.getPlayers()) {

            // If sender is the target
            if (player == sender) {
                ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> {
                    basicPlayer.setGameMode(gamemode);
                    gamemodeChangedSelf(player, gamemode);
                });
                continue;
            }

            ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> {
                basicPlayer.setGameMode(gamemode);
                gamemodeChanged(player, gamemode);
            });
        }

        // Sender feedback
        // If there was only 1 target
        if (players.getPlayers().size() == 1) {
            Player player = players.getPlayers().get(0);
            if (player != sender)
                ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> gamemodeChangedOtherSender(sender, basicPlayer, gamemode));
        // If there were many targets
        } else {
            gamemodeChangedManyTargets(sender, players.getPlayers().size(), gamemode);
        }
    }

    private void gamemodeChangedManyTargets(CommandSender sender, int amount, GameMode gameMode) {
        LanguageCache lang;
        if (sender instanceof Player player)
            lang = ServerBasics.getLang(player.locale());
        else
            lang = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang);
        String msg = lang.gamemode_set_many;

        HashMap<String, Component> placeholders = new HashMap<>();
        placeholders.put("%amount%", Component.text(amount));
        placeholders.put("%gamemode%", MiniMessage.markdown().parse(lang.getGamemode(gameMode)));
        sender.sendMessage(MessageParser.parseMessage(sender, msg, placeholders));
    }

    private void gamemodeChanged(Player player, GameMode gameMode) {
        LanguageCache lang = ServerBasics.getLang(player.locale());
        String msg = lang.gamemode_changed;
        HashMap<String, Component> placeholders = new HashMap<>();
        placeholders.put("%gamemode%", MiniMessage.markdown().parse(lang.getGamemode(gameMode)));
        player.sendMessage(MessageParser.parseMessage(player, msg, placeholders));
    }

    private void gamemodeChangedSelf(Player player, GameMode gameMode) {
        LanguageCache lang = ServerBasics.getLang(player.locale());
        String msg = lang.gamemode_changed_self;
        HashMap<String, Component> placeholders = new HashMap<>();
        placeholders.put("%gamemode%", MiniMessage.markdown().parse(lang.getGamemode(gameMode)));
        player.sendMessage(MessageParser.parseMessage(player, msg, placeholders));
    }

    private void gamemodeChangedOtherSender(CommandSender sender, BasicPlayer basicPlayer, GameMode gameMode) {
        LanguageCache lang;
        if (sender instanceof Player player)
            lang = ServerBasics.getLang(player.locale());
        else
            lang = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang);
        String msg = lang.gamemode_changed_other;
        HashMap<String, Component> placeholders = new HashMap<>();
        placeholders.put("%name", basicPlayer.getDisplayName());
        placeholders.put("%gamemode%", MiniMessage.markdown().parse(lang.getGamemode(gameMode)));
        sender.sendMessage(MessageParser.parseMessage(sender, msg, placeholders));
    }
}
