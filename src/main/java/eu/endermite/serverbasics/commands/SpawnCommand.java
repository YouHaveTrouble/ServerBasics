package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
import eu.endermite.serverbasics.players.BasicPlayer;
import eu.endermite.serverbasics.util.BasicWarp;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandRegistration
public class SpawnCommand {

    @CommandMethod("spawn <target>")
    @CommandDescription("Teleports player to spawn")
    @CommandPermission("serverbasics.command.spawn.others")
    private void commandSpawnOther(
            final CommandSender sender,
            final @Argument(value = "target", description = "player to target") SinglePlayerSelector targetToParse
            ) {
        if (!ServerBasics.getLocationsCache().isSpawnSet()) {
            if (sender instanceof Player player)
                MessageParser.sendMessage(sender, ServerBasics.getLang(player.locale()).spawn_not_set);
            else
                MessageParser.sendMessage(sender, ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).spawn_not_set);
            return;
        }

        Player target = targetToParse.getPlayer();

        if (target == null) {
            if (sender instanceof Player player)
                MessageParser.sendMessage(sender, ServerBasics.getLang(player.locale()).no_player_selected);
            else
                MessageParser.sendMessage(sender, ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).no_player_selected);
            return;
        }
        ServerBasics.getBasicPlayers().getBasicPlayer(target.getUniqueId()).thenAccept(BasicPlayer::teleportToSpawn);
    }

    @CommandMethod("spawn")
    @CommandDescription("Teleports you to spawn")
    @CommandPermission("serverbasics.command.spawn")
    private void commandSpawn(
            final Player player
    ) {
        if (!ServerBasics.getLocationsCache().isSpawnSet()) {
            MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).spawn_not_set);
            return;
        }
        ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(BasicPlayer::teleportToSpawn);
    }

    @CommandMethod("setspawn")
    @CommandDescription("Sets spawn location")
    @CommandPermission("serverbasics.command.setspawn")
    private void commandSetSpawn(
            final Player player
    ) {
        Location location = player.getLocation();
        BasicWarp spawn = BasicWarp.builder()
                .warpId("spawn")
                .displayName("Spawn")
                .location(location)
                .build();
        ServerBasics.getLocationsCache().setSpawn(spawn);
        MessageParser.sendMessage(player, ServerBasics.getLang(player).spawn_set);
    }

    @CommandMethod("setspawn none")
    @CommandDescription("Sets spawn location to none")
    @CommandPermission("serverbasics.command.setspawn")
    private void commandSetSpawnToNone(
            final Player player
    ) {
        ServerBasics.getLocationsCache().clearSpawn();
        MessageParser.sendMessage(player, ServerBasics.getLang(player).spawn_unset);
    }

}
