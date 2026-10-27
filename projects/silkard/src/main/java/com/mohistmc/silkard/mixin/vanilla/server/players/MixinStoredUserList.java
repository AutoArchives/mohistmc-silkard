package com.mohistmc.silkard.mixin.vanilla.server.players;

import com.mohistmc.silkard.injected.server.players.ContextStoredUserList;
import java.util.Map;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(StoredUserList.class)
public class MixinStoredUserList implements ContextStoredUserList {

    @Shadow
    public void removeExpired() {
        throw new AssertionError();
    }

    @Inject(method = "contains", at = @At("HEAD"))
    private void silkard$removeExpiredFirst(CallbackInfoReturnable<Boolean> cir) {
        // CraftBukkit - SPIGOT-7589: mirror what get(...) does
        this.removeExpired();
    }

    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object silkard$skipExpiredOnLoad(Map<Object, Object> map, Object key, Object value) {
        // CraftBukkit - do not load entries that already expired
        if (((StoredUserEntry<?>) value).hasExpired()) {
            return null;
        }
        return map.put(key, value);
    }
}
