package me.youhavetrouble.serverbasics.hooks;

import me.youhavetrouble.serverbasics.ServerBasics;
import me.youhavetrouble.serverbasics.economy.BasicBaltopEntry;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().hexColors().build();

    @Override
    public @NotNull String getIdentifier() {
        return "serverbasics";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", ServerBasics.getInstance().getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return ServerBasics.getInstance().getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        params = params.toLowerCase();

        // %serverbasics_nickname%
        if (params.equals("nickname")) {
            if (player.isOnline())
                return serializer.serialize(player.getPlayer().displayName());
            else
                return player.getName();
        }

        System.out.println("Paarams: "+params);

        if (params.startsWith("baltop_player_"))
            return handleBaltopPlayer(params);
        if (params.startsWith("baltop_balance_formatted_"))
            return handleBaltopBalanceFormat(params);
        if (params.startsWith("baltop_balance_"))
            return handleBaltopBalance(params);


        return null;
    }

    private String handleBaltopPlayer(String param) {
        if (ServerBasics.getBasicEconomy() == null || !ServerBasics.getBasicEconomy().isBasicEconomy()) return null;
        String numToParse = param.replace("baltop_player_", "");
        System.out.println(numToParse);
        try {
            int place = Integer.parseInt(numToParse);
            List<BasicBaltopEntry> entries = ServerBasics.getBasicEconomy().getBaltop();
            if (place >= entries.size()) return "";
            return entries.get(place).getName();
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private String handleBaltopBalance(String param) {
        if (ServerBasics.getBasicEconomy() == null || !ServerBasics.getBasicEconomy().isBasicEconomy()) return null;
        String numToParse = param.replace("baltop_balance_", "");
        try {
            int place = Integer.parseInt(numToParse);
            List<BasicBaltopEntry> entries = ServerBasics.getBasicEconomy().getBaltop();
            if (place >= entries.size()) return "";
            return entries.get(place).getMoney();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String handleBaltopBalanceFormat(String param) {
        if (ServerBasics.getBasicEconomy() == null || !ServerBasics.getBasicEconomy().isBasicEconomy()) return null;
        String numToParse = param.replace("baltop_balance_formatted_", "");
        try {
            int place = Integer.parseInt(numToParse);
            List<BasicBaltopEntry> entries = ServerBasics.getBasicEconomy().getBaltop();
            if (place >= entries.size()) return "";
            return entries.get(place).getFormattedMoney();
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
