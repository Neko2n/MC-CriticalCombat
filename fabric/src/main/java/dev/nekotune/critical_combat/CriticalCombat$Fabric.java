package dev.nekotune.critical_combat;

import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.platform.PlatformHelper$Fabric;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;

public class CriticalCombat$Fabric implements ModInitializer {
    
    static {
        Platform.init(new PlatformHelper$Fabric());
    }

    @Override
    public void onInitialize() {
        NeoForgeConfigRegistry.INSTANCE.register(CriticalCombat.MOD_ID, ModConfig.Type.COMMON, CCConfig.SPEC);
        CriticalCombat.init();
    }
}
