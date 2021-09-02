package eu.endermite.serverbasics.economy;

import eu.endermite.serverbasics.ServerBasics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.UUID;

public class BasicEconomyAccount {

    private long lastAccessed;
    private final UUID uuid;
    private double balance;

    public BasicEconomyAccount(UUID uuid, double balance) {
        this.uuid = uuid;
        this.balance = balance;
        updateLastAccess();
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getBalance() {
        updateLastAccess();
        return balance;
    }

    public void setBalance(double balance) {
        updateLastAccess();
        this.balance = balance;
    }

    public long getLastAccessed() {
        return lastAccessed;
    }

    private void updateLastAccess() {
        this.lastAccessed = Instant.now().getEpochSecond();
    }
}
