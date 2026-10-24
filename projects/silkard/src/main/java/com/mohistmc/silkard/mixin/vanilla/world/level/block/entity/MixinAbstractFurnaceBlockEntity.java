package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import com.mohistmc.silkard.injected.world.level.block.entity.ContextAbstractFurnaceBlockEntity;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class MixinAbstractFurnaceBlockEntity implements ContextAbstractFurnaceBlockEntity {

    // CraftBukkit start
    @Shadow
    private it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap<?> recipesUsed;

    @SuppressWarnings("unchecked")
    @Override
    public Object2IntOpenHashMap<net.minecraft.resources.Identifier> getRecipesUsed() {
        // CraftBukkit - vanilla 26.3 用 Reference2IntOpenHashMap<ResourceKey<Recipe>>；桥接为 CB 兼容视图
        Object2IntOpenHashMap<net.minecraft.resources.Identifier> converted = new Object2IntOpenHashMap<>();
        for (var entry : ((it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap<net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<?>>>) this.recipesUsed).reference2IntEntrySet()) {
            converted.put(entry.getKey().identifier(), entry.getIntValue());
        }
        return converted;
    }

    @Override
    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel serverlevel, Vec3 vec3, BlockPos blockpos, @Nullable Player player, ItemStack itemstack, int amount) {
        List<RecipeHolder<?>> list = ((AbstractFurnaceBlockEntity) (Object) this).getRecipesToAwardAndPopExperience(serverlevel, vec3);
        // CraftBukkit - experience per recipe handled by vanilla; extract/exp event in silkard$createExperience
        return list;
    }

    @Override
    public List<RecipeHolder<?>> silkard$dropExp(ServerPlayer entity, ItemStack itemstack, int amount) {
        AbstractFurnaceBlockEntity self = (AbstractFurnaceBlockEntity) (Object) this;
        return this.getRecipesToAwardAndPopExperience(entity.level(), entity.position(), self.getBlockPos(), entity, itemstack, amount);
    }

    // CraftBukkit - fire FurnaceExtractEvent / BlockExpEvent (CB createExperience hunk)
    @SuppressWarnings("unused")
    private static void silkard$createExperience(ServerLevel level, Vec3 position, int amount, float value, BlockPos blockpos, @Nullable Player player, ItemStack itemstack, int itemAmount) {
        int j = Mth.floor((float) amount * value);
        float f1 = Mth.frac((float) amount * value);
        if (f1 != 0.0F && level.getRandom().nextFloat() < f1) {
            ++j;
        }

        // CraftBukkit start
        BlockExpEvent event;
        if (itemAmount != 0) {
            event = new FurnaceExtractEvent((org.bukkit.entity.Player) player.getBukkitEntity(), CraftBlock.at(level, blockpos), CraftItemType.minecraftToBukkit(itemstack.getItem()), itemAmount, j);
        } else {
            event = new BlockExpEvent(CraftBlock.at(level, blockpos), j);
        }
        level.getCraftServer().getPluginManager().callEvent(event);
        j = event.getExpToDrop();
        // CraftBukkit end

        ExperienceOrb.award(level, position, j);
    }
    // CraftBukkit end
}
