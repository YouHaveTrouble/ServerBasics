package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.bukkit.BukkitCommandSender;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import eu.endermite.serverbasics.storage.PlayerDatabase;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

        if (!player.hasPermission("serverbasics.gamemode."+gamemode.toString().toLowerCase())) {
            MessageParser.sendDefaultTranslatedError(player, "debug.creative_spectator.error",  TextColor.color(255,255,255));
        }

        ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).setGameMode(gamemode);
        PlayerDatabase.saveSingleOption(player.getUniqueId(), "gamemode", gamemode.toString());
        Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> player.setGameMode(gamemode));
        MessageParser.sendDefaultTranslatedError(player, "commands.gamemode.success.self", "gameMode."+gamemode.toString().toLowerCase(), TextColor.color(255,255,255));
    }

    @CommandMethod("gamemode <gm> <target>")
    @CommandDescription("Set someones gamemode")
    @CommandPermission("serverbasics.command.gamemode.others")
    private void commandGamemodeOthers(
            final CommandSender sender,
            final @Argument(value = "gm") GameMode gamemode,
            final @Argument(value = "target")MultiplePlayerSelector players
            ) {

        if (!sender.hasPermission("serverbasics.gamemode."+gamemode.toString().toLowerCase())) {
            MessageParser.sendDefaultTranslatedError(sender, "debug.creative_spectator.error",  TextColor.color(255,255,255));
        }

        if (!players.hasAny()) {
            System.out.println(players.getSelector());
            return;
        }

        for (Player player : players.getPlayers()) {
            ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).setGameMode(gamemode);
            PlayerDatabase.saveSingleOption(player.getUniqueId(), "gamemode", gamemode.toString());
            Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> player.setGameMode(gamemode));
            MessageParser.sendDefaultTranslatedError(player, "gameMode.changed", "gameMode."+gamemode.toString().toLowerCase(), TextColor.color(255, 255, 255));
        }

        if (players.getPlayers().size() == 1) {

        }




    }

}
