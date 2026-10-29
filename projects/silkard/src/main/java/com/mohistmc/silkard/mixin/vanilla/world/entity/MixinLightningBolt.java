package com.mohistmc.silkard.mixin.vanilla.world.entity;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import com.mohistmc.silkard.injected.world.entity.ContextLightningBolt;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(LightningBolt.class)
public class MixinLightningBolt implements ContextLightningBolt {

    @Unique
    public boolean isSilent;

    @Override
    public boolean silkard$isSilent() {
        return isSilent;
    }

    @Override
    public void silkard$setIsSilent(boolean isSilent) {
        this.isSilent = isSilent;
    }

    // CraftBukkit start
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LightningBolt;discard()V"))
    private void silkard$discard(LightningBolt bolt) {
        ((ContextEntity) (Object) bolt).discard(EntityRemoveEvent.Cause.DESPAWN);
    }

    @Redirect(method = "spawnFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$setBlockAndUpdate(ServerLevel level, BlockPos pos, BlockState state) {
        if (CraftEventFactory.callBlockIgniteEvent(level, pos, (Entity) (Object) this).isCancelled()) {
            return false;
        }
        return level.setBlockAndUpdate(pos, state);
    }
    // CraftBukkit end
}
