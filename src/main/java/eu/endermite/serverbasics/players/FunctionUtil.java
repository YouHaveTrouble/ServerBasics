package eu.endermite.serverbasics.players;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FunctionUtil {

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
            World world = Bukkit.getWorld(worldString);
            return new Location(world, x, y, z, yaw.floatValue(), pitch.floatValue());
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONObject jsonFromLocation(Location location) {
        JSONObject json = new JSONObject();
        json.put("world", location.getWorld().getName());
        json.put("x", location.getX());
        json.put("y", location.getY());
        json.put("z", location.getZ());
        json.put("pitch", location.getPitch());
        json.put("yaw", location.getYaw());
        return json;
    }

}
