package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
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
import java.util.Locale;
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
        Component nickComponent = MiniMessage.markdown().parse(nick);
        String nickStripped = MiniMessage.get().stripTokens(nick);
        if (!player.hasPermission("serverbasics.command.nick.change") && !player.getName().equals(nickStripped)) {
            player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player.locale()).nick_only_same_as_name));
            return;
        }
        ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> {
            basicPlayer.setDisplayName(nick);
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

        ServerBasics.getBasicPlayers().getBasicPlayer(uuid).thenAccept(basicPlayer -> {
            Locale locale;
            if (sender instanceof Player playerSender)
                locale = playerSender.locale();
            else
                locale = Locale.forLanguageTag(ServerBasics.getConfigCache().default_lang);
            Component nickComponent = MiniMessage.markdown().parse(nick);
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%oldnickname%", basicPlayer.getDisplayName());
            placeholders.put("%newnickname%", nickComponent);
            basicPlayer.setDisplayName(nick);
            sender.sendMessage(MessageParser.parseMessage(sender, ServerBasics.getLang(locale).nick_other, placeholders));
            placeholders.clear();
            placeholders.put("%nickname%", nickComponent);
            basicPlayer.sendMessage(ServerBasics.getLang(basicPlayer.getLocale()).nick_changed_by_other);
        });
    }
}
