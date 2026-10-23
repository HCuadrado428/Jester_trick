package com.jester.jestertricks.ability;

import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.registry.ModEffects;
import com.jester.jestertricks.registry.ModParticles;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Poderes del Jester que se lanzan con una tecla, sin llevar ningún objeto. */
public final class JesterAbilities {
    public enum Ability {
        /** Carcajada: los monstruos cercanos huyen despavoridos. */
        LAUGH(600),
        /** Lluvia de confeti: ciega un momento a todos los que te rodean. */
        CONFETTI(400),
        /** Paso de sombra: invisible, rápido y los golpes te atraviesan durante 3 segundos. */
        SHADOW_STEP(500),
        /** Forma real: 10 segundos como sombra alta con máscara; los monstruos huyen. */
        TRUE_FORM(1200);

        public final int cooldown;

        Ability(int cooldown) {
            this.cooldown = cooldown;
        }

        String key() {
            return "jestertricks_cd_" + name().toLowerCase();
        }
    }

    public static void use(ServerPlayer player, Ability ability) {
        if (!JesterLock.check(player)) return;
        long now = player.level().getGameTime();
        long ready = player.getPersistentData().getLong(ability.key());
        if (now < ready) {
            long secs = (ready - now + 19) / 20;
            player.displayClientMessage(Component.translatable("message.jestertricks.ability_cooldown",
                    Component.translatable("ability.jestertricks." + ability.name().toLowerCase()), secs)
                    .withStyle(ChatFormatting.GRAY), true);
            return;
        }
        boolean done = switch (ability) {
            case LAUGH -> laugh(player);
            case CONFETTI -> confetti(player);
            case SHADOW_STEP -> shadowStep(player);
            case TRUE_FORM -> trueForm(player);
        };
        if (done) {
            player.getPersistentData().putLong(ability.key(), now + JesterConfig.cooldown(ability.cooldown));
        }
    }

    private static boolean laugh(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        Vec3 c = player.position();
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(14.0),
                e -> e != player && e.isAlive())) {
            if (e instanceof Mob mob && mob.getTarget() == player) mob.setTarget(null);
            if (e instanceof PathfinderMob pm) {
                Vec3 away = e.position().subtract(c);
                away = new Vec3(away.x, 0, away.z);
                away = away.lengthSqr() < 1.0E-4 ? new Vec3(1, 0, 0) : away.normalize();
                Vec3 flee = e.position().add(away.scale(14));
                pm.getNavigation().moveTo(flee.x, flee.y, flee.z, 1.6);
            }
            e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 160, 0), player);
            if (e instanceof Player) {
                e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0), player);
                e.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0), player);
            }
        }
        for (int i = 0; i < 3; i++) {
            Fx.petalRing(level, c, 2.0 + i * 2.5, 16 + i * 10);
        }
        level.sendParticles(ModParticles.ORIGIN_STAR.get(), c.x, c.y + 2.2, c.z, 10, 0.6, 0.4, 0.6, 0.02);
        level.playSound(null, c.x, c.y, c.z, ModSounds.JESTER_LAUGH.get(), SoundSource.PLAYERS, 2.0F, 0.8F);
        return true;
    }

    private static boolean confetti(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        Vec3 c = player.position().add(0, 1.2, 0);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(7.0),
                e -> e != player && e.isAlive())) {
            e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50, 0), player);
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1), player);
            if (e instanceof Mob mob && mob.getTarget() == player) mob.setTarget(null);
        }
        Fx.confetti(level, c, 60, 2.5);
        level.sendParticles(ModParticles.ORIGIN_STAR.get(), c.x, c.y + 1.0, c.z, 24, 2.5, 1.5, 2.5, 0.05);
        level.sendParticles(ParticleTypes.FIREWORK, c.x, c.y + 1.5, c.z, 30, 1.5, 1.0, 1.5, 0.1);
        level.playSound(null, c.x, c.y, c.z, ModSounds.MAGIC_POOF.get(), SoundSource.PLAYERS, 1.4F, 1.3F);
        level.playSound(null, c.x, c.y, c.z, ModSounds.BELLS.get(), SoundSource.PLAYERS, 1.0F, 1.2F);
        return true;
    }

    private static boolean shadowStep(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(ModEffects.SHADOW_STEP.get(), 60, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60, 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 2, false, false, true));
        ServerLevel level = player.serverLevel();
        for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(24.0), m -> m.getTarget() == player)) {
            mob.setTarget(null);
        }
        Vec3 c = player.position();
        level.sendParticles(ParticleTypes.LARGE_SMOKE, c.x, c.y + 1, c.z, 40, 0.4, 0.8, 0.4, 0.02);
        level.sendParticles(ParticleTypes.SQUID_INK, c.x, c.y + 1, c.z, 20, 0.4, 0.8, 0.4, 0.05);
        level.playSound(null, c.x, c.y, c.z, ModSounds.RIFT.get(), SoundSource.PLAYERS, 0.8F, 1.6F);
        return true;
    }

    private static boolean trueForm(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(ModEffects.TRUE_FORM.get(), 200, 0, false, false, true));
        com.jester.jestertricks.network.TrueFormPacket.broadcast(player);
        ServerLevel level = player.serverLevel();
        Vec3 c = player.position();
        level.sendParticles(ParticleTypes.SQUID_INK, c.x, c.y + 1.5, c.z, 60, 0.6, 1.5, 0.6, 0.08);
        level.sendParticles(ModParticles.ORIGIN_STAR.get(), c.x, c.y + 3.0, c.z, 12, 0.8, 0.6, 0.8, 0.03);
        level.playSound(null, c.x, c.y, c.z, ModSounds.RIFT.get(), SoundSource.PLAYERS, 1.5F, 0.5F);
        level.playSound(null, c.x, c.y, c.z, ModSounds.JESTER_LAUGH.get(), SoundSource.PLAYERS, 2.5F, 0.45F);
        return true;
    }

    private JesterAbilities() {}
}
