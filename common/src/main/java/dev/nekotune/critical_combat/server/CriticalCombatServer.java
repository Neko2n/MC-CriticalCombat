package dev.nekotune.critical_combat.server;

import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.shared.definition.WeaponTypes;

public interface CriticalCombatServer {

    public static void init() {

        // Hook up server-to-client replication of weapon type mappings
        Platform.get().events().onDatapackSyncEvent().subscribe(payload -> {
            WeaponTypes.syncToClient(payload.player());
        });
    }
}
