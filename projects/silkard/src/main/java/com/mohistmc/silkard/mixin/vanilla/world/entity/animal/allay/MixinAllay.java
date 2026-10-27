package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.allay;

import com.mohistmc.silkard.injected.world.entity.animal.allay.ContextAllay;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/5/1 17:57
 */
@Mixin(Allay.class)
public abstract class MixinAllay extends PathfinderMob implements ContextAllay {

    @Shadow @Final
    private static EntityDataAccessor<Boolean> DATA_CAN_DUPLICATE;
    @Shadow public abstract void resetDuplicationCooldown();

    public boolean forceDancing = false; // CraftBukkit

    @Unique
    private Allay silkard$lastDuplicated;

    protected MixinAllay(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public Allay duplicateAllay0() {
        Allay allay = EntityTypes.ALLAY.create(this.level(), EntitySpawnReason.BREEDING);

        if (allay != null) {
            allay.snapTo(this.position());
            allay.setPersistenceRequired();
            allay.resetDuplicationCooldown();
            this.resetDuplicationCooldown();
            this.level().addFreshEntity(allay, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.DUPLICATION); // CraftBukkit - reason for duplicated allay
        }
        return allay; // CraftBukkit
    }

    public void setCanDuplicate(boolean canDuplicate) {
        this.entityData.set(DATA_CAN_DUPLICATE, canDuplicate);
    }

    public boolean silkard$forceDancing() {
        return forceDancing;
    }

    public void silkard$forceDancing(boolean forceDancing) {
        this.forceDancing = forceDancing;
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/allay/Allay;heal(F)V"))
    private void silkard$heal(Allay instance, float amount) {
        instance.heal(amount, EntityRegainHealthEvent.RegainReason.REGEN); // CraftBukkit
    }

    @Inject(method = "shouldStopDancing", at = @At("HEAD"), cancellable = true)
    private void silkard$keepDancing(CallbackInfoReturnable<Boolean> cir) {
        if (this.forceDancing) {
            cir.setReturnValue(false);
        }
    }

    @Overwrite
    public void duplicateAllay() {
        this.silkard$lastDuplicated = this.duplicateAllay0();
    }

    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/allay/Allay;duplicateAllay()V", shift = At.Shift.AFTER), cancellable = true)
    private void silkard$cancelDuplicate(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (this.silkard$lastDuplicated == null) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
