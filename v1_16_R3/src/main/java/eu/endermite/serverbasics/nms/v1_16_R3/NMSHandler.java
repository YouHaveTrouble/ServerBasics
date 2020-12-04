package eu.endermite.serverbasics.nms.v1_16_R3;

import eu.endermite.serverbasics.api.NMS;
import net.minecraft.server.v1_16_R3.NBTBase;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftOfflinePlayer;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;

import java.lang.reflect.Method;
import java.util.UUID;

public class NMSHandler implements NMS {
    public Location getOfflinePlayerPostition(OfflinePlayer offlinePlayer) {
        try {
            final Method _getData = CraftOfflinePlayer.class.getDeclaredMethod("getData");
            _getData.setAccessible(true);
            NBTTagCompound nbt = (NBTTagCompound) _getData.invoke(offlinePlayer);
            NBTTagList coords = nbt.getList("Pos",CraftMagicNumbers.NBT.TAG_DOUBLE);
            long worldUuidMost = nbt.getLong("WorldUUIDMost");
            long worldUuidLeast = nbt.getLong("WorldUUIDLeast");
            NBTTagList rotation = nbt.getList("Rotation",CraftMagicNumbers.NBT.TAG_FLOAT);
            float yaw = rotation.i(0);
            float pitch = rotation.i(1);
            UUID worldUuid = new UUID(worldUuidMost, worldUuidLeast);
            World world = Bukkit.getWorld(worldUuid);
            double x = coords.h(0);
            double y = coords.h(1);
            double z = coords.h(2);
            return new Location(world, x, y, z, yaw, pitch);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setOfflinePlayerPostion(OfflinePlayer offlinePlayer, Location location) {
        try {
            final Method _getData = CraftOfflinePlayer.class.getDeclaredMethod("getData");
            _getData.setAccessible(true);
            NBTTagCompound nbt = (NBTTagCompound) _getData.invoke(offlinePlayer);
            nbt.setLong("WorldUUIDMost", location.getWorld().getUID().getMostSignificantBits());
            nbt.setLong("WorldUUIDLeast", location.getWorld().getUID().getLeastSignificantBits());

            NBTTagList list = new NBTTagList();



        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
