package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(TemptGoal.class)
public class MixinTemptGoal {

    @Shadow
    @Final
    protected Mob mob;

    @Shadow
    protected Player player;

    @Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
    private void silkard$temptTarget(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            return;
        }

        EntityTargetLivingEntityEvent event = CraftEventFactory.callEntityTargetLivingEvent(this.mob, this.player, EntityTargetEvent.TargetReason.TEMPT);
        if (event.isCancelled() || event.getTarget() == null) {
            this.player = null;
            cir.setReturnValue(false);
            return;
        }

        LivingEntity target = ((CraftLivingEntity) event.getTarget()).getHandle();
        if (target instanceof Player targetPlayer) {
            this.player = targetPlayer;
        } else {
            this.player = null;
            cir.setReturnValue(false);
        }
    }
}
