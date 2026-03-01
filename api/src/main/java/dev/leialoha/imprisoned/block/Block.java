package dev.leialoha.imprisoned.block;

public class Block {

    private final BlockData data;

    private int maxHealth = 100;

    public Block(BlockData data) {
        this.data = data;
    }

    public BlockData getData() {
        return data;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

}
