package dev.nekotune.critical_combat.client.definition;

import javax.annotation.ParametersAreNonnullByDefault;

import dev.nekotune.critical_combat.shared.definition.SlashParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;

/**
 * Variant of SlashParticle with random variation in roll and scale.
 */
@ParametersAreNonnullByDefault
public class LightSlashParticle extends SlashParticle {

    private final float rollVariance;

    protected LightSlashParticle(final ClientLevel level,
            final double x, final double y, final double z,
            final SlashParticleOptions options,
            final float size, final int lifetime,
            final SpriteSet sprites) {
        super(level, x, y, z, options, size, lifetime, sprites);
        this.rollVariance = (float) level.getRandom().triangle(0D, 10D);
        this.quadSize *= level.getRandom().triangle(1D, 0.1D);
    }
    
    @Override
    protected float globalRoll() {
        final float deg = super.globalRoll() * Mth.RAD_TO_DEG;
        return Mth.wrapDegrees(deg + this.rollVariance) * Mth.DEG_TO_RAD;
    }

    public static record Provider(SpriteSet sprites, float size, int lifetime)
            implements ParticleProvider<SlashParticleOptions> {
        @Override
        public Particle createParticle(final SlashParticleOptions options, final ClientLevel level,
                final double x, final double y, final double z,
                final double xSpeed, final double ySpeed, final double zSpeed) {
            return new LightSlashParticle(level, x, y, z, options, this.size, this.lifetime, this.sprites);
        }
    }
}
