package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import com.mohistmc.silkard.injected.world.level.block.entity.ContextContainerOpenersCounter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * @author Mgazul
 * @date 2026/3/28 18:43
 */
@Mixin(ContainerOpenersCounter.class)
public abstract class MixinContainerOpenersCounter implements ContextContainerOpenersCounter {

    // @formatter:off
    @Shadow protected abstract void onOpen(Level level, BlockPos pos, BlockState blockState);
    @Shadow protected abstract void onClose(Level level, BlockPos pos, BlockState blockState);
    @Shadow protected abstract void openerCountChanged(Level level, BlockPos pos, BlockState blockState, int previous, int current);
    @Shadow private int openCount;
    @Unique public boolean opened; // CraftBukkit
    // @formatter:on

    @Override
    public boolean silkard_opened() {
        return opened;
    }

    @Override
    public void silkard_opened(boolean opened) {
        this.opened = opened;
    }

    // CraftBukkit start
    @Override
    public void onAPIOpen(Level level, BlockPos blockpos, BlockState blockstate) {
        onOpen(level, blockpos, blockstate);
    }

    @Override
    public void onAPIClose(Level level, BlockPos blockpos, BlockState blockstate) {
        onClose(level, blockpos, blockstate);
    }

    @Override
    public void openerAPICountChanged(Level level, BlockPos blockpos, BlockState blockstate, int i, int j) {
        openerCountChanged(level, blockpos, blockstate, i, j);
    }

    // CraftBukkit - trapped chests emit a redstone change while the opener count moves
    @Inject(method = "incrementOpeners", at = @At("RETURN"))
    private void silkard$redstoneChangeOnOpen(LivingEntity entity, Level level, BlockPos pos, BlockState blockState, double maxInteractionRange, CallbackInfo ci) {
        this.silkard$callRedstoneChange(level, pos, this.openCount - 1);
    }

    @Inject(method = "decrementOpeners", at = @At("RETURN"))
    private void silkard$redstoneChangeOnClose(LivingEntity entity, Level level, BlockPos pos, BlockState blockState, CallbackInfo ci) {
        this.silkard$callRedstoneChange(level, pos, this.openCount + 1);
    }

    private void silkard$callRedstoneChange(Level level, BlockPos pos, int previousCount) {
        int oldPower = Math.max(0, Math.min(15, previousCount));
        int newPower = Math.max(0, Math.min(15, this.openCount));
        if (oldPower != newPower && level.getBlockState(pos).is(Blocks.TRAPPED_CHEST)) {
            CraftEventFactory.callRedstoneChange(level, pos, oldPower, newPower);
        }
    }

    // CraftBukkit - containers opened through the API have no player attached, count them in manually
    @Redirect(method = "recheckOpeners", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    private int silkard$countApiOpener(List<ContainerUser> list) {
        return list.size() + (this.opened ? 1 : 0);
    }
    // CraftBukkit end
}
