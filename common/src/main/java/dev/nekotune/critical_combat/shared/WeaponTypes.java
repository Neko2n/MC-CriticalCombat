package dev.nekotune.critical_combat.shared;

import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;

import dev.nekotune.critical_combat.CCConfig;
import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.client.ClientNetworkHandler;
import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.server.CriticalCombatServer;
import dev.nekotune.critical_combat.shared.CCPlayer_.CameraTracker;
import dev.nekotune.critical_combat.shared.definition.SlashParticleOptions;
import dev.nekotune.critical_combat.shared.registry.CCParticleTypes;
import dev.nekotune.critical_combat.shared.registry.CCSoundEvents;
import dev.nekotune.critical_combat.util.SoundOptions;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
        final MappingsBroadcastPayload payload = new MappingsBroadcastPayload(new IdentityHashMap<>(map));
        Platform.get().network().sendToClient(player, payload);
    }

    private WeaponTypes() {
    }

    /**
     * The payload for replicating WeaponType mappings between the client and the
     * server.
     * 
     * @param mappings Map of item registry IDs to their assigned WeaponTypes
     * @see ClientNetworkHandler
     */
    public static record MappingsBroadcastPayload(IdentityHashMap<Item, WeaponType> mappings) implements CustomPacketPayload {

        public static final Type<MappingsBroadcastPayload> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(CriticalCombat.MOD_ID, "weapon_types"));

        public static final StreamCodec<RegistryFriendlyByteBuf, MappingsBroadcastPayload> STREAM_CODEC = ByteBufCodecs
                .map(IdentityHashMap<Item, WeaponType>::new, ByteBufCodecs.registry(Registries.ITEM),
                        WeaponType.STREAM_CODEC)
                .map(MappingsBroadcastPayload::new, MappingsBroadcastPayload::mappings);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
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
        DEFAULT(new SwingEffects( // TODO default swing FX (downward slash, light swoosh, light crack on hit)
                null,
                new SoundOptions(CCSoundEvents.DEFAULT_ATTACK, 0.25f, 0.8f, 0.1f),
                null)) {

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
        HEAVY(new SwingEffects( // TODO heavy swing FX
                null,
                null,
                null)) {

            @Override
            public boolean shouldCrit(final Player player, final Entity target) {
                return false; // TODO heavy weapon crit logic
            }
        },

        /**
         * Weapons that behave like 1.8, with no attack cooldown.
         * Crits when up-close.
         * Typically for knives.
         */
        LIGHT(new SwingEffects(
                CCParticleTypes.LIGHT_SWING,
                new SoundOptions(CCSoundEvents.LIGHT_ATTACK, 0.25f, 1.4f, 0.1f),
                null)) { // TODO cut sound for light weapons

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
        },

        /**
         * Weapons that can parry projectiles with right-click, and do innate sweeping
         * damage.
         * Crits when the player's camera is moving fast.
         * Typically for swords.
         */
        BLADE(new SwingEffects(
                CCParticleTypes.BLADE_SWING,
                new SoundOptions(CCSoundEvents.BLADE_ATTACK, 0.25f, 1f, 0.1f),
                null)) {  // TODO cut sound for blades

            @Override
            public boolean shouldCrit(final Player player, final Entity target) {
                final CameraTracker cameraTracker = CCPlayer_.get(player).cameraTracker;
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
                        && bladeMotion.length() > CCConfig.BLADE_CRIT_SPEED.get();
            }
        },

        /**
         * Weapons that can be thrown with right-click.
         * Crits when the player is sprinting.
         * Typically for spear-like weapons.
         */
        POLEARM(new SwingEffects(
                CCParticleTypes.POLEARM_SWING,
                new SoundOptions(CCSoundEvents.POLEARM_ATTACK, 1f, 1f, 0.1f),
                new SoundOptions(CCSoundEvents.POLEARM_HIT, 1f, 1f, 0.1f))) {

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
        };

        public static final Codec<WeaponType> CODEC = StringRepresentable.fromEnum(WeaponType::values);

        public static final StreamCodec<ByteBuf, WeaponType> STREAM_CODEC = ByteBufCodecs.idMapper(
                ByIdMap.continuous(WeaponType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO),
                WeaponType::ordinal);

        public final SwingEffects swingEffects;

        private WeaponType(final SwingEffects swingEffects) {
            this.swingEffects = swingEffects;
        }

        /**
         * Handles logic for if the player should critically hit this frame.
         * 
         * @param player The player, assumed to be holding an item of this type
         * @param target The target entity being attacked
         * @return True if the player should crit this frame; false otherwise
         */
        public abstract boolean shouldCrit(final Player player, final Entity target);

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

        public static record SwingEffects(
                @Nullable Supplier<ParticleType<SlashParticleOptions>> slashParticle,
                @Nullable SoundOptions attackSound,
                @Nullable SoundOptions hitSound) {
        }
    }
}
