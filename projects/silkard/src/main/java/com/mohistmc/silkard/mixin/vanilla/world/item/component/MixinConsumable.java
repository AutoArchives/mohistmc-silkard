package com.mohistmc.silkard.mixin.vanilla.world.item.component;

import com.mohistmc.silkard.injected.world.item.component.ContextConsumable;
import com.mohistmc.silkard.injected.world.item.component.ContextConsumableListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(Consumable.class)
public class MixinConsumable implements ContextConsumable {

    // CraftBukkit start
    @Override
    public void cancelUsingItem(ServerPlayer player, ItemStack stack) {
        stack.getAllOfType(ConsumableListener.class).forEach(listener -> ((ContextConsumableListener) listener).cancelUsingItem(player, stack));
        player.server.getPlayerList().sendActivePlayerEffects(player);
    }
    // CraftBukkit end
}
