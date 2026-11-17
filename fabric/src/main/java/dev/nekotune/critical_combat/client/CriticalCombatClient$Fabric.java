package dev.nekotune.critical_combat.client;

import net.fabricmc.api.ClientModInitializer;

public class CriticalCombatClient$Fabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CriticalCombatClient.init();
    }
}
