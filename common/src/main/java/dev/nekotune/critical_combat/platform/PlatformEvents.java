package dev.nekotune.critical_combat.platform;

import dev.nekotune.javautils.types.event.IEventHook;
import net.minecraft.server.level.ServerPlayer;

public interface PlatformEvents {

    public IEventHook<DatapackSyncEvent$Payload> onDatapackSyncEvent();

    public static record DatapackSyncEvent$Payload(ServerPlayer player) {
    }
}
