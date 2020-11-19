package eu.endermite.serverbasics.locations;

import org.bukkit.Location;

public class SBasicLocation {

    private Location location;
    private final String name;

    public SBasicLocation(Location location, String name) {
        this.location = location;
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
