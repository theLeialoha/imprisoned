package dev.leialoha.imprisoned.world.data;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import dev.leialoha.imprisoned.world.BlockDrops;

public class BlockMetaData {

    public BlockDrops drops;

    private static final Map<Material, BlockMetaData> META_DATAS;

    public int getAttack(ItemStack itemStack) {
        return 0;
    }

    public BlockDrops getDrops() {
        return drops;
    }

    public static BlockMetaData get(Block block) {
        return META_DATAS.computeIfAbsent(block.getType(), material -> {
            return new BlockMetaData();
        });
    }



    static {
        META_DATAS = new HashMap<>();
    }

}
