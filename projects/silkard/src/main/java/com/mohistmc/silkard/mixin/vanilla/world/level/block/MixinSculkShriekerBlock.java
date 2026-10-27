package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.block.ContextBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.Action;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(SculkShriekerBlock.class)
public class MixinSculkShriekerBlock implements ContextBlock {

    // CraftBukkit start - Call interact event when a player steps on the shrieker
    @Inject(method = "stepOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntityType;)Ljava/util/Optional;"), cancellable = true)
    private void silkard$interactEvent(Level level, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
        if (!(level instanceof ServerLevel serverlevel)) {
            return;
        }
        ServerPlayer player = SculkShriekerBlockEntity.tryGetPlayer(entity);
        if (player == null) {
            return;
        }
        if (CraftEventFactory.callPlayerInteractEvent(player, Action.PHYSICAL, pos, null, null, null).isCancelled()) {
            ci.cancel();
        }
    }
    // CraftBukkit end

    // CraftBukkit start - Delegate to getExpDrop
    @Overwrite
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
    }

    public int getExpDrop(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        if (dropExperience) {
            return this.silkard$tryDropExperience(level, pos, tool, ConstantInt.of(5));
        }
        return 0;
    }
    // CraftBukkit end
}
