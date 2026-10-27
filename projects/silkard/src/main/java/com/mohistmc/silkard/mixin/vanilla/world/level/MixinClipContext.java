package com.mohistmc.silkard.mixin.vanilla.world.level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/27
 */
@Mixin(ClipContext.class)
public class MixinClipContext {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/shapes/CollisionContext;of(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/phys/shapes/CollisionContext;"))
    private static CollisionContext silkard$collisionContext(Entity entity) {
        return entity == null ? CollisionContext.empty() : CollisionContext.of(entity); // CraftBukkit
    }
}
