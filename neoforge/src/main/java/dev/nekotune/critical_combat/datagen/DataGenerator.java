package dev.nekotune.critical_combat.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import dev.nekotune.critical_combat.datagen.assets.CCItemModelProvider;
import dev.nekotune.critical_combat.datagen.data.CCRecipeProviders;
import dev.nekotune.critical_combat.datagen.data.CCTagProviders;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public record DataGenerator(PackOutput packOutput,
        CompletableFuture<HolderLookup.Provider> lookupProvider,
        @Nullable ExistingFileHelper existingFileHelper) {

    public List<DataProvider> generateServerData() {
        final List<DataProvider> providers = new ArrayList<>();
        providers.addAll(CCTagProviders.generate(this));
        providers.addAll(CCRecipeProviders.generate(this));
        return providers;
    }

    public List<DataProvider> generateClientResources() {
        final List<DataProvider> providers = new ArrayList<>();
        providers.add(new CCItemModelProvider(this));
        return providers;
    }
}