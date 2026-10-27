package com.mohistmc.silkard.mixin.vanilla.world.level;

import com.mohistmc.silkard.injected.world.level.ContextBlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(BlockGetter.class)
public interface MixinBlockGetter extends ContextBlockGetter {

    // CraftBukkit start - the per-position half of clip(ClipContext), used by CraftBlock#rayTrace
    @Override
    default BlockHitResult clip(ClipContext context, BlockPos pos) {
        BlockGetter self = (BlockGetter) this;
        BlockState blockState = self.getBlockState(pos);
        FluidState fluidState = self.getFluidState(pos);
        Vec3 from = context.getFrom();
        Vec3 to = context.getTo();
        VoxelShape blockShape = context.getBlockShape(blockState, self, pos);
        BlockHitResult blockResult = self.clipWithInteractionOverride(from, to, pos, blockShape, blockState);
        VoxelShape fluidShape = context.getFluidShape(fluidState, self, pos);
        BlockHitResult liquidResult = fluidShape.clip(from, to, pos);
        double blockDistanceSquared = blockResult == null ? Double.MAX_VALUE : from.distanceToSqr(blockResult.getLocation());
        double liquidDistanceSquared = liquidResult == null ? Double.MAX_VALUE : from.distanceToSqr(liquidResult.getLocation());
        return blockDistanceSquared <= liquidDistanceSquared ? blockResult : liquidResult;
    }
    // CraftBukkit end
}
