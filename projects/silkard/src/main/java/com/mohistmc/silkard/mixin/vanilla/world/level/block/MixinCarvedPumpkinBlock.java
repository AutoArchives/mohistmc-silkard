package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.ContextLevelWriter;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(CarvedPumpkinBlock.class)
public class MixinCarvedPumpkinBlock {

    @Unique
    private static boolean silkard$spawnFailed;

    @Inject(method = "spawnGolemInWorld", at = @At("HEAD"), cancellable = true)
    private static void silkard$spawnGolem(Level level, BlockPattern.BlockPatternMatch match, Entity golem, BlockPos spawnPos, CallbackInfo ci) {
        golem.snapTo(spawnPos.getX() + 0.5D, spawnPos.getY() + 0.05D, spawnPos.getZ() + 0.5D, 0.0F, 0.0F);
        EntityType<?> type = golem.getType();
        SpawnReason reason = type == EntityTypes.SNOW_GOLEM ? SpawnReason.BUILD_SNOWMAN
                : (type == EntityTypes.IRON_GOLEM ? SpawnReason.BUILD_IRONGOLEM : SpawnReason.BUILD_COPPERGOLEM);
        boolean spawned = ((ContextLevelWriter) level).addFreshEntity(golem, reason);
        silkard$spawnFailed = !spawned;
        if (spawned) {
            CarvedPumpkinBlock.clearPatternBlocks(level, match);
            for (ServerPlayer serverplayer : level.getEntitiesOfClass(ServerPlayer.class, golem.getBoundingBox().inflate(5.0D))) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer, golem);
            }
            CarvedPumpkinBlock.updatePatternBlocks(level, match);
        }
        ci.cancel();
    }

    // SPIGOT-8085: handle cancellation
    @Inject(method = "trySpawnGolem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CarvedPumpkinBlock;spawnGolemInWorld(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/pattern/BlockPattern$BlockPatternMatch;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/BlockPos;)V", ordinal = 2, shift = At.Shift.AFTER),
            cancellable = true)
    private void silkard$cancelSpawn(Level level, BlockPos pos, CallbackInfo ci) {
        if (silkard$spawnFailed) {
            ci.cancel();
        }
    }
}
