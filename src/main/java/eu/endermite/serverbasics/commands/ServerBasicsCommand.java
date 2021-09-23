package eu.endermite.serverbasics.commands;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.commands.registration.CommandRegistration;
import eu.endermite.serverbasics.hooks.Hook;
import eu.endermite.serverbasics.messages.MessageParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CommandRegistration
public class ServerBasicsCommand {

    @CommandMethod("serverbasics version")
    @CommandDescription("Display ServerBasics version")
    @CommandPermission("serverbasics.command.serverbasics")
    private void commandServerBasicsVersion(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        String msg = "ServerBasics " + plugin.getDescription().getVersion();
        MessageParser.sendMessage(sender, msg);
    }

    @CommandMethod("serverbasics debug")
    @CommandDescription("Display ServerBasics version")
    @CommandPermission("serverbasics.command.serverbasics")
    private void commandServerBasicsDebug(
            final CommandSender sender
    ) {
        sender.sendMessage(Component.text("---------- ServerBasics ----------"));
        sender.sendMessage(Component.text("Plugin version: " + ServerBasics.getInstance().getDescription().getVersion()));
        sender.sendMessage(Component.text("Server version: " + Bukkit.getVersion()));
        sender.sendMessage(Component.text("NMS version: " + Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit", "").replace(".", "")));
        sender.sendMessage(getHooksComponent(sender));
    }

    private Component getHooksComponent(CommandSender sender) {
        Component hooksComponent = Component.text("Hooks (hookamount): ");

        int hooks = 0;
        List<Component> inactive = new ArrayList<>();
        for (Map.Entry<String, Hook> e : ServerBasics.getHooks().getHooks().entrySet()) {
            if (hooks > 0) {
                hooksComponent = hooksComponent.append(Component.text(", "));
            }

            if (e.getValue().pluginEnabled()) {
                Component hoverText = MessageParser.miniMessage.parse(ServerBasics.getLang(sender).getHookDesc(e.getKey()));
                HoverEvent<Component> hoverEvent = HoverEvent.showText(hoverText);
                hooksComponent = hooksComponent.append(Component.text(e.getKey()).color(NamedTextColor.GREEN).hoverEvent(hoverEvent));
                hooks++;
            } else {
                String fix = String.format(ServerBasics.getLang(sender).hook_inactive, e.getKey());
                Component hoverText = Component.text(ServerBasics.getLang(sender).getHookDesc(e.getKey()))
                        .append(Component.newline())
                        .append(Component.text()
                                .append(Component.newline())
                                .append(MessageParser.miniMessage.parse(fix)));
                HoverEvent<Component> hoverEvent = HoverEvent.showText(hoverText);
                Component inactiveComponent = Component.text(e.getKey()).color(NamedTextColor.RED).hoverEvent(hoverEvent);
                inactive.add(inactiveComponent);
            }
        }

        int inactiveCount = 0;
        for (Component component : inactive) {
            if (inactiveCount > 1) {
                hooksComponent = hooksComponent.append(Component.text(", "));
            }
            hooksComponent = hooksComponent.append(component);
            inactiveCount++;
        }

        TextReplacementConfig replacer = TextReplacementConfig.builder()
                .times(1)
                .match("hookamount")
                .replacement(String.valueOf(hooks))
                .build();
        hooksComponent = hooksComponent.replaceText(replacer);

        return hooksComponent;
    }

    @CommandMethod("serverbasics reload")
    @CommandDescription("Reload all ServerBasics configurations")
    @CommandPermission("serverbasics.command.serverbasics.reload")
    private void commandServerBasicsReloadAll(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
            plugin.reloadConfigs();
            plugin.reloadLang();
            plugin.reloadLocations();
            MessageParser.sendMessage(sender, ServerBasics.getLang(sender).all_configs_reloaded);
        });
    }

    @CommandMethod("serverbasics reload config")
    @CommandDescription("Reload ServerBasics configuration")
    @CommandPermission("serverbasics.command.serverbasics.reload")
    private void commandServerBasicsReloadConf(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
            plugin.reloadConfigs();
            MessageParser.sendMessage(sender, ServerBasics.getLang(sender).config_reloaded);
        });
    }

    @CommandMethod("serverbasics reload language")
    @CommandDescription("Reload ServerBasics language files")
    @CommandPermission("serverbasics.command.serverbasics.reload")
    private void commandServerBasicsReloadLang(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(ServerBasics.getInstance(), () -> {
            plugin.reloadLang();
            MessageParser.sendMessage(sender, ServerBasics.getLang(sender).lang_reloaded);
        });
    }

    @CommandMethod("serverbasics")
    @CommandDescription("Main ServerBasics command")
    @CommandPermission("serverbasics.command.serverbasics")
    private void commandServerBasics(
            final CommandSender sender
    ) {
        ServerBasics plugin = ServerBasics.getInstance();
        String msg = "<white>ServerBasics " + plugin.getDescription().getVersion();
        MessageParser.sendMessage(sender, msg);
    }

}
