package com.mohistmc.silkard.mixin.vanilla.server;

import com.mohistmc.silkard.bukkit.MainHook;
import com.mojang.serialization.Dynamic;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.server.Main;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * CraftBukkit Main.patch 移植（silkard 变体）：静态槽位传递 OptionSet / DataLoadContext。
 * CB 把 main(String[]) 改为 main(OptionSet) 并在 WorldLoader.load 回调捕获 worldLoader；
 * silkard 走 Fabric 链无法改签名，改为：org.bukkit.craftbukkit.Main 预先 MainHook.configure(options)，
 * WorldLoader.load 回调 HEAD 注入捕获 DataLoadContext。
 *
 * @author Mgazul
 * @date 2026/10/24
 */
@Mixin(Main.class)
public class MixinMain {

    /**
     * vanilla Main.main HEAD：把 bukkit 选项解析结果交给 MainHook，
     * 让 MinecraftServer.silkard_options() 拿到带 bukkit 选项的 OptionSet
     * （CB Main 在 fabric 启动链上不会执行，只能从这里注入）。
     */
    @Inject(method = "main", at = @At("HEAD"))
    private static void silkard$bukkitOptions(String[] args, CallbackInfo ci) {
        MainHook.configure(org.bukkit.craftbukkit.Main.bukkitOptions(args)); // CraftBukkit
    }

    /**
     * WorldLoader.load 的 context 回调（Main.main 内 lambda）—— 捕获 DataLoadContext（CB worldLoader.set 等价）。
     */
    @Inject(method = "lambda$main$1", at = @At("HEAD"), require = 0)
    private static void silkard$captureWorldLoader(Dynamic levelDataTag, LevelStorageSource.LevelStorageAccess access, DedicatedServerSettings settings, OptionSet options, OptionSpec demo, OptionSpec bonusChest, WorldLoader.DataLoadContext context, CallbackInfoReturnable<WorldLoader.DataLoadOutput> cir) {
        MainHook.captureWorldLoader(context); // CraftBukkit
    }
}
