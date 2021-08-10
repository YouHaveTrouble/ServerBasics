package eu.endermite.serverbasics.util;

import eu.endermite.serverbasics.ServerBasics;
import lombok.Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;

@Builder
public class BasicWarp {

    private final String warpId;
    private String displayName;
    private Location location;

    public Component getDisplayName() {
        return MiniMessage.markdown().parse(displayName);
    }

    public String getRawDisplayName() {
        return displayName;
    }

    public void setDisplayName(String minimessage) {
        displayName = minimessage;
        ServerBasics.getInstance().getDatabase().saveWarp(this);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        ServerBasics.getInstance().getDatabase().saveWarp(this);
    }

    public String getWarpId() {
        return warpId;
    }
}
