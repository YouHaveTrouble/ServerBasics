package eu.endermite.serverbasics.hooks;

import eu.endermite.serverbasics.ServerBasics;
import lombok.Getter;

import java.util.HashMap;

public class Hooks {

    @Getter private final HashMap<String, Hook> softwareHooks = new HashMap<>();
    @Getter private final HashMap<String, Hook> pluginHooks = new HashMap<>();

    public Hooks() {

        // Server software hooks

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
        if (papiHook.pluginEnabled())
            pluginHooks.put(papiHook.getName(), papiHook);

    }

    public boolean isHooked(String name) {
        try {
            return pluginHooks.get(name).pluginEnabled() || softwareHooks.get(name).classExists();
        } catch (Exception e) {
            return false;
        }

    }
}
