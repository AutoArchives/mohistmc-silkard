package com.mohistmc.silkard.mixin.vanilla.world.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.scores.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntitySelector.class)
public class MixinEntitySelector {

    @Redirect(method = "lambda$pushableBy$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPushable()Z"))
    private static boolean silkard_canCollideWith(Entity entity1, Entity entity, Team team, Team.CollisionRule collisionRule, Entity entity2) {
        return entity1.canCollideWithBukkit(entity) && entity.canCollideWithBukkit(entity1);
    }
}
