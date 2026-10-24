package com.jester.jestertricks.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;

/** Postura de la pistola BANG! en tercera persona: brazo levantado apuntando hacia delante. */
public final class PistolArmPose {
    public static final HumanoidModel.ArmPose AIM = HumanoidModel.ArmPose.create("JESTERTRICKS_PISTOL", false,
            (model, entity, arm) -> {
                var part = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
                part.xRot = model.head.xRot - (float) (Math.PI / 2);
                part.yRot = model.head.yRot;
                part.zRot = 0.0F;
            });

    private PistolArmPose() {}
}
