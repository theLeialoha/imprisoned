package dev.leialoha.imprisoned.world.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dev.leialoha.imprisoned.world.BlockDrops;

public class InfectedBlockMetaData extends BlockMetaData {

    @Override
    public int getMaxHealth() {
        return 20*10;
    }


    @Override
    public int getDamageAmount(ItemStack itemStack) {
        if (itemStack.getType().equals(Material.STONE_PICKAXE))
            return 20;

        if (itemStack.getType().equals(Material.IRON_PICKAXE))
            return 1;


        return 0;
    }

    @Override
    public BlockDrops getDrops() {
        return BlockDrops.EMPTY;
    }

}
