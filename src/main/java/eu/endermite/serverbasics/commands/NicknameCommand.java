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
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@CommandRegistration
public class NicknameCommand {

    @CommandMethod("nick <nickname>")
    @CommandDescription("Set your nickname")
    @CommandPermission("serverbasics.command.nick")
    private void commandNick(
            final Player player,
            final @Argument(value = "nickname") String nick
    ) {
        String newNick = MessageParser.makeColorsWork('&', nick);
        Component nickComponent = MessageParser.basicMiniMessageWithoutMd.parse(newNick);
        String nickStripped = MiniMessage.get().stripTokens(nick);
        if (!player.hasPermission("serverbasics.command.nick.change") && !player.getName().equals(nickStripped)) {
            player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player.locale()).nick_only_same_as_name));
            return;
        }
        ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> {
            basicPlayer.setDisplayName(newNick);
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%nickname%", nickComponent);
            basicPlayer.sendMessage(ServerBasics.getLang(player.locale()).nick_self, placeholders);
        });
    }

    @CommandMethod("nick <nickname> <player>")
    @CommandDescription("Set nickname of other player")
    @CommandPermission("serverbasics.command.nick.other")
    private void commandNickOther(
            final CommandSender sender,
            final @Argument(value = "player") SinglePlayerSelector playerSelector,
            final @Argument(value = "nickname") String nick
    ) {
        UUID uuid;
        if (!playerSelector.hasAny()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerSelector.getSelector());
            if (!offlinePlayer.hasPlayedBefore()) {
                MessageParser.sendHaventPlayedError(sender);
                return;
            }
            uuid = offlinePlayer.getUniqueId();
        } else
            uuid = playerSelector.getPlayer().getUniqueId();

        String newNick = MessageParser.makeColorsWork('&', nick);

        ServerBasics.getBasicPlayers().getBasicPlayer(uuid).thenAccept(basicPlayer -> {
            Component nickComponent = MessageParser.basicMiniMessageWithoutMd.parse(newNick);
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%oldnickname%", basicPlayer.getDisplayName());
            placeholders.put("%newnickname%", nickComponent);
            basicPlayer.setDisplayName(newNick);
            sender.sendMessage(MessageParser.parseMessage(sender, ServerBasics.getLang(sender).nick_other, placeholders));
            Player player = Bukkit.getPlayer(basicPlayer.getUuid());
            if (player == null || !player.isOnline()) return;
            player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(basicPlayer.getLocale()).nick_changed_by_other, "%nickname%", nickComponent));
        });
    }
}
