package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.storage.PlayerDatabase;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class NicknameCommand {

    public void constructCommand() {
        ServerBasics.getCommandManager().getAnnotationParser().parse(this);
    }

    @CommandMethod("nick <player>")
    @CommandDescription("Set your nickname")
    @CommandPermission("serverbasics.command.nick.reset")
    private void commandNickReset(
            final Player player,
            final @Argument(value = "player") OfflinePlayer target
    ) {
        String nick = target.getName();

        ServerBasics.getBasicPlayers().getBasicPlayer(target.getUniqueId()).setDisplayName(nick);
        PlayerDatabase.saveSingleOption(player.getUniqueId(), "displayname", nick);
        if (!target.isOnline())
            return;

        Player onlineTarget = target.getPlayer();
        onlineTarget.setDisplayName(ChatColor.translateAlternateColorCodes('&', nick));
    }

    @CommandMethod("nick <player> <nickname>")
    @CommandDescription("Set your nickname")
    @CommandPermission("serverbasics.command.nick")
    private void commandNickOther(
            final Player player,
            final @Argument(value = "player") OfflinePlayer target,
            final @Argument(value = "nickname") String[] newNick
    ) {
        String nick = String.join(" ", newNick);

        ServerBasics.getBasicPlayers().getBasicPlayer(target.getUniqueId()).setDisplayName(nick);
        PlayerDatabase.saveSingleOption(player.getUniqueId(), "displayname", nick);
        if (!target.isOnline())
            return;

        Player onlineTarget = target.getPlayer();
        onlineTarget.setDisplayName(ChatColor.translateAlternateColorCodes('&', nick));
    }

}
