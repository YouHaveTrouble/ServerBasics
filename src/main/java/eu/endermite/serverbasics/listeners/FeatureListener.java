package eu.endermite.serverbasics.listeners;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import eu.endermite.serverbasics.players.BasicPlayer;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class FeatureListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (ServerBasics.getConfigCache().custom_join_msg || ServerBasics.getConfigCache().disable_join_msg)
            event.joinMessage(null);

        BasicPlayer.fromDatabase(player.getUniqueId()).thenAccept(basicPlayer -> {
            ServerBasics.getBasicPlayers().addBasicPlayer(basicPlayer);
            if (ServerBasics.getConfigCache().custom_join_msg)
                handleJoinMessage(basicPlayer, player);
            if (ServerBasics.getConfigCache().spawn_on_join)
                basicPlayer.teleportToSpawn();
        });
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeave(org.bukkit.event.player.PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (ServerBasics.getConfigCache().custom_leave_msg || ServerBasics.getConfigCache().disable_leave_msg)
            event.quitMessage(null);

        ServerBasics.getBasicPlayers().getBasicPlayer(uuid).thenAccept(basicPlayer -> {
            if (ServerBasics.getConfigCache().custom_leave_msg)
                handleLeaveMessage(basicPlayer, event.getPlayer());
        });

        ServerBasics.getInstance().getDatabase().savePlayerLastSeen(uuid, Instant.now().getEpochSecond());
        ServerBasics.getBasicPlayers().removeBasicPlayer(event.getPlayer().getUniqueId());
    }

    private void handleJoinMessage(BasicPlayer basicPlayer, Player player) {
        // Console message
        String consoleMsg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).custom_join_message;
        if (ServerBasics.getHooks().isHooked("PlaceholderAPI")) {
            consoleMsg = PlaceholderAPI.setPlaceholders(player, consoleMsg);
        }
        HashMap<String, Component> placeholders = new HashMap<>();
        placeholders.put("%nickname%", basicPlayer.getDisplayName());
        CommandSender sender = ServerBasics.getInstance().getServer().getConsoleSender();
        Component component = MessageParser.parseMessage(sender, consoleMsg, placeholders);
        ServerBasics.getInstance().getServer().getConsoleSender().sendMessage(component);

        if (ServerBasics.getConfigCache().disable_join_msg) return;

        // Message for players
        for (Player p : Bukkit.getOnlinePlayers()) {
            String msg = ServerBasics.getLang(p.locale()).custom_join_message;
            if (ServerBasics.getHooks().isHooked("PlaceholderAPI"))
                msg = PlaceholderAPI.setPlaceholders(player, msg);
            Component playerMessage = MessageParser.parseMessage(p, msg, placeholders);
            p.sendMessage(playerMessage);
        }
    }

    private void handleLeaveMessage(BasicPlayer basicPlayer, Player player) {
        // Console message
        String consoleMsg = ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).custom_leave_message;
        if (ServerBasics.getHooks().isHooked("PlaceholderAPI")) {
            consoleMsg = PlaceholderAPI.setPlaceholders(player, consoleMsg);
        }
        HashMap<String, Component> placeholders = new HashMap<>();
        placeholders.put("%nickname%", basicPlayer.getDisplayName());
        CommandSender sender = ServerBasics.getInstance().getServer().getConsoleSender();
        Component component = MessageParser.parseMessage(sender, consoleMsg, placeholders);
        ServerBasics.getInstance().getServer().getConsoleSender().sendMessage(component);

        if (ServerBasics.getConfigCache().disable_leave_msg) return;

        // Message for players
        for (Player p : Bukkit.getOnlinePlayers()) {
            String msg = ServerBasics.getLang(p.locale()).custom_leave_message;
            if (ServerBasics.getHooks().isHooked("PlaceholderAPI"))
                msg = PlaceholderAPI.setPlaceholders(player, msg);
            Component playerMessage = MessageParser.parseMessage(p, msg, placeholders);
            p.sendMessage(playerMessage);
        }
    }

}
