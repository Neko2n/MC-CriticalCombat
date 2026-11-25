package dev.nekotune.critical_combat.datagen.data;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import dev.nekotune.critical_combat.datagen.DataGenerator;
import dev.nekotune.critical_combat.shared.registry.CC$Items;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

@ParametersAreNonnullByDefault
public final class CC$RecipeProviders {

    public static List<RecipeProvider> generate(final DataGenerator generator) {
        final var subProvider$crafting = new CC$RecipeProviders$Crafting(generator);
        return List.of(subProvider$crafting);
    }

    public static final class CC$RecipeProviders$Crafting extends RecipeProvider implements IConditionBuilder {

        public CC$RecipeProviders$Crafting(final DataGenerator generator) {
            super(generator.packOutput(), generator.lookupProvider());
        }

        @Override
        protected void buildRecipes(final RecipeOutput output) {
            final ItemLike blackShard = CC$Items.BLACK_SHARD.get();
            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, CC$Items.DAGGER.get())
                    .define('#', blackShard)
                    .define('I', Items.BLAZE_ROD)
                    .pattern("#")
                    .pattern("I")
                    .unlockedBy(getHasName(blackShard), has(blackShard))
                    .save(output);
        }
    }

    private CC$RecipeProviders() {
    }
}
