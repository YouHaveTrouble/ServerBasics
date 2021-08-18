package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.messages.MessageParser;
import eu.endermite.serverbasics.util.BasicUtil;
import eu.endermite.serverbasics.util.BasicWarp;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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

    @CommandMethod("editwarp <name> displayname <displayname>")
    @CommandDescription("Set warps displayname")
    @CommandPermission("serverbasics.command.editwarp.displayname")
    private void commandEditWarpSetDisplayName(
            final Player player,
            final @Argument(value = "name", suggestions = "warps") String id,
            final @Argument(value = "displayname") String[] displayname
    ) {
        if (!ServerBasics.getLocationsCache().warpExists(id)) {
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%warp%", Component.text(id));
            player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player.locale()).warp_doesnt_exist, placeholders));
            return;
        }
        String joinedDisplayName = String.join(" ", displayname);

        BasicWarp basicWarp = ServerBasics.getLocationsCache().getWarp(id);
        basicWarp.setDisplayName(joinedDisplayName);
        HashMap<String, Component> placeholders = new HashMap<>();
        placeholders.put("%warp%", Component.text(id));
        placeholders.put("%displayname%", MiniMessage.markdown().parse(joinedDisplayName));
        player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player.locale()).warp_displayname_set, placeholders));
    }

    @CommandMethod("editwarp <name> delete")
    @CommandDescription("Remove the warp")
    @CommandPermission("serverbasics.command.editwarp.delete")
    private void commandEditWarpDelete(
            final Player player,
            final @Argument(value = "name", suggestions = "warps") String id
    ) {
        if (!ServerBasics.getLocationsCache().warpExists(id)) {
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%warp%", Component.text(id));
            player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player.locale()).warp_doesnt_exist, placeholders));
            return;
        }
        ServerBasics.getLocationsCache().deleteWarp(id);
        HashMap<String, Component> placeholders = new HashMap<>();
        placeholders.put("%warp%", Component.text(id));
        player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player.locale()).warp_removed, placeholders));
    }

    @CommandMethod("editwarp <name> requirepermission <boolean>")
    @CommandDescription("Set permission requirement")
    @CommandPermission("serverbasics.command.editwarp.permission")
    private void commandEditWarpPermission(
            final CommandSender sender,
            final @Argument(value = "name", suggestions = "warps") String id,
            final @Argument(value = "boolean") boolean requirePermission
    ) {
        Locale locale = BasicUtil.playerLocaleOrDefault(sender);
        HashMap<String, Component> placeholders = new HashMap<>();
        placeholders.put("%warp%", Component.text(id));
        if (!ServerBasics.getLocationsCache().warpExists(id)) {
            sender.sendMessage(MessageParser.parseMessage(sender, ServerBasics.getLang(locale).warp_doesnt_exist, placeholders));
            return;
        }
        BasicWarp basicWarp = ServerBasics.getLocationsCache().getWarp(id);
        basicWarp.requiresPermission(requirePermission);
        if (requirePermission)
            sender.sendMessage(MessageParser.parseMessage(sender, ServerBasics.getLang(locale).warp_perm_on));
        else
            sender.sendMessage(MessageParser.parseMessage(sender, ServerBasics.getLang(locale).warp_perm_off));

    }

    @Suggestions("warps")
    public List<String> warpSuggestions(CommandContext<Player> sender, String input) {
        List<String> warps = new ArrayList<>();
        ServerBasics.getLocationsCache().warpList().forEach(basicWarp -> {
            if (basicWarp.requiresPermission() && !sender.getSender().hasPermission("serverbasics.warp."+basicWarp.getWarpId()))
                return;
            warps.add(basicWarp.getWarpId());
        });
        return StringUtil.copyPartialMatches(input, warps, new ArrayList<>());
    }

}
