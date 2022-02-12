package me.youhavetrouble.serverbasics.economy;

import me.youhavetrouble.serverbasics.ServerBasics;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class BasicVaultHandler implements Economy {

    private final ServerBasics plugin = ServerBasics.getInstance();

    @Override
    public boolean isEnabled() {
        return plugin != null
                && ServerBasics.getBasicEconomy() != null
                && ServerBasics.getBasicEconomy().isBasicEconomy();
    }

    @Override
    public String getName() {
        return "ServerBasics";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return ServerBasics.getConfigCache().fractionalDigits;
    }

    @Override
    public String format(double v) {
        return ServerBasics.getBasicEconomy().formatMoney(v);
    }

    @Override
    public String currencyNamePlural() {
        return ServerBasics.getConfigCache().currencySymbol;
    }

    @Override
    public String currencyNameSingular() {
        return ServerBasics.getConfigCache().currencySymbol;
    }

    @Override
    public boolean hasAccount(String s) {
        return isEnabled();
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return isEnabled();
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return isEnabled();
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return isEnabled();
    }

    @Override
    public double getBalance(String s) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
        return getBalance(offlinePlayer);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        if (!isEnabled()) return 0;
        try {
            return ServerBasics.getBasicEconomy().getEconomyAccount(offlinePlayer.getUniqueId()).get().getBalance();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public double getBalance(String s, String s1) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
        return getBalance(offlinePlayer);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String s, double v) {
        return v <= getBalance(s);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        return v <= getBalance(offlinePlayer);
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return v <= getBalance(s);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return v <= getBalance(offlinePlayer);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
        return withdrawPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
        if (!isEnabled()) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Economy is disabled");
        if (amount < 0)
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds");
        AtomicReference<Double> newBalance = new AtomicReference<>((double) 0);
        if (has(offlinePlayer, amount)) {
            try {
                ServerBasics.getBasicEconomy().getEconomyAccount(offlinePlayer.getUniqueId()).thenAccept(account -> {
                    account.deductBalance(amount);
                    newBalance.set(account.getBalance());
                }).get();
                return new EconomyResponse(amount, newBalance.get(), EconomyResponse.ResponseType.SUCCESS, "");
            } catch (InterruptedException | ExecutionException e) {
                return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "An error occured when processing economy action");
            }
        } else {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
        return withdrawPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
        return depositPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
        if (!isEnabled()) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Economy is disabled");
        if (amount < 0)
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");
        AtomicReference<Double> newBalance = new AtomicReference<>((double) 0);
        try {
            ServerBasics.getBasicEconomy().getEconomyAccount(offlinePlayer.getUniqueId()).thenAccept(account -> {
                account.addBalance(amount);
                newBalance.set(account.getBalance());
            }).get();
            return new EconomyResponse(amount, newBalance.get(), EconomyResponse.ResponseType.SUCCESS, "");
        } catch (InterruptedException | ExecutionException e) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "An error occured when processing economy action");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(s);
        return depositPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer, v);
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not implemented");
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not implemented");
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not implemented");
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not implemented");
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not implemented");
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not implemented");
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not implemented");
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not implemented");
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Not implemented");
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return true;
    }
}
