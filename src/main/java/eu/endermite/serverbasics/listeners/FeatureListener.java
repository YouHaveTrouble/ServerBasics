package eu.endermite.serverbasics.listeners;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.players.BasicPlayer;
import eu.endermite.serverbasics.storage.PlayerDatabase;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FeatureListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if (PlayerDatabase.playerExists(player.getUniqueId())) {
            BasicPlayer basicPlayer = PlayerDatabase.getPlayerfromStorage(player.getUniqueId());
            ServerBasics.getBasicPlayers().addBasicPlayer(basicPlayer);

        } else {
            BasicPlayer basicPlayer = BasicPlayer.builder()
                    .uuid(player.getUniqueId())
                    .displayName(player.getDisplayName())
                    .player(player)
                    .fly(player.getAllowFlight())
                    .gameMode(player.getGameMode())
                    .build();
            ServerBasics.getBasicPlayers().addBasicPlayer(basicPlayer);
            PlayerDatabase.createPlayerStorage(basicPlayer);
        }

        boolean flying = ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).canFly();
        if (flying) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        String nickname = ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).getDisplayName();
        player.setDisplayName(nickname);

        GameMode gameMode = ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).getGameMode();
        player.setGameMode(gameMode);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLeave(org.bukkit.event.player.PlayerQuitEvent event) {
        BasicPlayer basicPlayer = ServerBasics.getBasicPlayers().getBasicPlayer(event.getPlayer().getUniqueId());
        PlayerDatabase.savePlayertoStorage(basicPlayer);
        ServerBasics.getBasicPlayers().removeBasicPlayer(basicPlayer);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerGMChange(org.bukkit.event.player.PlayerGameModeChangeEvent event) {

        Player player = event.getPlayer();

        if (event.getNewGameMode().equals(GameMode.CREATIVE)) {
            ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).setFly(true);
            PlayerDatabase.saveSingleOption(player.getUniqueId(), "fly", true);
            PlayerDatabase.saveSingleOption(player.getUniqueId(), "gamemode", GameMode.CREATIVE.toString());
            return;
        }
        if ( event.getNewGameMode().equals(GameMode.SPECTATOR)) {
            ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).setFly(true);
            PlayerDatabase.saveSingleOption(player.getUniqueId(), "fly", true);
            PlayerDatabase.saveSingleOption(player.getUniqueId(), "gamemode", GameMode.SPECTATOR.toString());
            return;
        }
        if (event.getNewGameMode().equals(GameMode.SURVIVAL)) {
            ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).setFly(false);
            PlayerDatabase.saveSingleOption(player.getUniqueId(), "fly", false);
            PlayerDatabase.saveSingleOption(player.getUniqueId(), "gamemode", GameMode.SURVIVAL.toString());
            return;
        }
        if (event.getNewGameMode().equals(GameMode.ADVENTURE)) {
            ServerBasics.getBasicPlayers().getBasicPlayer(player.getUniqueId()).setFly(false);
            PlayerDatabase.saveSingleOption(player.getUniqueId(), "fly", false);
            PlayerDatabase.saveSingleOption(player.getUniqueId(), "gamemode", GameMode.ADVENTURE.toString());
            return;
        }

    }


}
