package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.axolotl;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(Axolotl.class)
public class MixinAxolotl {

    // SPIGOT-6907: re-implement LivingEntity#setMaximumAir()
    public int getDefaultMaxAirSupply() {
        return 6000;
    }

    @Inject(method = "getMaxAirSupply", at = @At("RETURN"), cancellable = true)
    private void silkard$maxAirSupply(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(((ContextEntity) (Object) this).silkard_maxAirTicks());
    }

    @Redirect(method = "applySupportingEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$addEffect(Player player, MobEffectInstance effect, Entity source) {
        return ((ContextLivingEntity) player).addEffect(effect, source, EntityPotionEffectEvent.Cause.AXOLOTL);
    }
}
