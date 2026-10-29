package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(TheEndGatewayBlockEntity.class)
public class MixinTheEndGatewayBlockEntity {

    // CraftBukkit - work in alternate worlds: match the dimension type instead of the dimension key
    @Redirect(method = "getPortalPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;dimension()Lnet/minecraft/resources/ResourceKey;"))
    private ResourceKey<Level> silkard$dimension(ServerLevel level) {
        return level.dimensionTypeRegistration().is(BuiltinDimensionTypes.END.identifier()) ? Level.END : level.dimension();
    }
}
