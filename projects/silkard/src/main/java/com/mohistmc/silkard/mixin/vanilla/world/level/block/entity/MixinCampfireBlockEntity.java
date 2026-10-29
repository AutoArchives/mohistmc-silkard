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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(CampfireBlockEntity.class)
public class MixinCampfireBlockEntity {

    @Shadow
    @Final
    private int[] cookingProgress;

    @Shadow
    @Final
    private int[] cookingTime;

    @Overwrite
    public static void cookTick(ServerLevel level, BlockPos pos, BlockState state, CampfireBlockEntity entity, RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> check) {
        boolean flag = false;

        for (int i = 0; i < entity.getItems().size(); i++) {
            ItemStack itemstack = entity.getItems().get(i);
            if (!itemstack.isEmpty()) {
                flag = true;
                if (entity.cookingProgress[i] >= entity.cookingTime[i]) {
                    SingleRecipeInput input = new SingleRecipeInput(itemstack);
                    ItemStack itemstack1 = check.getRecipeFor(input, level).map(holder -> holder.value().assemble(input)).orElse(itemstack);

                    if (itemstack1.isItemEnabled(level.enabledFeatures())) {
                        BlockCookEvent event = new BlockCookEvent(CraftBlock.at(level, pos), CraftItemStack.asCraftMirror(itemstack), CraftItemStack.asBukkitCopy(itemstack1));
                        ((ContextLevel) level).getCraftServer().getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return;
                        }

                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), CraftItemStack.asNMSCopy(event.getResult()));
                        entity.getItems().set(i, ItemStack.EMPTY);
                        level.sendBlockUpdated(pos, state, state, 3);
                    }
                }
            }
        }

        if (flag) {
            level.blockEntityChanged(pos);
            if (!state.isAir()) {
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
            }
        }
    }

    @ModifyExpressionValue(method = "placeFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/CampfireCookingRecipe;cookingTime()I"))
    private int silkard$campfireStart(int original, ServerLevel level, LivingEntity sourceEntity, ItemStack placeItem, @Local(ordinal = 0) Optional<RecipeHolder<CampfireCookingRecipe>> optional) {
        BlockPos pos = ((net.minecraft.world.level.block.entity.BlockEntity) (Object) this).getBlockPos();
        CampfireStartEvent event = new CampfireStartEvent(CraftBlock.at(level, pos), CraftItemStack.asCraftMirror(placeItem), (org.bukkit.inventory.CampfireRecipe) optional.get().toBukkitRecipe());
        ((ContextLevel) level).getCraftServer().getPluginManager().callEvent(event);
        return event.getTotalCookTime();
    }
}
