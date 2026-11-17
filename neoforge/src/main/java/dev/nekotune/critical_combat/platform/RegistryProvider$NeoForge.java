package dev.nekotune.critical_combat.platform;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import dev.nekotune.critical_combat.CriticalCombat;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegistryProvider$NeoForge<T> extends RegistryProvider<T> {

    private final Supplier<DeferredRegister<T>> deferredRegister;

    public RegistryProvider$NeoForge(final Supplier<Registry<T>> registry, final IEventBus eventBus) {
        super(registry);
        this.deferredRegister = Suppliers.memoize(() -> {
            final DeferredRegister<T> d = DeferredRegister.create(this.registry.get(), CriticalCombat.MOD_ID);
            d.register(eventBus);
            return d;
        });
    }

    @Override
    protected <I extends T> Supplier<I> register(final ResourceLocation id, final Supplier<I> supplier) {
        return this.deferredRegister.get().register(id.getPath(), supplier);
    }
}
