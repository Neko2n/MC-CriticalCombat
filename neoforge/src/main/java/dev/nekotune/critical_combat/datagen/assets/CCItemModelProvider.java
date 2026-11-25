package dev.nekotune.critical_combat.datagen.assets;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.datagen.DataGenerator;
import dev.nekotune.critical_combat.shared.definition.DaggerItem;
import dev.nekotune.critical_combat.shared.registry.CCItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/**
 * Automatically defines models for items.
 */
@ParametersAreNonnullByDefault
public final class CCItemModelProvider extends ItemModelProvider {

    /**
     * Keeps track of which items have already been manually given an
     * item model.
     */
    private final Set<ResourceLocation> registered = new HashSet<>();

    public CCItemModelProvider(final DataGenerator generator) {
        super(generator.packOutput(), CriticalCombat.MOD_ID, generator.existingFileHelper());
    }

    @Override
    public ItemModelBuilder getBuilder(final String path) {
        registered.add(path.contains(":")
                ? ResourceLocation.parse(path)
                : ResourceLocation.fromNamespaceAndPath(modid, path));
        return super.getBuilder(path);
    }

    @Override
    protected void registerModels() {

        // Any items which didn't get an explicit model are given a generic flat model.
        // Block items are skipped, as they're given models by BlockStateProvider.
        final Stream<Item> items = CCItems.ITEMS.entries().stream().map(Supplier::get);
        items.forEach((final Item item) -> {
            if (item instanceof BlockItem)
                return;
            if (registered.contains(BuiltInRegistries.ITEM.getKey(item)))
                return;

            // Hand-held items (tools, weapons, etc.)
            if (item instanceof DaggerItem) {
                handheldItem(item);
                return;
            }

            // All other items
            basicItem(item);
        });
    }

    @Override
    public ItemModelBuilder handheldItem(final Item item) {
        return model(item, "item/handheld");
    }

    @Override
    public ItemModelBuilder basicItem(final Item item) {
        return model(item, "item/generated");
    }

    private ItemModelBuilder model(final Item item, final String modelPath) {
        ResourceLocation location = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
        final String texturePath = getTexturePath(item);
        return getBuilder(location.toString())
                .parent(new ModelFile.UncheckedModelFile(modelPath))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(
                        location.getNamespace(), texturePath));
    }

    private static String getTexturePath(final Item item) {
        ResourceLocation modelLocation = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
        String modifier = "";
        if (item instanceof ArmorItem) {
            modifier = "armor/";
        } else if (item instanceof TieredItem || item instanceof DaggerItem) {
            modifier = "tool/";
        } else {
            modifier = "material/";
        }
        return "item/" + modifier + modelLocation.getPath();
    }
}
