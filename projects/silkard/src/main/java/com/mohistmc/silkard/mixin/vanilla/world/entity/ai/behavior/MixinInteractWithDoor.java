package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.behavior;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.entity.EntityInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(InteractWithDoor.class)
public class MixinInteractWithDoor {

    // CraftBukkit start - entities opening doors
    // 26.3: CB 的 lambda$static$0 已改名为 lambda$create$2（declarative BehaviorBuilder 重构），setOpen 调用点不变
    @Redirect(method = "lambda$create$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/DoorBlock;setOpen(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Z)V"))
    private static void silkard$openDoorEvent(DoorBlock door, net.minecraft.world.entity.Entity entity, Level level, BlockState state, BlockPos pos, boolean open, @Local(argsOnly = true) LivingEntity body) {
        if (entity != null && !entity.is(body)) {
            door.setOpen(entity, level, state, pos, open);
            return;
        }
        EntityInteractEvent event = new EntityInteractEvent(body.getBukkitEntity(), CraftBlock.at(level, pos));
        level.getCraftServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            door.setOpen(entity, level, state, pos, open);
        }
    }
    // CraftBukkit end
}
