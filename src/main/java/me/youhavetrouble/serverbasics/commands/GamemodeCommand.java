package me.youhavetrouble.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.commands.registration.CommandRegistration;
import me.youhavetrouble.serverbasics.config.LanguageCache;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import me.youhavetrouble.serverbasics.players.BasicPlayer;
import net.kyori.adventure.text.Component;
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

        if (!player.hasPermission("serverbasics.command.gamemode.self." + gamemode.toString().toLowerCase())
                && !player.hasPermission("serverbasics.command.gamemode.*")
                && !player.hasPermission("serverbasics.command.gamemode.self.*")
        ) {
            MessageParser.sendMessage(player, ServerBasics.getLang(player).gamemode_no_perms);
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
        if (!sender.hasPermission("serverbasics.command.gamemode.others." + gamemode.toString().toLowerCase())
                && !sender.hasPermission("serverbasics.command.gamemode.*")
                && !sender.hasPermission("serverbasics.command.gamemode.others.*")) {

            String msg = String.format(ServerBasics.getLang(sender).gamemode_no_perms_to_set, gamemode);
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
        String msg = ServerBasics.getLang(sender).gamemode_set_many;
        HashMap<String, Component> placeholders = new HashMap<>();
        placeholders.put("%amount%", Component.text(amount));
        placeholders.put("%gamemode%", MessageParser.miniMessage.deserialize(ServerBasics.getLang(sender).getGamemode(gameMode)));
        sender.sendMessage(MessageParser.parseMessage(sender, msg, placeholders));
    }

    private void gamemodeChanged(Player player, GameMode gameMode) {
        LanguageCache lang = ServerBasics.getLang(player);
        String msg = lang.gamemode_changed;
        player.sendMessage(MessageParser.parseMessage(player, msg, "%gamemode%", MessageParser.miniMessage.deserialize(lang.getGamemode(gameMode))));
    }

    private void gamemodeChangedSelf(Player player, GameMode gameMode) {
        LanguageCache lang = ServerBasics.getLang(player.locale());
        String msg = lang.gamemode_changed_self;
        player.sendMessage(MessageParser.parseMessage(player, msg,"%gamemode%", MessageParser.miniMessage.deserialize(lang.getGamemode(gameMode))));
    }

    private void gamemodeChangedOtherSender(CommandSender sender, BasicPlayer basicPlayer, GameMode gameMode) {
        LanguageCache lang = ServerBasics.getLang(sender);
        String msg = lang.gamemode_changed_other;
        HashMap<String, Component> placeholders = new HashMap<>();
        placeholders.put("%name", basicPlayer.getDisplayName());
        placeholders.put("%gamemode%", MessageParser.miniMessage.deserialize(lang.getGamemode(gameMode)));
        sender.sendMessage(MessageParser.parseMessage(sender, msg, placeholders));
    }
}
