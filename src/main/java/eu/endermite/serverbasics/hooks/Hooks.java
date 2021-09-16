package eu.endermite.serverbasics.hooks;

import java.util.HashMap;

public class Hooks {

    private final HashMap<String, Hook> pluginHooks = new HashMap<>();

    public Hooks() {

        // Plugin hooks
        Hook papiHook = new Hook("PlaceholderAPI", null);
        if (papiHook.pluginEnabled()) {
            pluginHooks.put(papiHook.getName(), papiHook);
        }

        Hook vaultHook = new Hook("Vault", null);
        if (vaultHook.pluginEnabled()) {
            pluginHooks.put(vaultHook.getName(), vaultHook);
        }

    }

    public HashMap<String, Hook> getHooks() {
        return pluginHooks;
    }

    public boolean isHooked(String name) {
        try {
            return pluginHooks.get(name).pluginEnabled();
        } catch (Exception e) {
            return false;
        }

    }
}
