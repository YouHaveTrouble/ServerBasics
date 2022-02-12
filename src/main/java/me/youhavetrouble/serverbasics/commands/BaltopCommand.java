package me.youhavetrouble.serverbasics.commands;

import cloud.commandframework.annotations.*;
import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.commands.registration.CommandRegistration;
import me.youhavetrouble.serverbasics.economy.BasicBaltopEntry;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;

@CommandRegistration
public class BaltopCommand {

    @CommandMethod("baltop")
    @CommandDescription("Display top balances")
    @CommandPermission("serverbasics.command.baltop")
    private void commandBaltop(
            final CommandSender sender
    ) {

        if (ServerBasics.getBasicEconomy() == null || !ServerBasics.getBasicEconomy().isBasicEconomy()) {
            sender.sendMessage(MessageParser.parseMessage(sender, ServerBasics.getLang(sender).econ_disabled));
            return;
        }

        List<BasicBaltopEntry> entries = ServerBasics.getBasicEconomy().getBaltop();
        sender.sendMessage(MessageParser.parseMessage(sender, ServerBasics.getLang(sender).baltop_title));
        if (entries.isEmpty()) {
            sender.sendMessage(MessageParser.parseMessage(sender, ServerBasics.getLang(sender).baltop_empty));
            return;
        }
        int place = 0;
        for (BasicBaltopEntry entry : entries) {
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%place%", Component.text(++place));
            placeholders.put("%name%", entry.getComponentName());
            placeholders.put("%balance%", Component.text(entry.getFormattedMoney()));
            sender.sendMessage(MessageParser.parseMessage(sender, ServerBasics.getLang(sender).baltop_format, placeholders));
        }
    }

}
