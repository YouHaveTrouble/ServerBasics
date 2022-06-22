package me.youhavetrouble.serverbasics.util;

import me.youhavetrouble.serverbasics.ServerBasics;
import lombok.Builder;
import me.youhavetrouble.serverbasics.messages.MessageParser;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

@Builder
public class BasicWarp {

    private final String warpId;
    private String displayName;
    private Location location;
    private boolean requiresPermission;

    public Component getDisplayName() {
        if (displayName == null) return Component.text(warpId);
        return MessageParser.miniMessage.deserialize(displayName);
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

    public boolean requiresPermission() {
        return requiresPermission;
    }

    public void requiresPermission(boolean requiresPermission) {
        this.requiresPermission = requiresPermission;
    }
}
