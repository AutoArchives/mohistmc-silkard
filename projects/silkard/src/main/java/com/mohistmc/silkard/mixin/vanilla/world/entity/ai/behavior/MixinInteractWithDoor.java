package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(InteractWithDoor.class)
public class MixinInteractWithDoor {

    // CraftBukkit start - entities opening doors
    @Inject(method = "lambda$create$2", cancellable = true, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/level/block/DoorBlock;setOpen(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Z)V"))
    private static void silkard$openDoor(CallbackInfo ci, @Local(argsOnly = true) LivingEntity body, @Local(ordinal = 0) BlockPos pos) {
        silkard$callInteract(ci, body, pos);
    }

    @Inject(method = "lambda$create$2", cancellable = true, at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/level/block/DoorBlock;setOpen(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Z)V"))
    private static void silkard$openDoorForOtherMobs(CallbackInfo ci, @Local(argsOnly = true) LivingEntity body, @Local(ordinal = 1) BlockPos pos) {
        silkard$callInteract(ci, body, pos);
    }

    // CraftBukkit returns false when cancelled, which is what cancelling a boolean target does by default.
    private static void silkard$callInteract(CallbackInfo ci, LivingEntity body, BlockPos pos) {
        Level level = body.level();
        EntityInteractEvent event = new EntityInteractEvent(body.getBukkitEntity(), CraftBlock.at(level, pos));
        level.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
