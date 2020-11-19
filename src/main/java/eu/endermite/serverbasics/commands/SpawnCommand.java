package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.locations.SBasicLocation;
import eu.endermite.serverbasics.messages.MessageParser;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

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
                MessageParser.sendMessage(sender, ServerBasics.getLang(player.getLocale()).SPAWN_NOT_SET);
            }
            else
                MessageParser.sendMessage(sender, ServerBasics.getLang(ServerBasics.getConfigCache().DEFAULT_LANG).SPAWN_NOT_SET);
            return;
        }

        Player target = targetToParse.getPlayer();

        if (target == null) {
            MessageParser.sendDefaultTranslatedError(sender, "argument.entity.notfound.entity", TextColor.color(255,255,255));
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                PaperLib.teleportAsync(target, ServerBasics.getLocationsCache().spawn.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                    if (result) {
                        MessageParser.sendMessage(target, ServerBasics.getLang(target.getLocale()).TPD_SPAWN);
                    } else {
                        MessageParser.sendMessage(target, ServerBasics.getLang(target.getLocale()).COULD_NOT_TP);
                    }
                });
            }
        }.runTask(ServerBasics.getInstance());
    }

    @CommandMethod("spawn")
    @CommandDescription("Teleports you to spawn")
    @CommandPermission("serverbasics.command.spawn")
    private void commandSpawn(
            final Player player
    ) {
        if (!ServerBasics.getLocationsCache().isSpawnSet()) {
            MessageParser.sendMessage(player, ServerBasics.getLang(player.getLocale()).SPAWN_NOT_SET);
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                PaperLib.teleportAsync(player, ServerBasics.getLocationsCache().spawn.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                    if (result) {
                        MessageParser.sendMessage(player, ServerBasics.getLang(player.getLocale()).TPD_SPAWN);
                    } else {
                        MessageParser.sendMessage(player, ServerBasics.getLang(player.getLocale()).COULD_NOT_TP);
                    }
                });
            }
        }.runTask(ServerBasics.getInstance());
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
        MessageParser.sendMessage(player, ServerBasics.getLang(player.getLocale()).SPAWN_SET);
    }

}
