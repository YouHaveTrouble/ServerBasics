package me.youhavetrouble.serverbasics.chat;

import me.youhavetrouble.serverbasics.ServerBasics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    BasicChatRenderer chatRenderer = new BasicChatRenderer();
    StaffChatRenderer staffChatRenderer = new StaffChatRenderer();

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onChat(io.papermc.paper.event.player.AsyncChatEvent event) {

        Player player = event.getPlayer();
        String stringMessage = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());

        // Staffchat
        if (stringMessage.startsWith("!") && ServerBasics.getConfigCache().staffchat_enabled && player.hasPermission("serverbasics.staffchat.send")) {
            // Make sure only players with staffchat perms get the message
            event.viewers().removeIf(audience -> audience instanceof CommandSender sender && !sender.hasPermission("serverbasics.chat.staffchat.recieve"));
            // Remove the staffchat symbol
            stringMessage = stringMessage.substring(1);
            event.message(Component.text(stringMessage));
            event.renderer(staffChatRenderer);
            return;
        }

        // Chat format
        if (!ServerBasics.getConfigCache().chat_format_enabled)
            return;
        event.renderer(chatRenderer);
    }


}
