package com.mohistmc.silkard.mixin.vanilla.world.entity.monster;

import com.mohistmc.silkard.injected.world.entity.monster.ContextCreeper;
import com.mohistmc.silkard.injected.world.level.ContextLevelWriter;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/5/1 17:12
 */
@Mixin(Creeper.class)
public abstract class MixinCreeper extends Monster implements ContextCreeper {

    @Shadow @Final
    private static EntityDataAccessor<Boolean> DATA_IS_POWERED;

    @Shadow
    private void spawnLingeringCloud() {
        throw new AssertionError();
    }

    @Unique
    public Entity entityIgniter; // CraftBukkit

    protected MixinCreeper(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public void setPowered(boolean power) {
        this.entityData.set(DATA_IS_POWERED, power);
    }

    public void silkard$entityIgniter(Entity entityIgniter) {
        this.entityIgniter = entityIgniter;
    }

    public Entity silkard$entityIgniter() {
        return entityIgniter;
    }

    // CraftBukkit start - CreeperPowerEvent
    @Inject(method = "thunderHit", at = @At("HEAD"), cancellable = true)
    private void silkard$creeperPowerEvent(ServerLevel level, LightningBolt lightningBolt, CallbackInfo ci) {
        if (CraftEventFactory.callCreeperPowerEvent((Creeper) (Object) this, lightningBolt, CreeperPowerEvent.PowerCause.LIGHTNING).isCancelled()) {
            ci.cancel();
        }
    }
    // CraftBukkit end

    // CraftBukkit start - remember the igniter and keep unbreakable items intact
    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Creeper;ignite()V"))
    private void silkard$entityIgniter(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        this.entityIgniter = player;
    }

    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isDamageableItem()Z"))
    private boolean silkard$damageableIgniter(ItemStack stack) {
        return stack.getMaxDamage() != 0;
    }
    // CraftBukkit end

    // CraftBukkit start - ExplosionPrimeEvent and Bukkit remove cause
    @Inject(method = "explodeCreeper", at = @At("HEAD"), cancellable = true)
    private void silkard$explode(CallbackInfo ci) {
        Level level = this.level();
        if (level instanceof ServerLevel serverlevel) {
            float f = ((Creeper) (Object) this).isPowered() ? 2.0F : 1.0F;
            ExplosionPrimeEvent event = CraftEventFactory.callExplosionPrimeEvent((Creeper) (Object) this, ((Creeper) (Object) this).explosionRadius * f, false);
            if (!event.isCancelled()) {
                this.dead = true;
                serverlevel.explode((Entity) (Object) this, Explosion.getDefaultDamageSource(this.level(), this).customCausingEntityDamager(this.entityIgniter), null,
                        this.getX(), this.getY(), this.getZ(), event.getRadius(), event.getFire(), Level.ExplosionInteraction.MOB);
                this.spawnLingeringCloud();
                this.triggerOnDeathMobEffects(serverlevel, Entity.RemovalReason.KILLED);
                this.discard(EntityRemoveEvent.Cause.EXPLODE);
            } else {
                ((Creeper) (Object) this).swell = 0;
            }
        }
        ci.cancel();
    }

    @Redirect(method = "spawnLingeringCloud", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$addLingeringCloud(Level level, Entity entity) {
        if (entity instanceof AreaEffectCloud cloud) {
            cloud.setOwner((Creeper) (Object) this);
        }
        return ((ContextLevelWriter) level).addFreshEntity(entity, CreatureSpawnEvent.SpawnReason.EXPLOSION);
    }
    // CraftBukkit end
}
