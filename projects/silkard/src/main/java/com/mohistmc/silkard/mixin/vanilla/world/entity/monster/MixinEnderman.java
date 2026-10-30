package com.mohistmc.silkard.mixin.vanilla.world.entity.monster;

import com.mohistmc.silkard.injected.world.entity.ContextMob;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enderman;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(Enderman.class)
public abstract class MixinEnderman implements ContextMob {

    // @formatter:off
    @Shadow @Final private static Identifier SPEED_MODIFIER_ATTACKING_ID;
    @Shadow @Final private static AttributeModifier SPEED_MODIFIER_ATTACKING;
    @Shadow @Final private static EntityDataAccessor<Boolean> DATA_CREEPY;
    @Shadow @Final private static EntityDataAccessor<Boolean> DATA_STARED_AT;
    @Shadow private int targetChangeTime;
    // @formatter:on

    // CraftBukkit start - fire event
    @Override
    public boolean setTarget(@Nullable LivingEntity target, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        if (!((ContextMob) (Object) this).silkard$superSetTarget(target, reason, fireEvent)) {
            return false;
        }
        Enderman self = (Enderman) (Object) this;
        LivingEntity newTarget = self.getTarget();
        AttributeInstance attribute = self.getAttribute(Attributes.MOVEMENT_SPEED);

        if (newTarget == null) {
            this.targetChangeTime = 0;
            self.getEntityData().set(DATA_CREEPY, false);
            self.getEntityData().set(DATA_STARED_AT, false);
            attribute.removeModifier(SPEED_MODIFIER_ATTACKING_ID);
        } else {
            this.targetChangeTime = self.tickCount;
            self.getEntityData().set(DATA_CREEPY, true);
            if (!attribute.hasModifier(SPEED_MODIFIER_ATTACKING_ID)) {
                attribute.addTransientModifier(SPEED_MODIFIER_ATTACKING);
            }
        }
        return true;
    }
    // CraftBukkit end
}
