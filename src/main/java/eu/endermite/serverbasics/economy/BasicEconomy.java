package eu.endermite.serverbasics.economy;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.storage.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicEconomy {

    private final HashMap<UUID, BasicEconomyAccount> accounts = new HashMap<>();
    private final List<BasicEconomyAccount> baltop = new ArrayList<>();
    private final Database database;

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

    }

    public CompletableFuture<BasicEconomyAccount> getEconomyAccount(UUID uuid) {
        if (accounts.containsKey(uuid))
            return CompletableFuture.completedFuture(accounts.get(uuid));
        return database.getBalance(uuid).thenApplyAsync(balance -> {
            BasicEconomyAccount account = new BasicEconomyAccount(uuid, balance);
            accounts.put(uuid, account);
            return account;
        });
    }

    public List<BasicEconomyAccount> getBaltop() {
        return baltop;
    }

    public void addToCache(UUID uuid) {
        database.getBalance(uuid).thenAccept(balance -> {
            BasicEconomyAccount economyAccount = new BasicEconomyAccount(uuid, balance);
            accounts.putIfAbsent(uuid, economyAccount);
        });
    }

    public void refreshBaltop(boolean force) {
        if (force) {
            HashMap<UUID, BasicEconomyAccount> accountClone = new HashMap<>(accounts);
            CompletableFuture[] futures = new CompletableFuture[accountClone.size()];
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
                BasicEconomyAccount account = new BasicEconomyAccount(entry.getKey(), entry.getValue());
                baltop.add(account);
            }
        });
    }



}
