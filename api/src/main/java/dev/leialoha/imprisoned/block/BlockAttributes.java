package dev.leialoha.imprisoned.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class BlockAttributes {
    
    public static final Codec<BlockAttributes> CODEC;

    public int maxHealth;
    public int requiredToolLevel;
    public float baseDamageReduction;

    public float efficiencyBaseDamage;
    public float efficiencyBonusScaling;

    public float criticalHitChance;
    public float criticalHitMultiplier;

    public int maxDamagePerAttack;
    public int minDamagePerAttack;

    public BlockAttributes(
        int maxHealth,
        int requiredToolLevel,
        float baseDamageReduction,
        float efficiencyBaseDamage,
        float efficiencyBonusScaling,
        float criticalHitChance,
        float criticalHitMultiplier,
        int maxDamagePerAttack,
        int minDamagePerAttack
    ) {
        this.maxHealth = maxHealth;
        this.requiredToolLevel = requiredToolLevel;
        this.baseDamageReduction = baseDamageReduction;
        this.efficiencyBaseDamage = efficiencyBaseDamage;
        this.efficiencyBonusScaling = efficiencyBonusScaling;
        this.criticalHitChance = criticalHitChance;
        this.criticalHitMultiplier = criticalHitMultiplier;
        this.maxDamagePerAttack = maxDamagePerAttack;
        this.minDamagePerAttack = minDamagePerAttack;
    }

    public int getMaxHealth() {
        return this.maxHealth;
    }

    public int getRequiredToolLevel() {
        return this.requiredToolLevel;
    }

    public float getBaseDamageReduction() {
        return this.baseDamageReduction;
    }

    public float getEfficiencyBaseDamage() {
        return this.efficiencyBaseDamage;
    }

    public float getEfficiencyBonusScaling() {
        return this.efficiencyBonusScaling;
    }

    public float getCriticalHitChance() {
        return this.criticalHitChance;
    }

    public float getCriticalHitMultiplier() {
        return this.criticalHitMultiplier;
    }

    public int getMaxDamagePerAttack() {
        return this.maxDamagePerAttack;
    }

    public int getMinDamagePerAttack() {
        return this.minDamagePerAttack;
    }


    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.fieldOf("max_health").forGetter(BlockAttributes::getMaxHealth),
                Codec.INT.optionalFieldOf("required_tool_level", 0).forGetter(BlockAttributes::getRequiredToolLevel),
                Codec.FLOAT.optionalFieldOf("base_damage_reduction", 0f).forGetter(BlockAttributes::getBaseDamageReduction),
                Codec.FLOAT.optionalFieldOf("efficiency_base_damage", 1.25f).forGetter(BlockAttributes::getEfficiencyBaseDamage),
                Codec.FLOAT.optionalFieldOf("efficiency_bonus_scaling", 0.5f).forGetter(BlockAttributes::getEfficiencyBonusScaling),
                Codec.FLOAT.optionalFieldOf("critical_hit_chance", 0.2f).forGetter(BlockAttributes::getCriticalHitChance),
                Codec.FLOAT.optionalFieldOf("critical_hit_multiplier", 3f).forGetter(BlockAttributes::getCriticalHitMultiplier),
                Codec.INT.optionalFieldOf("max_damage_per_attack", Integer.MAX_VALUE).forGetter(BlockAttributes::getMaxDamagePerAttack),
                Codec.INT.optionalFieldOf("min_damage_per_attack", 0).forGetter(BlockAttributes::getMinDamagePerAttack)
            ).apply(instance, BlockAttributes::new)
        );
    }

}
