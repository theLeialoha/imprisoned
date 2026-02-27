package dev.leialoha.imprisoned.networking;

import org.bukkit.entity.Player;

import dev.leialoha.imprisoned.ImprisonedPlugin;
import dev.leialoha.imprisoned.utils.MinecraftUtils;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class PacketInjector {
    
    private static final String HANDLER_NAME;

    public static void injectPlayer(Player player) {
        ChannelPipeline pipeline = getPipeline(player);
        if (pipeline == null) return;

        ChannelDuplexHandler handler = new PacketHandler(player);
        pipeline.addBefore("packet_handler", HANDLER_NAME, handler);
    }

    public static void uninjectPlayer(Player player) {
        ChannelPipeline pipeline = getPipeline(player);
        if (pipeline == null) return;

        if (pipeline.get(HANDLER_NAME) != null)
            pipeline.remove(HANDLER_NAME);
    }

    private static ChannelPipeline getPipeline(Player player) {
        ServerGamePacketListenerImpl listener = MinecraftUtils.getPacketListener(player);
        Connection connection = listener.connection;
        return connection.channel.pipeline();
    }


    static {
        HANDLER_NAME = ImprisonedPlugin
            .getPlugin(ImprisonedPlugin.class)
            .getName() + "_handler";
    }
}
