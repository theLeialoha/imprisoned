package dev.leialoha.imprisoned.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public class MinecraftUtils {

    public static ServerGamePacketListenerImpl getPacketListener(Player player) {
        ServerPlayer serverPlayer = getHandle(player);
        return serverPlayer.connection;
    }

    public static ServerPlayer getHandle(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        return craftPlayer.getHandle();
    }

    public static net.minecraft.world.item.ItemStack getNMSItem(ItemStack item) {
        return CraftItemStack.asNMSCopy(item);
    }

    public static ItemStack getBukkitItem(net.minecraft.world.item.ItemStack item) {
        return CraftItemStack.asBukkitCopy(item);
    }

    public static BlockState getIBlockData(Material material) throws Exception {
        CraftBlockData blockData = CraftBlockData.newData(material.asBlockType(), null);
        return blockData.getState();
    }

    public static LevelChunk cloneChunk(LevelChunk chunk) throws Exception {
        Level level = chunk.getLevel();
        ChunkPos pos = chunk.getPos();

        return new LevelChunk(level, pos);
    }

    public static Location getLocation(BlockPos blockPos, World world) {
        return new Location(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static BlockPos getBlockPos(Location location) {
        return new BlockPos(location.blockX(), location.blockY(), location.blockZ());
    }


    // public static void listVariables(Player player) throws Exception {
    //     Class<?> entityPlayerClass = ReflectionUtils.getNMSClass("Entity", "net.minecraft.world.entity");

    //     Method getPrevXMethod = entityPlayerClass.getMethod("dg");
    //     Method getPrevYMethod = entityPlayerClass.getMethod("di");
    //     Method getPrevZMethod = entityPlayerClass.getMethod("dm");
    //     Method getPrevYawMethod = entityPlayerClass.getMethod("dr");
    //     Method getPrevPitchMethod = entityPlayerClass.getMethod("dt");

    //     Object playerConnection = ReflectionUtils.getConnection(player);
    //     Object playerEntity = ReflectionUtils.getHandle(player);

    //     double d0 = a(packetplayinflying.a(this.c.dg()));
    //     double d1 = b(packetplayinflying.b(this.c.di()));
    //     double d2 = a(packetplayinflying.c(this.c.dm()));

    //     double prevX = (double) getPrevXMethod.invoke(playerEntity);
    //     double prevY = (double) getPrevYMethod.invoke(playerEntity);
    //     double prevZ = (double) getPrevZMethod.invoke(playerEntity);
    //     float prevYaw = (float) getPrevYawMethod.invoke(playerEntity);
    //     float prevPitch = (float) getPrevPitchMethod.invoke(playerEntity);
    //     double d3 = (double) getPrevXMethod.invoke(playerEntity);
    //     double d4 = (double) getPrevYMethod.invoke(playerEntity);
    //     double d5 = (double) getPrevZMethod.invoke(playerEntity);
    //     double d6 = (double) getPrevYMethod.invoke(playerEntity);
    //     double d7 = d0 - this.o;
    //     double d8 = d1 - this.p;
    //     double d9 = d2 - this.q;
    //     double d10 = this.c.de().g();
    //     double d11 = d7 * d7 + d8 * d8 + d9 * d9;
    // }

    public static void sendPacket(Packet<?> packet, Player player) {
        ServerGamePacketListenerImpl connection = getPacketListener(player);
        connection.send(packet);
    }

    public static void sendPacketToAll(Packet<?> packet) {
        Bukkit.getOnlinePlayers()
            .forEach(p -> sendPacket(packet, p));
    }

    public static void sendPacketToNearby(Packet<?> packet, Location pos) {
        sendPacketToNearby(packet, pos, 60);
    }

    public static void sendPacketToNearby(Packet<?> packet, Location pos, int i) {
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.getLocation().distance(pos) <= i)
            .forEach(p -> sendPacket(packet, p));
    }
}
