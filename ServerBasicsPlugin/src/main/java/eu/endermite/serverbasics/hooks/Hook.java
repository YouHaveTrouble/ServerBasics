package eu.endermite.serverbasics.hooks;

import lombok.Builder;
import org.bukkit.Bukkit;

@Builder
public class Hook {

    private String name;
    private String checkClass;

    public String getName() {
        return name;
    }

    public boolean classExists() {
        try {
            Class.forName(checkClass);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean pluginEnabled() {
        if (Bukkit.getPluginManager().isPluginEnabled(name)) {
            return true;
        } else {
            return false;
        }
    }

}
