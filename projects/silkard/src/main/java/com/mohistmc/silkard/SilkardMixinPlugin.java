package com.mohistmc.silkard;

import io.izzel.arclight.mixin.MixinTools;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * @author Mgazul
 * @date 2026/3/28 22:30
 */
public class SilkardMixinPlugin implements IMixinConfigPlugin {

    private static final List<String> DISABLED_MIXINS = List.of(
            // 在这里按类名后缀禁用与其它模组冲突的 mixin，例如：
            // "vanilla.world.level.MixinSomeClass"
    );

    @Override
    public void onLoad(String mixinPackage) {
        MixinTools.setup();
    }

    @Override
    public String getRefMapperConfig() {
        // 官方映射运行环境，无需 refmap
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String relative = mixinClassName.startsWith("com.mohistmc.silkard.mixin.")
                ? mixinClassName.substring("com.mohistmc.silkard.mixin.".length())
                : mixinClassName;
        return !DISABLED_MIXINS.contains(relative);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return new ArrayList<>();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        MixinTools.onPostMixin(targetClassName, targetClass, mixinInfo);
    }
}
