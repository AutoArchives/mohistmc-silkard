package com.mohistmc.silkard.mixin.vanilla.world.entity.decoration;

import com.mohistmc.silkard.injected.world.entity.decoration.ContextItemFrame;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/5/1 17:48
 */
@Mixin(ItemFrame.class)
public class MixinItemFrame implements ContextItemFrame {

    @Shadow
    private void onItemChanged(ItemStack item) {
        throw new AssertionError();
    }

    // CraftBukkit start - EntityDamageEvent
    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ItemFrame;dropItem(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Z)V"), cancellable = true)
    private void silkard$hurtServer(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (CraftEventFactory.handleNonLivingEntityDamageEvent((ItemFrame) (Object) this, source, damage, false) || ((ItemFrame) (Object) this).isRemoved()) {
            cir.setReturnValue(true);
        }
    }
    // CraftBukkit end

    /**
     * @author Mgazul
     * @reason CraftBukkit - playSound switch for the 3-arg overload
     */
    @Overwrite
    public void setItem(ItemStack itemStack, boolean updateNeighbours) {
        this.setItem(itemStack, updateNeighbours, true);
    }

    @Override
    public void setItem(ItemStack itemStack, boolean updateNeighbours, boolean playSound) {
        ItemFrame self = (ItemFrame) (Object) this;
        if (!itemStack.isEmpty()) {
            itemStack = itemStack.copyWithCount(1);
        }
        this.onItemChanged(itemStack);
        self.getEntityData().set(ItemFrame.DATA_ITEM, itemStack);
        if (!itemStack.isEmpty() && playSound) { // CraftBukkit
            self.playSound(self.getAddItemSound(), 1.0F, 1.0F);
        }
        BlockPos pos = self.getPos();
        if (updateNeighbours && pos != null) {
            self.level().updateNeighbourForOutputSignal(pos, Blocks.AIR);
        }
    }
}
