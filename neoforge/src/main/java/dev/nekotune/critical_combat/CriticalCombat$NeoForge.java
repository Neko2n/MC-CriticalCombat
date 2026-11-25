package dev.nekotune.critical_combat;

import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.platform.PlatformHelper$NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(CriticalCombat.MOD_ID)
public class CriticalCombat$NeoForge {

    static {
        Platform.init(new PlatformHelper$NeoForge());
    }

    public CriticalCombat$NeoForge(final IEventBus eventBus, final ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, CC$Config.SPEC);
        CriticalCombat.init();
    }
}
