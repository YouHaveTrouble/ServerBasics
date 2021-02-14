package eu.endermite.serverbasics.nms.v1_16_R3;

import eu.endermite.serverbasics.api.NMS;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftOfflinePlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftMetaBook;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;

import java.io.File;
import java.lang.reflect.Field;
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

    public void setOfflinePlayerPostion(OfflinePlayer offlinePlayer, Location location) {
        try {
            final Method _getData = CraftOfflinePlayer.class.getDeclaredMethod("getData");
            _getData.setAccessible(true);
            NBTTagCompound nbt = (NBTTagCompound) _getData.invoke(offlinePlayer);
            nbt.setLong("WorldUUIDMost", location.getWorld().getUID().getMostSignificantBits());
            nbt.setLong("WorldUUIDLeast", location.getWorld().getUID().getLeastSignificantBits());
            NBTTagList pos = new NBTTagList();
            pos.add(NBTTagDouble.a(location.getX()));
            pos.add(NBTTagDouble.a(location.getY()));
            pos.add(NBTTagDouble.a(location.getZ()));
            nbt.set("Pos", pos);
            NBTTagList rotation = new NBTTagList();
            rotation.add(NBTTagFloat.a(location.getYaw()));
            rotation.add(NBTTagFloat.a(location.getPitch()));
            nbt.set("Rotation", rotation);

            savePlayerData(nbt, offlinePlayer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOfflinePlayerGamemode(OfflinePlayer offlinePlayer, GameMode gameMode) {
        try {
            final Method _getData = CraftOfflinePlayer.class.getDeclaredMethod("getData");
            _getData.setAccessible(true);

            NBTTagCompound nbt = (NBTTagCompound) _getData.invoke(offlinePlayer);
            switch (gameMode) {
                case SURVIVAL:
                    nbt.setInt("playerGameType", 0);
                    break;
                case CREATIVE:
                    nbt.setInt("playerGameType", 1);
                    break;
                case ADVENTURE:
                    nbt.setInt("playerGameType", 2);
                    break;
                case SPECTATOR:
                    nbt.setInt("playerGameType", 3);
                    break;
            }

            savePlayerData(nbt, offlinePlayer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void savePlayerData(NBTTagCompound nbtTagCompound, OfflinePlayer offlinePlayer) {
        try {
            Server server = Bukkit.getServer();
            Field dedicatedServerField = server.getClass().getDeclaredField("console");
            dedicatedServerField.setAccessible(true);
            DedicatedServer dedicatedServer = (DedicatedServer) dedicatedServerField.get(server);
            File playerDir = dedicatedServer.worldNBTStorage.getPlayerDir();
            File file = File.createTempFile(offlinePlayer.getUniqueId().toString() + "-", ".dat", playerDir);
            NBTCompressedStreamTools.a(nbtTagCompound, file);
            File file1 = new File(playerDir, offlinePlayer.getUniqueId().toString() + ".dat");
            File file2 = new File(playerDir, offlinePlayer.getUniqueId().toString() + ".dat_old");
            SystemUtils.a(file1, file, file2);
        } catch (Exception e) {
            Bukkit.getServer().getLogger().severe("Failed to save player data for "+offlinePlayer.getUniqueId().toString());
        }
    }

    
}
