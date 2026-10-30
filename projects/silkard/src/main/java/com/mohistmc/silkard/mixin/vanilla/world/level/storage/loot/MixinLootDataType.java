package com.mohistmc.silkard.mixin.vanilla.world.level.storage.loot;

import com.mohistmc.silkard.injected.world.level.storage.loot.ContextLootTable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(LootDataType.class)
public class MixinLootDataType {

    // CraftBukkit start - attach craft loot table after validation
    @Inject(method = "runValidation(Lnet/minecraft/world/level/storage/loot/ValidationContextSource;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/level/storage/loot/Validatable;)V", at = @At("RETURN"))
    private void silkard$postValidate(ValidationContextSource contextSource, ResourceKey key, Validatable value, CallbackInfo ci) {
        if ((LootDataType<?>) (Object) this == LootDataType.TABLE && value instanceof LootTable lootTable) {
            ((ContextLootTable) lootTable).silkard$craftLootTable(new CraftLootTable(CraftNamespacedKey.fromMinecraft(key.identifier()), lootTable));
        }
    }
    // CraftBukkit end
}
