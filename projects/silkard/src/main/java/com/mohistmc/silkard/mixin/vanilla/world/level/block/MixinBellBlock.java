package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BellBlock.class)
public class MixinBellBlock {

    @Inject(method = "attemptToRing(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BellBlockEntity;onHit(Lnet/minecraft/core/Direction;)V"), cancellable = true)
    private void silkard$tryShine(Entity ringingEntity, Level level, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (!CraftEventFactory.handleBellRingEvent(level, pos, direction, ringingEntity)) {
            cir.setReturnValue(false);
        }
    }
}
