package com.mohistmc.silkard.mixin.vanilla.server.players;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfileRepository;
import java.util.Optional;
import net.minecraft.server.players.CachedUserNameToIdResolver;
import net.minecraft.server.players.NameAndId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(CachedUserNameToIdResolver.class)
public class MixinCachedUserNameToIdResolver {

    @Shadow
    private Optional<NameAndId> lookupGameProfile(GameProfileRepository repository, String name) {
        throw new AssertionError();
    }

    // CraftBukkit - use correct case for offline players
    @Redirect(method = "get(Ljava/lang/String;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/CachedUserNameToIdResolver;lookupGameProfile(Lcom/mojang/authlib/GameProfileRepository;Ljava/lang/String;)Ljava/util/Optional;"))
    private Optional<NameAndId> silkard$lookupGameProfile(CachedUserNameToIdResolver self, GameProfileRepository repository, String lookupName, @Local(argsOnly = true) String name) {
        return this.lookupGameProfile(repository, name);
    }
    // CraftBukkit end
}
