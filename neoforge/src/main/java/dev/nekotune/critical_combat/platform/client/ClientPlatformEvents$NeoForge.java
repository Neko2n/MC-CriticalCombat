package dev.nekotune.critical_combat.platform.client;

import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import dev.nekotune.critical_combat.platform.client.ClientPlatformEvents.RegisterParticleProvidersEvent$Payload.Registrar;
import dev.nekotune.javautils.types.event.Event;
import dev.nekotune.javautils.types.event.IEventHook;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public final class ClientPlatformEvents$NeoForge implements ClientPlatformEvents {

    private static Supplier<ClientPlatformEvents$NeoForge> INSTANCE = Suppliers
            .memoize(ClientPlatformEvents$NeoForge::new);

    public static ClientPlatformEvents$NeoForge get() {
        return INSTANCE.get();
    }

    private static final Event<RegisterParticleProvidersEvent$Payload> REGISTER_PARTICLE_PROVIDERS = new Event<>();

    @Override
    public IEventHook<RegisterParticleProvidersEvent$Payload> onRegisterParticleProvidersEvent() {
        return REGISTER_PARTICLE_PROVIDERS;
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(final RegisterParticleProvidersEvent event) {
        REGISTER_PARTICLE_PROVIDERS.post(new RegisterParticleProvidersEvent$Payload(new Registrar() {
            @Override
            public <T extends ParticleOptions> void register(final ParticleType<T> type,
                    final Function<SpriteSet, ParticleProvider<T>> factory) {
                event.registerSpriteSet(type, factory::apply);
            }
        }));
    }
}
