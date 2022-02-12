package me.youhavetrouble.serverbasics.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.commands.registration.CommandRegistration;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.Locale;

@CommandRegistration
public class PlayTimeCommand {

    @CommandMethod("playtime")
    @CommandDescription("Check playtime")
    @CommandPermission("serverbasics.command.playtime")
    private void commandHeal(
            final Player player
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        int playtimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        Locale playerLang = player.locale();
        MessageParser.sendMessage(player, String.valueOf(playtimeTicks));
        //TODO make this display actual time
    }

}
