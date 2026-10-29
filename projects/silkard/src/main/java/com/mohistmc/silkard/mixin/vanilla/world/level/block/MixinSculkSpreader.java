package com.mohistmc.silkard.mixin.vanilla.world.level.block;

import com.mohistmc.silkard.injected.world.level.ContextLevel;
import com.mohistmc.silkard.injected.world.level.block.ContextChargeCursor;
import com.mohistmc.silkard.injected.world.level.block.ContextSculkSpreader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSpreader;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.SculkBloomEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(SculkSpreader.class)
public abstract class MixinSculkSpreader implements ContextSculkSpreader {

    @Shadow
    public abstract boolean isWorldGeneration();

    @Unique
    private Level silkard$level;

    @Override
    public Level silkard$level() {
        return silkard$level;
    }

    @Override
    public void silkard$setLevel(Level level) {
        this.silkard$level = level;
    }

    @Inject(method = "addCursor", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), cancellable = true)
    private void silkard$bloom(SculkSpreader.ChargeCursor cursor, CallbackInfo ci) {
        if (!this.isWorldGeneration() && this.silkard$level != null) {
            SculkBloomEvent event = new SculkBloomEvent(CraftBlock.at(this.silkard$level, cursor.getPos()), cursor.getCharge());
            ((ContextLevel) this.silkard$level).getCraftServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                ci.cancel();
                return;
            }
            ((ContextChargeCursor) (Object) cursor).silkard$setCharge(event.getCharge());
        }
    }
}
