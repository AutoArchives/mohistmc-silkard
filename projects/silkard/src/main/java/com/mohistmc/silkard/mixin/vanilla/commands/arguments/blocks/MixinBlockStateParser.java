package com.mohistmc.silkard.mixin.vanilla.commands.arguments.blocks;

import com.google.common.collect.Maps;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(BlockStateParser.class)
public class MixinBlockStateParser {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;", ordinal = 0))
    private <K, V> java.util.HashMap<K, V> silkard$stableProperties() {
        return Maps.newLinkedHashMap(); // CraftBukkit - stable ordering for suggestions and parsing
    }
}
