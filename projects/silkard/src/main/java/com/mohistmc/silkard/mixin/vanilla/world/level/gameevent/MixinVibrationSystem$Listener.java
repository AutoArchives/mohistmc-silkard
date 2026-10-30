package com.mohistmc.silkard.mixin.vanilla.world.level.gameevent;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.CraftGameEvent;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockReceiveGameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Mgazul
 * @date 2026/10/31
 */
@Mixin(VibrationSystem.Listener.class)
public class MixinVibrationSystem$Listener {

    // CraftBukkit start - BlockReceiveGameEvent
    @ModifyExpressionValue(method = "handleGameEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/gameevent/vibrations/VibrationSystem$User;canReceiveVibration(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Holder;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)Z"))
    private boolean silkard$receiveGameEvent(boolean original, @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) Holder<GameEvent> event, @Local(argsOnly = true) GameEvent.Context context, @Local Vec3 position) {
        BlockReceiveGameEvent bukkitEvent = new BlockReceiveGameEvent(
                CraftGameEvent.minecraftToBukkit(event.value()),
                CraftBlock.at(level, BlockPos.containing(position)),
                context.sourceEntity() == null ? null : context.sourceEntity().getBukkitEntity());
        bukkitEvent.setCancelled(!original);
        level.getCraftServer().getPluginManager().callEvent(bukkitEvent);
        return !bukkitEvent.isCancelled();
    }
    // CraftBukkit end
}
