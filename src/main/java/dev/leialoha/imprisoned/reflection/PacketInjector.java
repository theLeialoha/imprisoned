package dev.leialoha.imprisoned.reflection;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import dev.leialoha.imprisoned.ImprisonedPlugin;
import dev.leialoha.imprisoned.destruction.DestructionHandler;
import dev.leialoha.imprisoned.world.BlockLocation;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;

public class PacketInjector {
    
    private static final Logger LOGGER;
    private static final String HANDLER_NAME;

    private static final List<Class<?>> IGNORED_CLASSES;

    public static void injectPlayer(Player player) {
        Class<?> blockAction = BukkitReflectionUtils.getNMSClass("ServerboundPlayerActionPacket", "net.minecraft.network.protocol.game");
        Class<?> blockPosClass = BukkitReflectionUtils.getNMSClass("BlockPos", "net.minecraft.core");

        Class<?> blockActionType = BukkitReflectionUtils.getNMSClass("ServerboundPlayerActionPacket$Action", "net.minecraft.network.protocol.game");
        Object[] blockActionTypes = (Object[]) Reflection.callStaticMethod(blockActionType, "values");
        Object START_BREAK = blockActionTypes[0];
        Object ABORT_BREAK = blockActionTypes[1];
        Object STOP_BREAK = blockActionTypes[2];

        Optional<ChannelPipeline> oPipeline = getPipeline(player);

        if (!oPipeline.isPresent()) return;
        ChannelPipeline pipeline = oPipeline.get();

        ChannelDuplexHandler handler = new ChannelDuplexHandler() {
            // PacketPlayIn (from client to server)
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                Player packetPlayer = Bukkit.getPlayer(player.getUniqueId());

                if (packet.getClass().equals(blockAction)) {
                    Object type = Reflection.getFieldByType(packet, blockAction, blockActionType);
                    Object blockPos = Reflection.getFieldByType(packet, blockAction, blockPosClass);

                    Location bukkitLocation = BukkitReflectionUtils.getLocation(blockPos, packetPlayer.getWorld());
                    BlockLocation location = BlockLocation.from(bukkitLocation);

                    if (type.equals(START_BREAK)) DestructionHandler.startDestruction(location, player);
                    else if (type.equals(ABORT_BREAK)) DestructionHandler.stopDestruction(location, player);
                    else if (type.equals(STOP_BREAK)) DestructionHandler.stopDestruction(location, player);
                }

                if (!IGNORED_CLASSES.contains(packet.getClass()))
                    LOGGER.info(packet.getClass().getName());
                super.channelRead(ctx, packet);
            };
            
            // PacketPlayOut (from server to client)
            @Override
            public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
                if (!IGNORED_CLASSES.contains(packet.getClass()))
                    LOGGER.info(packet.getClass().getName());
                super.write(ctx, packet, promise);
            };
        };


        pipeline.addBefore("packet_handler", HANDLER_NAME, handler);
    }

    public static void uninjectPlayer(Player player) {
        Optional<ChannelPipeline> oPipeline = getPipeline(player);

        if (!oPipeline.isPresent()) return;
        ChannelPipeline pipeline = oPipeline.get();

        if (pipeline.get(HANDLER_NAME) != null)
            pipeline.remove(HANDLER_NAME);
    }

    private static Optional<ChannelPipeline> getPipeline(Player player) {
        Class<?> scPacketListenerClass = BukkitReflectionUtils.getNMSClass("ServerCommonPacketListenerImpl", "net.minecraft.server.network");
        Class<?> networkManagerClass = BukkitReflectionUtils.getNMSClass("Connection", "net.minecraft.network");
        
        return BukkitReflectionUtils.getConnection(player)
            .map(p -> Reflection.getFieldByType(p, scPacketListenerClass, networkManagerClass))
            .map(m -> Reflection.getFieldByType(m, networkManagerClass, Channel.class))
            .map(Channel::pipeline);
    }


    static {
        LOGGER = Logger.getLogger(PacketInjector.class.getSimpleName());

        HANDLER_NAME = ImprisonedPlugin
            .getPlugin(ImprisonedPlugin.class)
            .getName() + "_handler";
            
            
        String nmsGamePackage = "net.minecraft.network.protocol.game";

        IGNORED_CLASSES = List.of(
            BukkitReflectionUtils.getNMSClass("ServerboundMovePlayerPacket$Pos", nmsGamePackage),
            BukkitReflectionUtils.getNMSClass("ServerboundMovePlayerPacket$Rot", nmsGamePackage),
            BukkitReflectionUtils.getNMSClass("ServerboundMovePlayerPacket$PosRot", nmsGamePackage),
            BukkitReflectionUtils.getNMSClass("ServerboundMovePlayerPacket$StatusOnly", nmsGamePackage),
            BukkitReflectionUtils.getNMSClass("ServerboundClientTickEndPacket", nmsGamePackage),
            BukkitReflectionUtils.getNMSClass("ServerboundSwingPacket", nmsGamePackage),

            BukkitReflectionUtils.getNMSClass("ClientboundLevelChunkWithLightPacket", nmsGamePackage),
            BukkitReflectionUtils.getNMSClass("ClientboundSetTimePacket", nmsGamePackage),
            BukkitReflectionUtils.getNMSClass("ClientboundPlayerInfoUpdatePacket", nmsGamePackage),

            BukkitReflectionUtils.getNMSClass("ClientboundKeepAlivePacket", "net.minecraft.network.protocol.common"),
            BukkitReflectionUtils.getNMSClass("ServerboundKeepAlivePacket", "net.minecraft.network.protocol.common")
        );
    }
}
