package eu.endermite.serverbasics.v1_16_R3;

import eu.endermite.serverbasics.api.NMS;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_16_R3.CraftOfflinePlayer;
import java.lang.reflect.Method;

public class NMSHandler implements NMS {
    public Location getPlayerPostition(OfflinePlayer player) {
        try {
            final Method _getData = CraftOfflinePlayer.class.getDeclaredMethod("getData");
            _getData.setAccessible(true);
            NBTTagCompound nbt = (NBTTagCompound) _getData.invoke(player);



            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }



    }
}
