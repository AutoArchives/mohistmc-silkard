package com.mohistmc.silkard.mixin.vanilla.commands.arguments.selector;

import com.mojang.brigadier.StringReader;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mohistmc.silkard.injected.commands.arguments.selector.ContextEntitySelectorParser;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author Mgazul
 * @date 2026/6/8
 */
@Mixin(EntitySelectorParser.class)
public abstract class MixinEntitySelectorParser implements ContextEntitySelectorParser {

    @Shadow
    protected abstract void parseOptions() throws CommandSyntaxException;

    @Shadow
    protected abstract void parseNameOrUUID() throws CommandSyntaxException;

    @Shadow
    private void finalizePredicates() {
        throw new AssertionError();
    }

    @Shadow
    public abstract EntitySelector getSelector();

    @Shadow
    private void limitToType(EntityType<?> type) {
        throw new AssertionError();
    }

    @Shadow
    private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> names) {
        throw new AssertionError();
    }

    @Shadow
    private CompletableFuture<Suggestions> suggestSelector(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> names) {
        throw new AssertionError();
    }

    @Shadow
    private CompletableFuture<Suggestions> suggestOpenOptions(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> names) {
        throw new AssertionError();
    }

    @Shadow
    private CompletableFuture<Suggestions> suggestOptionsKeyOrClose(SuggestionsBuilder builder, Consumer<SuggestionsBuilder> names) {
        throw new AssertionError();
    }

    @Shadow
    @Final
    private StringReader reader;

    @Shadow
    @Final
    private boolean allowSelectors;

    @Shadow
    private int maxResults;

    @Shadow
    private boolean includesEntities;

    @Shadow
    private BiConsumer<Vec3, List<? extends Entity>> order;

    @Shadow
    private boolean currentEntity;

    @Shadow
    private boolean usesSelectors;

    @Shadow
    private int startPosition;

    @Shadow
    private List<Predicate<Entity>> predicates;

    @Shadow
    private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions;

    // CraftBukkit start
    /**
     * @author Mgazul
     * @reason CraftBukkit - delegate to overridePermissions overload
     */
    @Overwrite
    public EntitySelector parse() throws CommandSyntaxException {
        return this.parse(false);
    }

    @Override
    public EntitySelector parse(boolean overridePermissions) throws CommandSyntaxException {
        this.startPosition = this.reader.getCursor();
        this.suggestions = this::suggestNameOrSelector;
        if (this.reader.canRead() && this.reader.peek() == '@') {
            if (!this.allowSelectors) {
                throw EntitySelectorParser.ERROR_SELECTORS_NOT_ALLOWED.createWithContext(this.reader);
            }

            this.reader.skip();
            this.parseSelector(overridePermissions);
        } else {
            this.parseNameOrUUID();
        }

        this.finalizePredicates();
        return this.getSelector();
    }

    /**
     * @author Mgazul
     * @reason CraftBukkit - delegate to overridePermissions overload
     */
    @Overwrite
    protected void parseSelector() throws CommandSyntaxException {
        this.parseSelector(true);
    }

    @Override
    public void parseSelector(boolean overridePermissions) throws CommandSyntaxException {
        this.usesSelectors = !overridePermissions; // CraftBukkit
        this.suggestions = this::suggestSelector;
        if (!this.reader.canRead()) {
            throw EntitySelectorParser.ERROR_MISSING_SELECTOR_TYPE.createWithContext(this.reader);
        }

        int start = this.reader.getCursor();
        char type = this.reader.read();

        if (switch (type) {
            case 'a' -> {
                this.maxResults = Integer.MAX_VALUE;
                this.includesEntities = false;
                this.order = EntitySelector.ORDER_ARBITRARY;
                this.limitToType(EntityTypes.PLAYER);
                yield false;
            }
            default -> {
                this.reader.setCursor(start);
                throw EntitySelectorParser.ERROR_UNKNOWN_SELECTOR_TYPE.createWithContext(this.reader, "@" + type);
            }
            case 'e' -> {
                this.maxResults = Integer.MAX_VALUE;
                this.includesEntities = true;
                this.order = EntitySelector.ORDER_ARBITRARY;
                yield true;
            }
            case 'n' -> {
                this.maxResults = 1;
                this.includesEntities = true;
                this.order = EntitySelectorParser.ORDER_NEAREST;
                yield true;
            }
            case 'p' -> {
                this.maxResults = 1;
                this.includesEntities = false;
                this.order = EntitySelectorParser.ORDER_NEAREST;
                this.limitToType(EntityTypes.PLAYER);
                yield false;
            }
            case 'r' -> {
                this.maxResults = 1;
                this.includesEntities = false;
                this.order = EntitySelectorParser.ORDER_RANDOM;
                this.limitToType(EntityTypes.PLAYER);
                yield false;
            }
            case 's' -> {
                this.maxResults = 1;
                this.includesEntities = true;
                this.currentEntity = true;
                yield false;
            }
        }) {
            this.predicates.add(Entity::isAlive);
        }

        this.suggestions = this::suggestOpenOptions;
        if (this.reader.canRead() && this.reader.peek() == '[') {
            this.reader.skip();
            this.suggestions = this::suggestOptionsKeyOrClose;
            this.parseOptions();
        }
    }
    // CraftBukkit end
}
