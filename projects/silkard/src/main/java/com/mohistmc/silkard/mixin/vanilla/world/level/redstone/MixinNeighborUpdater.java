package com.mohistmc.silkard.mixin.vanilla.world.level.redstone;

import com.mohistmc.silkard.injected.world.level.ContextLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.level.redstone.Orientation;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(NeighborUpdater.class)
public interface MixinNeighborUpdater {

    // CraftBukkit start
    @Inject(method = "executeUpdate", at = @At("HEAD"), cancellable = true)
    private static void silkard$blockPhysics(Level level, BlockState state, BlockPos pos, Block changedBlock, Orientation orientation, boolean movedByPiston, CallbackInfo ci) {
        // BUKKIT-4923: Ignore Block Physics in Chunk population.
        if (((ContextLevel) level).silkard$populating()) {
            ci.cancel();
            return;
        }

        if (level instanceof ServerLevel serverLevel) {
            CraftWorld cworld = serverLevel.getWorld();
            if (cworld != null) {
                BlockPhysicsEvent event = new BlockPhysicsEvent(CraftBlock.at(level, pos), CraftBlockData.fromData(state));
                serverLevel.getCraftServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    ci.cancel();
                }
            }
        }
    }
    // CraftBukkit end
}
