package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.goal;

import com.mohistmc.silkard.injected.world.entity.ai.goal.ContextMoveToBlockGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(MoveToBlockGoal.class)
public abstract class MixinMoveToBlockGoal implements ContextMoveToBlockGoal {

    @Shadow
    @Final
    protected PathfinderMob mob;

    @Shadow
    protected BlockPos blockPos;

    @Override
    public PathfinderMob silkard$mob() {
        return this.mob;
    }

    @Override
    public BlockPos silkard$blockPos() {
        return this.blockPos;
    }
}
