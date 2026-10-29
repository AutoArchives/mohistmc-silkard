package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile.hurtingprojectile;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.silkard.injected.world.entity.projectile.hurtingprojectile.ContextAbstractHurtingProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(SmallFireball.class)
public class MixinSmallFireball {

    // CraftBukkit - incendiary follows MOB_GRIEFING when launched by a mob
    @Inject(method = "<init>", at = @At("RETURN"))
    private void silkard$incendiary(CallbackInfo ci, @Local(argsOnly = true) Level level) {
        SmallFireball self = (SmallFireball) (Object) this;

        if (self.getOwner() instanceof Mob) {
            ((ContextAbstractHurtingProjectile) self).silkard$isIncendiary(level instanceof ServerLevel serverLevel && serverLevel.getGameRules().get(GameRules.MOB_GRIEFING));
        }
    }

    // CraftBukkit - EntityCombustByEntityEvent
    @Redirect(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
    private void silkard$ignite(Entity entity, float seconds) {
        SmallFireball self = (SmallFireball) (Object) this;
        EntityCombustByEntityEvent event = new EntityCombustByEntityEvent((org.bukkit.entity.Projectile) self.getBukkitEntity(), entity.getBukkitEntity(), seconds);

        entity.level().getCraftServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            entity.igniteForSeconds(event.getDuration(), false);
        }
    }

    // CraftBukkit - plugin-settable isIncendiary instead of the raw game rule
    @Redirect(method = "onHitBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"))
    private Object silkard$mobGriefing(GameRules instance, GameRule<?> key) {
        return ((ContextAbstractHurtingProjectile) (Object) this).silkard$isIncendiary();
    }

    // CraftBukkit - BlockIgniteEvent
    @Redirect(method = "onHitBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isEmptyBlock(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean silkard$canIgnite(Level level, BlockPos pos) {
        return level.isEmptyBlock(pos) && !CraftEventFactory.callBlockIgniteEvent(level, pos, (Entity) (Object) this).isCancelled();
    }

    // CraftBukkit - add Bukkit remove cause
    @Redirect(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/hurtingprojectile/SmallFireball;discard()V"))
    private void silkard$discard(SmallFireball instance) {
        instance.discard(EntityRemoveEvent.Cause.HIT);
    }
}
