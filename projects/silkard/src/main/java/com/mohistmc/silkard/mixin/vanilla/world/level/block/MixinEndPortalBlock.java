package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.silkard.bukkit.EndPlatformHelper;
import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import com.mohistmc.silkard.injected.world.level.portal.ContextTeleportTransition;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.event.CraftPortalEvent;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(EndPortalBlock.class)
public class MixinEndPortalBlock {

    // CraftBukkit start - Entity in portal
    @Inject(method = "entityInside", at = @At("HEAD"))
    protected void silkard$portalEnter(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise, CallbackInfo ci) {
        if (entity.canUsePortal(false)) {
            EntityPortalEnterEvent event = new EntityPortalEnterEvent(entity.getBukkitEntity(), new org.bukkit.Location(level.getWorld(), pos.getX(), pos.getY(), pos.getZ()));
            level.getCraftServer().getPluginManager().callEvent(event);
        }
    }
    // CraftBukkit end

    // CraftBukkit start - SPIGOT-8111: send back to main overworld
    @Redirect(method = "getPortalDestination", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getRespawnData()Lnet/minecraft/world/level/storage/LevelData$RespawnData;"))
    private LevelData.RespawnData silkard$respawnData(ServerLevel level) {
        return level.getServer().findRespawnDimension().getRespawnData();
    }
    // CraftBukkit end

    // CraftBukkit start - SPIGOT-6152: send back to main overworld in custom ends
    @Redirect(method = "getPortalDestination", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;dimension()Lnet/minecraft/resources/ResourceKey;"))
    private ResourceKey<Level> silkard$currentDimension(ServerLevel level) {
        return level.getTypeKey() == LevelStem.END ? Level.END : level.dimension();
    }
    // CraftBukkit end

    // CraftBukkit start - pass the entity so a PortalCreateEvent(END_PLATFORM) can be fired
    @Redirect(method = "getPortalDestination", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/EndPlatformFeature;createEndPlatform(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Z)V"))
    private void silkard$createEndPlatform(ServerLevelAccessor accessor, BlockPos origin, boolean dropResources, ServerLevel level, Entity entity) {
        EndPlatformHelper.createEndPlatform(accessor, origin, dropResources, entity);
    }
    // CraftBukkit end

    // CraftBukkit start - always fire event in case plugins wish to change it
    @Inject(method = "getPortalDestination", at = @At("RETURN"), cancellable = true)
    private void silkard$missingPortalLevel(CallbackInfoReturnable<TeleportTransition> cir, @Local(index = 9) ServerLevel newLevel) {
        if (newLevel == null) {
            TeleportTransition transition = new TeleportTransition(null, Vec3.ZERO, Vec3.ZERO, 0.0F, 0.0F, Set.of(), TeleportTransition.DO_NOTHING);
            ((ContextTeleportTransition) (Object) transition).silkard$cause(PlayerTeleportEvent.TeleportCause.END_PORTAL);
            cir.setReturnValue(transition);
        }
    }
    // CraftBukkit end

    // CraftBukkit start - EntityPortalEvent
    @Redirect(method = "getPortalDestination", at = @At(value = "NEW", target = "Lnet/minecraft/world/level/portal/TeleportTransition;"))
    private static TeleportTransition silkard$portalEvent(ServerLevel newLevel, Vec3 pos, Vec3 delta, float yRot, float xRot, Set<Relative> relatives, TeleportTransition.PostTeleportTransition post, @Local(argsOnly = true) Entity entity) {
        CraftPortalEvent event = ((ContextEntity) (Object) entity).callPortalEvent(entity, CraftLocation.toBukkit(pos, newLevel.getWorld(), yRot, xRot), PlayerTeleportEvent.TeleportCause.END_PORTAL, 0, 0);
        if (event == null) {
            return null;
        }
        Location to = event.getTo();
        TeleportTransition transition = new TeleportTransition(((CraftWorld) to.getWorld()).getHandle(), CraftLocation.toVec3D(to), entity.getDeltaMovement(), to.getYaw(), to.getPitch(), relatives, post);
        ((ContextTeleportTransition) (Object) transition).silkard$cause(PlayerTeleportEvent.TeleportCause.END_PORTAL);
        return transition;
    }
    // CraftBukkit end
}
