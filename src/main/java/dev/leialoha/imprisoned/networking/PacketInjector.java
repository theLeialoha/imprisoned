package dev.leialoha.imprisoned.networking;

import java.util.Optional;

import org.bukkit.entity.Player;

import dev.leialoha.imprisoned.ImprisonedPlugin;
import dev.leialoha.imprisoned.reflection.BukkitReflectionUtils;
import dev.leialoha.imprisoned.reflection.Reflection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelPipeline;

public class PacketInjector {
    
    private static final String HANDLER_NAME;

    public static void injectPlayer(Player player) {
        Optional<ChannelPipeline> oPipeline = getPipeline(player);

        if (!oPipeline.isPresent()) return;
        ChannelPipeline pipeline = oPipeline.get();

        ChannelDuplexHandler handler = new PacketHandler(player);
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
        HANDLER_NAME = ImprisonedPlugin
            .getPlugin(ImprisonedPlugin.class)
            .getName() + "_handler";
    }
}
