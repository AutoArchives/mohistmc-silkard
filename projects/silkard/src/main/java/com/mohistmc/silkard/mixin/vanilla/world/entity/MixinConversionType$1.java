package com.mohistmc.silkard.mixin.vanilla.world.entity;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(targets = "net.minecraft.world.entity.ConversionType$1")
public class MixinConversionType$1 {

    // CraftBukkit start
    @Redirect(method = "convert", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;remove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"))
    private void silkard$removeWithCause(Entity entity, Entity.RemovalReason reason, Mob from, Mob to, ConversionParams params) {
        ((ContextEntity) entity).remove(reason, EntityRemoveEvent.Cause.TRANSFORMATION);
    }

    @Redirect(method = "convert", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copyAndClear()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack silkard$copyItem(ItemStack stack, Mob from, Mob to, ConversionParams params) {
        return stack.copy(); // SPIGOT-7996: don't clear yet
    }

    public void postConvert(Mob from, Mob to, ConversionParams params) {
        if (params.keepEquipment()) {
            for (EquipmentSlot slot : EquipmentSlot.VALUES) {
                ItemStack stack = from.getItemBySlot(slot);
                if (!stack.isEmpty()) {
                    stack.setCount(0); // SPIGOT-7996: clear after conversion
                }
            }
        }
    }
    // CraftBukkit end
}
