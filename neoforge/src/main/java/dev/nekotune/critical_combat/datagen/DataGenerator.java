package dev.nekotune.critical_combat.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import dev.nekotune.critical_combat.datagen.assets.CC$ItemModelProvider;
import dev.nekotune.critical_combat.datagen.data.CC$RecipeProviders;
import dev.nekotune.critical_combat.datagen.data.CC$TagProviders;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public record DataGenerator(PackOutput packOutput,
        CompletableFuture<HolderLookup.Provider> lookupProvider,
        @Nullable ExistingFileHelper existingFileHelper) {

    public List<DataProvider> generateServerData() {
        final List<DataProvider> providers = new ArrayList<>();
        providers.addAll(CC$TagProviders.generate(this));
        providers.addAll(CC$RecipeProviders.generate(this));
        return providers;
    }

    public List<DataProvider> generateClientResources() {
        final List<DataProvider> providers = new ArrayList<>();
        providers.add(new CC$ItemModelProvider(this));
        return providers;
    }
}