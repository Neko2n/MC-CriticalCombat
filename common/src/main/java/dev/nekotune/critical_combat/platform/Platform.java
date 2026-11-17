package dev.nekotune.critical_combat.platform;

import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.core.Registry;

/**
 * Class which handles cross-platform logic.
 * Defines an abstraction for cross-platform logic, {@link IPlatformHelper},
 * which is then implemented per-platform and passed through {@link #init}.
 */
public final class Platform {

    @Nullable
    private static IPlatformHelper INSTANCE = null;

    /**
     * Initializes the platform service with a loader-specific implementation of
     * {@link IPlatformHelper}
     * 
     * @param helper Implementation
     */
    public static void init(final IPlatformHelper helper) {
        Platform.INSTANCE = helper;
    }

    /**
     * @return The active {@link IPlatformHelper} instance.
     */
    public static IPlatformHelper get() {
        return Objects.requireNonNull(INSTANCE,
                "No service defined for IPlatformHelper; did you forget to call Platform.init?");
    }

    private Platform(final IPlatformHelper helper) {
    }

    public static interface IPlatformHelper {

        /**
         * Creates a new {@link RegistryProvider} to provide objects to a registry.
         * 
         * @param registry The registry to open for writing new objects to
         * @return The registry accessor, used to register new objects to the game
         */
        public abstract <T> RegistryProvider<T> openRegistry(final Supplier<Registry<T>> registry);

        /**
         * @return True if the mod with the given ID is loaded
         */
        public abstract boolean isModLoaded(final String modId);

        /**
         * @return True if the runtime is a developer environment
         */
        public abstract boolean isDevelopmentEnvironment();

        /**
         * @return The distribution side of the runtime environment
         */
        public abstract Environment getEnvironmentType();

        public static enum Environment {
            CLIENT,
            DEDICATED_SERVER;
        }
    }
}
