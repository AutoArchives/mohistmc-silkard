package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import com.mohistmc.silkard.injected.world.entity.projectile.ContextProjectile;
import com.mohistmc.silkard.injected.world.entity.projectile.ContextShulkerBullet;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(ShulkerBullet.class)
public abstract class MixinShulkerBullet extends Projectile implements ContextShulkerBullet {

    @Shadow
    private EntityReference<Entity> finalTarget;
    @Shadow
    private Direction currentMoveDirection;
    @Shadow
    private void selectNextMoveDirection(Direction.Axis axis, Entity target) {
    }

    public MixinShulkerBullet(net.minecraft.world.entity.EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    // CraftBukkit start
    @Override
    public Entity getTarget() {
        return EntityReference.get(this.finalTarget, this.level(), Entity.class);
    }

    @Override
    public void setTarget(Entity e) {
        this.finalTarget = (e != null) ? EntityReference.of(e) : null;
        this.currentMoveDirection = Direction.UP;
        this.selectNextMoveDirection(Direction.Axis.X, e);
    }
    // CraftBukkit end

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Direction$Axis;)V", at = @At("RETURN"))
    private void silkard$init(Level level, LivingEntity owner, Entity target, Direction.Axis axis, CallbackInfo ci) {
        if (owner != null) {
            ((ContextEntity) (Object) this).silkard_projectileSource((ProjectileSource) owner.getBukkitEntity());
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ShulkerBullet;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    private ProjectileDeflection silkard$hitTargetOrDeflectSelf(ShulkerBullet self, HitResult hitResult) {
        return ((ContextProjectile) (Object) self).preHitTargetOrDeflectSelf(hitResult);
    }

    @Redirect(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ShulkerBullet;discard()V"))
    private void silkard$discardDespawn(ShulkerBullet self) {
        ((ContextEntity) (Object) self).discard(EntityRemoveEvent.Cause.DESPAWN);
    }

    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ShulkerBullet;destroy()V"))
    private void silkard$destroyOnHit(ShulkerBullet self) {
        silkard$destroy(self, EntityRemoveEvent.Cause.HIT);
    }

    @Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ShulkerBullet;destroy()V"))
    private void silkard$destroyHurt(ShulkerBullet self) {
        silkard$destroy(self, EntityRemoveEvent.Cause.DEATH);
    }

    private void silkard$destroy(ShulkerBullet self, EntityRemoveEvent.Cause cause) {
        ((ContextEntity) (Object) self).discard(cause);
        self.level().gameEvent(GameEvent.ENTITY_DAMAGE, self.position(), GameEvent.Context.of((Entity) self));
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void silkard$hurtServer(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if (CraftEventFactory.handleNonLivingEntityDamageEvent((Entity) (Object) this, source, damage, false)) {
            cir.setReturnValue(false);
        }
    }
}
