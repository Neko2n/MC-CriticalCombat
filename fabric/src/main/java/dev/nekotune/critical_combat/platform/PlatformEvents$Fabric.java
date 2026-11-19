package dev.nekotune.critical_combat.platform;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Suppliers;
import com.mojang.brigadier.CommandDispatcher;

import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.javautils.types.event.Event;
import dev.nekotune.javautils.types.event.IEventHook;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SyncDataPackContents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public final class PlatformEvents$Fabric implements PlatformEvents {

    private static final Supplier<PlatformEvents$Fabric> INSTANCE = Suppliers.memoize(PlatformEvents$Fabric::new);

    public static PlatformEvents$Fabric get() {
        return INSTANCE.get();
    }

    private static final Event<DatapackSyncEvent$Payload> DATAPACK_SYNC = new Event<>();
    private static final Event<RegisterCommandsEvent$Payload> REGISTER_COMMANDS = new Event<>();
    private static final Event<RegisterReloadListenersEvent$Payload> REGISTER_RELOAD_LISTENERS = new Event<>();

    public PlatformEvents$Fabric() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(new SyncDataPackContents() {
            @Override
            public void onSyncDataPackContents(final ServerPlayer player, final boolean joined) {
                DATAPACK_SYNC.post(new DatapackSyncEvent$Payload(player));
            }
        });
        CommandRegistrationCallback.EVENT.register(new CommandRegistrationCallback() {
            @Override
            public void register(final CommandDispatcher<CommandSourceStack> dispatcher,
                    final CommandBuildContext registryAccess, final CommandSelection environment) {
                REGISTER_COMMANDS.post(new RegisterCommandsEvent$Payload(dispatcher, registryAccess));
            }
        });

        CriticalCombat.onModLoaded().thenRun(() -> {
            REGISTER_RELOAD_LISTENERS.post(new RegisterReloadListenersEvent$Payload(listeners -> {
                final ResourceManagerHelper registrar = ResourceManagerHelper.get(PackType.SERVER_DATA);
                listeners.stream().map(PlatformEvents$Fabric::wrapReloadListener)
                        .forEach(registrar::registerReloadListener);
            }));
        }).exceptionally((final Throwable t) -> {
            CriticalCombat.LOG.error("Exception registering reload listeners", t);
            return null;
        });
    }

    @Override
    public IEventHook<DatapackSyncEvent$Payload> onDatapackSyncEvent() {
        return DATAPACK_SYNC;
    }

    @Override
    public IEventHook<RegisterCommandsEvent$Payload> onRegisterCommandsEvent() {
        return REGISTER_COMMANDS;
    }

    @Override
    public IEventHook<RegisterReloadListenersEvent$Payload> onRegisterReloadListenersEvent() {
        return REGISTER_RELOAD_LISTENERS;
    }

    private static IdentifiableResourceReloadListener wrapReloadListener(
            final SimplePreparableReloadListener<?> listener) {
        return new IdentifiableResourceReloadListener() {

            @Override
            @ParametersAreNonnullByDefault
            public CompletableFuture<Void> reload(final PreparationBarrier preparationBarrier,
                    final ResourceManager resourceManager,
                    final ProfilerFiller preparationsProfiler,
                    final ProfilerFiller reloadProfiler, final Executor backgroundExecutor,
                    final Executor gameExecutor) {
                return listener.reload(preparationBarrier, resourceManager, preparationsProfiler,
                        reloadProfiler, backgroundExecutor, gameExecutor);
            }

            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath(CriticalCombat.MOD_ID,
                        listener.getName().toLowerCase());
            }
        };
    }
}
