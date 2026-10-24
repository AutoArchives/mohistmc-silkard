package com.mohistmc.silkard.mixin.vanilla.world.entity.animal;

import com.mohistmc.silkard.injected.world.entity.animal.ContextAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(Animal.class)
public class MixinAnimal implements ContextAnimal {

    // CB 自增字段，26.3 vanilla 无此成员
    @Unique
    public ItemStack breedItem;

    @Override
    public ItemStack getBreedItem() {
        return breedItem;
    }
}
