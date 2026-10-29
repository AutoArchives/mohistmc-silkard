package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.TNTPrimeEvent.PrimeCause;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(TntBlock.class)
public class MixinTntBlock {

    // CraftBukkit start - TNTPrimeEvent
    @Redirect(method = {"onPlace", "neighborChanged"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;prime(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean silkard$primeRedstone(Level level, BlockPos pos) {
        return CraftEventFactory.callTNTPrimeEvent(level, pos, PrimeCause.REDSTONE, null, null) && TntBlock.prime(level, pos);
    }

    @Redirect(method = "playerWillDestroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;prime(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"))
    private boolean silkard$primeBlockBreak(Level level, BlockPos pos, Level levelArg, BlockPos posArg, BlockState state, Player player) {
        return CraftEventFactory.callTNTPrimeEvent(level, pos, PrimeCause.BLOCK_BREAK, player, null) && TntBlock.prime(level, pos);
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void silkard$primePlayer(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if ((itemStack.is(Items.FLINT_AND_STEEL) || itemStack.is(Items.FIRE_CHARGE)) && !CraftEventFactory.callTNTPrimeEvent(level, pos, PrimeCause.PLAYER, player, null)) {
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }

    @Redirect(method = "onProjectileHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/TntBlock;prime(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean silkard$primeProjectile(Level level, BlockPos pos, LivingEntity source, ItemStack itemStack, Level levelArg, BlockState state, BlockHitResult blockHit, net.minecraft.world.entity.projectile.Projectile projectile) {
        return CraftEventFactory.callEntityChangeBlockEvent(projectile, pos, Blocks.AIR.defaultBlockState())
            && CraftEventFactory.callTNTPrimeEvent(level, pos, PrimeCause.PROJECTILE, projectile, null)
            && TntBlock.prime(level, pos, source, itemStack);
    }
    // CraftBukkit end
}
