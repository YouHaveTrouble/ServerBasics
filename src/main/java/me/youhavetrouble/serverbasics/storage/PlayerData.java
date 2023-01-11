package me.youhavetrouble.serverbasics.storage;

import me.youhavetrouble.serverbasics.players.BasicPlayer;


import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerData {

    CompletableFuture<BasicPlayer> loadBasicPlayer(UUID uuid);

    CompletableFuture<Void> saveBasicPlayer(BasicPlayer basicPlayer);

}
