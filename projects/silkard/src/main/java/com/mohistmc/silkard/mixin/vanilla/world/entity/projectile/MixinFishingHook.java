package com.mohistmc.silkard.mixin.vanilla.world.entity.projectile;

import com.llamalad7.mixinextras.sugar.Local;
import com.mohistmc.silkard.injected.world.entity.projectile.ContextFishingHook;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerFishEvent;
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
 * @date 2026/5/1 17:24
 */
@Mixin(FishingHook.class)
public class MixinFishingHook implements ContextFishingHook {

    // CraftBukkit start - Extra variables to enable modification of fishing wait time, values are minecraft defaults
    public int minWaitTime = 100;
    public int maxWaitTime = 600;
    public int minLureTime = 20;
    public int maxLureTime = 80;
    public float minLureAngle = 0.0F;
    public float maxLureAngle = 360.0F;
    public boolean applyLure = true;
    public boolean rainInfluenced = true;
    public boolean skyInfluenced = true;
    // CraftBukkit end

    @Shadow @Final private int lureSpeed;

    @Unique private boolean silkard$abortRetrieve;
    @Unique private int silkard$expToDrop;

    public int silkard$minLureTime() {
        return minLureTime;
    }

    public void silkard$minLureTime(int minLureTime) {
        this.minLureTime = minLureTime;
    }

    public int silkard$maxLureTime() {
        return maxLureTime;
    }

    public void silkard$maxLureTime(int maxLureTime) {
        this.maxLureTime = maxLureTime;
    }

    public float silkard$minLureAngle() {
        return minLureAngle;
    }

    public void silkard$minLureAngle(float minLureAngle) {
        this.minLureAngle = minLureAngle;
    }

    public float silkard$maxLureAngle() {
        return maxLureAngle;
    }

    public void silkard$maxLureAngle(float maxLureAngle) {
        this.maxLureAngle = maxLureAngle;
    }

    public boolean silkard$rainInfluenced() {
        return rainInfluenced;
    }

    public void silkard$rainInfluenced(boolean rainInfluenced) {
        this.rainInfluenced = rainInfluenced;
    }

    public boolean silkard$skyInfluenced() {
        return skyInfluenced;
    }

    public void silkard$skyInfluenced(boolean skyInfluenced) {
        this.skyInfluenced = skyInfluenced;
    }

    public int silkard$minWaitTime() {
        return minWaitTime;
    }

    public void silkard$minWaitTime(int minWaitTime) {
        this.minWaitTime = minWaitTime;
    }

    public int silkard$maxWaitTime() {
        return maxWaitTime;
    }

