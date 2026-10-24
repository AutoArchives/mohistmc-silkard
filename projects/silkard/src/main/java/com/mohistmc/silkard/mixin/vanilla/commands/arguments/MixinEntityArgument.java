package com.mohistmc.silkard.mixin.vanilla.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mohistmc.silkard.injected.commands.arguments.ContextEntityArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(EntityArgument.class)
public abstract class MixinEntityArgument implements ContextEntityArgument {

    @Shadow
    @Final
    private boolean single;

    @Shadow
    @Final
    private boolean playersOnly;

    /**
     * @author Mgazul
     * @reason CraftBukkit - delegate to overridePermissions overload
     */
    @Overwrite
    private EntitySelector parse(StringReader reader, boolean allowSelectors) throws CommandSyntaxException {
        // CraftBukkit start
        return this.parse(reader, allowSelectors, false);
    }

    public EntitySelector parse(StringReader reader, boolean allowSelectors, boolean overridePermissions) throws CommandSyntaxException {
        int start = 0;
        EntitySelectorParser parser = new EntitySelectorParser(reader, allowSelectors);
        EntitySelector entityselector = ((com.mohistmc.silkard.injected.commands.arguments.selector.ContextEntitySelectorParser) parser).parse(overridePermissions); // CraftBukkit
        if (entityselector.getMaxResults() > 1 && this.single) {
            if (this.playersOnly) {
                reader.setCursor(0);
                throw EntityArgument.ERROR_NOT_SINGLE_PLAYER.createWithContext(reader);
            } else {
                reader.setCursor(0);
                throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.createWithContext(reader);
            }
        } else if (entityselector.includesEntities() && this.playersOnly && !entityselector.isSelfSelector()) {
            reader.setCursor(0);
            throw EntityArgument.ERROR_ONLY_PLAYERS_ALLOWED.createWithContext(reader);
        } else {
            return entityselector;
        }
    }
    // CraftBukkit end
}
