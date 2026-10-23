package com.jester.jestertricks.client;

import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.ability.JesterAbilities;
import com.jester.jestertricks.network.AbilityPacket;
import com.jester.jestertricks.network.JesterNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/** Teclas de los poderes (se pueden cambiar en Opciones > Controles > Jester's Tricks). */
public final class JesterKeys {
    private static final String CATEGORY = "key.categories.jestertricks";
    public static final KeyMapping LAUGH = new KeyMapping("key.jestertricks.laugh", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
    public static final KeyMapping CONFETTI = new KeyMapping("key.jestertricks.confetti", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping SHADOW_STEP = new KeyMapping("key.jestertricks.shadow_step", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, CATEGORY);
    public static final KeyMapping PASSIVES = new KeyMapping("key.jestertricks.passives", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, CATEGORY);

    @Mod.EventBusSubscriber(modid = JesterTricks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class Register {
        @SubscribeEvent
        public static void onRegister(RegisterKeyMappingsEvent event) {
            event.register(LAUGH);
            event.register(CONFETTI);
            event.register(SHADOW_STEP);
            event.register(PASSIVES);
        }

        private Register() {}
    }

    @Mod.EventBusSubscriber(modid = JesterTricks.MOD_ID, value = Dist.CLIENT)
    public static final class Input {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().player == null) return;
            while (LAUGH.consumeClick()) send(JesterAbilities.Ability.LAUGH);
            while (CONFETTI.consumeClick()) send(JesterAbilities.Ability.CONFETTI);
            while (SHADOW_STEP.consumeClick()) send(JesterAbilities.Ability.SHADOW_STEP);
            while (PASSIVES.consumeClick()) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.screen == null) mc.setScreen(new PassivesScreen(null));
            }
        }

        private static void send(JesterAbilities.Ability ability) {
            JesterNetwork.CHANNEL.sendToServer(new AbilityPacket(ability));
        }

        private Input() {}
    }

    private JesterKeys() {}
}
