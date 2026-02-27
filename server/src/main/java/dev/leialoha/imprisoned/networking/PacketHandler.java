package dev.leialoha.imprisoned.networking;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ServerboundClientTickEndPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;

public class PacketHandler extends ChannelDuplexHandler {

    private final UUID uuid;

    private int currentClick;
    private boolean isMining;
    private int miningState = 0;

    public PacketHandler(Player player) {
        this.uuid = player.getUniqueId();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packetObj) throws Exception {
        Packet<?> packet = (Packet<?>) packetObj;
        Class<?> packetClass = packet.getClass();

        if (!IGNORED_CLASSES.contains(packetClass))
            LOGGER.info(packetClass.getName());

        List<ListenerData> listeners = PacketManager.get(packetClass);
        if (listeners != null) {
            boolean cancelled = !listeners.stream()
                .allMatch(p -> p.sendPacket(packet, this));

            if (cancelled) return;
        }

        super.channelRead(ctx, packet);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object packetObj, ChannelPromise promise) throws Exception {
        Packet<?> packet = (Packet<?>) packetObj;
        Class<?> packetClass = packet.getClass();

        if (!IGNORED_CLASSES.contains(packetClass))
            LOGGER.info(packetClass.getName());

        List<ListenerData> listeners = PacketManager.get(packetClass);
        if (listeners != null) {
            boolean cancelled = !listeners.stream()
                .allMatch(p -> p.sendPacket(packet, this));

            if (cancelled) return;
        }

        super.write(ctx, packet, promise);
    }

    // Why this way? If I make the player
    // as the object of reference instead of
    // looking it up, if the player dies
    // then the "death" state is saved to the
    // referenced object. Looking up the
    // player gives me the latest changes.
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean isMining() {
        return this.isMining;
    }

    public void setMining(boolean mining) {
        this.isMining = mining;
        if (!mining) miningState++;
    }

    public int getMiningState() {
        return this.miningState;
    }

    public void setCurrentClick(int currentClick) {
        this.currentClick = currentClick;
    }

    public int getNextClick() {
        return ++currentClick;
    }

    private static final Logger LOGGER;
    private static final List<Class<? extends Packet<?>>> IGNORED_CLASSES;

    static {
        LOGGER = Logger.getLogger(PacketInjector.class.getSimpleName());

        IGNORED_CLASSES = List.of(
            ServerboundMovePlayerPacket.Pos.class,
            ServerboundMovePlayerPacket.Rot.class,
            ServerboundMovePlayerPacket.PosRot.class,
            ServerboundMovePlayerPacket.StatusOnly.class,
            ServerboundClientTickEndPacket.class,
            ServerboundSwingPacket.class,
            ClientboundLevelChunkWithLightPacket.class,
            ClientboundSetTimePacket.class,
            ClientboundPlayerInfoUpdatePacket.class,
            ClientboundKeepAlivePacket.class,
            ServerboundKeepAlivePacket.class
        );
    }

}
