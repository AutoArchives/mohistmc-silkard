package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.feline;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(Cat.class)
public class MixinCat {

    // CraftBukkit start - fire EntityTameEvent
    @Redirect(method = "tryToTame", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/TamableAnimal;tame(Lnet/minecraft/world/entity/player/Player;)V"))
    private void silkard$tame(TamableAnimal self, Player player) {
        if (CraftEventFactory.callEntityTameEvent((Mob) (Object) this, player).isCancelled()) {
            return;
        }
        self.tame(player);
    }
    // CraftBukkit end
}
