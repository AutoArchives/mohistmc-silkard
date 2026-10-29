package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.bukkit.EndPlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelData;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    @Redirect(method = "getPortalDestination", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;dimension()Lnet/minecraft/resources/ResourceKey;"))
    private ResourceKey<Level> silkard$currentDimension(Level level) {
        return level.getTypeKey() == LevelStem.END ? Level.END : level.dimension();
    }
    // CraftBukkit end

    // CraftBukkit start - pass the entity so a PortalCreateEvent(END_PLATFORM) can be fired
    @Redirect(method = "getPortalDestination", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/EndPlatformFeature;createEndPlatform(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Z)V"))
    private void silkard$createEndPlatform(ServerLevelAccessor accessor, BlockPos origin, boolean dropResources, ServerLevel level, Entity entity) {
        EndPlatformHelper.createEndPlatform(accessor, origin, dropResources, entity);
    }
    // CraftBukkit end
}
