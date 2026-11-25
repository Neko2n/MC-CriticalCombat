package dev.nekotune.critical_combat.platform;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import dev.nekotune.javautils.types.event.Event;
import dev.nekotune.javautils.types.event.IEventHook;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class PlatformNetwork$Fabric implements PlatformNetwork {

    private static final Supplier<PlatformNetwork$Fabric> INSTANCE = Suppliers.memoize(PlatformNetwork$Fabric::new);

    public static PlatformNetwork$Fabric get() {
        return INSTANCE.get();
    }

    private final Event<PayloadHandler<?>> payloadRegistered = new Event<>();
    public final IEventHook<PayloadHandler<?>> onPayloadRegistered = payloadRegistered;

    @Override
    public void sendToClient(final ServerPlayer player, final CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void sendToServer(final CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    @Override
    public void sendToClientsTrackingEntity(final Entity entity, final CustomPacketPayload payload) {
        for (final ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public <T extends CustomPacketPayload> void register$S2C(final Type<T> payload,
            final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            final Consumer<T> handler) {
        PayloadTypeRegistry.playS2C().register(payload, codec);
        this.payloadRegistered.post(new PayloadHandler<>(payload, handler));
    }

    @Override
    public <T extends CustomPacketPayload> void register$C2S(final Type<T> type,
            final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            final BiConsumer<T, ServerPlayer> handler) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type,
                (payload, context) -> handler.accept(payload, context.player()));
    }

    public static record PayloadHandler<T extends CustomPacketPayload>(Type<T> payload, Consumer<T> handler) {
    }
}
