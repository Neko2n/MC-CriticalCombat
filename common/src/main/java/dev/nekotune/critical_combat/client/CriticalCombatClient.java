package dev.nekotune.critical_combat.client;

import dev.nekotune.critical_combat.shared.WeaponTypes;

public interface CriticalCombatClient {

    public static void init() {

        // Register client network payload receivers
        WeaponTypes.registerPayload();
    }
}
