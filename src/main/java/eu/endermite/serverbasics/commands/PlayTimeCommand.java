package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class PlayTimeCommand {

    public void constructCommand() {
        ServerBasics.getCommandManager().getAnnotationParser().parse(this);
    }

    @CommandMethod("playtime")
    @CommandDescription("Check playtime")
    @CommandPermission("serverbasics.command.playtime")
    private void commandHeal(
            final Player player
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        int playtimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        String playerLang = player.getLocale();
        MessageParser.sendMessage(player, String.valueOf(playtimeTicks));
        //TODO make this display actual time
    }

}
