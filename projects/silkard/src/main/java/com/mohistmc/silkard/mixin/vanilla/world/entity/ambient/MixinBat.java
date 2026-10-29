package com.mohistmc.silkard.mixin.vanilla.world.entity.ambient;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(Bat.class)
public class MixinBat {

    @Unique
    private static boolean silkard$awakeAllowed;

    @Redirect(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getNearestPlayer(Lnet/minecraft/world/entity/ai/targeting/TargetingConditions;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/entity/player/Player;"))
    private static Player silkard$nearestPlayer(ServerLevel level, TargetingConditions conditions, LivingEntity entity) {
        Player player = level.getNearestPlayer(conditions, entity);
        if (player != null && !CraftEventFactory.handleBatToggleSleepEvent(entity, true)) {
            return null;
        }
        return player;
    }

    @WrapWithCondition(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ambient/Bat;setResting(Z)V", ordinal = 1))
    private static boolean silkard$gateAwake(Bat bat, boolean resting) {
        silkard$awakeAllowed = CraftEventFactory.handleBatToggleSleepEvent(bat, true);
        return silkard$awakeAllowed;
    }

    @WrapWithCondition(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;levelEvent(Lnet/minecraft/world/entity/Entity;ILnet/minecraft/core/BlockPos;I)V", ordinal = 1))
    private static boolean silkard$gateAwakeSound(ServerLevel level, Entity entity, int type, BlockPos pos, int data) {
        return silkard$awakeAllowed;
    }

    @ModifyExpressionValue(method = "customServerAiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isRedstoneConductor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z", ordinal = 1))
    private boolean silkard$gateSleep(boolean original) {
        return original && CraftEventFactory.handleBatToggleSleepEvent((Entity) (Object) this, false);
    }

    @ModifyExpressionValue(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ambient/Bat;isResting()Z"))
    private boolean silkard$gateHurt(boolean original) {
        return original && CraftEventFactory.handleBatToggleSleepEvent((Entity) (Object) this, true);
    }
}
