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
public interface CCParticleTypes {

    public static final RegistryProvider<ParticleType<?>> PARTICLE_TYPES = Platform.get()
            .openRegistry(() -> BuiltInRegistries.PARTICLE_TYPE);

    /** Swing particle for LIGHT-type weapons */
    public static final Supplier<ParticleType<SlashParticleOptions>> LIGHT_SWING = PARTICLE_TYPES
            .define("light_swing", SlashParticleOptions.Type::new);

    /** Swing particle for BLADE-type weapons */
    public static final Supplier<ParticleType<SlashParticleOptions>> BLADE_SWING = PARTICLE_TYPES
            .define("blade_swing", SlashParticleOptions.Type::new);

    /** Swing particle for POLEARM-type weapons */
    public static final Supplier<ParticleType<SlashParticleOptions>> POLEARM_SWING = PARTICLE_TYPES
            .define("polearm_swing", SlashParticleOptions.Type::new);
}
