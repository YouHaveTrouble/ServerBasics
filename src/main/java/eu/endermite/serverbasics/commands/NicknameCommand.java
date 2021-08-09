package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
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
                if (!offlinePlayer.hasPlayedBefore()) {
                    sendHaventPlayedError(sender);
                    return;
                }

                Component message = Component.translatable(
                        "commands.scoreboard.objectives.modify.displayname",
                        NamedTextColor.WHITE,
                        Component.text(offlinePlayer.getName()),
                        MiniMessage.markdown().parse(offlinePlayer.getName())
                );
                sender.sendMessage(message);
            } catch (Exception e) {
                sendHaventPlayedError(sender);
            }
            return;
        }

        Player target = player.getPlayer();

        String nick = target.getName();



        Component message = Component.translatable(
                "commands.scoreboard.objectives.modify.displayname",
                NamedTextColor.WHITE,
                Component.text(target.getName()),
               MiniMessage.get().parse(nick)
        );
        sender.sendMessage(message);

        Player onlineTarget = target.getPlayer();
        onlineTarget.displayName(MiniMessage.markdown().parse(nick));

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
                if (!offlinePlayer.hasPlayedBefore()) {
                    sendHaventPlayedError(sender);
                    return;
                }

                Component message = Component.translatable(
                        "commands.scoreboard.objectives.modify.displayname",
                        NamedTextColor.WHITE,
                        Component.text(offlinePlayer.getName()),
                        MiniMessage.markdown().parse(nick)
                );
                sender.sendMessage(message);
                return;
            } catch (Exception e) {
                sendHaventPlayedError(sender);
            }
        }

        Player target = player.getPlayer();

        Component message = Component.translatable(
                "commands.scoreboard.objectives.modify.displayname",
                NamedTextColor.WHITE,
                Component.text(target.getName()),
                MiniMessage.markdown().parse(nick)
        );
        sender.sendMessage(message);

        if (!target.isOnline()) return;

        Player onlineTarget = target.getPlayer();
        ServerBasics.getBasicPlayers().getBasicPlayer(target.getUniqueId()).thenAccept(basicPlayer -> {
            basicPlayer.setDisplayName(nick);
        });
        onlineTarget.displayName(MiniMessage.markdown().parse(nick));
    }

    private void sendHaventPlayedError(CommandSender sender) {
        if (sender instanceof Player player) {
            String msg = ServerBasics.getLang(player.locale()).havent_played;
            MessageParser.sendMessage(player, msg);
        } else {
            String msg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).havent_played;
            MessageParser.sendMessage(sender, msg);
        }
    }

}
