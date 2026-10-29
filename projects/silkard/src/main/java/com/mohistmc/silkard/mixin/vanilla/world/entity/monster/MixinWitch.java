package com.mohistmc.silkard.mixin.vanilla.world.entity.monster;

import java.util.function.Consumer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.alchemy.PotionContents;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(Witch.class)
public class MixinWitch {

    // CraftBukkit - add EntityPotionEffectEvent cause
    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionContents;forEachEffect(Ljava/util/function/Consumer;F)V"))
    private void silkard$forEachEffect(PotionContents contents, Consumer<MobEffectInstance> consumer, float scale) {
        Witch self = (Witch) (Object) this;
        contents.forEachEffect((effect) -> self.addEffect(effect, EntityPotionEffectEvent.Cause.ATTACK), scale);
    }
}
