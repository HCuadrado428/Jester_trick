package com.jester.jestertricks.client;

import com.jester.jestertricks.ability.JesterPassiveSettings.Passive;
import com.jester.jestertricks.network.JesterNetwork;
import com.jester.jestertricks.network.TogglePassivePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;
import java.util.Map;

/**
 * Pantalla de pasivas del Jester: un interruptor por pasiva.
 * Se abre con la tecla J o con el botón de la estrella del inventario.
 */
public class PassivesScreen extends Screen {
    private static final int BTN_W = 220;
    private static final int BTN_H = 20;
    private static final int GAP = 26;

    private final Screen parent;
    private final Map<Passive, Button> toggles = new EnumMap<>(Passive.class);

    public PassivesScreen(Screen parent) {
        super(Component.translatable("screen.jestertricks.passives"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // pide el estado real al servidor (por si ha cambiado la configuración)
        JesterNetwork.CHANNEL.sendToServer(TogglePassivePacket.request());
        toggles.clear();
        int x = (width - BTN_W) / 2;
        int y = height / 2 - 50;
        for (Passive p : Passive.values()) {
            Button b = Button.builder(label(p), btn -> {
                        boolean next = !ClientPassives.isOn(p);
                        JesterNetwork.CHANNEL.sendToServer(new TogglePassivePacket(p.ordinal(), next));
                    })
                    .bounds(x, y, BTN_W, BTN_H)
                    .tooltip(Tooltip.create(Component.translatable("screen.jestertricks.passive." + p.key() + ".desc")))
                    .build();
            toggles.put(p, addRenderableWidget(b));
            y += GAP;
        }
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> onClose())
                .bounds((width - 150) / 2, y + 14, 150, BTN_H).build());
        refresh();
    }

    private static Component label(Passive p) {
        boolean on = ClientPassives.isOn(p);
        return Component.translatable("screen.jestertricks.passive." + p.key())
                .append(": ")
                .append(Component.translatable(on ? "options.on" : "options.off")
                        .withStyle(on ? ChatFormatting.WHITE : ChatFormatting.DARK_GRAY));
    }

    /** Vuelve a pintar los interruptores con lo que diga el servidor. */
    public void refresh() {
        boolean jester = ClientPassives.isJester();
        toggles.forEach((p, b) -> {
            b.setMessage(label(p));
            b.active = jester;
        });
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        int top = height / 2 - 50;
        g.drawCenteredString(font, title, width / 2, top - 34, 0xFFFFFF);
        Component sub = ClientPassives.isJester()
                ? Component.translatable("screen.jestertricks.passives.sub")
                : Component.translatable("message.jestertricks.not_jester");
        g.drawCenteredString(font, sub, width / 2, top - 20, 0xA0A0A0);
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
