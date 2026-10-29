package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.bukkit.BukkitUtils;
import com.mohistmc.silkard.injected.world.level.ContextLevel;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.craftbukkit.block.CapturedBlockState;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.world.StructureGrowEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(SaplingBlock.class)
public abstract class MixinSaplingBlock {

    // @formatter:off
    @Shadow @Final protected TreeGrower treeGrower;
    // @formatter:on

    // CraftBukkit start - fire StructureGrowEvent on natural sapling growth
    @Inject(method = "advanceTree", at = @At("HEAD"), cancellable = true)
    private void silkard$advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random, CallbackInfo ci) {
        if (state.getValue(SaplingBlock.STAGE) != 0) {
            ContextLevel context = (ContextLevel) level;
            if (context.silkard$captureTreeGeneration()) {
                this.treeGrower.growTree(level, level.getChunkSource().getGenerator(), pos, state, random);
            } else {
                context.silkard$captureTreeGeneration(true);
                this.treeGrower.growTree(level, level.getChunkSource().getGenerator(), pos, state, random);
                context.silkard$captureTreeGeneration(false);
                if (!context.silkard$capturedBlockStates().isEmpty()) {
                    TreeType treeType = BukkitUtils.treeType;
                    BukkitUtils.setTreeType(null);
                    Location location = CraftLocation.toBukkit(pos, level.getWorld());
                    List<org.bukkit.block.BlockState> blocks = new ArrayList<>(context.silkard$capturedBlockStates().values());
                    context.silkard$capturedBlockStates().clear();

                    StructureGrowEvent event = null;
                    if (treeType != null) {
                        event = new StructureGrowEvent(location, treeType, false, null, blocks);
                        Bukkit.getPluginManager().callEvent(event);
                    }

                    if (event == null || !event.isCancelled()) {
                        for (org.bukkit.block.BlockState capturedBlockState : blocks) {
                            CapturedBlockState.setBlockState(capturedBlockState);
                        }
                    }
                }
            }
            ci.cancel();
        }
    }
    // CraftBukkit end
}