    public void silkard$maxWaitTime(int maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public boolean silkard$applyLure() {
        return applyLure;
    }

    public void silkard$applyLure(boolean applyLure) {
        this.applyLure = applyLure;
    }

    // CraftBukkit start - add Bukkit remove cause
    @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/entity/projectile/FishingHook;discard()V"))
    private void silkard$discardNoOwner(FishingHook self) {
        self.discard(EntityRemoveEvent.Cause.DESPAWN);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/entity/projectile/FishingHook;discard()V"))
    private void silkard$discardTimeout(FishingHook self) {
        self.discard(EntityRemoveEvent.Cause.DESPAWN);
    }
    // CraftBukkit end

    // CraftBukkit - projectile hit event
    @Redirect(method = "checkCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;hitTargetOrDeflectSelf(Lnet/minecraft/world/phys/HitResult;)Lnet/minecraft/world/entity/projectile/ProjectileDeflection;"))
    private ProjectileDeflection silkard$preHitTargetOrDeflectSelf(FishingHook self, HitResult hitResult) {
        return self.preHitTargetOrDeflectSelf(hitResult);
    }

    // CraftBukkit start - logic to modify fishing wait time, lure time and lure angle
    @Redirect(method = "catchingFish", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I"))
    private int silkard$lureTime(RandomSource random, int min, int max) {
        return Mth.nextInt(random, this.minLureTime, this.maxLureTime);
    }

    @Redirect(method = "catchingFish", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I"))
    private int silkard$waitTime(RandomSource random, int min, int max) {
        // the vanilla code subtracts lureSpeed right after this assignment, so add it back when the lure is disabled
        return Mth.nextInt(random, this.minWaitTime, this.maxWaitTime) + (this.applyLure ? 0 : this.lureSpeed);
    }

    @Redirect(method = "catchingFish", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/util/Mth;nextFloat(Lnet/minecraft/util/RandomSource;FF)F"))
    private float silkard$lureAngle(RandomSource random, float min, float max) {
        return Mth.nextFloat(random, this.minLureAngle, this.maxLureAngle);
    }

    // CraftBukkit - the rain/sky influence checks must not consume a random value when disabled
    @Redirect(method = "catchingFish", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/util/RandomSource;nextFloat()F"))
    private float silkard$rainRoll(RandomSource random) {
        return this.rainInfluenced ? random.nextFloat() : 1.0F;
    }

    @Redirect(method = "catchingFish", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/util/RandomSource;nextFloat()F"))
    private float silkard$skyRoll(RandomSource random) {
        return this.skyInfluenced ? random.nextFloat() : 1.0F;
    }
    // CraftBukkit end

    // CraftBukkit - PlayerFishEvent FAILED_ATTEMPT
    @Inject(method = "catchingFish", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/network/syncher/SynchedEntityData;set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;)V", shift = At.Shift.AFTER))
    private void silkard$failedAttempt(CallbackInfo ci) {
        FishingHook self = (FishingHook) (Object) this;
        Player owner = self.getPlayerOwner();
        if (owner == null) {
            return;
        }
        PlayerFishEvent event = new PlayerFishEvent((org.bukkit.entity.Player) owner.getBukkitEntity(), null, (org.bukkit.entity.FishHook) self.getBukkitEntity(), PlayerFishEvent.State.FAILED_ATTEMPT);
        Bukkit.getPluginManager().callEvent(event);
    }

    // CraftBukkit - PlayerFishEvent BITE
    @Inject(method = "catchingFish", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"), cancellable = true)
    private void silkard$bite(CallbackInfo ci) {
        FishingHook self = (FishingHook) (Object) this;
        Player owner = self.getPlayerOwner();
        if (owner == null) {
            return;
        }
        PlayerFishEvent event = new PlayerFishEvent((org.bukkit.entity.Player) owner.getBukkitEntity(), null, (org.bukkit.entity.FishHook) self.getBukkitEntity(), PlayerFishEvent.State.BITE);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    // CraftBukkit - PlayerFishEvent CAUGHT_ENTITY
    @Inject(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;pullEntity(Lnet/minecraft/world/entity/Entity;)V"), cancellable = true)
    private void silkard$caughtEntity(CallbackInfoReturnable<Integer> cir) {
        FishingHook self = (FishingHook) (Object) this;
        Player owner = self.getPlayerOwner();
        if (owner == null || self.hookedIn == null) {
            return;
        }
        PlayerFishEvent event = new PlayerFishEvent((org.bukkit.entity.Player) owner.getBukkitEntity(), self.hookedIn.getBukkitEntity(), (org.bukkit.entity.FishHook) self.getBukkitEntity(), PlayerFishEvent.State.CAUGHT_ENTITY);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(0);
        }
    }

    // CraftBukkit - PlayerFishEvent CAUGHT_FISH
    @Inject(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(DDD)V"), cancellable = true)
    private void silkard$caughtFish(CallbackInfoReturnable<Integer> cir, @Local ItemEntity itemEntity) {
        FishingHook self = (FishingHook) (Object) this;
        Player owner = self.getPlayerOwner();
        if (owner == null) {
            return;
        }
        PlayerFishEvent event = new PlayerFishEvent((org.bukkit.entity.Player) owner.getBukkitEntity(), itemEntity.getBukkitEntity(), (org.bukkit.entity.FishHook) self.getBukkitEntity(), PlayerFishEvent.State.CAUGHT_FISH);
        event.setExpToDrop(self.random.nextInt(6) + 1);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(0);
            return;
        }
        this.silkard$expToDrop = event.getExpToDrop();
    }

    // CraftBukkit - the caught fish drops the experience the plugin asked for
    @Redirect(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int silkard$expRoll(RandomSource random, int bound) {
        return Math.max(0, this.silkard$expToDrop) - 1;
    }

    @Redirect(method = "retrieve", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean silkard$addFreshEntity(Level level, Entity entity) {
        if (this.silkard$expToDrop <= 0) {
            return false;
        }
        return level.addFreshEntity(entity);
    }

    // CraftBukkit - PlayerFishEvent IN_GROUND
    @Redirect(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;onGround()Z"))
    private boolean silkard$onGround(FishingHook self) {
        boolean grounded = self.onGround();
        if (!grounded) {
            return false;
        }
        Player owner = self.getPlayerOwner();
        if (owner == null) {
            return true;
        }
        PlayerFishEvent event = new PlayerFishEvent((org.bukkit.entity.Player) owner.getBukkitEntity(), null, (org.bukkit.entity.FishHook) self.getBukkitEntity(), PlayerFishEvent.State.IN_GROUND);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            this.silkard$abortRetrieve = true;
            return false;
        }
        return true;
    }

    // CraftBukkit - PlayerFishEvent REEL_IN + add Bukkit remove cause
    @Redirect(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;discard()V"))
    private void silkard$discard(FishingHook self, @Local int dmg) {
        if (this.silkard$abortRetrieve) {
            return;
        }
        if (dmg == 0) {
            Player owner = self.getPlayerOwner();
            if (owner != null) {
                PlayerFishEvent event = new PlayerFishEvent((org.bukkit.entity.Player) owner.getBukkitEntity(), null, (org.bukkit.entity.FishHook) self.getBukkitEntity(), PlayerFishEvent.State.REEL_IN);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    this.silkard$abortRetrieve = true;
                    return;
                }
            }
        }
        self.discard(EntityRemoveEvent.Cause.DESPAWN);
    }

    // CraftBukkit - a cancelled event aborts the whole retrieve
    @Inject(method = "retrieve", at = @At(value = "RETURN", ordinal = 1))
    private void silkard$abortRetrieve(CallbackInfoReturnable<Integer> cir) {
        if (this.silkard$abortRetrieve) {
            this.silkard$abortRetrieve = false;
            cir.setReturnValue(0);
        }
    }
}
