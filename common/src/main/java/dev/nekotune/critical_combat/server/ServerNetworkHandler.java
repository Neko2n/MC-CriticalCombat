package dev.nekotune.critical_combat.server;

import java.util.WeakHashMap;

import javax.annotation.Nullable;

import dev.nekotune.critical_combat.platform.Platform;
import dev.nekotune.critical_combat.shared.CCPlayer_;
import dev.nekotune.critical_combat.shared.SwingEffects;
import dev.nekotune.critical_combat.shared.WeaponTypes;
import dev.nekotune.critical_combat.shared.SwingEffects.SwingFXPayload;
import dev.nekotune.critical_combat.shared.WeaponTypes.WeaponType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class ServerNetworkHandler {

    private static final RateLimiter swingFXRateLimiter = new RateLimiter(4);

    public static void registerPayloadHandlers() {
        Platform.get().network().register$C2S(SwingFXPayload.TYPE, SwingFXPayload.STREAM_CODEC,
                ServerNetworkHandler::handleSwingFXPayload);
    }

    private static void handleSwingFXPayload(final SwingFXPayload payload, final ServerPlayer sender) {
        swingFXRateLimiter.pcall(sender, () -> {
            final ItemStack weaponItem = CCPlayer_.get(sender).player.getWeaponItem();
            final @Nullable WeaponType heldWeaponType = WeaponTypes.get(weaponItem.getItem());
            if (heldWeaponType != null) {
                SwingEffects.play(sender, heldWeaponType);
            }
        });
    }

    private ServerNetworkHandler() {
    }

    private static final class RateLimiter {

        private final WeakHashMap<ServerPlayer, Integer> lastCallTicks = new WeakHashMap<>();
        private final int maxRateTicks;

        public RateLimiter(final int maxRateTicks) {
            this.maxRateTicks = maxRateTicks;
        }

        public boolean pcall(final ServerPlayer liable, final Runnable runnable) {
            final int lastCallTick = lastCallTicks.getOrDefault(liable, 0);
            final int now = liable.server.getTickCount();
            lastCallTicks.put(liable, now);
            if (now - lastCallTick < this.maxRateTicks)
                return false;
            runnable.run();
            return true;
        }
    }
}
