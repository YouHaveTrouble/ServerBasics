package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.*;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.economy.BasicEconomyAccount;
import eu.endermite.serverbasics.messages.MessageParser;
import eu.endermite.serverbasics.players.BasicPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@CommandRegistration
public class MoneyCommand {

    @CommandMethod("money set <player> <amount>")
    @CommandDescription("Set player's balance")
    @CommandPermission("serverbasics.command.money.set")
    private void commandMoneySet(
            final CommandSender sender,
            final @Argument(value = "player") SinglePlayerSelector singlePlayerSelector,
            final @Argument(value = "amount") double amount
            ) {
        if (ServerBasics.getBasicEconomy() == null || !ServerBasics.getBasicEconomy().isBasicEconomy()) {
            sender.sendMessage(MessageParser.parseMessage(sender, ServerBasics.getLang(sender).econ_disabled));
            return;
        }
        if (!singlePlayerSelector.hasAny()) {
            String selector = singlePlayerSelector.getSelector();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(selector);
            if (!offlinePlayer.hasPlayedBefore()) {
                sender.sendMessage(ServerBasics.getLang(sender).havent_played);
                return;
            }
            setBalance(sender, offlinePlayer.getUniqueId(), amount);
        } else {
            Player player = singlePlayerSelector.getPlayer();
            setBalance(sender, player.getUniqueId(), amount);
        }
    }

    @CommandMethod("money add <player> <amount>")
    @CommandDescription("Add money to player's balance")
    @CommandPermission("serverbasics.command.money.add")
    private void commandMoneyAdd(
            final CommandSender sender,
            final @Argument(value = "player") SinglePlayerSelector singlePlayerSelector,
            final @Argument(value = "amount") double amount,
            final @Flag(value = "silent") boolean silent
    ) {
        if (ServerBasics.getBasicEconomy() == null || !ServerBasics.getBasicEconomy().isBasicEconomy()) {
            sender.sendMessage(MessageParser.parseMessage(sender, ServerBasics.getLang(sender).econ_disabled));
            return;
        }

        if (!singlePlayerSelector.hasAny()) {
            String selector = singlePlayerSelector.getSelector();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(selector);
            if (!offlinePlayer.hasPlayedBefore()) {
                sender.sendMessage(ServerBasics.getLang(sender).havent_played);
                return;
            }
            addBalance(sender, offlinePlayer.getUniqueId(), amount, silent);
        } else {
            Player player = singlePlayerSelector.getPlayer();
            addBalance(sender, player.getUniqueId(), amount, silent);
        }
    }

    @CommandMethod("money remove <player> <amount>")
    @CommandDescription("Remove money from player's balance")
    @CommandPermission("serverbasics.command.money.remove")
    private void commandMoneyRemove(
            final CommandSender sender,
            final @Argument(value = "player") SinglePlayerSelector singlePlayerSelector,
            final @Argument(value = "amount") double amount,
            final @Flag(value = "silent") boolean silent
    ) {
        if (ServerBasics.getBasicEconomy() == null || !ServerBasics.getBasicEconomy().isBasicEconomy()) {
            sender.sendMessage(MessageParser.parseMessage(sender, ServerBasics.getLang(sender).econ_disabled));
            return;
        }
        if (!singlePlayerSelector.hasAny()) {
            String selector = singlePlayerSelector.getSelector();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(selector);
            if (!offlinePlayer.hasPlayedBefore()) {
                sender.sendMessage(ServerBasics.getLang(sender).havent_played);
                return;
            }
            removeBalance(sender, offlinePlayer.getUniqueId(), amount, silent);
        } else {
            Player player = singlePlayerSelector.getPlayer();
            removeBalance(sender, player.getUniqueId(), amount, silent);
        }
    }

    private void setBalance(CommandSender feedback, UUID uuid, double amount) {
        CompletableFuture<BasicPlayer> basicPlayerFuture = ServerBasics.getBasicPlayers().getBasicPlayer(uuid);
        CompletableFuture<BasicEconomyAccount> basicEconAccount = ServerBasics.getBasicEconomy().getEconomyAccount(uuid);
        CompletableFuture.allOf(basicEconAccount, basicPlayerFuture).thenRun(() -> {
            BasicPlayer basicPlayer = basicPlayerFuture.join();
            BasicEconomyAccount economyAccount = basicEconAccount.join();
            economyAccount.setBalance(amount);
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%amount%", Component.text(economyAccount.getBalance()));
            placeholders.put("%player%", basicPlayer.getDisplayName());
            feedback.sendMessage(MessageParser.parseMessage(feedback, ServerBasics.getLang(feedback).balance_set, placeholders));
        });
    }

    private void addBalance(CommandSender feedback, UUID uuid, double amount, boolean silent) {
        if (amount < 0) {
            feedback.sendMessage(ServerBasics.getLang(feedback).negative_value);
            return;
        }
        CompletableFuture<BasicPlayer> basicPlayerFuture = ServerBasics.getBasicPlayers().getBasicPlayer(uuid);
        CompletableFuture<BasicEconomyAccount> basicEconAccount = ServerBasics.getBasicEconomy().getEconomyAccount(uuid);
        CompletableFuture.allOf(basicEconAccount, basicPlayerFuture).thenRun(() -> {
            BasicPlayer basicPlayer = basicPlayerFuture.join();
            BasicEconomyAccount economyAccount = basicEconAccount.join();
            economyAccount.addBalance(amount);
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%amount%", Component.text(ServerBasics.getBasicEconomy().formatMoney(economyAccount.getBalance())));
            if (!silent) {
                Player player = Bukkit.getPlayer(basicPlayer.getUuid());
                if (player != null)
                    basicPlayer.sendMessage(ServerBasics.getLang(player).balance_got, placeholders);
            }
            placeholders.put("%player%", basicPlayer.getDisplayName());
            feedback.sendMessage(MessageParser.parseMessage(feedback, ServerBasics.getLang(feedback).balance_add, placeholders));
        });
    }

    private void removeBalance(CommandSender feedback, UUID uuid, double amount, boolean silent) {
        if (amount < 0) {
            feedback.sendMessage(ServerBasics.getLang(feedback).negative_value);
            return;
        }
        CompletableFuture<BasicPlayer> basicPlayerFuture = ServerBasics.getBasicPlayers().getBasicPlayer(uuid);
        CompletableFuture<BasicEconomyAccount> basicEconAccount = ServerBasics.getBasicEconomy().getEconomyAccount(uuid);
        CompletableFuture.allOf(basicEconAccount, basicPlayerFuture).thenRun(() -> {
            BasicPlayer basicPlayer = basicPlayerFuture.join();
            BasicEconomyAccount economyAccount = basicEconAccount.join();
            economyAccount.deductBalance(amount);
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%amount%", Component.text(ServerBasics.getBasicEconomy().formatMoney(economyAccount.getBalance())));
            if (!silent) {
                Player player = Bukkit.getPlayer(basicPlayer.getUuid());
                if (player != null)
                    basicPlayer.sendMessage(ServerBasics.getLang(player).balance_deducted, placeholders);
            }
            placeholders.put("%player%", basicPlayer.getDisplayName());
            feedback.sendMessage(MessageParser.parseMessage(feedback, ServerBasics.getLang(feedback).balance_deducted, placeholders));
        });
    }


}
