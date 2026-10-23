package com.jester.jestertricks.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/** Material del Jester: irrompible (0 usos = sin durabilidad) y muy encantable. */
public enum JesterTier implements Tier {
    INSTANCE;

    @Override public int getUses() { return 0; }
    @Override public float getSpeed() { return 12.0F; }
    @Override public float getAttackDamageBonus() { return 0.0F; }
    @Override public int getLevel() { return 4; }
    @Override public int getEnchantmentValue() { return 30; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
}
