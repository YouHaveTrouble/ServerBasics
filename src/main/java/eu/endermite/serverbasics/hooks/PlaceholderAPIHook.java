package eu.endermite.serverbasics.hooks;

import eu.endermite.serverbasics.ServerBasics;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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
        return null;
    }
}
