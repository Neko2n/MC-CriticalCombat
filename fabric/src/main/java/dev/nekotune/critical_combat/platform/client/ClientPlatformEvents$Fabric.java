package dev.nekotune.critical_combat.platform.client;

import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.client.CriticalCombatClient;
import dev.nekotune.critical_combat.platform.client.ClientPlatformEvents.RegisterParticleProvidersEvent$Payload.Registrar;
import dev.nekotune.javautils.types.event.Event;
import dev.nekotune.javautils.types.event.IEventHook;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public class ClientPlatformEvents$Fabric implements ClientPlatformEvents {

    private static Supplier<ClientPlatformEvents$Fabric> INSTANCE = Suppliers
            .memoize(ClientPlatformEvents$Fabric::new);

    public static ClientPlatformEvents$Fabric get() {
        return INSTANCE.get();
    }

    private static final Event<RegisterParticleProvidersEvent$Payload> REGISTER_PARTICLE_PROVIDERS = new Event<>();

    private ClientPlatformEvents$Fabric() {

        CriticalCombatClient.onModLoaded().thenRun(() -> {
            REGISTER_PARTICLE_PROVIDERS.post(new RegisterParticleProvidersEvent$Payload(new Registrar() {
                @Override
                public <T extends ParticleOptions> void register(final ParticleType<T> type,
                        final Function<SpriteSet, ParticleProvider<T>> factory) {
                    ParticleFactoryRegistry.getInstance().register(type, factory::apply);
                }
            }));
        }).exceptionally((final Throwable t) -> {
            CriticalCombat.LOG.error("Exception registering particle providers", t);
            return null;
        });
    }

    @Override
    public IEventHook<RegisterParticleProvidersEvent$Payload> onRegisterParticleProvidersEvent() {
        return REGISTER_PARTICLE_PROVIDERS;
    }
}
