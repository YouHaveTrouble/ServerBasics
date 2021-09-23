package eu.endermite.serverbasics.economy;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.storage.Database;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicEconomy {

    private final HashMap<UUID, BasicEconomyAccount> accounts = new HashMap<>();
    private final List<BasicBaltopEntry> baltop = new ArrayList<>();
    private final Database database;
    private Economy economy;

    public BasicEconomy(ServerBasics plugin) {
        database = plugin.getDatabase();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            addToCache(uuid);
        }
        long interval = ServerBasics.getConfigCache().economySaveInterval;

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            Iterator<BasicEconomyAccount> iterator = accounts.values().iterator();
            long now = Instant.now().getEpochSecond();
            while (iterator.hasNext()) {
                BasicEconomyAccount account = iterator.next();
                if (account.changedSinceLastSave())
                    database.saveBalance(account.getUuid(), account.getBalance()).thenRun(() -> account.changedSinceLastSave(false));

                Player player = Bukkit.getPlayer(account.getUuid());
                if ((player == null || !player.isOnline()) && now > account.getLastAccessed()+(interval*0.75)) iterator.remove();
            }
        }, interval*20, interval*20);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> refreshBaltop(false), 0, ServerBasics.getConfigCache().baltopRefreshInterval* 20L);

    }

    /**
     * @return Economy account associated with provided UUID
     */
    public CompletableFuture<BasicEconomyAccount> getEconomyAccount(UUID uuid) {
        if (accounts.containsKey(uuid))
            return CompletableFuture.completedFuture(accounts.get(uuid));
        return database.getBalance(uuid).thenApplyAsync(balance -> {
            BasicEconomyAccount account = new BasicEconomyAccount(uuid, balance);
            accounts.put(uuid, account);
            return account;
        });
    }

    /**
     * @return List of current baltop entries
     */
    public List<BasicBaltopEntry> getBaltop() {
        return baltop;
    }

    protected void addToCache(UUID uuid) {
        database.getBalance(uuid).thenAccept(balance -> {
            BasicEconomyAccount economyAccount = new BasicEconomyAccount(uuid, balance);
            accounts.putIfAbsent(uuid, economyAccount);
        });
    }

    /**
     * Refresh the baltop ranking
     * @param force If true, all currently cached accounts are saved and baltop is recalculated.
     *              Otherwise baltop might be slightly out of date, but will be leass heavy on the server.
     */
    public void refreshBaltop(boolean force) {
        if (force) {
            HashMap<UUID, BasicEconomyAccount> accountClone = new HashMap<>(accounts);
            CompletableFuture<?>[] futures = new CompletableFuture[accountClone.size()];
            AtomicInteger i = new AtomicInteger();
            accountClone.values().forEach(account -> futures[i.getAndIncrement()] = ServerBasics.getInstance().getDatabase().saveBalance(account.getUuid(), account.getBalance()));
            CompletableFuture.allOf(futures).thenRun(this::updateBaltopEntries);
            return;
        }
        updateBaltopEntries();
    }

    private void updateBaltopEntries() {
        database.getBaltop(ServerBasics.getConfigCache().baltopSize).thenAccept(newBaltop ->  {
            baltop.clear();
            for (Map.Entry<UUID, Double> entry : newBaltop.entrySet()) {
                BasicBaltopEntry account = new BasicBaltopEntry(entry.getKey(), entry.getValue());
                baltop.add(account);
            }
        });
    }

    /**
     * @return True if currently used economy is provided by ServerBasics
     */
    public boolean isBasicEconomy() {
        if (economy == null) return false;
        return economy instanceof BasicVaultHandler;
    }

    public String formatMoney(double amount) {
        int decPoints = ServerBasics.getConfigCache().fractionalDigits;

        if (amount >= Math.pow(10, 12))
           return String.format("%,."+decPoints+"ft", amount/Math.pow(10, 12));
        if (amount >= Math.pow(10, 9))
            return String.format("%,."+decPoints+"fb", amount/Math.pow(10, 9));
        if (amount >= Math.pow(10, 6))
            return String.format("%,."+decPoints+"fm", amount/Math.pow(10, 6));

        return String.format("%,."+decPoints+"f", amount);
    }

    /**
     * Used internally to set economy
     */
    public void activateEconomy() {
        RegisteredServiceProvider<Economy> rsp = ServerBasics.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return;
        if (rsp.getProvider() instanceof BasicVaultHandler)
            economy = rsp.getProvider();
    }
}
