package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import java.util.Set;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(BlockEntityTypes.class)
public class MixinBlockEntityTypes {

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/Set;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Set;"))
    private static Set<BlockEntityType<?>> silkard$opOnlyCustomData(Object a, Object b, Object c, Object d, Object e, Object f) {
        return Set.of((BlockEntityType<?>) a, (BlockEntityType<?>) b, (BlockEntityType<?>) c, (BlockEntityType<?>) d, (BlockEntityType<?>) e, (BlockEntityType<?>) f, BlockEntityTypes.CHEST);
    }
}
