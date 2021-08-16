package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
import eu.endermite.serverbasics.util.BasicWarp;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

@CommandRegistration
public class WarpCommand {

    @CommandMethod("warp <name>")
    @CommandDescription("Teleport to warp")
    @CommandPermission("serverbasics.command.warp")
    private void commandWarp(
            final Player player,
            final @Argument(value = "name", suggestions = "warps") String warpId
    ) {
        BasicWarp warp = ServerBasics.getLocationsCache().getWarp(warpId);
        if (warp == null) {
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%warp%", Component.text(warpId));
            player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player.locale()).warp_doesnt_exist, placeholders));
            return;
        }
        ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).thenAccept(basicPlayer -> {
            basicPlayer.teleportPlayer(warp.getLocation());
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%warp%", warp.getDisplayName());
            basicPlayer.sendMessage(ServerBasics.getLang(player.locale()).warped, placeholders);
        });
    }

    @CommandMethod("setwarp <name>")
    @CommandDescription("Create new warp or relocate existing one")
    @CommandPermission("serverbasics.command.setwarp")
    private void commandSetWarp(
            final Player player,
            final @Argument(value = "name") String id
    ) {
        if (id.equalsIgnoreCase("spawn")) {
            player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player.locale()).warp_cant_use_name));
            return;
        }
        if (ServerBasics.getLocationsCache().warpExists(id)) {
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%warp%", Component.text(id));
            player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player.locale()).warp_exists, placeholders));
            return;
        }
        Location location = player.getLocation();
        BasicWarp basicWarp = BasicWarp.builder()
                .warpId(id)
                .location(location)
                .build();
        ServerBasics.getLocationsCache().addWarp(basicWarp);
        HashMap<String, Component> placeholders = new HashMap<>();
        placeholders.put("%warp%", Component.text(id));
        player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player.locale()).warp_set, placeholders));
    }

    @Suggestions("warps")
    public List<String> methodName(CommandContext<Player> sender, String input) {
        List<String> warps = new ArrayList<>();
        ServerBasics.getLocationsCache().warpList().forEach(basicWarp -> {
            if (basicWarp.requiresPermission() && !sender.getSender().hasPermission("serverbasics.warp."+basicWarp.getWarpId()))
                return;
            warps.add(basicWarp.getWarpId());
        });
        return StringUtil.copyPartialMatches(input, warps, new ArrayList<>());
    }

}
