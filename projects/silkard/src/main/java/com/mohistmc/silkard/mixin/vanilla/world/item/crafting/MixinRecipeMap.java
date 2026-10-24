package com.mohistmc.silkard.mixin.vanilla.world.item.crafting;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mohistmc.silkard.injected.world.item.crafting.ContextRecipeMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(RecipeMap.class)
public abstract class MixinRecipeMap implements ContextRecipeMap {

    @Mutable
    @Shadow
    private Multimap<RecipeType<?>, RecipeHolder<?>> byType;

    @Mutable
    @Shadow
    private Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey;

    // CraftBukkit start - mutable, ordered
    @Inject(method = "create", at = @At("RETURN"))
    private static void silkard$makeMutable(HolderLookup<Recipe<?>> recipes, CallbackInfoReturnable<RecipeMap> cir) {
        ((ContextRecipeMap) cir.getReturnValue()).silkard$makeMutable();
    }

    @Override
    public void silkard$makeMutable() {
        if (!(this.byType instanceof LinkedHashMultimap)) {
            this.byType = LinkedHashMultimap.create(this.byType);
        }
        if (!(this.byKey instanceof java.util.LinkedHashMap)) {
            this.byKey = Maps.newLinkedHashMap(this.byKey);
        }
    }

    @Override
    public void addRecipe(RecipeHolder<?> irecipe) {
        Collection<RecipeHolder<?>> map = this.byType.get(irecipe.value().getType());

        if (this.byKey.containsKey(irecipe.id())) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + irecipe.id());
        } else {
            map.add(irecipe);
            ((java.util.SequencedMap<ResourceKey<Recipe<?>>, RecipeHolder<?>>) this.byKey).putFirst(irecipe.id(), irecipe); // CraftBukkit - ordered
        }
    }

    @Override
    public boolean removeRecipe(ResourceKey<Recipe<?>> mcKey) {
        boolean removed = false;
        Iterator<RecipeHolder<?>> iter = this.byType.values().iterator();
        while (iter.hasNext()) {
            RecipeHolder<?> recipe = iter.next();
            if (recipe.id().equals(mcKey)) {
                iter.remove();
                removed = true;
            }
        }
        removed |= this.byKey.remove(mcKey) != null;

        return removed;
    }
    // CraftBukkit end
}
