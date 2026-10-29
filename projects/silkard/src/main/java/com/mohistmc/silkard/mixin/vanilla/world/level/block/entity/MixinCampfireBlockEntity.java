package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.silkard.injected.world.level.ContextLevel;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CampfireStartEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(CampfireBlockEntity.class)
public class MixinCampfireBlockEntity {

    @Inject(method = "cookTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V"), cancellable = true)
    private static void silkard$blockCook(ServerLevel level, BlockPos pos, BlockState state, CampfireBlockEntity entity,
                                          RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> check,
                                          @Local(ordinal = 0) ItemStack source, @Local(ordinal = 1) ItemStack result,
                                          CallbackInfo ci) {
        BlockCookEvent event = new BlockCookEvent(CraftBlock.at(level, pos), CraftItemStack.asCraftMirror(source), CraftItemStack.asBukkitCopy(result));
        ((ContextLevel) level).getCraftServer().getPluginManager().callEvent(event);
        ci.cancel();
        if (event.isCancelled()) {
            return;
        }
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), CraftItemStack.asNMSCopy(event.getResult()));
        for (int k = 0; k < entity.getItems().size(); k++) {
            if (entity.getItems().get(k) == source) {
                entity.getItems().set(k, ItemStack.EMPTY);
                break;
            }
        }
        level.sendBlockUpdated(pos, state, state, 3);
    }

    /*
    @ModifyExpressionValue(method = "placeFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/CampfireCookingRecipe;cookingTime()I"))
    private int silkard$campfireStart(int original, ServerLevel level, LivingEntity sourceEntity, ItemStack placeItem, @Local(ordinal = 0) Optional<RecipeHolder<CampfireCookingRecipe>> optional) {
        CampfireStartEvent event = new CampfireStartEvent(CraftBlock.at(level, ((net.minecraft.world.level.block.entity.BlockEntity) (Object) this).getBlockPos()), CraftItemStack.asCraftMirror(placeItem), (org.bukkit.inventory.CampfireRecipe) optional.get().toBukkitRecipe());
        ((ContextLevel) level).getCraftServer().getPluginManager().callEvent(event);
        return event.getTotalCookTime();
    }
    */
}
