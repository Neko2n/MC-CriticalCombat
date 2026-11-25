package dev.nekotune.critical_combat.datagen.data;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.ParametersAreNonnullByDefault;

import dev.nekotune.critical_combat.CriticalCombat;
import dev.nekotune.critical_combat.datagen.DataGenerator;
import dev.nekotune.critical_combat.shared.registry.CCItems;
import dev.nekotune.critical_combat.shared.registry.tags.CCItemTags;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

@ParametersAreNonnullByDefault
public final class CCTagProviders {

    public static List<TagsProvider<?>> generate(final DataGenerator generator) {
        final var subProvider$Block = new CCTagsProvider$Block(generator);
        final var subProvider$Item = new CCTagsProvider$Item(generator, subProvider$Block.contentsGetter());
        return List.of(subProvider$Block, subProvider$Item);
    }

    protected static final class CCTagsProvider$Block extends BlockTagsProvider {

        public CCTagsProvider$Block(final DataGenerator generator) {
            super(generator.packOutput(), generator.lookupProvider(), CriticalCombat.MOD_ID,
                    generator.existingFileHelper());
        }

        @Override
        protected void addTags(final Provider provider) {
        }
    }

    protected static final class CCTagsProvider$Item extends ItemTagsProvider {

        public CCTagsProvider$Item(final DataGenerator generator, final CompletableFuture<TagLookup<Block>> blockTags) {
            super(generator.packOutput(), generator.lookupProvider(), blockTags, CriticalCombat.MOD_ID,
                    generator.existingFileHelper());
        }

        @Override
        protected void addTags(final Provider provider) {

            tag(CCItemTags.PIERCES_ARMOR)
                    .addTag(ItemTags.PICKAXES);

            tag(CCItemTags.TWO_HANDED)
                    .add(Items.MACE);

            tag(CCItemTags.UPWARDS_KNOCKBACK)
                    .addTag(ItemTags.SHOVELS);

            tag(ItemTags.SHARP_WEAPON_ENCHANTABLE)
                    .add(CCItems.DAGGER.get());
        }
    }

    private CCTagProviders() {
    }
}
