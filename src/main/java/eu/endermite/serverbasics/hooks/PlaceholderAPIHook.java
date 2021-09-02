package eu.endermite.serverbasics.hooks;

import eu.endermite.serverbasics.ServerBasics;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {


    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().hexColors().build();

    public PlaceholderAPIHook() {
    }

    @Override
    public @NotNull String getIdentifier() {
        return "serverbasics";
    }

    @Override
    public @NotNull String getAuthor() {
        return "YouHaveTrouble";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
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
        return null;
    }
}
