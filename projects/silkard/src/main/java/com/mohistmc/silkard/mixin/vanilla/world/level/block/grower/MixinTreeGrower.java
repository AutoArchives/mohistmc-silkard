package com.mohistmc.silkard.mixin.vanilla.world.level.block.grower;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mohistmc.silkard.bukkit.BukkitUtils;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.Feature;
import org.bukkit.TreeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(TreeGrower.class)
public class MixinTreeGrower {

    // CraftBukkit start - resolve the Bukkit TreeType before the feature is placed
    @ModifyExpressionValue(method = "growTree", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;get(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;"))
    private Optional<Holder.Reference<Feature>> silkard$setTreeType(Optional<Holder.Reference<Feature>> original) {
        original.ifPresent(holder -> {
            ResourceKey<Feature> key = holder.unwrapKey().orElseThrow();
            BukkitUtils.setTreeType(silkard$treeType(key));
        });
        return original;
    }

    private static TreeType silkard$treeType(ResourceKey<Feature> key) {
        if (key == TreeFeatures.OAK || key == TreeFeatures.OAK_BEES_005) {
            return TreeType.TREE;
        } else if (key == TreeFeatures.HUGE_RED_MUSHROOM) {
            return TreeType.RED_MUSHROOM;
        } else if (key == TreeFeatures.HUGE_BROWN_MUSHROOM) {
            return TreeType.BROWN_MUSHROOM;
        } else if (key == TreeFeatures.JUNGLE_TREE) {
            return TreeType.COCOA_TREE;
        } else if (key == TreeFeatures.JUNGLE_TREE_NO_VINE) {
            return TreeType.SMALL_JUNGLE;
        } else if (key == TreeFeatures.PINE) {
            return TreeType.TALL_REDWOOD;
        } else if (key == TreeFeatures.SPRUCE) {
            return TreeType.REDWOOD;
        } else if (key == TreeFeatures.ACACIA) {
            return TreeType.ACACIA;
        } else if (key == TreeFeatures.BIRCH || key == TreeFeatures.BIRCH_BEES_005) {
            return TreeType.BIRCH;
        } else if (key == TreeFeatures.SUPER_BIRCH_BEES_0002) {
            return TreeType.TALL_BIRCH;
        } else if (key == TreeFeatures.SWAMP_OAK) {
            return TreeType.SWAMP;
        } else if (key == TreeFeatures.FANCY_OAK || key == TreeFeatures.FANCY_OAK_BEES_005) {
            return TreeType.BIG_TREE;
        } else if (key == TreeFeatures.JUNGLE_BUSH) {
            return TreeType.JUNGLE_BUSH;
        } else if (key == TreeFeatures.DARK_OAK) {
            return TreeType.DARK_OAK;
        } else if (key == TreeFeatures.MEGA_SPRUCE) {
            return TreeType.MEGA_REDWOOD;
        } else if (key == TreeFeatures.MEGA_PINE) {
            return TreeType.MEGA_PINE;
        } else if (key == TreeFeatures.MEGA_JUNGLE_TREE) {
            return TreeType.JUNGLE;
        } else if (key == TreeFeatures.AZALEA_TREE) {
            return TreeType.AZALEA;
        } else if (key == TreeFeatures.MANGROVE) {
            return TreeType.MANGROVE;
        } else if (key == TreeFeatures.TALL_MANGROVE) {
            return TreeType.TALL_MANGROVE;
        } else if (key == TreeFeatures.CHERRY || key == TreeFeatures.CHERRY_BEES_005) {
            return TreeType.CHERRY;
        } else if (key == TreeFeatures.PALE_OAK || key == TreeFeatures.PALE_OAK_BONEMEAL) {
            return TreeType.PALE_OAK;
        } else if (key == TreeFeatures.PALE_OAK_CREAKING) {
            return TreeType.PALE_OAK_CREAKING;
        } else if (key == TreeFeatures.RED_POPLAR) {
            return TreeType.RED_POPLAR;
        } else if (key == TreeFeatures.ORANGE_POPLAR) {
            return TreeType.ORANGE_POPLAR;
        } else if (key == TreeFeatures.YELLOW_POPLAR) {
            return TreeType.YELLOW_POPLAR;
        } else {
            throw new IllegalArgumentException("Unknown tree generator " + key);
        }
    }
    // CraftBukkit end
}
