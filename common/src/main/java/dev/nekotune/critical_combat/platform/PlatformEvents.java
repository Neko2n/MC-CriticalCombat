package dev.nekotune.critical_combat.platform;

import java.util.Set;
import java.util.function.Consumer;

import com.mojang.brigadier.CommandDispatcher;

import dev.nekotune.javautils.types.event.IEventHook;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

public interface PlatformEvents {

    public IEventHook<DatapackSyncEvent$Payload> onDatapackSyncEvent();

    public IEventHook<RegisterCommandsEvent$Payload> onRegisterCommandsEvent();

    public IEventHook<RegisterReloadListenersEvent$Payload> onRegisterReloadListenersEvent();

    public static record DatapackSyncEvent$Payload(
            ServerPlayer player) {
    }

    public static record RegisterCommandsEvent$Payload(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext context) {
    }

    public static record RegisterReloadListenersEvent$Payload(
            Consumer<Set<SimplePreparableReloadListener<?>>> consumer
    ) {}
}
