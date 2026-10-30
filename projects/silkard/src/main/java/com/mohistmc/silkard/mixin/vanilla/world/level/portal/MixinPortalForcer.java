package com.mohistmc.silkard.mixin.vanilla.world.level.portal;

import com.mohistmc.silkard.injected.world.level.portal.ContextPortalForcer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.util.BlockUtil;
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.event.world.PortalCreateEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(PortalForcer.class)
public class MixinPortalForcer implements ContextPortalForcer {

    @Shadow
    private ServerLevel level;

    // CraftBukkit start
    @Unique
    public int portalCreateRadius;

    @Unique
    public int searchRadius;

    @Unique
    public Entity portalCreateEntity;

    @Unique
    private BlockStateListPopulator silkard$blockList;
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

    @ModifyVariable(method = "findClosestPortalPosition", at = @At("STORE"), ordinal = 0)
    private int silkard$searchRadius(int original) {
        return this.searchRadius > 0 ? this.searchRadius : original;
    }

    // CraftBukkit start - configurable portal creation radius
    @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;spiralAround(Lnet/minecraft/core/BlockPos;ILnet/minecraft/core/Direction;Lnet/minecraft/core/Direction;)Ljava/lang/Iterable;"))
    private Iterable<BlockPos> silkard$spiralAround(BlockPos origin, int radius, Direction direction, Direction direction2) {
        return (Iterable<BlockPos>) (Iterable) BlockPos.spiralAround(origin, this.portalCreateRadius > 0 ? this.portalCreateRadius : radius, direction, direction2);
    }

    @Inject(method = "createPortal", at = @At("HEAD"))
    private void silkard$initBlockList(BlockPos origin, Direction.Axis portalAxis, CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> cir) {
        this.silkard$blockList = new BlockStateListPopulator(this.level);
    }

    @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private boolean silkard$setBlockAndUpdate(ServerLevel level, BlockPos pos, BlockState state) {
        return this.silkard$blockList.setBlock(pos, state, 3);
    }

    @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private boolean silkard$setBlock(ServerLevel level, BlockPos pos, BlockState state, int flags) {
        return this.silkard$blockList.setBlock(pos, state, flags);
    }

    @Inject(method = "createPortal", at = @At("RETURN"), cancellable = true)
    private void silkard$portalCreateEvent(BlockPos origin, Direction.Axis portalAxis, CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> cir) {
        PortalCreateEvent event = new PortalCreateEvent((java.util.List<org.bukkit.block.BlockState>) (java.util.List) this.silkard$blockList.getList(), this.level.getWorld(), this.portalCreateEntity == null ? null : this.portalCreateEntity.getBukkitEntity(), PortalCreateEvent.CreateReason.NETHER_PAIR);
        this.level.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(Optional.empty());
            return;
        }
        this.silkard$blockList.updateList();
    }
    // CraftBukkit end
}
