package eu.endermite.serverbasics.util;

import eu.endermite.serverbasics.ServerBasics;
import lombok.Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;

@Builder
public class BasicWarp {

    private final String warpId;
    private Component displayName;
    private Location location;

    public Component getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String minimessage) {
        displayName = MiniMessage.markdown().parse(minimessage);
        ServerBasics.getInstance().getDatabase().saveWarp(this);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        ServerBasics.getInstance().getDatabase().saveWarp(this);
    }
}
