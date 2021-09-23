package eu.endermite.serverbasics.economy;

import eu.endermite.serverbasics.ServerBasics;
import eu.endermite.serverbasics.messages.MessageParser;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class BasicBaltopEntry {

    private String name;
    private Component componentName;
    private final double money;

    protected BasicBaltopEntry(UUID uuid, double money) {

        this.name = "";
        this.money = money;

        ServerBasics.getBasicPlayers().getBasicPlayer(uuid).thenAccept(basicPlayer -> {
            this.componentName = basicPlayer.getDisplayName();
            this.name = MessageParser.formattedStringFromMinimessage(basicPlayer.getDisplayName());
        });
    }

    public String getName() {
        return name;
    }

    public Component getComponentName() {
        return componentName;
    }

    public String getFormattedMoney() {
        return ServerBasics.getBasicEconomy().formatMoney(money);
    }

    public String getMoney() {
        return String.valueOf(money);
    }

}
