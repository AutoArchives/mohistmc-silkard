package com.mohistmc.silkard.mixin.vanilla.world.entity.ai.goal;

import com.mohistmc.silkard.injected.world.entity.ai.goal.ContextBreedGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(BreedGoal.class)
public abstract class MixinBreedGoal implements ContextBreedGoal {

    @Shadow
    @Final
    protected Animal animal;

    @Shadow
    protected Animal partner;

    @Override
    public Animal silkard$animal() {
        return this.animal;
    }

    @Override
    public Animal silkard$partner() {
        return this.partner;
    }
}
