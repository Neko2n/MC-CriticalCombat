package dev.nekotune.critical_combat.platform;

import java.util.function.Supplier;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;

public class PlatformHelper$Fabric implements Platform.IPlatformHelper {

    @Override
    public boolean isModLoaded(final String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Environment getEnvironmentType() {
        return switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT -> Environment.CLIENT;
            case SERVER -> Environment.DEDICATED_SERVER;
        };
    }

    @Override
    public <T> RegistryProvider<T> openRegistry(final Supplier<Registry<T>> registry) {
        return new RegistryProvider$Fabric<>(registry);
    }
}
