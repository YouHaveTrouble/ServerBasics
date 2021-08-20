package eu.endermite.serverbasics.chat;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import io.papermc.paper.chat.ChatRenderer;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StaffChatRenderer implements ChatRenderer {
    @Override
    public @NotNull Component render(@NotNull Player player, @NotNull Component component, @NotNull Component component2, @NotNull Audience audience) {
        String stringMessage = PlainTextComponentSerializer.plainText().serialize(component2);

        String stringFormat = ServerBasics.getConfigCache().staffchat_format;

        if (ServerBasics.getHooks().isHooked("PlaceholderAPI"))
            stringFormat = PlaceholderAPI.setPlaceholders(player, stringFormat);

        Component format = MessageParser.miniMessage.parse(stringFormat);
        TextReplacementConfig.Builder messageReplacementConfig = TextReplacementConfig.builder()
                .match("%message%");

        if (player.hasPermission("serverbasics.chat.color")) {
            stringMessage = MessageParser.makeColorsWork('&', stringMessage);
            messageReplacementConfig.replacement(MessageParser.basicMiniMessage.parse(stringMessage));
        } else
            messageReplacementConfig.replacement(Component.text(stringMessage));
        format = format.replaceText(messageReplacementConfig.build());

        TextReplacementConfig nameReplacementConfig = TextReplacementConfig.builder()
                .match("%nickname%")
                .replacement(component)
                .build();
        format = format.replaceText(nameReplacementConfig);
        return format;
    }
}
