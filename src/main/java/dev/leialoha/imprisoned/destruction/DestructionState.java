package dev.leialoha.imprisoned.destruction;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import dev.leialoha.imprisoned.reflection.BukkitReflectionUtils;
import dev.leialoha.imprisoned.reflection.Reflection;
import dev.leialoha.imprisoned.world.BlockLocation;
import dev.leialoha.imprisoned.world.MineableWorld;
import dev.leialoha.imprisoned.world.data.BlockMetaData;

public class DestructionState {

    private static final Class<?> DESTRUCTION_PACKET;

    private final Set<Player> attackers = new HashSet<>();
    private final BlockMetaData data;
    private final BlockLocation pos;

    private int maxHealth;
    private int health;
    private int lastState = -1;

    public DestructionState(BlockLocation pos) {
        this.data = MineableWorld.getMetaData(pos);
        this.pos = pos;

        this.maxHealth = data.getMaxHealth();
        this.health = maxHealth;
    }

    public DestructionState startAttackBlock(Player player) {
        if (!this.beenDestroyed())
            this.attackers.add(player);

        return this;
    }

    public DestructionState stopAttackBlock(Player player) {
        this.attackers.remove(player);

        if (!this.beenDestroyed() && this.attackers.isEmpty()) {
            this.health = this.maxHealth;
            sendPacket();
        }

        return this;
    }

    public void onTick() {
        if (this.beenDestroyed()) return;
        if (attackers.size() == 0) return;

        int attackAmount = attackers.stream()
            .map(p -> p.getEquipment())
            .map(e -> e.getItemInMainHand())
            .map(data::getDamageAmount)
            .reduce((t, u) -> t + u)
            .orElse(0);

        this.health -= attackAmount;

        sendPacket();

        if (this.beenDestroyed()) 
            DestructionHandler.breakBlock(this.pos, this.attackers);
    }

    private void sendPacket() {

        int state = (int) Math.floor(((this.maxHealth - this.health) * 11f) / (float) this.maxHealth) - 1;
        if (this.lastState != state) {

            // ClientboundBlockDestructionPacket
            Location bukkitLocation = pos.getLocation();
            Object blockPos = BukkitReflectionUtils.getBlockPos(bukkitLocation);

            try {
                Constructor<?> constructor = DESTRUCTION_PACKET.getConstructor(int.class, blockPos.getClass(), int.class);
                Object packet = Reflection.createInstance(constructor, 199, blockPos, state);
                BukkitReflectionUtils.sendPacketToNearby(packet, pos.getLocation(), getBlockId());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            this.lastState = state;
        }
    }

    private int getBlockId() {
        int id = 9301;

        id = id ^ (pos.x() << 13);
        id = id ^ (pos.y() >> 7);
        id = id ^ (pos.z() << 17);

        return id & 0b11111111111 | 0b000000000001;
    } 

    public BlockMetaData getBlockData() {
        return data;
    }

    public boolean beenDestroyed() {
        return this.health < 0;
    }

    static {
        DESTRUCTION_PACKET = BukkitReflectionUtils.getNMSClass("ClientboundBlockDestructionPacket", "net.minecraft.network.protocol.game");
    }

}
