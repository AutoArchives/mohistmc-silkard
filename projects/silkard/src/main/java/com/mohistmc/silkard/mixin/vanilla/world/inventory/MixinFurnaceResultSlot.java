package com.mohistmc.silkard.mixin.vanilla.world.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FurnaceResultSlot.class)
public class MixinFurnaceResultSlot {

    @Redirect(method = "checkTakeAchievements", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;awardUsedRecipesAndPopExperience(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void silkard$awardUsedRecipesAndPopExperience(AbstractFurnaceBlockEntity entity, ServerPlayer player) {
        entity.awardUsedRecipesAndPopExperience(player);
    }
}
