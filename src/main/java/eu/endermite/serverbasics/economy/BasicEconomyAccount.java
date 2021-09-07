package eu.endermite.serverbasics.economy;

import eu.endermite.serverbasics.ServerBasics;
import net.kyori.adventure.text.Component;

import java.time.Instant;
import java.util.UUID;

public class BasicEconomyAccount {

    private long lastAccessed;
    private final UUID uuid;
    private Component name;
    private double balance;
    private boolean changedSinceLastSave = false;

    public BasicEconomyAccount(UUID uuid, double balance) {
        this.uuid = uuid;
        this.balance = balance;
        this.name = Component.text(ServerBasics.getLang(ServerBasics.getConfigCache().default_lang).unknown_player);
        updateLastAccess();
        ServerBasics.getBasicPlayers().getBasicPlayer(uuid).thenAccept(basicPlayer -> {
           name = basicPlayer.getDisplayName();
        });
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getBalance() {
        updateLastAccess();
        return balance;
    }

    public Component getName() {
        return name;
    }

    public void setBalance(double balance) {
        updateLastAccess();
        this.changedSinceLastSave = true;
        this.balance = balance;
    }

    protected boolean changedSinceLastSave() {
        return changedSinceLastSave;
    }

    protected void changedSinceLastSave(boolean changedSinceLastSave) {
        this.changedSinceLastSave = changedSinceLastSave;
    }

    public long getLastAccessed() {
        return lastAccessed;
    }

    private void updateLastAccess() {
        this.lastAccessed = Instant.now().getEpochSecond();
    }
}
