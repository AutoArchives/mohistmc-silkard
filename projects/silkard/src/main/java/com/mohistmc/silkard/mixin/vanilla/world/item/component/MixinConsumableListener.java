package com.mohistmc.silkard.mixin.vanilla.world.item.component;

import com.mohistmc.silkard.injected.world.item.component.ContextConsumableListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ConsumableListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ConsumableListener.class)
public interface MixinConsumableListener extends ContextConsumableListener {

    @Override
    default void cancelUsingItem(ServerPlayer serverplayer, ItemStack itemstack) {
    }
}
