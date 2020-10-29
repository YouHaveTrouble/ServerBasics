package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnCommand {

    public void constructCommand() {
        ServerBasics.getCommandManager().getAnnotationParser().parse(this);
    }

    @CommandMethod("spawn")
    @CommandDescription("Teleports you to spawn")
    @CommandPermission("serverbasics.command.spawn")
    private void commandSpawn(
            final Player player
    ) {
        if (!ServerBasics.getInstance().getLocationsCache().isSpawnSet()) {
            MessageParser.sendMessage(player, ServerBasics.getInstance().getLang(player.getLocale()).SPAWN_NOT_SET);
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                PaperLib.teleportAsync(player, ServerBasics.getInstance().getLocationsCache().spawn, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                    if (result) {
                        MessageParser.sendMessage(player, ServerBasics.getInstance().getLang(player.getLocale()).TPD_SPAWN);
                    } else {
                        MessageParser.sendMessage(player, ServerBasics.getInstance().getLang(player.getLocale()).COULD_NOT_TP);
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
        ServerBasics.getInstance().getLocationsCache().setSpawn(newSpawn);
        MessageParser.sendMessage(player, ServerBasics.getInstance().getLang(player.getLocale()).SPAWN_SET);
    }

}
