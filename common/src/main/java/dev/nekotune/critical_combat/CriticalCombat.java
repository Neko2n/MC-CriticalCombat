package dev.nekotune.critical_combat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.platform.Platform.IPlatformHelper.Environment;
import dev.nekotune.critical_combat.server.CriticalCombatServer;
import dev.nekotune.critical_combat.shared.registry.ModRegistries;

public interface CriticalCombat {
    public static final String MOD_ID = "critical_combat";
    public static final String MOD_NAME = "Critical Combat";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        ModRegistries.registerAll();

        if (Platform.get().getEnvironmentType() == Environment.DEDICATED_SERVER) {
            CriticalCombatServer.init();
        }
    }
}
