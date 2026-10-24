package com.mohistmc.silkard.bukkit;

import java.util.concurrent.atomic.AtomicReference;
import joptsimple.OptionSet;
import net.minecraft.server.WorldLoader;

/**
 * CraftBukkit Main.patch 的静态槽位持有者。
 * CB 把 main(String[]) 改成 main(OptionSet) 从而拿到 options；silkard 走 Fabric 链改不了签名，
 * 改由 org.bukkit.craftbukkit.Main 在此预设，WorldLoader.load 回调经 MixinMain 捕获 DataLoadContext。
 * 注: 槽位必须放在普通类里而不是 mixin 内 —— mixin 的静态成员会被合并进目标类，
 * 直接对 mixin 类做静态调用会加载到另一份彼此隔离的状态。
 */
public final class MainHook {

    private static final AtomicReference<WorldLoader.DataLoadContext> WORLD_LOADER = new AtomicReference<>();
    private static OptionSet options;

    private MainHook() {
    }

    public static void configure(OptionSet options) {
        MainHook.options = options;
    }

    public static OptionSet options() {
        return options;
    }

    public static WorldLoader.DataLoadContext worldLoader() {
        return WORLD_LOADER.get();
    }

    public static void capture(OptionSet options, WorldLoader.DataLoadContext context) {
        MainHook.options = options;
        WORLD_LOADER.set(context);
    }
}
