package dev.nekotune.critical_combat.platform;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import dev.nekotune.javautils.types.event.Event;
import dev.nekotune.javautils.types.event.IEventHook;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SyncDataPackContents;
import net.minecraft.server.level.ServerPlayer;

public final class PlatformEvents$Fabric implements PlatformEvents {

    private static final Supplier<PlatformEvents$Fabric> INSTANCE = Suppliers.memoize(PlatformEvents$Fabric::new);

    public static PlatformEvents$Fabric get() {
        return INSTANCE.get();
    }

    private static final Event<DatapackSyncEvent$Payload> DATAPACK_SYNC_EVENT = new Event<>();

    public PlatformEvents$Fabric() {
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(new SyncDataPackContents() {
            @Override
            public void onSyncDataPackContents(final ServerPlayer player, final boolean joined) {
                DATAPACK_SYNC_EVENT.post(new DatapackSyncEvent$Payload(player));
            }
        });
    }

    @Override
    public IEventHook<DatapackSyncEvent$Payload> onDatapackSyncEvent() {
        return DATAPACK_SYNC_EVENT;
    }
}
