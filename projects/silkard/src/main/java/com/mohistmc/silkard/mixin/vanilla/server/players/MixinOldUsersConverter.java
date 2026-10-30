package com.mohistmc.silkard.mixin.vanilla.server.players;

import net.minecraft.server.players.OldUsersConverter;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @author Mgazul
 * @date 2026/10/30
 */
@Mixin(OldUsersConverter.class)
public class MixinOldUsersConverter {

    // CraftBukkit start - don't print stacktrace
    @Redirect(method = {"convertUserBanlist", "convertIpBanlist", "convertOpsList", "convertWhiteList"}, at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    private static void silkard$warnNoTrace(Logger logger, String format, Object arg1, Object arg2) {
        logger.warn(format, arg1);
    }
    // CraftBukkit end
}
