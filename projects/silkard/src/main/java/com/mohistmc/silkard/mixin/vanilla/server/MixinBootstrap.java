package com.mohistmc.silkard.mixin.vanilla.server;

import java.util.Map;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.ItemIdFix;
import net.minecraft.util.datafix.fixes.ItemSpawnEggFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(Bootstrap.class)
public class MixinBootstrap {

    // CraftBukkit start
    @Inject(method = "bootStrap", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/Bootstrap;wrapStreams()V", shift = At.Shift.AFTER))
    private static void silkard$legacyIds(CallbackInfo ci) {
        for (int i = 0; i < 16; i++) {
            BlockStateData.register(1008 + i, BlockStateData.create("minecraft:oak_sign", Map.of("rotation", String.valueOf(i))), BlockStateData.create("minecraft:standing_sign", Map.of("rotation", String.valueOf(i))));
        }

        ItemIdFix.ITEM_NAMES.put(323, "minecraft:oak_sign");

        BlockStateData.register(1440, BlockStateData.create("minecraft:portal", Map.of("axis", "x")), BlockStateData.create("minecraft:portal", Map.of("axis", "x")));

        ItemIdFix.ITEM_NAMES.put(409, "minecraft:prismarine_shard");
        ItemIdFix.ITEM_NAMES.put(410, "minecraft:prismarine_crystals");
        ItemIdFix.ITEM_NAMES.put(411, "minecraft:rabbit");
        ItemIdFix.ITEM_NAMES.put(412, "minecraft:cooked_rabbit");
        ItemIdFix.ITEM_NAMES.put(413, "minecraft:rabbit_stew");
        ItemIdFix.ITEM_NAMES.put(414, "minecraft:rabbit_foot");
        ItemIdFix.ITEM_NAMES.put(415, "minecraft:rabbit_hide");
        ItemIdFix.ITEM_NAMES.put(416, "minecraft:armor_stand");

        ItemIdFix.ITEM_NAMES.put(423, "minecraft:mutton");
        ItemIdFix.ITEM_NAMES.put(424, "minecraft:cooked_mutton");
        ItemIdFix.ITEM_NAMES.put(425, "minecraft:banner");
        ItemIdFix.ITEM_NAMES.put(426, "minecraft:end_crystal");
        ItemIdFix.ITEM_NAMES.put(427, "minecraft:spruce_door");
        ItemIdFix.ITEM_NAMES.put(428, "minecraft:birch_door");
        ItemIdFix.ITEM_NAMES.put(429, "minecraft:jungle_door");
        ItemIdFix.ITEM_NAMES.put(430, "minecraft:acacia_door");
        ItemIdFix.ITEM_NAMES.put(431, "minecraft:dark_oak_door");
        ItemIdFix.ITEM_NAMES.put(432, "minecraft:chorus_fruit");
        ItemIdFix.ITEM_NAMES.put(433, "minecraft:chorus_fruit_popped");
        ItemIdFix.ITEM_NAMES.put(434, "minecraft:beetroot");
        ItemIdFix.ITEM_NAMES.put(435, "minecraft:beetroot_seeds");
        ItemIdFix.ITEM_NAMES.put(436, "minecraft:beetroot_soup");
        ItemIdFix.ITEM_NAMES.put(437, "minecraft:dragon_breath");
        ItemIdFix.ITEM_NAMES.put(438, "minecraft:splash_potion");
        ItemIdFix.ITEM_NAMES.put(439, "minecraft:spectral_arrow");
        ItemIdFix.ITEM_NAMES.put(440, "minecraft:tipped_arrow");
        ItemIdFix.ITEM_NAMES.put(441, "minecraft:lingering_potion");
        ItemIdFix.ITEM_NAMES.put(442, "minecraft:shield");
        ItemIdFix.ITEM_NAMES.put(443, "minecraft:elytra");
        ItemIdFix.ITEM_NAMES.put(444, "minecraft:spruce_boat");
        ItemIdFix.ITEM_NAMES.put(445, "minecraft:birch_boat");
        ItemIdFix.ITEM_NAMES.put(446, "minecraft:jungle_boat");
        ItemIdFix.ITEM_NAMES.put(447, "minecraft:acacia_boat");
        ItemIdFix.ITEM_NAMES.put(448, "minecraft:dark_oak_boat");
        ItemIdFix.ITEM_NAMES.put(449, "minecraft:totem_of_undying");
        ItemIdFix.ITEM_NAMES.put(450, "minecraft:shulker_shell");
        ItemIdFix.ITEM_NAMES.put(452, "minecraft:iron_nugget");
        ItemIdFix.ITEM_NAMES.put(453, "minecraft:knowledge_book");

        ItemSpawnEggFix.ID_TO_ENTITY[23] = "Arrow";
    }
    // CraftBukkit end
}
