package com.mohistmc.silkard.mixin.vanilla.world.level.gameevent;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftGameEvent;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.world.GenericGameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(GameEventDispatcher.class)
public class MixinGameEventDispatcher {

    @Shadow
    private ServerLevel level;

    @Inject(method = "post", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;blockToSectionCoord(I)I", ordinal = 0))
    private void silkard$genericGameEvent(Holder<GameEvent> gameEvent, Vec3 position, GameEvent.Context context, CallbackInfo ci, @Local(ordinal = 0) LocalIntRef radius) {
        GenericGameEvent event = new GenericGameEvent(CraftGameEvent.minecraftToBukkit(gameEvent.value()),
                CraftLocation.toBukkit(BlockPos.containing(position), this.level.getWorld()),
                context.sourceEntity() == null ? null : context.sourceEntity().getBukkitEntity(),
                radius.get(), !Bukkit.isPrimaryThread());
        this.level.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        radius.set(event.getRadius());
    }
}
