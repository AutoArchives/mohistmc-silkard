package com.mohistmc.silkard.mixin.vanilla.server.level;

import com.mohistmc.silkard.bukkit.BukkitUtils;
import com.mohistmc.silkard.injected.server.level.ContextTicketType;
import net.minecraft.server.level.TicketType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/3/28 20:14
 */

@Mixin(TicketType.class)
public abstract class MixinTicketType implements ContextTicketType {

    @Shadow public long timeout;

    @Override
    @Overwrite
    public long timeout() {
        return (((TicketType)(Object)this) != PLUGIN) ? this.timeout : BukkitUtils.pluginTimeout;
    }
}
