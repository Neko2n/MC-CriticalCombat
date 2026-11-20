package dev.nekotune.critical_combat.platform.client;

import java.util.function.Function;

import dev.nekotune.javautils.types.event.IEventHook;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public interface ClientPlatformEvents {

    public IEventHook<RegisterParticleProvidersEvent$Payload> onRegisterParticleProvidersEvent();

    public static record RegisterParticleProvidersEvent$Payload(Registrar registrar) {
        public <T extends ParticleOptions> void register(final ParticleType<T> type,
                final Function<SpriteSet, ParticleProvider<T>> factory) {
            this.registrar().register(type, factory);
        }

        public static interface Registrar {
            public <T extends ParticleOptions> void register(final ParticleType<T> type,
                    final Function<SpriteSet, ParticleProvider<T>> factory);
        }
    }
}
