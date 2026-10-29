package com.mohistmc.silkard.mixin.vanilla.world.level.block.entity;

import com.mohistmc.silkard.injected.world.level.block.entity.ContextCatalystListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.entity.SculkCatalystBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(SculkCatalystBlockEntity.CatalystListener.class)
public class MixinSculkCatalystBlockEntity$CatalystListener implements ContextCatalystListener {

    @Shadow
    @Final
    private SculkSpreader sculkSpreader;

    @Unique
    private Level silkard$level;

    @Override
    public Level silkard$level() {
        return this.silkard$level;
    }

    @Override
    public void silkard$setLevel(Level level) {
        this.silkard$level = level;
        ((com.mohistmc.silkard.injected.world.level.block.ContextSculkSpreader) this.sculkSpreader).silkard$setLevel(level);
    }
}
