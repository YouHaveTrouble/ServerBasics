package me.youhavetrouble.serverbasics;

import net.minecraft.Util;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class NMSHandler {


    private static final MinecraftServer minecraftServer = MinecraftServer.getServer();
    private static final PlayerDataStorage playerData = minecraftServer.playerDataStorage;

    public static Location getOfflinePlayerPosition(OfflinePlayer offlinePlayer) {
        CompoundTag nbt = playerData.getPlayerData(offlinePlayer.getUniqueId().toString());
        ListTag coords = nbt.getList("Pos", CraftMagicNumbers.NBT.TAG_DOUBLE);
        long worldUuidMost = nbt.getLong("WorldUUIDMost");
        long worldUuidLeast = nbt.getLong("WorldUUIDLeast");
        ListTag rotation = nbt.getList("Rotation", CraftMagicNumbers.NBT.TAG_FLOAT);
        float yaw = rotation.getFloat(0);
        float pitch = rotation.getFloat(1);
        UUID worldUuid = new UUID(worldUuidMost, worldUuidLeast);
        World world = Bukkit.getWorld(worldUuid);
        double x = coords.getDouble(0);
        double y = coords.getDouble(1);
        double z = coords.getDouble(2);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static void setOfflinePlayerPosition(OfflinePlayer offlinePlayer, Location location) {
        CompoundTag nbt = playerData.getPlayerData(offlinePlayer.getUniqueId().toString());
        nbt.putLong("WorldUUIDMost", location.getWorld().getUID().getMostSignificantBits());
        nbt.putLong("WorldUUIDLeast", location.getWorld().getUID().getLeastSignificantBits());
        ListTag pos = new ListTag();
        pos.add(DoubleTag.valueOf(location.getX()));
        pos.add(DoubleTag.valueOf(location.getY()));
        pos.add(DoubleTag.valueOf(location.getZ()));
        nbt.put("Pos", pos);
        ListTag rotation = new ListTag();
        rotation.add(FloatTag.valueOf(location.getYaw()));
        rotation.add(FloatTag.valueOf(location.getPitch()));
        nbt.put("Rotation", rotation);
        savePlayerData(nbt, offlinePlayer);
    }

    public static void setOfflinePlayerGamemode(OfflinePlayer offlinePlayer, GameMode gameMode) {
        CompoundTag nbt = playerData.getPlayerData(offlinePlayer.getUniqueId().toString());
        switch (gameMode) {
            case SURVIVAL -> nbt.putInt("playerGameType", 0);
            case CREATIVE -> nbt.putInt("playerGameType", 1);
            case ADVENTURE -> nbt.putInt("playerGameType", 2);
            case SPECTATOR -> nbt.putInt("playerGameType", 3);
        }
        savePlayerData(nbt, offlinePlayer);
    }

    public static GameMode getOfflinePlayerGamemode(OfflinePlayer offlinePlayer) {
        CompoundTag nbt = playerData.getPlayerData(offlinePlayer.getUniqueId().toString());
        return switch (nbt.getInt("playerGameType")) {
            case 0 -> GameMode.SURVIVAL;
            case 1 -> GameMode.CREATIVE;
            case 2 -> GameMode.ADVENTURE;
            case 3 -> GameMode.SPECTATOR;
            default -> null;
        };
    }

    public static boolean getOfflinePlayerCanFly(OfflinePlayer offlinePlayer) {
        CompoundTag nbt = playerData.getPlayerData(offlinePlayer.getUniqueId().toString());
        CompoundTag abilities = nbt.getCompound("abilities");
        return abilities.getByte("mayfly") == 1;
    }

    public static void setOfflinePlayerCanFly(OfflinePlayer offlinePlayer, boolean canFly) {
        CompoundTag nbt = playerData.getPlayerData(offlinePlayer.getUniqueId().toString());
        CompoundTag abilities = nbt.getCompound("abilities");
        if (canFly)
            abilities.putByte("mayfly", (byte) 1);
        else
            abilities.putByte("mayfly", (byte) 0);
        nbt.put("abilities", abilities);
        savePlayerData(nbt, offlinePlayer);
    }

    public static void setOfflinePlayerFallDistance(OfflinePlayer offlinePlayer, float distance) {
        CompoundTag nbt = playerData.getPlayerData(offlinePlayer.getUniqueId().toString());
        nbt.putFloat("FallDistance", distance);
        savePlayerData(nbt, offlinePlayer);
    }

    private static void savePlayerData(CompoundTag nbtTagCompound, OfflinePlayer offlinePlayer) {
        try {
            File playerDir = playerData.getPlayerDir();
            File file = File.createTempFile(offlinePlayer.getUniqueId() + "-", ".dat", playerDir);
            NbtIo.writeCompressed(nbtTagCompound, file);
            File file1 = new File(playerDir, offlinePlayer.getUniqueId() + ".dat");
            File file2 = new File(playerDir, offlinePlayer.getUniqueId() + ".dat_old");
            Util.safeReplaceFile(file1, file, file2);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe("Failed to save player data for " + offlinePlayer.getUniqueId());
        }
    }


}
