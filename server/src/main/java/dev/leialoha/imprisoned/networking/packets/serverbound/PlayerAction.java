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
import dev.leialoha.imprisoned.utils.MinecraftUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;

public class PlayerAction implements PacketListener {
    
    @SuppressWarnings("incomplete-switch")
    @HandlePacket(ServerboundPlayerActionPacket.class)
    public void onClientPacket(ServerboundPlayerActionPacket packet, PacketHandler handler) {
        Player player = handler.getPlayer();
        World world = player.getWorld();

        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        ServerboundPlayerActionPacket.Action action = packet.getAction();
        BlockPos blockPos = packet.getPos();

        Location bukkitLocation = MinecraftUtils.getLocation(blockPos, world);
        BlockLocation location = BlockLocation.from(bukkitLocation);

        switch (action) {
            case START_DESTROY_BLOCK:
                boolean allowed = DestructionHandler.startAction(location, player);
                handler.setMining(allowed);
                break;
            case ABORT_DESTROY_BLOCK:
            case STOP_DESTROY_BLOCK:
                DestructionHandler.stopAction(location, player);
                handler.setMining(false);
        }
    }
}
