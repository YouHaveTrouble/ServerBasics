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
        ServerBasics.getBasicPlayers().getBasicPlayer(uuid).thenAccept(basicPlayer -> name = basicPlayer.getDisplayName());
    }

    /**
     * @return UUID associated with the account.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return Component name.
     *         Might return null if the name was not resolved yet.
     *         Might return empty component if the uuid can't be resolved.
     */
    public Component getName() {
        return name;
    }

    /**
     * @return Balance.
     */
    public double getBalance() {
        updateLastAccess();
        return balance;
    }

    /**
     * Set balance.
     * @param balance Balance to set.
     */
    public void setBalance(double balance) {
        updateLastAccess();
        this.changedSinceLastSave = true;
        this.balance = balance;
    }

    /**
     * Add to balance.
     * @param balance Balance to add. Negative values will result in adding nothing.
     */
    public void addBalance(double balance) {
        updateLastAccess();
        this.changedSinceLastSave = true;
        if (balance < 0) balance = 0;
        this.balance = this.balance + balance;
    }

    /**
     * Remove from balance.
     * @param balance Balance to deduct. Negative values will result in deducting nothing.
     */
    public void deductBalance(double balance) {
        updateLastAccess();
        this.changedSinceLastSave = true;
        if (balance < 0) balance = 0;
        this.balance = this.balance - balance;
    }

    protected boolean changedSinceLastSave() {
        return changedSinceLastSave;
    }

    protected void changedSinceLastSave(boolean changedSinceLastSave) {
        this.changedSinceLastSave = changedSinceLastSave;
    }

    /**
     * All public methods concerning balance will cause timestamp to update to current time.
     * @return Timestamp of when the balance was last accessed.
     */
    public long getLastAccessed() {
        return lastAccessed;
    }

    private void updateLastAccess() {
        this.lastAccessed = Instant.now().getEpochSecond();
    }
}
