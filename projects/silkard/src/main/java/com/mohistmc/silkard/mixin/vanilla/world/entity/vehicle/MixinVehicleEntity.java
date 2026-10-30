package com.mohistmc.silkard.mixin.vanilla.world.entity.vehicle;

import com.mohistmc.silkard.injected.world.entity.ContextEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(VehicleEntity.class)
public abstract class MixinVehicleEntity {

    @Shadow
    protected abstract void destroy(ServerLevel level, DamageSource source);

    @Unique
    private float silkard$damage;

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void silkard$vehicleDamage(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        VehicleDamageEvent event = new VehicleDamageEvent(silkard$vehicle(), silkard$attacker(source), damage);
        level.getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
            return;
        }
        this.silkard$damage = (float) event.getDamage();
    }

    @Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/VehicleEntity;setDamage(F)V"))
    private void silkard$applyDamage(VehicleEntity instance, float value, ServerLevel level, DamageSource source, float damage) {
        instance.setDamage(value - damage * 10.0F + this.silkard$damage * 10.0F);
    }

    @Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/VehicleEntity;discard()V"))
    private void silkard$destroyDiscard(VehicleEntity instance, ServerLevel level, DamageSource source, float damage) {
        VehicleDestroyEvent event = new VehicleDestroyEvent(silkard$vehicle(instance), silkard$attacker(source));
        instance.level().getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            instance.setDamage(40.0F);
            return;
        }
        instance.discard(EntityRemoveEvent.Cause.DEATH);
    }

    @Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/VehicleEntity;destroy(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V"))
    private void silkard$destroyBySource(VehicleEntity instance, ServerLevel level, DamageSource source) {
        VehicleDestroyEvent event = new VehicleDestroyEvent(silkard$vehicle(instance), silkard$attacker(source));
        instance.level().getCraftServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            instance.setDamage(40.0F);
            return;
        }
        this.destroy(level, source);
    }

    @Unique
    private Vehicle silkard$vehicle() {
        return (Vehicle) ((ContextEntity) (Object) this).getBukkitEntity();
    }

    @Unique
    private static Vehicle silkard$vehicle(VehicleEntity entity) {
        return (Vehicle) ((ContextEntity) (Object) entity).getBukkitEntity();
    }

    @Unique
    private static org.bukkit.entity.Entity silkard$attacker(DamageSource source) {
        return source.getEntity() == null ? null : ((ContextEntity) (Object) source.getEntity()).getBukkitEntity();
    }
}
