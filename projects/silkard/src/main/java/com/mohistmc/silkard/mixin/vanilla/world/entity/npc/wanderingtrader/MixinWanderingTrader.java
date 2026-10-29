package com.mohistmc.silkard.mixin.vanilla.world.entity.npc.wanderingtrader;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(WanderingTrader.class)
public class MixinWanderingTrader {

    // CraftBukkit start - set default despawn delay
    @Inject(method = "<init>", at = @At("RETURN"))
    private void silkard$despawnDelay(EntityType<? extends WanderingTrader> type, Level level, CallbackInfo ci) {
        ((WanderingTrader) (Object) this).setDespawnDelay(48000);
    }
    // CraftBukkit end

    // CraftBukkit start - add Bukkit remove cause
    @Redirect(method = "maybeDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/wanderingtrader/WanderingTrader;discard()V"))
    private void silkard$discard() {
        ((ContextEntity) (Object) this).discard(EntityRemoveEvent.Cause.DESPAWN);
    }
    // CraftBukkit end
}
