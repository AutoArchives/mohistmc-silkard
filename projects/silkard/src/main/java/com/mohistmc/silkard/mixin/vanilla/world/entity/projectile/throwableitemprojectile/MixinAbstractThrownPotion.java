package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile.throwableitemprojectile;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(AbstractThrownPotion.class)
public class MixinAbstractThrownPotion {

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionContents;hasEffects()Z"))
    private boolean silkard$hasEffects(PotionContents instance) {
        return true;
    }

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/AbstractThrownPotion;discard()V"))
    private void silkard$destroy(AbstractThrownPotion potion) {
        ((ContextEntity) (Object) potion).discard(EntityRemoveEvent.Cause.HIT);
    }

    /**
     * @author Mgazul
     * @date 2026/10/30
     */
    @Overwrite
    private void douseFire(BlockPos pos) {
        AbstractThrownPotion self = (AbstractThrownPotion) (Object) this;
        Level level = self.level();
        BlockState blockstate = level.getBlockState(pos);
        if (blockstate.is(BlockTags.FIRE)) {
            if (CraftEventFactory.callEntityChangeBlockEvent((Entity) (Object) self, pos, Blocks.AIR.defaultBlockState())) {
                level.destroyBlock(pos, false, (Entity) (Object) self);
            }
        } else if (AbstractCandleBlock.isLit(blockstate)) {
            if (CraftEventFactory.callEntityChangeBlockEvent((Entity) (Object) self, pos, (BlockState) blockstate.setValue(AbstractCandleBlock.LIT, false))) {
                AbstractCandleBlock.extinguish((Player) null, blockstate, level, pos);
            }
        } else if (CampfireBlock.isLitCampfire(blockstate)) {
            if (CraftEventFactory.callEntityChangeBlockEvent((Entity) (Object) self, pos, (BlockState) blockstate.setValue(CampfireBlock.LIT, false))) {
                level.levelEvent((Entity) null, 1009, pos, 0);
                CampfireBlock.douse(self.getOwner(), (LevelAccessor) level, pos, blockstate);
                level.setBlockAndUpdate(pos, (BlockState) blockstate.setValue(CampfireBlock.LIT, false));
            }
        }
    }
}
