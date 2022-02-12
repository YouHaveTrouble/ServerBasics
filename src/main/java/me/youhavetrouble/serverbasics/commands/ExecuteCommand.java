package me.youhavetrouble.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.commands.registration.SyncCommandRegistration;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SyncCommandRegistration
public class ExecuteCommand {

    @CommandMethod("execute as <target> run <command>")
    @CommandDescription("Make player execute a command")
    @CommandPermission("serverbasics.command.execute.as")
    private void commandExecuteAs(
            final CommandSender sender,
            final @Argument(value = "target") Player target,
            final @Argument(value = "command") @Greedy String string
    )
    {
        if (target.hasPermission("serverbasics.command.execute.bypass")) {
            MessageParser.sendMessage(sender, ServerBasics.getLang(sender).cannot_execute_as);
            return;
        }
        target.performCommand(string);
        String msg = ServerBasics.getLang(sender).executed_command_as;
        msg = msg.replaceAll("%command%", string).replaceAll("%player%", target.getName());
        MessageParser.sendMessage(sender, msg);
    }

}
