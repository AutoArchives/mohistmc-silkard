package com.mohistmc.silkard.mixin.vanilla.server;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerAdvancementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/10/28
 */
@Mixin(ServerAdvancementManager.class)
public class MixinServerAdvancementManager {

    @Shadow @Mutable
    public Map<Identifier, AdvancementHolder> advancements;

    // CraftBukkit - SPIGOT-7734: mutable
    @Inject(method = "<init>", at = @At("RETURN"))
    private void silkard$mutableAdvancements(CallbackInfo ci) {
        this.advancements = new HashMap<>(this.advancements);
    }
}
