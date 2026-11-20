package dev.nekotune.critical_combat.platform.client;

import java.util.Objects;

import javax.annotation.Nullable;

/**
 * Class which handles cross-platform logic.
 * Only exists on the client.
 * Defines an abstraction for cross-platform logic, {@link IClientPlatformHelper},
 * which is then implemented per-platform and passed through {@link #init}.
 */
public class ClientPlatform {

    @Nullable
    private static IClientPlatformHelper INSTANCE = null;

    /**
     * Initializes the platform service with a loader-specific implementation of
     * {@link IClientPlatformHelper}
     * 
     * @param helper Implementation
     */
    public static void init(final IClientPlatformHelper helper) {
        INSTANCE = helper;
    }

    /**
     * @return The active {@link IClientPlatformHelper} instance.
     */
    public static IClientPlatformHelper get() {
        return Objects.requireNonNull(INSTANCE,
                "No service defined for IPlatformHelper; did you forget to call Platform.init?");
    }

    private ClientPlatform(final IClientPlatformHelper helper) {
    }
    
    public static interface IClientPlatformHelper {
        
        /**
         * @return Platform-dependent event hooks
         */
        public abstract ClientPlatformEvents events();
    }
}
