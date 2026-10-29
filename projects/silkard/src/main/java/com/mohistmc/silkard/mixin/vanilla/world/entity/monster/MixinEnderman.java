package com.mohistmc.silkard.mixin.vanilla.world.entity.monster;

import com.mohistmc.silkard.injected.world.entity.ContextMob;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enderman;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(Enderman.class)
public abstract class MixinEnderman extends Monster {

    @Shadow @Final
    private static Identifier SPEED_MODIFIER_ATTACKING_ID;

    @Shadow @Final
    private static AttributeModifier SPEED_MODIFIER_ATTACKING;

    @Shadow @Final
    private static EntityDataAccessor<Boolean> DATA_CREEPY;

    @Shadow @Final
    private static EntityDataAccessor<Boolean> DATA_STARED_AT;

    @Shadow
    private int targetChangeTime;

    protected MixinEnderman(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - fire EntityTargetEvent
     */
    @Overwrite
    public void setTarget(@Nullable LivingEntity target) {
        this.setTarget(target, EntityTargetEvent.TargetReason.UNKNOWN, true);
    }

    // CraftBukkit start
    @Override
    public boolean setTarget(@Nullable LivingEntity target, EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        if (!((ContextMob) (Object) this).silkard$superSetTarget(target, reason, fireEvent)) {
            return false;
        }
        LivingEntity current = this.getTarget();
        AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeinstance != null) {
            if (current == null) {
                this.targetChangeTime = 0;
                this.entityData.set(DATA_CREEPY, false);
                this.entityData.set(DATA_STARED_AT, false);
                attributeinstance.removeModifier(SPEED_MODIFIER_ATTACKING_ID);
            } else {
                this.targetChangeTime = this.tickCount;
                this.entityData.set(DATA_CREEPY, true);
                if (!attributeinstance.hasModifier(SPEED_MODIFIER_ATTACKING_ID)) {
                    attributeinstance.addTransientModifier(SPEED_MODIFIER_ATTACKING);
                }
            }
        }
        return true;
    }
    // CraftBukkit end
}
