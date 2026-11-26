package dev.nekotune.critical_combat.shared;

import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import dev.nekotune.critical_combat.CCConfig;
import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.client.ClientNetworkHandler;
import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.server.ServerNetworkHandler;
import dev.nekotune.critical_combat.shared.WeaponTypes.WeaponType;
import dev.nekotune.critical_combat.shared.definition.SlashParticleOptions;
import dev.nekotune.critical_combat.util.SoundOptions;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@ParametersAreNonnullByDefault 
public interface SwingEffects {

    /**
     * Plays swinging particles and sounds in front of the player.
     * 
     * @param entity     The entity to play the effects at
     * @param weaponType The WeaponType whose particles/sounds should be used
     */
    public static void play(final Entity entity, final WeaponType weaponType) {
        final Level level = entity.level();

        // On the server, just replicate to clients
        if (level instanceof ServerLevel) {
            final var broadcast = new SwingFXBroadcastPayload(entity.getId(), weaponType);
            Platform.get().network().sendToClientsTrackingEntity(entity, broadcast);
            return;
        }

        @Nullable Player player = null;
        boolean flipped = false;
        if (entity instanceof final Player p) {
            player = p;
            flipped = CCPlayer_.get(p).slashTracker.isSlashFlipped();
        }

        final Vec3 look = entity.getLookAngle();
        final double x = entity.getX() + look.x * 0.8;
        final double y = entity.getEyeY() + look.y * 0.8 - 0.2;
        final double z = entity.getZ() + look.z * 0.8;

        final @Nullable Supplier<ParticleType<SlashParticleOptions>> particleType = weaponType.swingEffects
                .slashParticle();
        if (particleType != null && CCConfig.SWING_PARTICLES.get()) {
            final float yaw = entity.getYRot();
            final float pitch = entity.getXRot();
            final var particleOptions = new SlashParticleOptions(particleType.get(),
                    entity.getId(), flipped, yaw, pitch, 0f, 1f);
            level.addParticle(particleOptions, x, y, z, 0D, 0D, 0D);
        }

        final @Nullable SoundOptions attackSound = weaponType.swingEffects.attackSound();
        if (attackSound != null && CCConfig.SWING_SOUNDS.get()) {
            attackSound.playSound(level, player, x, y, z, entity.getSoundSource());
        }
    }

    /** 
     * @see ServerNetworkHandler
     */
    public static record SwingFXPayload() implements CustomPacketPayload {
        private static final SwingFXPayload INSTANCE = new SwingFXPayload();
        public static final Type<SwingFXPayload> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(CriticalCombat.MOD_ID, "swing_fx"));
        public static final StreamCodec<ByteBuf, SwingFXPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public Type<SwingFXPayload> type() {
            return TYPE;
        }
    }

    /**
     * @see ClientNetworkHandler
     */
    public static record SwingFXBroadcastPayload(int entityId, WeaponType weaponType) implements CustomPacketPayload {
        public static final Type<SwingFXBroadcastPayload> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(CriticalCombat.MOD_ID, "swing_fx_broadcast"));
        public static final StreamCodec<ByteBuf, SwingFXBroadcastPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, SwingFXBroadcastPayload::entityId,
                WeaponType.STREAM_CODEC, SwingFXBroadcastPayload::weaponType,
                SwingFXBroadcastPayload::new);

        @Override
        public Type<SwingFXBroadcastPayload> type() {
            return TYPE;
        }
    }
}
