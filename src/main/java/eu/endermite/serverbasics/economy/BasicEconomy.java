package eu.endermite.serverbasics.economy;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.storage.Database;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BasicEconomy {

    private final HashMap<UUID, BasicEconomyAccount> accounts = new HashMap<>();
    private final LinkedHashMap<Component, Double> baltop = new LinkedHashMap<>();

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
                database.saveBalance(account.getUuid(), account.getBalance());
                Player player = Bukkit.getPlayer(account.getUuid());
                if ((player == null || !player.isOnline()) && now > account.getLastAccessed()+(interval*0.75))
                    iterator.remove();
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

    public void addToCache(UUID uuid) {
        database.getBalance(uuid).thenAccept(balance -> {
            BasicEconomyAccount economyAccount = new BasicEconomyAccount(uuid, balance);
            accounts.putIfAbsent(uuid, economyAccount);
        });
    }



}
