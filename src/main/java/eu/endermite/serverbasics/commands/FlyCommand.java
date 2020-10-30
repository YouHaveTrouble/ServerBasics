package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class FlyCommand {

    public void constructCommand() {
        ServerBasics.getCommandManager().getAnnotationParser().parse(this);
    }

    public static final NamespacedKey flyKey = new NamespacedKey(ServerBasics.getInstance(), "flying");

    @CommandMethod("fly")
    @CommandDescription("Toggle flight mode")
    @CommandPermission("serverbasics.command.fly")
    private void commandFly(
            final Player player
    ) {

        String flying = player.getPersistentDataContainer().get(flyKey, PersistentDataType.STRING);

        if (flying != null && flying.equals("true")) {
            player.setFlying(false);
            player.setAllowFlight(false);
            player.getPersistentDataContainer().set(flyKey, PersistentDataType.STRING, "false");
        } else {
            player.setAllowFlight(true);
            Bukkit.getScheduler().runTask(ServerBasics.getInstance(), task -> {
                player.teleport(player.getLocation().add(0, 0.1, 0));
                player.setFlying(true);
            });
            player.getPersistentDataContainer().set(flyKey, PersistentDataType.STRING, "true");

        }

    }

}
