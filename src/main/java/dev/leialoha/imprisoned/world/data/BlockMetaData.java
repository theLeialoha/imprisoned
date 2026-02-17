package dev.leialoha.imprisoned.world.data;

import java.io.InvalidObjectException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import dev.leialoha.imprisoned.world.BlockDrops;

public class BlockMetaData {

    public BlockDrops drops;

    private static final Map<Material, BlockMetaData> META_DATAS;

    public int getMaxHealth() {
        return 3;
    }

    public int getDamageAmount(ItemStack itemStack) {
        return 0;
    }

    public BlockDrops getDrops() {
        return drops;
    }

    public static BlockMetaData get(Block block) {
        return META_DATAS.computeIfAbsent(block.getType(), material -> {
            if (material.equals(Material.INFESTED_STONE))
                return new InfectedBlockMetaData();

            InvalidObjectException exception = new InvalidObjectException("Unknown material: " + material.toString());
            throw new RuntimeException(exception);

            // return new BlockMetaData();
        });
    }

    static {
        META_DATAS = new HashMap<>();
    }

}
