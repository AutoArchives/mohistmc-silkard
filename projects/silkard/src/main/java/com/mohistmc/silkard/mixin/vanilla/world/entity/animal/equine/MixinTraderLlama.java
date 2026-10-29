package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.equine;

import net.minecraft.world.entity.animal.equine.TraderLlama;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(TraderLlama.class)
public class MixinTraderLlama {

    // CraftBukkit - add Bukkit remove cause
    @Redirect(method = "maybeDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/equine/TraderLlama;discard()V"))
    private void silkard$discard(TraderLlama entity) {
        entity.discard(EntityRemoveEvent.Cause.DESPAWN);
    }
}
