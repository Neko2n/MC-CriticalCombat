package dev.nekotune.critical_combat.shared;

import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;

import dev.nekotune.critical_combat.CC$Config;
import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.client.CriticalCombatClient;
import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.platform.Platform.IPlatformHelper.Environment;
import dev.nekotune.critical_combat.server.CriticalCombatServer;
import dev.nekotune.critical_combat.shared.CC$Player_.CameraTracker;
import dev.nekotune.critical_combat.shared.definition.SlashParticleOptions;
import dev.nekotune.critical_combat.shared.registry.CC$ParticleTypes;
import dev.nekotune.critical_combat.shared.registry.CC$SoundEvents;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * Service containing mappings for items' WeaponTypes
 */
@ParametersAreNonnullByDefault
public final class WeaponTypes {

    private static volatile Map<Item, WeaponType> map = Map.of();

    /**
     * Returns the WeaponType associated with the given item, if it has one.
     * 
     * @param item The item to get the WeaponType for
     * @return The WeaponType associated with the given item, or null if the item
     *         has no defined WeaponType
     */
    public static @Nullable WeaponType get(final ItemLike item) {
        return map.get(item.asItem());
    }

    /**
     * Adds mappings of items to their WeaponTypes.
     * 
     * @param mappings Map of items to WeaponTypes
     */
    public static void setMappings(final Map<Item, WeaponType> mappings) {
        map = Map.copyOf(mappings);
    }

    /**
     * Syncs weapon type mappings data to the client.
     * 
     * @param player The ServerPlayer to sync data to
     * @see CriticalCombatServer#init()
     */
    public static void syncToClient(final ServerPlayer player) {
        final MappingsPayload payload = new MappingsPayload(new IdentityHashMap<>(map));
        Platform.get().network().sendToClient(player, payload);
    }

    /**
     * Registers the client to receive and handle packets from the server.
     * 
     * @throws IllegalStateException If called on the server-side
     * @see CriticalCombatClient#init()
     */
    public static void registerPayload() {
        if (Platform.get().getEnvironmentType() != Environment.CLIENT)
            throw new IllegalStateException("WeaponTypes#registerPayload can only be called on the client");
        Platform.get().network().register$S2C(MappingsPayload.TYPE, MappingsPayload.STREAM_CODEC,
                MappingsPayload::handle);
    }

    private WeaponTypes() {
    }

    /**
     * The payload for replicating WeaponType mappings between the client and the
     * server.
     * 
     * @param mappings Map of item registry IDs to their assigned WeaponTypes
     */
    public static record MappingsPayload(IdentityHashMap<Item, WeaponType> mappings) implements CustomPacketPayload {

        public static final Type<MappingsPayload> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(CriticalCombat.MOD_ID, "weapon_types"));

