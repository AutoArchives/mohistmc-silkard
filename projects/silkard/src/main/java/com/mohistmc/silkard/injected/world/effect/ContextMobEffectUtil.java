package com.mohistmc.silkard.injected.world.effect;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public interface ContextMobEffectUtil {

    ThreadLocal<EntityPotionEffectEvent.Cause> CAUSE = new ThreadLocal<>();

    static List<ServerPlayer> addEffectToPlayersAround(ServerLevel level, Entity source, Vec3 position, double radius, MobEffectInstance effect, int duration, EntityPotionEffectEvent.Cause cause) {
        CAUSE.set(cause);
        try {
            return MobEffectUtil.addEffectToPlayersAround(level, source, position, radius, effect, duration);
        } finally {
            CAUSE.remove();
        }
    }
}
