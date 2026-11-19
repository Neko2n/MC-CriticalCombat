package dev.nekotune.critical_combat.platform;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import dev.nekotune.javautils.types.event.Event;
import dev.nekotune.javautils.types.event.IEventHook;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public final class PlatformEvents$NeoForge implements PlatformEvents {

    private static final Supplier<PlatformEvents$NeoForge> INSTANCE = Suppliers.memoize(PlatformEvents$NeoForge::new);

    public static PlatformEvents$NeoForge get() {
        return INSTANCE.get();
    }

    private static final Event<DatapackSyncEvent$Payload> DATAPACK_SYNC = new Event<>();
    private static final Event<RegisterCommandsEvent$Payload> REGISTER_COMMANDS = new Event<>();
    private static final Event<RegisterReloadListenersEvent$Payload> REGISTER_RELOAD_LISTENERS = new Event<>();

    private PlatformEvents$NeoForge() {
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

    @SubscribeEvent 
    public static void onDatapackSyncEvent(final OnDatapackSyncEvent event) {
        DATAPACK_SYNC.post(new DatapackSyncEvent$Payload(event.getPlayer()));
    }

    @SubscribeEvent 
    public static void onRegisterCommandsEvent(final RegisterCommandsEvent event) {
        REGISTER_COMMANDS.post(new RegisterCommandsEvent$Payload(event.getDispatcher(), event.getBuildContext()));
    }

    @SubscribeEvent 
    public static void onAddReloadListener(final AddReloadListenerEvent event) {
        REGISTER_RELOAD_LISTENERS.post(new RegisterReloadListenersEvent$Payload(listeners -> {
            listeners.forEach(event::addListener);
        }));
    }
}
