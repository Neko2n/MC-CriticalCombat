package dev.nekotune.critical_combat.datagen;

import java.util.concurrent.CompletableFuture;

import dev.nekotune.critical_combat.CriticalCombat;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Data Generator for {@link CriticalCombat}
 */
@EventBusSubscriber
public final class CCDatagenManager {

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent event) {
        final PackOutput packOutput = event.getGenerator().getPackOutput();
        final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        final ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        final DataGenerator generator = new DataGenerator(packOutput, lookupProvider, existingFileHelper);
        if (event.includeServer())
            generator.generateServerData().forEach(event::addProvider);
        if (event.includeClient())
            generator.generateClientResources().forEach(event::addProvider);
    }

    private CCDatagenManager() {
    }
}
