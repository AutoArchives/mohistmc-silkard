package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.warden;

import com.mohistmc.silkard.injected.world.effect.ContextMobEffectUtil;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(Warden.class)
public class MixinWarden {

    @Redirect(method = "applyDarknessAround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectUtil;addEffectToPlayersAround(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;DLnet/minecraft/world/effect/MobEffectInstance;I)Ljava/util/List;"))
    private static List<ServerPlayer> silkard$darknessCause(ServerLevel level, Entity source, Vec3 position, double radius, MobEffectInstance effect, int duration) {
        return ContextMobEffectUtil.addEffectToPlayersAround(level, source, position, radius, effect, duration, EntityPotionEffectEvent.Cause.WARDEN);
    }
}
