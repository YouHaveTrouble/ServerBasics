package eu.endermite.serverbasics.hooks;

import lombok.Getter;

import java.util.HashMap;

public class Hooks {

    @Getter private final HashMap<String, Hook> pluginHooks = new HashMap<>();

    public Hooks() {

        // Plugin hooks

        Hook papiHook = Hook.builder()
                .name("PlaceholderAPI")
                .build();
        if (papiHook.pluginEnabled())
            pluginHooks.put(papiHook.getName(), papiHook);

    }

    public boolean isHooked(String name) {
        try {
            return pluginHooks.get(name).pluginEnabled();
        } catch (Exception e) {
            return false;
        }

    }
}
