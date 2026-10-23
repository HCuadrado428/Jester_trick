package com.jester.jestertricks;

import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/** Solo el Jester (los nombres de la configuración) puede usar estos objetos. */
public final class JesterLock {

    public static boolean isJester(Player player) {
        List<? extends String> names;
        try {
            names = JesterConfig.JESTER_NAMES.get();
        } catch (IllegalStateException notLoadedYet) {
            return true;
        }
        if (names.isEmpty()) return true;
        String me = player.getGameProfile().getName();
        for (String n : names) {
            if (n.equalsIgnoreCase(me)) return true;
        }
        return false;
    }

    /** Devuelve true si puede usarlo; si no, le avisa con una risita y devuelve false. */
    public static boolean check(Player player) {
        if (isJester(player)) return true;
        if (!player.level().isClientSide) {
            player.displayClientMessage(Component.translatable("message.jestertricks.not_jester")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), true);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.JESTER_LAUGH.get(), SoundSource.PLAYERS, 0.5F, 1.4F);
        }
        return false;
    }

    private JesterLock() {}
}
