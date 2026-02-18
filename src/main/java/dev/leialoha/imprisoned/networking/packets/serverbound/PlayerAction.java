package dev.leialoha.imprisoned.networking.packets.serverbound;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import dev.leialoha.imprisoned.mines.destruction.DestructionHandler;
import dev.leialoha.imprisoned.mines.world.BlockLocation;
import dev.leialoha.imprisoned.networking.PacketHandler;
import dev.leialoha.imprisoned.networking.annotations.HandlePacket;
import dev.leialoha.imprisoned.networking.packets.PacketListener;
import dev.leialoha.imprisoned.reflection.BukkitReflectionUtils;
import dev.leialoha.imprisoned.reflection.Reflection;

public class PlayerAction implements PacketListener {

    private static final Class<?> BLOCK_POS_CLASS;
    private static final Class<?> PLAYER_ACTION_CLASS;
    private static final Class<?> PLAYER_ACTIONS_CLASS;
    private static final Object[] PLAYER_ACTIONS;
    
    @HandlePacket("ServerboundPlayerActionPacket")
    public boolean onClientPacket(Object packet, PacketHandler handler) {
        Player player = handler.getPlayer();
        World world = player.getWorld();

        if (player.getGameMode().equals(GameMode.CREATIVE))
            return true;

        Object type = Reflection.getFieldByType(packet, PLAYER_ACTION_CLASS, PLAYER_ACTIONS_CLASS);
        Object blockPos = Reflection.getFieldByType(packet, PLAYER_ACTION_CLASS, BLOCK_POS_CLASS);

        final Object START_BREAK = PLAYER_ACTIONS[0];
        final Object ABORT_BREAK = PLAYER_ACTIONS[1];
        final Object STOP_BREAK = PLAYER_ACTIONS[2];

        Location bukkitLocation = BukkitReflectionUtils.getLocation(blockPos, world);
        BlockLocation location = BlockLocation.from(bukkitLocation);

        if (type.equals(START_BREAK)) {
            boolean allowed = DestructionHandler.startAction(location, player);
            handler.setMining(allowed);
        } else if (type.equals(ABORT_BREAK) || type.equals(STOP_BREAK)) {
            DestructionHandler.stopAction(location, player);
            handler.setMining(false);
        }

        return true;
    }

    static {
        BLOCK_POS_CLASS = BukkitReflectionUtils.getNMSClass("BlockPos", "net.minecraft.core");
        PLAYER_ACTION_CLASS = BukkitReflectionUtils.getPacketClass("ServerboundPlayerActionPacket");
        PLAYER_ACTIONS_CLASS = BukkitReflectionUtils.getPacketClass("ServerboundPlayerActionPacket$Action");
        PLAYER_ACTIONS = (Object[]) Reflection.callStaticMethod(PLAYER_ACTIONS_CLASS, "values");
    }

}
