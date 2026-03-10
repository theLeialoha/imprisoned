package dev.leialoha.imprisoned.mines.destruction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.entity.Player;

import dev.leialoha.imprisoned.block.Block;
import dev.leialoha.imprisoned.data.IntLocation;
import dev.leialoha.imprisoned.data.ResourceKey;
import dev.leialoha.imprisoned.registration.Registry;
import dev.leialoha.imprisoned.registration.RegistryEntry;
import dev.leialoha.imprisoned.registration.RegistryKeys;
import dev.leialoha.imprisoned.utils.MinecraftUtils;
import dev.leialoha.imprisoned.utils.BukkitConversion;
import dev.leialoha.imprisoned.utils.ItemBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;

public class DestructionState {

    private final Set<Player> attackers = new HashSet<>();
    private final Block block;
    private final IntLocation pos;

    private int maxHealth;
    private int health;
    private int lastState = -1;

    public DestructionState(IntLocation pos) {
        this.block = getBlock(pos);
        this.pos = pos;

        this.maxHealth = block.attributes().getMaxHealth();
        this.health = maxHealth;
    }

    public boolean hasAttackingPlayer(Player player) {
        return this.attackers.contains(player);
    }

    public DestructionState startAttackBlock(Player player) {
        if (!this.beenDestroyed())
            this.attackers.add(player);

        // if (!this.isTicking())
        //     this.startTicking();

        return this;
    }

    public DestructionState stopAttackBlock(Player player) {
        this.attackers.remove(player);

        if (!this.beenDestroyed() && this.attackers.isEmpty())
            setHealth(this.maxHealth);

        return this;
    }

    public void onTick() {
        if (this.beenDestroyed() || attackers.isEmpty()) {
            // this.stopTicking();
            return;
        }

        int attackAmount = attackers.stream()
            .map(p -> p.getEquipment())
            .map(e -> e.getItemInMainHand())
            .map(ItemBuilder.ItemHolderBuilder::from)
            .map(ItemBuilder::build)
            .map(block::getDamageAmount)
            .reduce((t, u) -> t + u)
            .orElse(0);

        setHealth(this.health - attackAmount);

        if (this.beenDestroyed()) 
            DestructionHandler.breakBlock(this.pos, this.attackers);
    }

    private void sendPacket() {

        int state = (int) Math.floor(((this.maxHealth - this.health) * 11f) / (float) this.maxHealth) - 1;
        if (this.lastState != state) {

            Location bukkitLocation = BukkitConversion.asLocation(pos);
            BlockPos blockPos = MinecraftUtils.getBlockPos(bukkitLocation);

            ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(199, blockPos, state);
            MinecraftUtils.sendPacketToNearby(packet, bukkitLocation, getBlockId());

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

    public Block getBlock() {
        return this.block;
    }

    public boolean beenDestroyed() {
        return this.health < 0;
    }

    private void setHealth(int health) {
        this.health = health;
        sendPacket();
    }


    private static Block getBlock(IntLocation pos) {
        Location location = BukkitConversion.asLocation(pos);
        org.bukkit.block.Block block = location.getBlock();

        ResourceKey blockKey = BukkitConversion.asResourceKey(block.getType().getKey());
        Map<String, String> states = ((CraftBlockData) block.getBlockData()).toStates(true);

        Registry<Block> registry = RegistryKeys.BLOCKS.getRegistry();
        Collection<RegistryEntry<Block>> entries = registry.getEntries();

        return entries.stream().map(RegistryEntry::get)
            .filter(b -> b.is(blockKey, states)).findFirst().orElseThrow();
    }

}
