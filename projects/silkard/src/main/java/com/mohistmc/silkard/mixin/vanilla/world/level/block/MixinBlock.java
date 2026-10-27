package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.block.ContextBlock;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(Block.class)
public class MixinBlock implements ContextBlock {

    // CraftBukkit start
    @Override
    public int getExpDrop(BlockState blockState, ServerLevel world, BlockPos blockPos, ItemStack itemStack, boolean flag) {
        return 0;
    }

    @Override
    public int silkard$tryDropExperience(ServerLevel level, BlockPos pos, ItemStack heldItem, IntProvider amount) {
        return EnchantmentHelper.processBlockExperience(level, heldItem, amount.sample(level.getRandom()));
    }

    @Redirect(method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean silkard$captureDrop(Level level, Entity entity) {
        List<ItemEntity> captureDrops = level.silkard$captureDrops();
        if (captureDrops != null) {
            captureDrops.add((ItemEntity) entity);
            return true;
        }
        return level.addFreshEntity(entity);
    }
    // CraftBukkit end
}
