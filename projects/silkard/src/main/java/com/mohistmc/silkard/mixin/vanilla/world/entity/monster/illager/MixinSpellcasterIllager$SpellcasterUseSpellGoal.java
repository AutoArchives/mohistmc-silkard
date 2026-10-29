package com.mohistmc.silkard.mixin.vanilla.world.entity.monster.illager;

import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(targets = "net.minecraft.world.entity.monster.illager.SpellcasterIllager$SpellcasterUseSpellGoal")
public abstract class MixinSpellcasterIllager$SpellcasterUseSpellGoal {

    @Shadow
    @Final
    private SpellcasterIllager this$0;

    @Shadow
    protected abstract SpellcasterIllager.IllagerSpell getSpell();

    // CraftBukkit start - EntitySpellCastEvent
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/illager/SpellcasterIllager$SpellcasterUseSpellGoal;performSpellCasting()V"), cancellable = true)
    private void silkard$spellCastEvent(CallbackInfo ci) {
        if (!CraftEventFactory.handleEntitySpellCastEvent(this.this$0, this.getSpell())) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
