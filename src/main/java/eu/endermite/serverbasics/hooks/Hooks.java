package eu.endermite.serverbasics.hooks;

import java.util.HashMap;

public class Hooks {

    private HashMap<String, Hook> softwareHooks = new HashMap<>();
    private HashMap<String, Hook> pluginHooks = new HashMap<>();

    public Hooks() {

        // Server software hooks

        Hook spigotHook = Hook.builder()
                .name("Spigot")
                .checkClass("net.md_5.bungee.api.ChatColor")
                .build();
        spigotHook.classExists();
        softwareHooks.put(spigotHook.getName(), spigotHook);

        Hook paperHook = Hook.builder()
                .name("Paper")
                .checkClass("com.destroystokyo.paper.PaperConfig")
                .build();
        paperHook.classExists();
        softwareHooks.put(paperHook.getName(), paperHook);

        // Plugin hooks

        Hook papiHook = Hook.builder()
                .name("PlaceholderAPI")
                .build();
        pluginHooks.put(papiHook.getName(), papiHook);

    }

    public HashMap<String, Hook> getSoftwareHooks() {
        return softwareHooks;
    }

    public HashMap<String, Hook> getPluginHooks() {
        return pluginHooks;
    }

    public boolean isHooked(String name) {
        try {
            return pluginHooks.get(name).pluginEnabled() || softwareHooks.get(name).classExists();
        } catch (Exception e) {
            return false;
        }

    }
}
