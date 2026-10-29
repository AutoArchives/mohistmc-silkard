package com.mohistmc.silkard.mixin.vanilla.world.level.portal;

import com.mohistmc.silkard.injected.world.level.portal.ContextPortalForcer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalForcer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(PortalForcer.class)
public class MixinPortalForcer implements ContextPortalForcer {

    // CraftBukkit start
    @Unique
    public int portalCreateRadius;

    @Unique
    public int searchRadius;

    @Unique
    public Entity portalCreateEntity;
    // CraftBukkit end

    @Override
    public void pushPortalCreate(Entity entity, int createRadius) {
        this.portalCreateEntity = entity;
        this.portalCreateRadius = createRadius;
    }

    @Override
    public void pushSearchRadius(int searchRadius) {
        this.searchRadius = searchRadius;
    }

    // CraftBukkit start - configurable portal creation radius
    @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;spiralAround(Lnet/minecraft/core/BlockPos;ILnet/minecraft/core/Direction;Lnet/minecraft/core/Direction;)Ljava/lang/Iterable;"))
    private Iterable<BlockPos> silkard$spiralAround(BlockPos origin, int radius, Direction direction, Direction direction2) {
        return BlockPos.spiralAround(origin, this.portalCreateRadius > 0 ? this.portalCreateRadius : radius, direction, direction2);
    }
    // CraftBukkit end
}
