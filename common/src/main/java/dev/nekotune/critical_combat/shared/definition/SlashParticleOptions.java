package dev.nekotune.critical_combat.shared.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.nekotune.critical_combat.client.definition.SlashParticle;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * @see SlashParticle
 */
public record SlashParticleOptions(ParticleType<SlashParticleOptions> type,
        int followEntityId, boolean flipped, float yaw, float pitch, float roll, float scale) implements ParticleOptions {

    public static final int NO_ENTITY = -1;

    public static MapCodec<SlashParticleOptions> codec(final ParticleType<SlashParticleOptions> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.INT.optionalFieldOf("followEntityId", NO_ENTITY).forGetter(SlashParticleOptions::followEntityId),
                Codec.BOOL.fieldOf("flipped").forGetter(SlashParticleOptions::flipped),
                Codec.FLOAT.fieldOf("yaw").forGetter(SlashParticleOptions::yaw),
                Codec.FLOAT.fieldOf("pitch").forGetter(SlashParticleOptions::pitch),
                Codec.FLOAT.fieldOf("roll").forGetter(SlashParticleOptions::roll),
                Codec.FLOAT.fieldOf("scale").forGetter(SlashParticleOptions::scale)).apply(instance,
                        (followEntityId, flipped, yaw, pitch, roll, scale) -> new SlashParticleOptions(type, followEntityId,
                                flipped, yaw, pitch, roll, scale)));
    }

    public static StreamCodec<ByteBuf, SlashParticleOptions> streamCodec(
            final ParticleType<SlashParticleOptions> type) {
        return StreamCodec.composite(
                ByteBufCodecs.VAR_INT, SlashParticleOptions::followEntityId,
                ByteBufCodecs.BOOL, SlashParticleOptions::flipped,
                ByteBufCodecs.FLOAT, SlashParticleOptions::yaw,
                ByteBufCodecs.FLOAT, SlashParticleOptions::pitch,
                ByteBufCodecs.FLOAT, SlashParticleOptions::roll,
                ByteBufCodecs.FLOAT, SlashParticleOptions::scale,
                (followEntityId, flipped, yaw, pitch, roll, scale) -> new SlashParticleOptions(type, followEntityId, flipped,
                        yaw, pitch, roll, scale));
    }

    @Override
    public ParticleType<SlashParticleOptions> getType() {
        return this.type;
    }

    public static final class Type extends ParticleType<SlashParticleOptions> {
        private final MapCodec<SlashParticleOptions> codec = SlashParticleOptions.codec(this);
        private final StreamCodec<ByteBuf, SlashParticleOptions> streamCodec = SlashParticleOptions.streamCodec(this);

        public Type() {
            super(false);
        }

        @Override
        public MapCodec<SlashParticleOptions> codec() {
            return this.codec;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, SlashParticleOptions> streamCodec() {
            return this.streamCodec;
        }
    }
}
