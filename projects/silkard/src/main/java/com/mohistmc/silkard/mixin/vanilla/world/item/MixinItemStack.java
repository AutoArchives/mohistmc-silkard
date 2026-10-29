package com.mohistmc.silkard.mixin.vanilla.world.item;

import com.mohistmc.silkard.injected.world.item.ContextItemStack;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/5/1 17:38
 */
@Mixin(ItemStack.class)
public abstract class MixinItemStack implements ContextItemStack {

    @Shadow
    private @Nullable Holder<Item> item;

    @Shadow
    @Final
    private PatchedDataComponentMap components;

    @Deprecated
    @Override
    public void setItem(Holder<Item> item) {
        this.item = item;
    }

    // CraftBukkit start
    @Override
    public void restorePatch(DataComponentPatch patch) {
        this.components.restorePatch(patch);
    }

    @Override
    public DataComponentPatch asPatch() {
        return this.components.asPatch();
    }

    @Shadow
    private int count;

    @Invoker("processDurabilityChange")
    abstract int silkard$processDurabilityChange(int amount, ServerLevel level, @Nullable ServerPlayer player);

    @Redirect(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;processDurabilityChange(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;)I"))
    private int silkard$playerItemDamage(ItemStack stack, int amount, ServerLevel level, @Nullable ServerPlayer player) {
        int j = this.silkard$processDurabilityChange(amount, level, player);
        if (player != null) {
            PlayerItemDamageEvent event = new PlayerItemDamageEvent(player.getBukkitEntity(), CraftItemStack.asCraftMirror(stack), j);
            event.getPlayer().getServer().getPluginManager().callEvent(event);
            if (j != event.getDamage() || event.isCancelled()) {
                event.getPlayer().updateInventory();
            }
            if (event.isCancelled()) {
                return 0;
            }
            j = event.getDamage();
        }
        return j;
    }

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void silkard$itemBreak(int newDamage, @Nullable ServerPlayer player, Consumer<ItemStack> onBreak, CallbackInfo ci) {
        if (this.count == 1 && player != null) {
            CraftEventFactory.callPlayerItemBreakEvent(player, (ItemStack) (Object) this);
        }
    }

    @Inject(method = "consume", at = @At("HEAD"), cancellable = true)
    private void silkard$consumeEmpty(int amount, LivingEntity owner, CallbackInfo ci) {
        if ((Object) this == ItemStack.EMPTY) {
            ci.cancel();
        }
    }
    // CraftBukkit end
}
