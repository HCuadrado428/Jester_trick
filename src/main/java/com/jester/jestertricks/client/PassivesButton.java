package com.jester.jestertricks.client;

import com.jester.jestertricks.JesterTricks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Botón de la estrella (✦) a la derecha del inventario (normal y de creativo):
 * abre la pantalla de pasivas. Solo lo ve el Jester.
 */
@Mod.EventBusSubscriber(modid = JesterTricks.MOD_ID, value = Dist.CLIENT)
public final class PassivesButton {
    private static Button button;

    @SubscribeEvent
    public static void onInit(ScreenEvent.Init.Post event) {
        button = null;
        if (!(event.getScreen() instanceof EffectRenderingInventoryScreen<?> inv) || !ClientPassives.isJester()) return;
        button = Button.builder(Component.literal("✦"), b -> Minecraft.getInstance().setScreen(new PassivesScreen(inv)))
                .bounds(0, 0, 20, 20)
                .tooltip(Tooltip.create(Component.translatable("screen.jestertricks.passives")))
                .build();
        place(inv);
        event.addListener(button);
    }

    /** El libro de recetas desplaza el inventario: el botón le sigue. */
    @SubscribeEvent
    public static void onRender(ScreenEvent.Render.Pre event) {
        if (button != null && event.getScreen() instanceof AbstractContainerScreen<?> s) place(s);
    }

    private static void place(AbstractContainerScreen<?> s) {
        button.setX(s.getGuiLeft() + s.getXSize() + 3);
        button.setY(s.getGuiTop() + 4);
    }

    private PassivesButton() {}
}
