package dev.leialoha.imprisoned.mines.destruction;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import dev.leialoha.imprisoned.mines.Tickable;
import dev.leialoha.imprisoned.mines.world.BlockLocation;
import dev.leialoha.imprisoned.mines.world.MineableWorld;
import dev.leialoha.imprisoned.mines.world.data.BlockMetaData;
import dev.leialoha.imprisoned.utils.MinecraftUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;

public class DestructionState implements Tickable {

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

    public boolean hasAttackingPlayer(Player player) {
        return this.attackers.contains(player);
    }

    public DestructionState startAttackBlock(Player player) {
        if (!this.beenDestroyed())
            this.attackers.add(player);

        if (!this.isTicking())
            this.startTicking();

        return this;
    }

    public DestructionState stopAttackBlock(Player player) {
        this.attackers.remove(player);

        if (!this.beenDestroyed() && this.attackers.isEmpty())
            setHealth(this.maxHealth);

        return this;
    }

    @Override
    public void onTick() {
        if (this.beenDestroyed() || attackers.isEmpty()) {
            this.stopTicking();
            return;
        }

        int attackAmount = attackers.stream()
            .map(p -> p.getEquipment())
            .map(e -> e.getItemInMainHand())
            .map(data::getDamageAmount)
            .reduce((t, u) -> t + u)
            .orElse(0);

        setHealth(this.health - attackAmount);

        if (this.beenDestroyed()) 
            DestructionHandler.breakBlock(this.pos, this.attackers);
    }

    private void sendPacket() {

        int state = (int) Math.floor(((this.maxHealth - this.health) * 11f) / (float) this.maxHealth) - 1;
        if (this.lastState != state) {

            Location bukkitLocation = pos.getLocation();
            BlockPos blockPos = MinecraftUtils.getBlockPos(bukkitLocation);

            ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(199, blockPos, state);
            MinecraftUtils.sendPacketToNearby(packet, pos.getLocation(), getBlockId());

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

    private void setHealth(int health) {
        this.health = health;
        sendPacket();
    }

}
