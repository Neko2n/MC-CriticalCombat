package dev.nekotune.critical_combat.server;

import java.util.Set;

import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.shared.WeaponTypes;

public interface CriticalCombatServer {

    public static void init() {

        // Hook up server-to-client replication of weapon type mappings
        Platform.get().events().onDatapackSyncEvent().subscribe(payload -> {
            WeaponTypes.syncToClient(payload.player());
        });

        Platform.get().events().onRegisterReloadListenersEvent().subscribe(payload -> {
            payload.consumer().accept(Set.of(WeaponTypeDataManager.get()));
        });
    }
}
