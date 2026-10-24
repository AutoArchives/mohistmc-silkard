package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile.throwableitemprojectile;

import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
// 26.3 的 onHit / discard() 只在 AbstractThrownPotion，ThrownLingeringPotion 本身不再声明 onHit
@Mixin(AbstractThrownPotion.class)
public class MixinAbstractThrownPotion {

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/AbstractThrownPotion;discard()V"))
    private void silkard_discard(AbstractThrownPotion potion) {
        if (potion instanceof ThrownLingeringPotion) {
            potion.discard(org.bukkit.event.entity.EntityRemoveEvent.Cause.HIT); // CraftBukkit - add Bukkit remove cause
        } else {
            potion.discard();
        }
    }
}
