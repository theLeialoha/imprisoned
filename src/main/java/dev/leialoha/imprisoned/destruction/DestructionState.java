package dev.leialoha.imprisoned.destruction;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import dev.leialoha.imprisoned.world.MineableWorld;
import dev.leialoha.imprisoned.world.data.BlockMetaData;
import net.minecraft.core.BlockPos;

public class DestructionState {

    private final Set<Player> attackers = new HashSet<>();
    private final BlockMetaData data;
    private final BlockPos pos;

    private int maxHealth;
    private int health;

    public DestructionState(BlockPos pos) {
        this.data = MineableWorld.getMetaData(pos);
        this.pos = pos;
    }

    public DestructionState startAttackBlock(Player player) {
        if (!this.beenDestroyed())
            this.attackers.add(player);

        return this;
    }

    public DestructionState stopAttackBlock(Player player) {
        this.attackers.remove(player);

        if (!this.beenDestroyed() && this.attackers.isEmpty())
            this.health = maxHealth;

        return this;
    }

    public void onTick() {
        if (this.beenDestroyed()) return;

        int attackAmount = attackers.stream()
            .map(p -> p.getEquipment())
            .map(e -> e.getItemInMainHand())
            .map(data::getAttack)
            .reduce((t, u) -> t + u)
            .orElse(0);

        health -= attackAmount;

        if (this.beenDestroyed()) 
            DestructionHandler.breakBlock(this.pos, this.attackers);
    }

    public BlockMetaData getBlockData() {
        return data;
    }

    public boolean beenDestroyed() {
        return this.health < 0;
    }

}
