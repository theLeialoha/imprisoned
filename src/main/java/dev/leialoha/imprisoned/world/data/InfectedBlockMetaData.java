package dev.leialoha.imprisoned.world.data;

import org.bukkit.inventory.ItemStack;

public class InfectedBlockMetaData extends BlockMetaData {
    
    @Override
    public int getAttack(ItemStack itemStack) {
        return 1;
    }

}
