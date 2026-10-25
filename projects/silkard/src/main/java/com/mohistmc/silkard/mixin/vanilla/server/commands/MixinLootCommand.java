package com.mohistmc.silkard.mixin.vanilla.server.commands;

import net.minecraft.server.commands.LootCommand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(LootCommand.class)
public class MixinLootCommand {

    @ModifyVariable(method = "dropInWorld", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static List<ItemStack> silkard_removeEmpty(List<ItemStack> drops) {
        drops.removeIf(ItemStack::isEmpty); // CraftBukkit - SPIGOT-6959 Remove empty items for avoid throw an error in new EntityItem
        return drops;
    }
}
