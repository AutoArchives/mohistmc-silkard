package com.mohistmc.silkard.mixin.vanilla.world.entity;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(EntityFluidInteraction.class)
public class MixinEntityFluidInteraction {

    // CraftBukkit start - store last lava contact location
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockGetter;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"))
    private FluidState silkard$trackLavaContact(BlockGetter level, BlockPos position, Entity entity, boolean flag) {
        FluidState fluidState = level.getFluidState(position);
        if (!fluidState.isEmpty() && fluidState.is(FluidTags.LAVA)) {
            ((ContextEntity) entity).silkard$setLastLavaContact(position.immutable());
        }
        return fluidState;
    }
    // CraftBukkit end
}
