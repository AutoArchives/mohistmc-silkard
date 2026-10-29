package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.silkard.injected.world.level.ContextLevel;
import com.mohistmc.silkard.injected.world.level.ContextLevelWriter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(WitherSkullBlock.class)
public class MixinWitherSkullBlock {

    @Unique
    private static final ThreadLocal<Boolean> silkard$spawnFailed = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Unique
    private static final String CHECK_SPAWN = "checkSpawn(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/SkullBlockEntity;)V";

    // CraftBukkit start - skip while capturing block states
    @Inject(method = CHECK_SPAWN, at = @At("HEAD"), cancellable = true)
    private static void silkard$capturedCheckSpawn(Level level, BlockPos pos, SkullBlockEntity placedSkull, CallbackInfo ci) {
        if (((ContextLevel) level).silkard$captureBlockStates()) {
            ci.cancel();
        }
    }

    // CraftBukkit start - only clear the pattern after the wither was spawned successfully
    @Redirect(method = CHECK_SPAWN, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CarvedPumpkinBlock;clearPatternBlocks(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/pattern/BlockPattern$BlockPatternMatch;)V"))
    private static void silkard$clearPatternBlocks(Level level, BlockPattern.BlockPatternMatch match) {
    }

    @Redirect(method = CHECK_SPAWN, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean silkard$addFreshEntity(Level level, Entity entity, @Local BlockPattern.BlockPatternMatch match) {
        boolean spawned = ((ContextLevelWriter) level).addFreshEntity(entity, CreatureSpawnEvent.SpawnReason.BUILD_WITHER);
        silkard$spawnFailed.set(!spawned);
        if (spawned) {
            CarvedPumpkinBlock.clearPatternBlocks(level, match);
        }
        return spawned;
    }

    @Inject(method = CHECK_SPAWN, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CarvedPumpkinBlock;updatePatternBlocks(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/pattern/BlockPattern$BlockPatternMatch;)V", shift = At.Shift.BEFORE), cancellable = true)
    private static void silkard$skipPatternUpdate(Level level, BlockPos pos, SkullBlockEntity placedSkull, CallbackInfo ci) {
        boolean failed = silkard$spawnFailed.get();
        silkard$spawnFailed.remove();
        if (failed) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
