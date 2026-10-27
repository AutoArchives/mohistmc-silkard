package com.mohistmc.silkard.mixin.vanilla.world.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(Ravager.class)
public class MixinRavager {

    // CraftBukkit - leaves eaten by the roar go through EntityChangeBlockEvent
    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$destroyLeaves(ServerLevel level, BlockPos pos, boolean drop, Entity entity) {
        if (!CraftEventFactory.callEntityChangeBlockEvent((Ravager) (Object) this, pos, Blocks.AIR.defaultBlockState())) {
            return false;
        }
        return level.destroyBlock(pos, drop, entity);
    }
}
