package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import com.mohistmc.silkard.injected.world.level.block.entity.ContextCatalystListener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
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
@Mixin(SculkCatalystBlockEntity.class)
public abstract class MixinSculkCatalystBlockEntity extends BlockEntity {

    public MixinSculkCatalystBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    @Final
    private SculkCatalystBlockEntity.CatalystListener catalystListener;

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        ((ContextCatalystListener) (Object) this.catalystListener).silkard$setLevel(level);
    }

    // SPIGOT-7068: source block override
    @Inject(method = "serverTick", at = @At("HEAD"))
    private static void silkard$overrideSource(Level level, BlockPos pos, BlockState state, SculkCatalystBlockEntity entity, CallbackInfo ci) {
        CraftEventFactory.sourceBlockOverride = entity.getBlockPos();
    }

    @Inject(method = "serverTick", at = @At("RETURN"))
    private static void silkard$resetSource(Level level, BlockPos pos, BlockState state, SculkCatalystBlockEntity entity, CallbackInfo ci) {
        CraftEventFactory.sourceBlockOverride = null;
    }
}
