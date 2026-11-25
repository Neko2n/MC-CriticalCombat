package dev.nekotune.critical_combat.datagen.data;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.ParametersAreNonnullByDefault;

import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.datagen.DataGenerator;
import dev.nekotune.critical_combat.shared.registry.CC$Items;
import dev.nekotune.critical_combat.shared.registry.tags.CC$ItemTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

@ParametersAreNonnullByDefault
public final class CC$TagProviders {

    public static List<TagsProvider<?>> generate(final DataGenerator generator) {
        final var subProvider$Block = new CC$TagsProvider$Block(generator);
        final var subProvider$Item = new CC$TagsProvider$Item(generator, subProvider$Block.contentsGetter());
        return List.of(subProvider$Block, subProvider$Item);
    }

    protected static final class CC$TagsProvider$Block extends BlockTagsProvider {

        public CC$TagsProvider$Block(final DataGenerator generator) {
            super(generator.packOutput(), generator.lookupProvider(), CriticalCombat.MOD_ID,
                    generator.existingFileHelper());
        }

        @Override
        protected void addTags(final Provider provider) {
        }
    }

    protected static final class CC$TagsProvider$Item extends ItemTagsProvider {

        public CC$TagsProvider$Item(final DataGenerator generator, final CompletableFuture<TagLookup<Block>> blockTags) {
            super(generator.packOutput(), generator.lookupProvider(), blockTags, CriticalCombat.MOD_ID,
                    generator.existingFileHelper());
        }

        @Override
        protected void addTags(final Provider provider) {

            tag(CC$ItemTags.PIERCES_ARMOR)
                    .addTag(ItemTags.PICKAXES);

            tag(CC$ItemTags.TWO_HANDED)
                    .add(Items.MACE);

            tag(CC$ItemTags.UPWARDS_KNOCKBACK)
                    .addTag(ItemTags.SHOVELS);

            tag(ItemTags.SHARP_WEAPON_ENCHANTABLE)
                    .add(CC$Items.DAGGER.get());
        }
    }

    private CC$TagProviders() {
    }
}
