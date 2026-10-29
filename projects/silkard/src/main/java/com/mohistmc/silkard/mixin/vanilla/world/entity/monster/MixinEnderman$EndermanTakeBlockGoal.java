package com.mohistmc.silkard.mixin.vanilla.world.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(targets = "net.minecraft.world.entity.monster.Enderman$EndermanTakeBlockGoal")
public abstract class MixinEnderman$EndermanTakeBlockGoal {

    @Shadow @Final
    private Enderman enderman;

    @Unique
    private boolean silkard$takeCancelled;

    @Inject(method = "tick", at = @At("HEAD"))
    private void silkard$reset(CallbackInfo ci) {
        this.silkard$takeCancelled = false;
    }

    // CraftBukkit start - EntityChangeBlockEvent
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean silkard$removeBlock(Level level, BlockPos pos, boolean update) {
        if (!CraftEventFactory.callEntityChangeBlockEvent(this.enderman, pos, Blocks.AIR.defaultBlockState())) {
            this.silkard$takeCancelled = true;
            return false;
        }
        return level.removeBlock(pos, update);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V"))
    private void silkard$gameEvent(Level level, Holder gameEvent, BlockPos pos, GameEvent.Context context) {
        if (this.silkard$takeCancelled) {
            return;
        }
        level.gameEvent(gameEvent, pos, context);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Enderman;setCarriedBlock(Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void silkard$setCarriedBlock(Enderman enderman, BlockState state) {
        if (this.silkard$takeCancelled) {
            return;
        }
        enderman.setCarriedBlock(state);
    }
    // CraftBukkit end
}
