package me.youhavetrouble.serverbasics.chat;

import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import io.papermc.paper.chat.ChatRenderer;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BasicChatRenderer implements ChatRenderer {

    /**
     * Renderer for default chat format
     * @param player source
     * @param component sourceDisplayName
     * @param component2 message
     * @param audience viewer
     * @return Rendered message
     */
    @Override
    public @NotNull Component render(@NotNull Player player, @NotNull Component component, @NotNull Component component2, @NotNull Audience audience) {
        String stringMessage = PlainTextComponentSerializer.plainText().serialize(component2);

        String stringFormat = ServerBasics.getConfigCache().chat_format;

        if (ServerBasics.getHooks().isHooked("PlaceholderAPI"))
            stringFormat = PlaceholderAPI.setPlaceholders(player, stringFormat);

        Component format = MessageParser.miniMessage.parse(stringFormat);
        TextReplacementConfig.Builder messageReplacementConfig = TextReplacementConfig.builder()
                .match("%message%")
                .replacement(MessageParser.basicMiniMessage.parse(stringMessage));

        if (player.hasPermission("serverbasics.chat.color")) {
            stringMessage = MessageParser.makeColorsWork('&', stringMessage);
            messageReplacementConfig.replacement(MessageParser.basicMiniMessage.parse(stringMessage));
        }
        format = format.replaceText(messageReplacementConfig.build());

        TextReplacementConfig nameReplacementConfig = TextReplacementConfig.builder()
                .match("%nickname%")
                .replacement(player.displayName())
                .build();
        format = format.replaceText(nameReplacementConfig);
        return format;
    }
}
