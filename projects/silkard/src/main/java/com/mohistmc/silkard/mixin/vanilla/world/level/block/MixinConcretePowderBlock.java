package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockFormEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(ConcretePowderBlock.class)
public class MixinConcretePowderBlock {

    @Shadow
    @Final
    private Block concrete;

    @Unique
    private BlockState silkard$formResult;

    // CraftBukkit start - BlockFormEvent when landing in liquid
    @Redirect(method = "onLand", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$formOnLand(Level level, BlockPos pos, BlockState state) {
        return CraftEventFactory.handleBlockFormEvent(level, pos, state, 3);
    }
    // CraftBukkit end

    // CraftBukkit start - BlockFormEvent on placement
    @Redirect(method = "getStateForPlacement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;defaultBlockState()Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState silkard$formOnPlacement(Block block, BlockPlaceContext context) {
        CraftBlockState blockState = CraftBlockStates.getBlockState(context.getLevel(), context.getClickedPos());
        blockState.setData(block.defaultBlockState());
        BlockFormEvent event = new BlockFormEvent(blockState.getBlock(), blockState);
        context.getLevel().getCraftServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            return blockState.getHandle();
        }
        return ((Block) (Object) this).defaultBlockState();
    }
    // CraftBukkit end

    // CraftBukkit start - BlockFormEvent on liquid contact
    @Redirect(method = "updateShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/ConcretePowderBlock;touchesLiquid(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean silkard$touchesLiquid(BlockGetter level, BlockPos pos) {
        this.silkard$formResult = null;
        if (!(level instanceof Level actualLevel)) {
            return true;
        }
        CraftBlockState blockState = CraftBlockStates.getBlockState(actualLevel, pos);
        blockState.setData(this.concrete.defaultBlockState());
        BlockFormEvent event = new BlockFormEvent(blockState.getBlock(), blockState);
        actualLevel.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        this.silkard$formResult = blockState.getHandle();
        return true;
    }

    @Redirect(method = "updateShape", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;defaultBlockState()Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState silkard$formState(Block block) {
        return this.silkard$formResult != null ? this.silkard$formResult : block.defaultBlockState();
    }
    // CraftBukkit end
}
