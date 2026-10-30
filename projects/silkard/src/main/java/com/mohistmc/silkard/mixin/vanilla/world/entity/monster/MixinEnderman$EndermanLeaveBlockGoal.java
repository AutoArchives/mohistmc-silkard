package com.mohistmc.silkard.mixin.vanilla.world.entity.monster;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(targets = "net.minecraft.world.entity.monster.Enderman$EndermanLeaveBlockGoal")
public class MixinEnderman$EndermanLeaveBlockGoal {

    @Shadow
    @Final
    private Enderman enderman;

    @Unique
    private boolean silkard$allowed;

    // CraftBukkit start - place event
    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$placeBlock(Level level, BlockPos pos, BlockState state) {
        this.silkard$allowed = CraftEventFactory.callEntityChangeBlockEvent(this.enderman, pos, state);
        return this.silkard$allowed;
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V"))
    private boolean silkard$gameEvent(Level level, Holder<GameEvent> gameEvent, BlockPos pos, GameEvent.Context context) {
        return this.silkard$allowed;
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Enderman;setCarriedBlock(Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private boolean silkard$clearCarried(Enderman instance, BlockState state) {
        return this.silkard$allowed;
    }
    // CraftBukkit end
}
