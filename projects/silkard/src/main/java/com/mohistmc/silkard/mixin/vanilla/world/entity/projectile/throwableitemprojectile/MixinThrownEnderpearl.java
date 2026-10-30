package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile.throwableitemprojectile;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.silkard.injected.world.damagesource.ContextDamageSource;
import com.mohistmc.silkard.injected.world.level.ContextLevel;
import com.mohistmc.silkard.injected.world.level.ContextLevelWriter;
import com.mohistmc.silkard.injected.world.level.portal.ContextTeleportTransition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(ThrownEnderpearl.class)
public abstract class MixinThrownEnderpearl extends ThrowableItemProjectile {

    @Unique
    private ServerPlayer silkard$teleportResult;

    protected MixinThrownEnderpearl(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    // CraftBukkit start - teleport the owner before the endermite spawn, with a Bukkit teleport cause
    @Inject(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextFloat()F", shift = At.Shift.BEFORE), cancellable = true)
    private void silkard$teleportOwner(CallbackInfo ci, @Local(name = "level") ServerLevel level, @Local(name = "teleportPos") Vec3 teleportPos,
                                       @Local(name = "player") ServerPlayer player) {
        TeleportTransition transition = new TeleportTransition(level, teleportPos, Vec3.ZERO, 0.0F, 0.0F, Relative.ROTATION, TeleportTransition.DO_NOTHING);
        ((ContextTeleportTransition) (Object) transition).silkard$cause(PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
        this.silkard$teleportResult = player.teleport(transition);
        if (this.silkard$teleportResult == null) {
            this.discard(EntityRemoveEvent.Cause.HIT);
            ci.cancel();
        }
    }

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;"))
    private ServerPlayer silkard$teleportDone(ServerPlayer player, TeleportTransition transition) {
        return this.silkard$teleportResult;
    }
    // CraftBukkit end

    // CraftBukkit start - spawn the endermite with a Bukkit spawn reason
    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$addEndermite(ServerLevel level, Entity entity) {
        return ((ContextLevelWriter) level).addFreshEntity(entity, CreatureSpawnEvent.SpawnReason.ENDER_PEARL);
    }
    // CraftBukkit end

    // CraftBukkit start - the pearl is the damager of the ender pearl damage
    @ModifyExpressionValue(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSources;enderPearl()Lnet/minecraft/world/damagesource/DamageSource;"))
    private DamageSource silkard$customDamager(DamageSource source) {
        return ((ContextDamageSource) source).customEntityDamager((Entity) (Object) this);
    }
    // CraftBukkit end

    // CraftBukkit start - add Bukkit remove cause
    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/ThrownEnderpearl;discard()V", ordinal = 1))
    private void silkard$discardHit(ThrownEnderpearl self) {
        self.discard(EntityRemoveEvent.Cause.HIT);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/throwableitemprojectile/ThrownEnderpearl;discard()V"))
    private void silkard$discardDespawn(ThrownEnderpearl self) {
        self.discard(EntityRemoveEvent.Cause.DESPAWN);
    }
    // CraftBukkit end

    // CraftBukkit start - compare the level type instead of the dimension key
    @Overwrite
    public boolean canTeleport(Level from, Level to) {
        if (((ContextLevel) from).getTypeKey() == LevelStem.END && ((ContextLevel) to).getTypeKey() == LevelStem.OVERWORLD) {
            Entity entity = this.getOwner();
            if (entity instanceof ServerPlayer serverplayer) {
                return super.canTeleport(from, to) && serverplayer.seenCredits;
            }
        }
        return super.canTeleport(from, to);
    }
    // CraftBukkit end
}
