package eu.endermite.serverbasics.api;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public interface NMS {
    public Location getOfflinePlayerPostition(OfflinePlayer player);
    public void setOfflinePlayerPostion(OfflinePlayer offlinePlayer, Location location);
    public void setOfflinePlayerGamemode(OfflinePlayer offlinePlayer, GameMode gameMode);
}
