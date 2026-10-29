package com.mohistmc.silkard.mixin.vanilla.world.entity.monster;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.monster.Silverfish;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(targets = "net.minecraft.world.entity.monster.Silverfish$SilverfishWakeUpFriendsGoal")
public class MixinSilverfish$SilverfishWakeUpFriendsGoal {

    @Shadow
    @Final
    private Silverfish silverfish;

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$destroy(Level level, BlockPos pos, boolean drop, Entity entity) {
        return CraftEventFactory.callEntityChangeBlockEvent(this.silverfish, pos, Blocks.AIR.defaultBlockState());
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$place(Level level, BlockPos pos, BlockState state) {
        return CraftEventFactory.callEntityChangeBlockEvent(this.silverfish, pos, Blocks.AIR.defaultBlockState());
    }
}
