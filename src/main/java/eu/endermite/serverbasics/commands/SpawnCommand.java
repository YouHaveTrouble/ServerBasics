package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
import eu.endermite.serverbasics.players.PlayerUtil;
import eu.endermite.serverbasics.storage.ServerDatabase;
import eu.endermite.serverbasics.locations.SBasicLocation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            if (sender instanceof Player) {
                Player player = (Player) sender;
                MessageParser.sendMessage(sender, ServerBasics.getLang(player.locale()).SPAWN_NOT_SET);
            }
            else
                MessageParser.sendMessage(sender, ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).SPAWN_NOT_SET);
            return;
        }

        Player target = targetToParse.getPlayer();

        if (target == null) {
            final Component message = Component.translatable(
                    "argument.entity.notfound.entity",
                    NamedTextColor.WHITE);
            sender.sendMessage(message);
            return;
        }
        PlayerUtil.teleportPlayerToSpawn(target.getPlayer());
    }

    @CommandMethod("spawn")
    @CommandDescription("Teleports you to spawn")
    @CommandPermission("serverbasics.command.spawn")
    private void commandSpawn(
            final Player player
    ) {
        if (!ServerBasics.getLocationsCache().isSpawnSet()) {
            MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).SPAWN_NOT_SET);
            return;
        }
        PlayerUtil.teleportPlayerToSpawn(player);
    }

    @CommandMethod("setspawn")
    @CommandDescription("Sets spawn location")
    @CommandPermission("serverbasics.command.setspawn")
    private void commandSetSpawn(
            final Player player
    ) {
        Location newSpawn = player.getLocation();
        SBasicLocation sBasicLocation = new SBasicLocation(newSpawn, "spawn");
        ServerBasics.getLocationsCache().setSpawn(sBasicLocation);
        ServerDatabase.saveSpawn(sBasicLocation);
        MessageParser.sendMessage(player, ServerBasics.getLang(player.locale()).SPAWN_SET);
    }

}
