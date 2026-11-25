package dev.nekotune.critical_combat.shared.registry;

import java.util.function.Supplier;

import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.platform.RegistryProvider;
import dev.nekotune.critical_combat.shared.definition.SlashParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Particles registry for {@link CriticalCombat}
 */
public interface CC$ParticleTypes {

    public static final RegistryProvider<ParticleType<?>> PARTICLE_TYPES = Platform.get()
            .openRegistry(() -> BuiltInRegistries.PARTICLE_TYPE);

    /** Slash particles for LIGHT-type weapons */
    public static final Supplier<ParticleType<SlashParticleOptions>> LIGHT_SLASH = PARTICLE_TYPES
            .define("light_slash", SlashParticleOptions.Type::new);
}