        public static final StreamCodec<RegistryFriendlyByteBuf, MappingsPayload> STREAM_CODEC = ByteBufCodecs
                .map(IdentityHashMap<Item, WeaponType>::new, ByteBufCodecs.registry(Registries.ITEM),
                        WeaponType.STREAM_CODEC)
                .map(MappingsPayload::new, MappingsPayload::mappings);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        /**
         * Handles a network payload sent from the server.
         * 
         * @param payload The MappingsPayload sent by the server
         */
        public static void handle(final MappingsPayload payload) {
            CriticalCombat.LOG.debug("Received WeaponType mappings payload: {}", payload.mappings());
            WeaponTypes.setMappings(payload.mappings());
        }
    }

    /**
     * Enums for weapon types. Handle WeaponType-dependent logic.
     */
    public static enum WeaponType implements StringRepresentable {

        /**
         * Weapons that behave as default vanilla weapons.
         * Crits when falling.
         */
        DEFAULT {

            @Override
            public boolean shouldCrit(final Player player, final Entity target) {
                return player.getAttackStrengthScale(0.5f) > 0.9f
                        && player.fallDistance > 0.0f
                        && !player.onGround()
                        && !player.onClimbable()
                        && !player.isInWater()
                        && !player.hasEffect(MobEffects.BLINDNESS)
                        && !player.isPassenger()
                        && target instanceof LivingEntity;
                // Dropped the "cannot be sprinting" rule
            }

            @Override
            public @Nullable ParticleType<SlashParticleOptions> slashParticle() {
                return null;
            }

            @Override
            public @Nullable SoundEvent slashSound() {
                return null;
            }
        },

        /**
         * Weapons that require charging up to swing and mine.
         * Deals knockback scaling with your charge level. You can deal increased
         * knockback by charging up past 100%.
         * Crits when charged to 200%.
         * Beyond 100% charge, deals damage in an area in front of you instead of just
         * with a raycast.
         * Typically for hammer-like weapons.
         */
        HEAVY {

            @Override
            public boolean shouldCrit(final Player player, final Entity target) {
                return false; // TODO heavy weapon crit logic
            }

            @Override
            public @Nullable ParticleType<SlashParticleOptions> slashParticle() {
                return null;
            }

            @Override
            public @Nullable SoundEvent slashSound() {
                return null;
            }
        },

        /**
         * Weapons that behave like 1.8, with no attack cooldown.
         * Crits when up-close.
         * Typically for knives.
         */
        LIGHT {

            @Override
            public boolean shouldCrit(final Player player, final Entity target) {
                final @Nullable AttributeInstance interactRange = player
                        .getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
                if (interactRange == null)
                    return false;
                final double range = interactRange.getValue() / 2.0;
                final double dist = target.getBoundingBox().distanceToSqr(player.getEyePosition());
                return !player.hasEffect(MobEffects.BLINDNESS)
                        && !player.isPassenger()
                        && target instanceof LivingEntity
                        && dist <= range * range;
            }

            @Override
            public @Nullable ParticleType<SlashParticleOptions> slashParticle() {
                return CC$ParticleTypes.LIGHT_SLASH.get();
            }

            @Override
            public @Nullable SoundEvent slashSound() {
                return CC$SoundEvents.LIGHT_SLASH.get();
            }
        },

        /**
         * Weapons that can parry projectiles with right-click, and do innate sweeping
         * damage.
         * Crits when the player's camera is moving fast.
         * Typically for swords.
         */
        BLADE {

            @Override
            public boolean shouldCrit(final Player player, final Entity target) {
                final CameraTracker cameraTracker = CC$Player_.get(player).cameraTracker;
                final Vec3 movement = player.getKnownMovement();
                final float yawRad = player.getYRot() * Mth.DEG_TO_RAD;
                final Vec3 right = new Vec3(-Mth.cos(yawRad), 0, -Mth.sin(yawRad));
                final Vec3 up = right.cross(player.getLookAngle());
                final double distance = Math
                        .max(player.getEyePosition().distanceTo(target.getBoundingBox().getCenter()), 0.5);
                final float movementYaw = (float) Math.toDegrees(movement.dot(right) / distance);
                final float movementPitch = (float) -Math.toDegrees(movement.dot(up) / distance);
                final Vec2 bladeMotion = cameraTracker.get().add(new Vec2(movementYaw, movementPitch));
                if (!player.level().isClientSide)
                    CriticalCombat.LOG.info("blade speed: {} deg/tick", bladeMotion.length());
                return player.getAttackStrengthScale(0.5f) > 0.9f
                        && !player.hasEffect(MobEffects.BLINDNESS)
                        && !player.isPassenger()
                        && target instanceof LivingEntity
                        && bladeMotion.length() > CC$Config.BLADE_CRIT_SPEED.get();
            }

            @Override
            public @Nullable ParticleType<SlashParticleOptions> slashParticle() {
                return null;
            }

            @Override
            public @Nullable SoundEvent slashSound() {
                return null;
            }
        },

        /**
         * Weapons that can be thrown with right-click.
         * Crits when the player is sprinting.
         * Typically for spear-like weapons.
         */
        POLEARM {

            private static final float MIN_JOUST_SPEED = 0.2f; // Slowest horse is 0.24f

            @Override
            public boolean shouldCrit(final Player player, final Entity target) {
                final boolean canJoust = player.getKnownMovement().length() >= MIN_JOUST_SPEED;
                if (player.isPassenger() && !canJoust)
                    return false; // Cannot crit while riding a mount unless moving fast
                return player.getAttackStrengthScale(0.5f) > 0.9f && !player.onClimbable() && !player.isInWater()
                        && !player.hasEffect(MobEffects.BLINDNESS) && target instanceof LivingEntity
                        && (player.isSprinting() || player.isPassenger());
            }

            @Override
            public @Nullable ParticleType<SlashParticleOptions> slashParticle() {
                return null;
            }

            @Override
            public @Nullable SoundEvent slashSound() {
                return null;
            }
        };

        public static final Codec<WeaponType> CODEC = StringRepresentable.fromEnum(WeaponType::values);

        public static final StreamCodec<ByteBuf, WeaponType> STREAM_CODEC = ByteBufCodecs.idMapper(
                ByIdMap.continuous(WeaponType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO),
                WeaponType::ordinal);

        /**
         * Handles logic for if the player should critically hit this frame.
         * 
         * @param player The player, assumed to be holding an item of this type
         * @param target The target entity being attacked
         * @return True if the player should crit this frame; false otherwise
         */
        public abstract boolean shouldCrit(final Player player, final Entity target);

        /**
         * @return The particle to show when slashing a weapon of this type
         */
        public abstract @Nullable ParticleType<SlashParticleOptions> slashParticle();

        /**
         * @return The sound to play when slashing a weapon of this type
         */
        public abstract @Nullable SoundEvent slashSound();

        /**
         * @return True if the given item is mapped to this WeaponType, false otherwise.
         */
        public boolean contains(final ItemLike item) {
            return WeaponTypes.get(item) == this;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
