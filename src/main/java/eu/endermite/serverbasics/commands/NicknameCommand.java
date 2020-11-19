package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.storage.PlayerDatabase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandRegistration
public class NicknameCommand {

    @CommandMethod("nick <player>")
    @CommandDescription("Set your nickname")
    @CommandPermission("serverbasics.command.nick.reset")
    private void commandNickReset(
            final Player player,
            final @Argument(value = "player") OfflinePlayer target
    ) {
        String nick = target.getName();

        PlayerDatabase.saveSingleOption(player.getUniqueId(), "displayname", nick);
        if (!target.isOnline())
            return;

        Player onlineTarget = target.getPlayer();
        ServerBasics.getBasicPlayers().getBasicPlayer(target.getUniqueId()).setDisplayName(nick);
        onlineTarget.setDisplayName(ChatColor.translateAlternateColorCodes('&', nick));

    }

    @CommandMethod("nick <player> <nickname>")
    @CommandDescription("Set your nickname")
    @CommandPermission("serverbasics.command.nick")
    private void commandNickOther(
            final CommandSender sender,
            final @Argument(value = "player") OfflinePlayer target,
            final @Argument(value = "nickname") String[] newNick
    ) {
        String nick = String.join(" ", newNick);


        PlayerDatabase.saveSingleOption(target.getUniqueId(), "displayname", nick);

        Component message = Component.translatable(
                "commands.scoreboard.objectives.modify.displayname",
                NamedTextColor.WHITE,
                Component.text(target.getName()),
                Component.text(ChatColor.translateAlternateColorCodes('&', nick))
        );
        ServerBasics.getCommandManager().bukkitAudiences.sender(sender).sendMessage(message);

        if (!target.isOnline())
            return;

        Player onlineTarget = target.getPlayer();
        ServerBasics.getBasicPlayers().getBasicPlayer(target.getUniqueId()).setDisplayName(nick);
        onlineTarget.setDisplayName(ChatColor.translateAlternateColorCodes('&', nick));
    }

}
