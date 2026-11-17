package dev.nekotune.critical_combat.platform;

import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class RegistryProvider$Fabric<T> extends RegistryProvider<T> {

    public RegistryProvider$Fabric(final Supplier<Registry<T>> registry) {
        super(registry);
    }

    @Override
    protected <I extends T> Supplier<I> register(final ResourceLocation id, final Supplier<I> supplier) {
        final I value = supplier.get();
        Registry.registerForHolder(this.registry.get(), id, value);
        return () -> value;
    }
}
