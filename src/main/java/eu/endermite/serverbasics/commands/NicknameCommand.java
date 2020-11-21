package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import eu.endermite.serverbasics.storage.PlayerDatabase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
            final CommandSender sender,
            final @Argument(value = "player") SinglePlayerSelector player
    ) {

        if (!player.hasAny()) {
            try {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getSelector());
                if (!PlayerDatabase.playerExists(offlinePlayer.getUniqueId())) {
                    sendHaventPlayedError(sender);
                    return;
                }

                PlayerDatabase.saveSingleOption(offlinePlayer.getUniqueId(), "displayname", offlinePlayer.getName());

                Component message = Component.translatable(
                        "commands.scoreboard.objectives.modify.displayname",
                        NamedTextColor.WHITE,
                        Component.text(offlinePlayer.getName()),
                        Component.text(ChatColor.translateAlternateColorCodes('&', offlinePlayer.getName()))
                );
                ServerBasics.getCommandManager().bukkitAudiences.sender(sender).sendMessage(message);
                return;
            } catch (Exception e) {
                sendHaventPlayedError(sender);
            }
        }

        Player target = player.getPlayer();

        String nick = target.getName();

        Component message = Component.translatable(
                "commands.scoreboard.objectives.modify.displayname",
                NamedTextColor.WHITE,
                Component.text(target.getName()),
                Component.text(ChatColor.translateAlternateColorCodes('&', nick))
        );
        ServerBasics.getCommandManager().bukkitAudiences.sender(sender).sendMessage(message);

        PlayerDatabase.saveSingleOption(target.getUniqueId(), "displayname", nick);

        Player onlineTarget = target.getPlayer();
        ServerBasics.getBasicPlayers().getBasicPlayer(target.getUniqueId()).setDisplayName(nick);
        onlineTarget.setDisplayName(ChatColor.translateAlternateColorCodes('&', nick));

    }

    @CommandMethod("nick <player> <nickname>")
    @CommandDescription("Set your nickname")
    @CommandPermission("serverbasics.command.nick")
    private void commandNickOther(
            final CommandSender sender,
            final @Argument(value = "player") SinglePlayerSelector player,
            final @Argument(value = "nickname") String[] newNick
    ) {
        String nick = String.join(" ", newNick);

        if (!player.hasAny()) {
            try {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getSelector());
                if (!PlayerDatabase.playerExists(offlinePlayer.getUniqueId())) {
                    sendHaventPlayedError(sender);
                    return;
                }

                PlayerDatabase.saveSingleOption(offlinePlayer.getUniqueId(), "displayname", nick);

                Component message = Component.translatable(
                        "commands.scoreboard.objectives.modify.displayname",
                        NamedTextColor.WHITE,
                        Component.text(offlinePlayer.getName()),
                        Component.text(ChatColor.translateAlternateColorCodes('&', nick))
                );
                ServerBasics.getCommandManager().bukkitAudiences.sender(sender).sendMessage(message);
                return;
            } catch (Exception e) {
                sendHaventPlayedError(sender);
            }
        }

        Player target = player.getPlayer();

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

    private void sendHaventPlayedError(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String msg = ServerBasics.getLang(player.getLocale()).HAVENT_PLAYED;
            MessageParser.sendMessage(player, msg);
        } else {
            String msg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).HAVENT_PLAYED;
            MessageParser.sendMessage(sender, msg);
        }
    }

}
