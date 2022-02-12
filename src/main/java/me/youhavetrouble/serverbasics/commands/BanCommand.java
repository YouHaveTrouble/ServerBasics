package me.youhavetrouble.serverbasics.commands;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import cloud.commandframework.context.CommandContext;
import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.commands.registration.CommandRegistration;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@CommandRegistration
public class BanCommand {

    @CommandMethod("ban <player>")
    @CommandDescription("Ban player")
    @CommandPermission("serverbasics.command.ban")
    private void commandBan(
            final CommandSender sender,
            @Argument(value = "player", description = "Player to ban") SinglePlayerSelector playerSelector
    ) {

        OfflinePlayer target;

        if (!playerSelector.hasAny()) {
            target = Bukkit.getOfflinePlayer(playerSelector.getSelector());
            if (!target.hasPlayedBefore()) {
                MessageParser.sendHaventPlayedError(sender);
                return;
            }
        } else
            target = playerSelector.getPlayer();

        Component banMessage = Component.empty();
        Locale locale;
        if (target.isOnline())
            locale = target.getPlayer().locale();
        else
            locale = ServerBasics.getConfigCache().default_lang;

        for (String line : ServerBasics.getLang(locale).ban_message) {
            line = line.replaceAll("%reason%", ServerBasics.getLang(locale).ban_reason);
            banMessage = banMessage.append(MessageParser.miniMessage.parse(line)).append(Component.newline());
        }

        String finalKickReasonParsed = LegacyComponentSerializer.legacySection().serialize(banMessage);
        Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> target.banPlayer(finalKickReasonParsed, sender.getName()));
    }

    @CommandMethod("ban <player> <reason>")
    @CommandDescription("Ban player")
    @CommandPermission("serverbasics.command.ban")
    private void commandBanWithReason(
            final CommandSender sender,
            @Argument(value = "player", description = "Player to ban") SinglePlayerSelector playerSelector,
            @Argument(value = "reason", description = "Reason for ban") String[] reason
    ) {
        OfflinePlayer target;


        if (!playerSelector.hasAny()) {
            target = Bukkit.getOfflinePlayer(playerSelector.getSelector());
            if (!target.hasPlayedBefore()) {
                MessageParser.sendHaventPlayedError(sender);
                return;
            }
        } else
            target = playerSelector.getPlayer();

        Component joinedReason = MessageParser.basicMiniMessage.parse(String.join(" ", reason));
        Component banMessage = Component.empty();
        Locale locale;
        if (target.isOnline())
            locale = target.getPlayer().locale();
        else
            locale = ServerBasics.getConfigCache().default_lang;

        for (String line : ServerBasics.getLang(locale).ban_message) {
            banMessage = banMessage.append(MessageParser.miniMessage.parse(line)).append(Component.newline());
        }

        TextReplacementConfig reasonReplacer = TextReplacementConfig.builder().match("%reason%").replacement(joinedReason).build();
        banMessage = banMessage.replaceText(reasonReplacer);

        String finalKickReasonParsed = LegacyComponentSerializer.legacySection().serialize(banMessage);
        Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> target.banPlayer(finalKickReasonParsed, sender.getName()));
    }

    @CommandMethod("tempban <player> <time>")
    @CommandDescription("Ban player")
    @CommandPermission("serverbasics.command.tempban")
    private void commandTempBan(
            final CommandSender sender,
            @Argument(value = "player", description = "Player to ban") SinglePlayerSelector playerSelector,
            @Argument(value = "time", description = "time to ban for", suggestions = "time") @Regex(value = "([1-9]+)(s|m|h|d|mo|y)$") String timeString
    ) {
        OfflinePlayer target;

        if (!playerSelector.hasAny()) {
            target = Bukkit.getOfflinePlayer(playerSelector.getSelector());
            if (!target.hasPlayedBefore()) {
                MessageParser.sendHaventPlayedError(sender);
                return;
            }
        } else
            target = playerSelector.getPlayer();

        Component banMessage = Component.empty();
        Locale locale;
        if (target.isOnline())
            locale = target.getPlayer().locale();
        else
            locale = ServerBasics.getConfigCache().default_lang;

        for (String line : ServerBasics.getLang(locale).ban_message) {
            line = line.replaceAll("%reason%", ServerBasics.getLang(locale).ban_reason);
            banMessage = banMessage.append(MessageParser.miniMessage.parse(line)).append(Component.newline());
        }

        String finalKickReasonParsed = LegacyComponentSerializer.legacySection().serialize(banMessage);
        Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> target.banPlayer(finalKickReasonParsed, dateFromString(timeString), sender.getName()));

    }

    @CommandMethod("tempban <player> <time> <reason>")
    @CommandDescription("Ban player")
    @CommandPermission("serverbasics.command.tempban")
    private void commandTempBanWithReason(
            final CommandSender sender,
            @Argument(value = "player", description = "Player to ban") SinglePlayerSelector playerSelector,
            @Argument(value = "time", description = "time to ban for", suggestions = "time") @Regex(value = "([1-9]+)(s|m|h|d|mo|y)$") String timeString,
            @Argument(value = "reason", description = "Reason for ban") String[] reason
    ) {
        OfflinePlayer target;

        if (!playerSelector.hasAny()) {
            target = Bukkit.getOfflinePlayer(playerSelector.getSelector());
            if (!target.hasPlayedBefore()) {
                MessageParser.sendHaventPlayedError(sender);
                return;
            }
        } else
            target = playerSelector.getPlayer();

        Component banMessage = Component.empty();
        Locale locale;
        if (target.isOnline())
            locale = target.getPlayer().locale();
        else
            locale = ServerBasics.getConfigCache().default_lang;

        Component reasonComponent = MessageParser.miniMessage.parse(String.join(" ", reason));

        for (String line : ServerBasics.getLang(locale).ban_message) {
            banMessage = banMessage.append(MessageParser.miniMessage.parse(line)).append(Component.newline());
        }

        TextReplacementConfig reasonReplacer = TextReplacementConfig.builder().match("%reason%").replacement(reasonComponent).build();
        banMessage = banMessage.replaceText(reasonReplacer);

        String finalKickReasonParsed = LegacyComponentSerializer.legacySection().serialize(banMessage);
        Bukkit.getScheduler().runTask(ServerBasics.getInstance(), () -> target.banPlayer(finalKickReasonParsed, dateFromString(timeString), sender.getName()));

    }

    @Suggestions("time")
    public List<String> timeSuggestions(CommandContext<Player> sender, String input) {
        List<String> suggestions = new ArrayList<>();
        if (!input.matches("([1-9]+)")) return suggestions;
        suggestions.add(input+"s");
        suggestions.add(input+"m");
        suggestions.add(input+"h");
        suggestions.add(input+"d");
        suggestions.add(input+"mo");
        suggestions.add(input+"y");
        return StringUtil.copyPartialMatches(input, suggestions, new ArrayList<>());
    }

    private Date dateFromString(String string) {
        Instant instant = Instant.now();
        if (string.endsWith("s")) {
            string = string.replace("s", "");
            instant = instant.plus(Long.parseLong(string), ChronoUnit.SECONDS);
        } else if (string.endsWith("m")) {
            string = string.replace("m", "");
            instant = instant.plus(Long.parseLong(string), ChronoUnit.MINUTES);
        } else if (string.endsWith("h")) {
            string = string.replace("h", "");
            instant = instant.plus(Long.parseLong(string), ChronoUnit.HOURS);
        } else if (string.endsWith("d")) {
            string = string.replace("d", "");
            instant = instant.plus(Long.parseLong(string), ChronoUnit.DAYS);
        } else if (string.endsWith("mo")) {
            string = string.replace("mo", "");
            instant = instant.plus(Long.parseLong(string)*30, ChronoUnit.DAYS);
        } else if (string.endsWith("y")) {
            string = string.replace("y", "");
            instant = instant.plus(Long.parseLong(string)*356, ChronoUnit.DAYS);
        }
        return Date.from(instant);
    }

}
