package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.nautilus;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(AbstractNautilus.class)
public class MixinAbstractNautilus {

    @Redirect(method = "createInventory", at = @At(value = "NEW", target = "Lnet/minecraft/world/SimpleContainer;"))
    private SimpleContainer silkard_createInventory(int size) {
        // CraftBukkit - store entity
        return new SimpleContainer(size);
    }
}
