package dev.nekotune.critical_combat.platform;

import java.util.Objects;
import java.util.function.Supplier;

import dev.nekotune.critical_combat.CriticalCombat;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

public final class PlatformHelper$NeoForge implements Platform.IPlatformHelper {

    @Override
    public boolean isModLoaded(final String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Environment getEnvironmentType() {
        return switch (FMLLoader.getDist()) {
            case CLIENT -> Environment.CLIENT;
            case DEDICATED_SERVER -> Environment.DEDICATED_SERVER;
        };
    }

    @Override
    public <T> RegistryProvider<T> openRegistry(final Supplier<Registry<T>> registry) {
        final ModContainer container = ModList.get().getModContainerById(CriticalCombat.MOD_ID).orElseThrow();
        final IEventBus modBus = Objects.requireNonNull(container.getEventBus());
        return new RegistryProvider$NeoForge<>(registry, modBus);
    }

    @Override
    public PlatformEvents events() {
        return PlatformEvents$NeoForge.get();
    }

    @Override
    public PlatformNetwork network() {
        return PlatformNetwork$NeoForge.get();
    }
}
