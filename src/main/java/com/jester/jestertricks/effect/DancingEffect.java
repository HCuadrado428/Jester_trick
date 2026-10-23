package com.jester.jestertricks.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** ¡A bailar!: el afectado da vueltas y saltitos sin poder evitarlo. */
public class DancingEffect extends MobEffect {
    public DancingEffect() {
        super(MobEffectCategory.HARMFUL, 0xF0F0F0);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity e, int amplifier) {
        if (!(e instanceof Player)) {
            float yaw = e.getYRot() + 24.0F;
            e.setYRot(yaw);
            e.yBodyRot = yaw;
            e.yHeadRot = yaw;
        }
        if (!e.level().isClientSide && e.onGround() && e.tickCount % 10 == 0) {
            e.setDeltaMovement(e.getDeltaMovement().x, 0.42, e.getDeltaMovement().z);
            e.hurtMarked = true;
        }
    }
}
