package dev.nekotune.critical_combat.shared.definition;

import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;

import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.client.CriticalCombatClient;
import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.platform.Platform.IPlatformHelper.Environment;
import dev.nekotune.critical_combat.server.CriticalCombatServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

/**
 * Service containing mappings for items' WeaponTypes
 */
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
     * @throws IllegalStateException If called on the client-side
     * @see CriticalCombatServer#init()
     */
    public static void syncToClient(final ServerPlayer player) {
        if (Platform.get().getEnvironmentType() != Environment.DEDICATED_SERVER)
            throw new IllegalStateException("WeaponTypes#syncToClient can only be called on the server");
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

    public static enum WeaponType implements StringRepresentable {

        /**
         * Weapons that behave as default vanilla weapons.
         * Crits when falling.
         */
        DEFAULT,

        /**
         * Weapons that require charging up to swing and mine, and do innate AoE damage.
         * Crits when charged to 200%.
         * Typically for hammer-like weapons.
         */
        HEAVY,

        /**
         * Weapons that behave like 1.8, with no attack cooldown.
         * Crits when up-close.
         * Typically for knives.
         */
        LIGHT,

        /**
         * Weapons that can parry projectiles with right-click, and do innate sweeping
         * damage.
         * Crits when the player's camera is moving fast.
         * Typically for swords.
         */
        BLADE,

        /**
         * Weapons that can be thrown with right-click.
         * Crits when the player is sprinting.
         * Typically for spear-like weapons.
         */
        POLEARM;

        public static final Codec<WeaponType> CODEC = StringRepresentable.fromEnum(WeaponType::values);

        public static final StreamCodec<ByteBuf, WeaponType> STREAM_CODEC = ByteBufCodecs.idMapper(
                ByIdMap.continuous(WeaponType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO),
                WeaponType::ordinal);

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
