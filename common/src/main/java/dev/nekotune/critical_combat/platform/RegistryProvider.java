package dev.nekotune.critical_combat.platform;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import dev.nekotune.critical_combat.CriticalCombat;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

/**
 * Abstraction for cross-platform registry logic.
 * 
 * @param <T> The object type.
 */
public abstract class RegistryProvider<T> {

    private final LinkedList<RegistryObject<T, ?>> entries = new LinkedList<>();
    private boolean frozen = false;

    protected final Supplier<Registry<T>> registry;

    public RegistryProvider(final Supplier<Registry<T>> registry) {
        this.registry = registry;
    }

    /**
     * Registers a new object at the provided resource location.
     * 
     * @param id       The id to register the object under
     * @param supplier A supplier to build the registered object
     * @return A deferred supplier for the registered object
     */
    protected abstract <I extends T> Supplier<I> register(final ResourceLocation id, final Supplier<I> supplier);

    /**
     * Maps an object constructor to an ID under the {@link CriticalCombat#MOD_ID}
     * namespace with the provided name.
     * 
     * @param name     The name to map the object to
     * @param supplier A supplier to build the registered object
     * @return A deferred supplier for the registered object
     */
    public final <I extends T> Supplier<I> define(final String name, final Supplier<I> supplier) {
        if (frozen)
            throw new IllegalStateException(
                    "RegistryProvider#define cannot be called after RegistryProvider#register has been called.");
        final ResourceLocation id = ResourceLocation.fromNamespaceAndPath(CriticalCombat.MOD_ID, name);
        final RegistryObject<T, I> entry = new RegistryObject<>(this, id, supplier);
        this.entries.add(entry);
        return entry;
    }

    /**
     * Registers all objects, calling {@link #register(ResourceLocation, Supplier)}
     * on them.
     */
    public final void register() {
        if (frozen)
            throw new IllegalStateException("RegistryProvider#register cannot be called more than once.");
        this.entries.forEach(RegistryObject::register);
        this.frozen = true;
    }

    private static final class RegistryObject<T, I extends T> implements Supplier<I> {

        private @Nullable Supplier<I> registered = null;
        private final RegistryProvider<T> provider;
        private final ResourceLocation id;
        public final Supplier<I> supplier;

        public RegistryObject(final RegistryProvider<T> provider, final ResourceLocation id,
                final Supplier<I> supplier) {
            this.provider = provider;
            this.id = id;
            this.supplier = supplier;
        }

        public void register() {
            this.registered = this.provider.register(this.id, this.supplier);
        }

        @Override
        public I get() {
            return Objects
                    .requireNonNull(this.registered,
                            "Attempted get() on RegistryObject with ID \"" + this.id + "\" that is not yet registered")
                    .get();
        }
    }
}
