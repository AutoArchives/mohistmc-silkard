package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.silkard.injected.world.level.block.ContextBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(RedStoneOreBlock.class)
public class MixinRedStoneOreBlock implements ContextBlock {

    @Shadow
    private static void interact(BlockState state, Level level, BlockPos pos) {
    }

    // CraftBukkit start - pass the entity through so the interactions become cancellable
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RedStoneOreBlock;interact(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
    private static void silkard$attack(BlockState state, Level level, BlockPos pos, @Local(argsOnly = true) Player player) {
        silkard$interact(state, level, pos, player);
    }

    @Redirect(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RedStoneOreBlock;interact(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
    private static void silkard$useItemOn(BlockState state, Level level, BlockPos pos, @Local(argsOnly = true) Player player) {
        silkard$interact(state, level, pos, player);
    }

    @Redirect(method = "stepOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RedStoneOreBlock;interact(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
    private static void silkard$stepOn(BlockState state, Level level, BlockPos pos, @Local(argsOnly = true) Entity entity) {
        if (entity instanceof Player player) {
            if (CraftEventFactory.callPlayerInteractEvent(player, Action.PHYSICAL, pos, null, null, null).isCancelled()) {
                return;
            }
        } else {
            EntityInteractEvent event = new EntityInteractEvent(entity.getBukkitEntity(), CraftBlock.at(level, pos));
            level.getCraftServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
        }
        silkard$interact(level.getBlockState(pos), level, pos, entity);
    }

    private static void silkard$interact(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!(Boolean) state.getValue(RedStoneOreBlock.LIT)) {
            if (!CraftEventFactory.callEntityChangeBlockEvent(entity, pos, (BlockState) state.setValue(RedStoneOreBlock.LIT, true))) {
                return;
            }
        }
        interact(state, level, pos);
    }
    // CraftBukkit end

    @Inject(method = "randomTick", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private void silkard$blockFade(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (CraftEventFactory.callBlockFadeEvent(level, pos, (BlockState) state.setValue(RedStoneOreBlock.LIT, false)).isCancelled()) {
            ci.cancel();
        }
    }

    // CraftBukkit start - Delegate to getExpDrop
    @Overwrite
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
    }

    public int getExpDrop(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        return dropExperience ? this.silkard$tryDropExperience(level, pos, tool, UniformInt.of(1, 5)) : 0;
    }
    // CraftBukkit end
}
