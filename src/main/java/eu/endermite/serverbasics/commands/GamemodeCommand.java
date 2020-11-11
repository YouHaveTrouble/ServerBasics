package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import eu.endermite.serverbasics.storage.PlayerDatabase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand {

    public void constructCommand() {
        ServerBasics.getCommandManager().getAnnotationParser().parse(this);
    }

    @CommandMethod("gamemode <gm>")
    @CommandDescription("Set your gamemode")
    @CommandPermission("serverbasics.command.gamemode")
    private void commandGamemode(
            final Player player,
            final @Argument(value = "gm") GameMode gamemode
    ) {

        if (!player.hasPermission("serverbasics.gamemode." + gamemode.toString().toLowerCase()) && !player.hasPermission("serverbasics.gamemode.*")) {
            MessageParser.sendDefaultTranslatedError(player, "debug.creative_spectator.error", TextColor.color(255, 255, 255));
        }

        ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).setGameMode(gamemode);
        PlayerDatabase.saveSingleOption(player.getUniqueId(), "gamemode", gamemode.toString());
        Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> player.setGameMode(gamemode));
        final Component message = Component.translatable(
                "commands.gamemode.success.self",
                NamedTextColor.WHITE,
                Component.translatable("gameMode." + gamemode.toString().toLowerCase(), NamedTextColor.WHITE)
        );
        ServerBasics.getCommandManager().bukkitAudiences.player(player).sendMessage(message);
    }

    @CommandMethod("gamemode <gm> <target>")
    @CommandDescription("Set someones gamemode")
    @CommandPermission("serverbasics.command.gamemode.others")
    private void commandGamemodeOthers(
            final CommandSender sender,
            final @Argument(value = "gm") GameMode gamemode,
            final @Argument(value = "target") MultiplePlayerSelector players
    ) {

        if (!sender.hasPermission("serverbasics.gamemode." + gamemode.toString().toLowerCase()) && !sender.hasPermission("serverbasics.gamemode.*")) {
            MessageParser.sendDefaultTranslatedError(sender, "debug.creative_spectator.error", TextColor.color(255, 255, 255));
        }

        if (!players.hasAny()) {
            try {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(players.getSelector());
                if (!PlayerDatabase.playerExists(offlinePlayer.getUniqueId())) {
                    sendHaventPlayedError(sender);
                    return;
                }
                PlayerDatabase.saveSingleOption(offlinePlayer.getUniqueId(), "gamemode", gamemode.toString());
                String offlineName = (String) PlayerDatabase.getSingleOption(offlinePlayer.getUniqueId(), "displayname");

                Component message = Component.translatable(
                        "commands.gamemode.success.other",
                        NamedTextColor.WHITE,
                        Component.text(ChatColor.translateAlternateColorCodes('&', offlineName)),
                        Component.translatable("gameMode." + gamemode.toString().toLowerCase())
                );
                ServerBasics.getCommandManager().bukkitAudiences.sender(sender).sendMessage(message);
                return;

            } catch (Exception e) {
                sendHaventPlayedError(sender);
            }
            return;
        }

        for (Player player : players.getPlayers()) {
            ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).setGameMode(gamemode);
            PlayerDatabase.saveSingleOption(player.getUniqueId(), "gamemode", gamemode.toString());
            Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> player.setGameMode(gamemode));

            if (player == sender) {
                Component message = Component.translatable(
                        "commands.gamemode.success.self",
                        NamedTextColor.WHITE,
                        Component.translatable("gameMode." + gamemode.toString().toLowerCase())
                );
                ServerBasics.getCommandManager().bukkitAudiences.sender(sender).sendMessage(message);
                continue;
            }

            final Component message = Component.translatable(
                    "gameMode.changed",
                    NamedTextColor.WHITE,
                    Component.translatable("gameMode." + gamemode.toString().toLowerCase(), NamedTextColor.WHITE)
            );
            ServerBasics.getCommandManager().bukkitAudiences.player(player).sendMessage(message);

        }

        if (players.getPlayers().size() == 1) {

            Player player = players.getPlayers().get(0);
            Component message;

            if (sender instanceof Player) {

                message = Component.translatable(
                        "commands.gamemode.success.other",
                        NamedTextColor.WHITE,
                        Component.text(player.getDisplayName()),
                        Component.translatable("gameMode." + gamemode.toString().toLowerCase())
                );
                ServerBasics.getCommandManager().bukkitAudiences.sender(sender).sendMessage(message);
                return;
            }
            message = Component.translatable(
                    "commands.gamemode.success.other",
                    NamedTextColor.WHITE,
                    Component.text(player.getDisplayName()),
                    Component.translatable("gameMode." + gamemode.toString().toLowerCase())
            );
            ServerBasics.getCommandManager().bukkitAudiences.sender(sender).sendMessage(message);
        } else {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Component message = Component.translatable(
                        ServerBasics.getLang(player.getLocale()).GAMEMODE_SET_MANY,
                        NamedTextColor.WHITE,
                        Component.text(players.getPlayers().size()),
                        Component.translatable("gameMode." + gamemode.toString().toLowerCase())
                );
                ServerBasics.getCommandManager().bukkitAudiences.sender(sender).sendMessage(message);
            } else {
                Component message = Component.translatable(
                        ServerBasics.getLang(ServerBasics.getConfigCache().DEFAULT_LANG).GAMEMODE_SET_MANY,
                        NamedTextColor.WHITE,
                        Component.text(players.getPlayers().size()),
                        Component.translatable("gameMode." + gamemode.toString().toLowerCase())
                );
                ServerBasics.getCommandManager().bukkitAudiences.sender(sender).sendMessage(message);
            }
        }
    }

    private void sendHaventPlayedError(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String msg = ServerBasics.getLang(player.getLocale()).HAVENT_PLAYED;
            MessageParser.sendMessage(player, msg);
        } else {
            String msg = ServerBasics.getLang(ServerBasics.getConfigCache().DEFAULT_LANG).HAVENT_PLAYED;
            MessageParser.sendMessage(sender, msg);
        }
    }
}
