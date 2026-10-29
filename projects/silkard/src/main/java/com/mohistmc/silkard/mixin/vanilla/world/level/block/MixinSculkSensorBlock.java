package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.mohistmc.silkard.injected.world.level.block.ContextBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(SculkSensorBlock.class)
public class MixinSculkSensorBlock implements ContextBlock {

    @Inject(method = "stepOn", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private void silkard$stepOnInteract(Level level, BlockPos pos, BlockState onState, Entity entity, CallbackInfo ci) {
        boolean cancelled;
        if (entity instanceof Player player) {
            cancelled = CraftEventFactory.callPlayerInteractEvent(player, Action.PHYSICAL, pos, null, null, null).isCancelled();
        } else {
            EntityInteractEvent event = new EntityInteractEvent(entity.getBukkitEntity(), CraftBlock.at(level, pos));
            level.getCraftServer().getPluginManager().callEvent(event);
            cancelled = event.isCancelled();
        }
        if (cancelled) {
            ci.cancel();
        }
    }

    @Inject(method = "deactivate", cancellable = true, at = @At("HEAD"))
    private static void silkard$deactivateRedstone(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        int newCurrent = CraftEventFactory.callRedstoneChange(level, pos, state.getValue(SculkSensorBlock.POWER), 0).getNewCurrent();
        if (newCurrent > 0) {
            level.setBlockAndUpdate(pos, (BlockState) state.setValue(SculkSensorBlock.POWER, newCurrent));
            ci.cancel();
        }
    }

    @Inject(method = "activate", cancellable = true, at = @At("HEAD"))
    private void silkard$activateRedstone(Entity sourceEntity, Level level, BlockPos pos, BlockState state, int calculatedPower, int frequency, CallbackInfo ci, @Local(argsOnly = true, ordinal = 0) LocalIntRef power) {
        int newCurrent = CraftEventFactory.callRedstoneChange(level, pos, state.getValue(SculkSensorBlock.POWER), calculatedPower).getNewCurrent();
        if (newCurrent <= 0) {
            ci.cancel();
            return;
        }
        power.set(newCurrent);
    }

    // CraftBukkit start - Delegate to getExpDrop
    @Overwrite
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
    }

    public int getExpDrop(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        return dropExperience ? this.silkard$tryDropExperience(level, pos, tool, ConstantInt.of(5)) : 0;
    }
    // CraftBukkit end
}
