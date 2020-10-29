package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand {

    public void constructCommand() {
        ServerBasics.getCommandManager().getAnnotationParser().parse(this);
    }

    @CommandMethod("feed")
    @CommandDescription("Feed yourself")
    @CommandPermission("serverbasics.command.feed")
    private void commandFeed(
            final Player player
    ) {

        player.setFoodLevel(20);
        player.setSaturation(0);
        String msg = ServerBasics.getInstance().getLang(player.getLocale()).FED;
        MessageParser.sendMessage(player, msg);
    }

    @CommandMethod("feed <target>")
    @CommandDescription("Feed yourself or other player")
    @CommandPermission("serverbasics.command.feed.others")
    private void commandFeedOther(
            final CommandSender player,
            final @Argument(value = "target", description = "Player to feed") MultiplePlayerSelector targetPlayer
    ) {
        int amountFed = targetPlayer.getPlayers().size();
        Player lastPlayer = null;

        for (Player target : targetPlayer.getPlayers()) {
            lastPlayer = target;

            target.setFoodLevel(20);
            target.setSaturation(0);

            String playerLang = target.getLocale();
            String msg;
            if (player != target)
                msg = String.format(ServerBasics.getLang(playerLang).FED_BY_OTHER, player.getName());
            else
                msg = ServerBasics.getLang(playerLang).FED;

            MessageParser.sendMessage(target, msg);
        }
        if (player instanceof Player) {
            String msg;
            String playerLang = ((Player) player).getLocale();
            if (amountFed == 1)
                msg = String.format(ServerBasics.getLang(playerLang).FED_BY_OTHER, lastPlayer.getDisplayName());
            else if (amountFed > 1)
                msg = String.format(ServerBasics.getLang(playerLang).HEALED_MANY, amountFed);
            else
                msg = ServerBasics.getLang(playerLang).HEALED_NOONE;
            MessageParser.sendMessage(player, msg);
        } else {
            String msg;
            if (amountFed == 1)
                msg = String.format(ServerBasics.getLang("en_us").HEALED_BY_OTHER, lastPlayer.getDisplayName());
            else if (amountFed > 1)
                msg = String.format(ServerBasics.getLang("en_us").HEALED_MANY, amountFed);
            else
                msg = ServerBasics.getLang("en_us").HEALED_NOONE;
            MessageParser.sendMessage(player, msg);
        }
    }

}
