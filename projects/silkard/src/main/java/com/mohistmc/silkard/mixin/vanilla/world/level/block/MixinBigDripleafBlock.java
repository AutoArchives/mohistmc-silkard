package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BigDripleafBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(BigDripleafBlock.class)
public class MixinBigDripleafBlock {

    @Shadow
    private void setTiltAndScheduleTick(BlockState state, Level level, BlockPos pos, Tilt tilt, SoundEvent sound) {
        throw new AssertionError();
    }

    // CraftBukkit start - tilt dripleaf with interact and change events
    @Unique
    private void silkard$tiltAndScheduleTick(BlockState state, Level level, BlockPos pos, Tilt tilt, SoundEvent sound, Entity entity, boolean interact) {
        if (entity != null) {
            if (interact) {
                Cancellable cancellable;
                if (entity instanceof Player player) {
                    cancellable = CraftEventFactory.callPlayerInteractEvent(player, Action.PHYSICAL, pos, null, null, null);
                } else {
                    cancellable = new EntityInteractEvent(entity.getBukkitEntity(), level.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()));
                    level.getCraftServer().getPluginManager().callEvent((EntityInteractEvent) cancellable);
                }
                if (cancellable.isCancelled()) {
                    return;
                }
            }
            if (!CraftEventFactory.callEntityChangeBlockEvent(entity, pos, state.setValue(BlockStateProperties.TILT, tilt))) {
                return;
            }
        }
        this.setTiltAndScheduleTick(state, level, pos, tilt, sound);
    }

    @Redirect(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BigDripleafBlock;setTiltAndScheduleTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/properties/Tilt;Lnet/minecraft/sounds/SoundEvent;)V"))
    private void silkard$projectileTilt(BigDripleafBlock self, BlockState state, Level level, BlockPos pos, Tilt tilt, SoundEvent sound,
                                         Level levelArg, BlockState stateArg, BlockHitResult hit, Projectile projectile) {
        this.silkard$tiltAndScheduleTick(state, level, pos, tilt, sound, projectile, false);
    }

    @Redirect(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BigDripleafBlock;setTiltAndScheduleTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/properties/Tilt;Lnet/minecraft/sounds/SoundEvent;)V"))
    private void silkard$entityTilt(BigDripleafBlock self, BlockState state, Level level, BlockPos pos, Tilt tilt, SoundEvent sound,
                                    BlockState stateArg, Level levelArg, BlockPos posArg, Entity entity, InsideBlockEffectApplier applier, boolean precise) {
        this.silkard$tiltAndScheduleTick(state, level, pos, tilt, sound, entity, true);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BigDripleafBlock;setTiltAndScheduleTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/properties/Tilt;Lnet/minecraft/sounds/SoundEvent;)V", ordinal = 0))
    private void silkard$tickTilt0(BigDripleafBlock self, BlockState state, Level level, BlockPos pos, Tilt tilt, SoundEvent sound) {
        this.silkard$tiltAndScheduleTick(state, level, pos, tilt, sound, null, false);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BigDripleafBlock;setTiltAndScheduleTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/properties/Tilt;Lnet/minecraft/sounds/SoundEvent;)V", ordinal = 1))
    private void silkard$tickTilt1(BigDripleafBlock self, BlockState state, Level level, BlockPos pos, Tilt tilt, SoundEvent sound) {
        this.silkard$tiltAndScheduleTick(state, level, pos, tilt, sound, null, false);
    }
    // CraftBukkit end
}
