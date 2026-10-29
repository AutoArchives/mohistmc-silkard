package com.mohistmc.silkard.mixin.vanilla.world.entity.animal.parrot;

import com.mohistmc.silkard.injected.world.entity.ContextLivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/29
 */
@Mixin(Parrot.class)
public abstract class MixinParrot extends TamableAnimal {

    public MixinParrot(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
    }

    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int silkard$tameRoll(RandomSource random, int bound, Player player, InteractionHand hand) {
        int roll = random.nextInt(bound);
        if (roll == 0 && CraftEventFactory.callEntityTameEvent(this, player).isCancelled()) {
            return 1;
        }
        return roll;
    }

    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/parrot/Parrot;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean silkard$poison(Parrot self, MobEffectInstance effect) {
        return ((ContextLivingEntity) (Object) self).addEffect(effect, EntityPotionEffectEvent.Cause.FOOD);
    }

    @Overwrite
    public boolean isPushable() {
        return false;
    }

    @Overwrite
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        if (this.isInvulnerableTo(level, source)) {
            return false;
        }

        boolean result = super.hurtServer(level, source, damage);
        if (result) {
            this.setOrderedToSit(false);
        }
        return result;
    }
}
