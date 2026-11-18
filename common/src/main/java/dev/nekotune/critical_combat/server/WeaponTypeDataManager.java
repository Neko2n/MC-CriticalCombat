package dev.nekotune.critical_combat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Suppliers;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.shared.ItemUtils;
import dev.nekotune.critical_combat.shared.definition.WeaponTypes;
import dev.nekotune.critical_combat.shared.definition.WeaponTypes.WeaponType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;

/**
 * Manages loading weapon types mappings data for {@link CriticalCombat}
 */
@ParametersAreNonnullByDefault
public final class WeaponTypeDataManager extends SimplePreparableReloadListener<Map<Item, WeaponType>> {

    private static final Supplier<WeaponTypeDataManager> INSTANCE = Suppliers
            .memoize(() -> new WeaponTypeDataManager());

    public static WeaponTypeDataManager get() {
        return INSTANCE.get();
    }

    private static final ResourceLocation FILE = ResourceLocation
            .fromNamespaceAndPath(CriticalCombat.MOD_ID, "weapon_overrides.json");

    private static Map<Item, WeaponType> defaultMappings() {
        final HashMap<Item, WeaponType> mappings = new HashMap<>();
        for (final Item item : BuiltInRegistries.ITEM) {
            final @Nullable WeaponType type = defaultType(item);
            if (type != null) {
                mappings.put(item, type);
            }
        }
        return Map.copyOf(mappings);
    }

    private static @Nullable WeaponType defaultType(final Item item) {
        // BLADE to weapons extending from SwordItem,
        // POLEARM to weapons extending from SpearItem or TridentItem,
        // LIGHT to weapons extending from KnifeItem,
        // HEAVY to weapons extending from HammerItem or MaceItem,
        // POLEARM to weapons extending from ProjectileItem,
        // LIGHT to weapons with >= 2.0 attack speed,
        // HEAVY to weapons with <= 0.6 attack speed,
        // DEFAULT to all other items tagged as #c:weapon or extending from WeaponItem

        if (item instanceof SwordItem)
            return WeaponType.BLADE;
        if (item instanceof TridentItem /* || item instanceof SpearItem */)
            return WeaponType.POLEARM;
        if (item instanceof MaceItem /* || item instanceof HammerItem */)
            return WeaponType.HEAVY;
        if (item instanceof ProjectileItem)
            return WeaponType.POLEARM;

        if (ItemUtils.getAttribute(Attributes.ATTACK_DAMAGE.value(), item) > 0) {
            final double attackSpeed = ItemUtils.getAttribute(Attributes.ATTACK_SPEED.value(), item);
            if (attackSpeed >= 2.0f)
                return WeaponType.LIGHT;
            if (attackSpeed <= 0.6f)
                return WeaponType.HEAVY;
            return WeaponType.DEFAULT;
        }
        return null;
    }

    private boolean hasErrors = false;

    private WeaponTypeDataManager() {
    }

    /**
     * @return True if there were errors loading WeaponType mappings from JSON data
     */
    public boolean hadErrorsLoading() {
        return this.hasErrors;
    }

    @Override
    protected Map<Item, WeaponType> prepare(final ResourceManager manager, final ProfilerFiller profiler) {
        this.hasErrors = false;
        final Map<Item, WeaponType> result = new IdentityHashMap<>();
        for (final Resource resource : manager.getResourceStack(FILE)) {
            try (BufferedReader reader = resource.openAsReader()) {
                final JsonElement json = JsonParser.parseReader(reader);
                final WeaponTypesData data = WeaponTypesData.CODEC.parse(JsonOps.INSTANCE, json)
                        .getOrThrow(JsonParseException::new);
                if (data.replace())
                    result.clear();
                result.putAll(data.values());
            } catch (IOException | RuntimeException e) {
                CriticalCombat.LOG.error("Failed to load {} from pack {}", FILE, resource.sourcePackId(), e);
                this.hasErrors = true;
            }
        }
        return result;
    }

    @Override
    protected void apply(final Map<Item, WeaponType> prepared, final ResourceManager manager,
            final ProfilerFiller profiler) {
        // TODO config setting to disable default mappings
        final HashMap<Item, WeaponType> mappings = new HashMap<>(defaultMappings());
        mappings.putAll(prepared);
        WeaponTypes.setMappings(mappings);
    }

    /**
     * Data structure for the {@link WeaponTypeDataManager#FILE WeaponType
     * overrides JSON file}
     */
    private static record WeaponTypesData(boolean replace, Map<Item, WeaponType> values) {

        private static final Codec<Map<Item, WeaponType>> VALUES_CODEC = Codec
                .unboundedMap(ResourceLocation.CODEC, WeaponType.CODEC)
                .xmap(WeaponTypesData::resolve, WeaponTypesData::unresolve);

        public static final Codec<WeaponTypesData> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.BOOL.optionalFieldOf("replace", false).forGetter(WeaponTypesData::replace),
                VALUES_CODEC.fieldOf("values").forGetter(WeaponTypesData::values))
                .apply(i, WeaponTypesData::new));

        private static Map<Item, WeaponType> resolve(final Map<ResourceLocation, WeaponType> raw) {
            final Map<Item, WeaponType> resolved = new IdentityHashMap<>();
            raw.forEach((id, type) -> BuiltInRegistries.ITEM.getOptional(id).ifPresentOrElse(
                    item -> resolved.put(item, type),
                    () -> {
                        if (Platform.get().isModLoaded(id.getNamespace())) {
                            CriticalCombat.LOG.warn("No item found with ID \"{}\"; skipping", id);
                        }
                    }));
            return resolved;
        }

        private static Map<ResourceLocation, WeaponType> unresolve(final Map<Item, WeaponType> values) {
            final Map<ResourceLocation, WeaponType> raw = new LinkedHashMap<>();
            values.forEach((item, type) -> raw.put(BuiltInRegistries.ITEM.getKey(item), type));
            return raw;
        }
    }
}
