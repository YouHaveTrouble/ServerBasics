package eu.endermite.serverbasics.locations;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;

@Data
@AllArgsConstructor
public class SBasicLocation {
    private Location location;
    private final String name;
}
