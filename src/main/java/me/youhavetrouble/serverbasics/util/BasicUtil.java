package me.youhavetrouble.serverbasics.util;

import me.youhavetrouble.serverbasics.ServerBasics;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Locale;
import java.util.UUID;

public class BasicUtil {

    /**
     * @param json JSON string to deserialize
     * @return Bukkit's Location object using world uuid and coordinates from json
     */
    public static Location locationFromJson(String json) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(json);
            String worldString = (String) jsonObject.get("world");
            double x = (double) jsonObject.get("x");
            double y = (double) jsonObject.get("y");
            double z = (double) jsonObject.get("z");
            Double pitch = (Double) jsonObject.get("pitch");
            Double yaw = (Double) jsonObject.get("yaw");
            World world = Bukkit.getWorld(UUID.fromString(worldString));
            return new Location(world, x, y, z, yaw.floatValue(), pitch.floatValue());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Serializes Bukkit Location to JSONObject
     * @param location the location
     * @return the bukkit location as a JSONObject
     */
    public static JSONObject jsonFromLocation(Location location) {
        JSONObject json = new JSONObject();
        json.put("world", location.getWorld().getUID().toString());
        json.put("x", location.getX());
        json.put("y", location.getY());
        json.put("z", location.getZ());
        json.put("pitch", location.getPitch());
        json.put("yaw", location.getYaw());
        return json;
    }

    /**
     * Gets pretty player name, entity custom name or entity name as Component
     * @param entity the entity to prettify
     * @return prettified entity name
     */
    public static Component entityName(Entity entity) {
        if (entity instanceof Player targetPlayer) {
            return targetPlayer.displayName();
        } else {
            if (entity.getCustomName() != null)
                return Component.text(entity.getCustomName());
            else
                return Component.text(entity.getName());
        }
    }

    /**
     * Returns the {@link Locale} of the player
     * @param sender the sender to check against
     * @return the locale of the player
     */
    public static Locale playerLocaleOrDefault(CommandSender sender) {
        if (sender instanceof Player player)
            return player.locale();
        else
            return ServerBasics.getConfigCache().default_lang;
    }

}
