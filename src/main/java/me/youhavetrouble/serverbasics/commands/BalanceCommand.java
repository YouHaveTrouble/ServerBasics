package me.youhavetrouble.serverbasics.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.commands.registration.CommandRegistration;
import me.youhavetrouble.serverbasics.economy.BasicEconomyAccount;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import me.youhavetrouble.serverbasics.players.BasicPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@CommandRegistration
public class BalanceCommand {

    @CommandMethod("balance")
    @CommandDescription("Check your balance")
    @CommandPermission("serverbasics.command.balance")
    private void commandBalance(
            final Player player
    ) {
        if (ServerBasics.getBasicEconomy() == null || !ServerBasics.getBasicEconomy().isBasicEconomy()) {
            player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player).econ_disabled));
            return;
        }
        ServerBasics.getBasicEconomy().getEconomyAccount(player.getUniqueId()).thenAccept(basicEconomyAccount -> {
            double balance = basicEconomyAccount.getBalance();
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%balance%", Component.text(ServerBasics.getBasicEconomy().formatMoney(balance)));
            player.sendMessage(MessageParser.parseMessage(player, ServerBasics.getLang(player.locale()).balance, placeholders));
        });
    }

    @CommandMethod("balance <player>")
    @CommandDescription("Check someone's balance")
    @CommandPermission("serverbasics.command.balance.others")
    private void commandBalanceOthers(
            final CommandSender sender,
            final @Argument(value = "player") SinglePlayerSelector singlePlayerSelector
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
            checkBalance(sender, offlinePlayer.getUniqueId());
        } else {
            checkBalance(sender, singlePlayerSelector.getPlayer().getUniqueId());
        }
    }

    private void checkBalance(CommandSender feedback, UUID uuid) {
        CompletableFuture<BasicPlayer> basicPlayerFuture = ServerBasics.getBasicPlayers().getBasicPlayer(uuid);
        CompletableFuture<BasicEconomyAccount> basicEconAccount = ServerBasics.getBasicEconomy().getEconomyAccount(uuid);
        CompletableFuture.allOf(basicEconAccount, basicPlayerFuture).thenRun(() -> {
            BasicPlayer basicPlayer = basicPlayerFuture.join();
            BasicEconomyAccount economyAccount = basicEconAccount.join();
            HashMap<String, Component> placeholders = new HashMap<>();
            placeholders.put("%balance%", Component.text(ServerBasics.getBasicEconomy().formatMoney(economyAccount.getBalance())));
            placeholders.put("%player%", basicPlayer.getDisplayName());
            feedback.sendMessage(MessageParser.parseMessage(feedback, ServerBasics.getLang(feedback).balance_other, placeholders));
        });
    }

}
