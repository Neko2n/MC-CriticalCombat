package dev.nekotune.critical_combat.platform;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import dev.nekotune.javautils.types.event.Event;
import dev.nekotune.javautils.types.event.IEventHook;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@EventBusSubscriber
public final class PlatformEvents$NeoForge implements PlatformEvents {

    private static final Supplier<PlatformEvents$NeoForge> INSTANCE = Suppliers.memoize(PlatformEvents$NeoForge::new);

    public static PlatformEvents$NeoForge get() {
        return INSTANCE.get();
    }

    private static final Event<DatapackSyncEvent$Payload> DATAPACK_SYNC_EVENT = new Event<>();

    private PlatformEvents$NeoForge() {
    }

    @Override
    public IEventHook<DatapackSyncEvent$Payload> onDatapackSyncEvent() {
        return DATAPACK_SYNC_EVENT;
    }

    @SubscribeEvent 
    public static void onDatapackSyncEvent(final OnDatapackSyncEvent event) {
        DATAPACK_SYNC_EVENT.post(new DatapackSyncEvent$Payload(event.getPlayer()));
    }
}
