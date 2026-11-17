package dev.nekotune.critical_combat;

import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.platform.PlatformHelper$Fabric;
import net.fabricmc.api.ModInitializer;

public class CriticalCombat$Fabric implements ModInitializer {
    
    static {
        Platform.init(new PlatformHelper$Fabric());
    }

    @Override
    public void onInitialize() {
        CriticalCombat.init();
    }
}
