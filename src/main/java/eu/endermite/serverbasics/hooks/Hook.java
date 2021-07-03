package eu.endermite.serverbasics.hooks;


import org.bukkit.Bukkit;

public class Hook {

    private final String name, checkClass;

    public Hook(String name, String checkClass) {
        this.name = name;
        this.checkClass = checkClass;
    }

    public String getName() {
        return name;
    }

    public boolean classExists() {
        if (checkClass == null) return false;
        try {
            Class.forName(checkClass);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean pluginEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled(name);
    }



}
