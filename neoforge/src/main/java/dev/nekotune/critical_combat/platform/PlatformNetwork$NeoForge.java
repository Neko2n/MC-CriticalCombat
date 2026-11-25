package dev.nekotune.critical_combat.platform;

import java.util.LinkedList;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.base.Suppliers;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class PlatformNetwork$NeoForge implements PlatformNetwork {

    private static final Supplier<PlatformNetwork$NeoForge> INSTANCE = Suppliers.memoize(PlatformNetwork$NeoForge::new);

    public static PlatformNetwork$NeoForge get() {
        return INSTANCE.get();
    }

    private static final LinkedList<Runnable> deferred = new LinkedList<>();
    private static volatile @Nullable PayloadRegistrar registrar = null;

    @Override
    public void sendToClient(final ServerPlayer player, final CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public void sendToServer(final CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    @Override
    public void sendToClientsTrackingEntity(final Entity entity, final CustomPacketPayload payload) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, payload);
    }

    @Override
    public <T extends CustomPacketPayload> void register$S2C(final Type<T> type,
            final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            final Consumer<T> handler) {
        if (registrar == null) {
            deferred.push(() -> this.register$S2C(type, codec, handler));
            return;
        }
        registrar.playToClient(type, codec,
                (payload, context) -> handler.accept(payload));
    }

    @Override
    public <T extends CustomPacketPayload> void register$C2S(final Type<T> type,
            final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            final BiConsumer<T, ServerPlayer> handler) {
        if (registrar == null) {
            deferred.push(() -> this.register$C2S(type, codec, handler));
            return;
        }
        registrar.playToServer(type, codec,
                (payload, context) -> handler.accept(payload, (ServerPlayer) context.player()));
    }

    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        registrar = event.registrar("1");
        deferred.forEach(Runnable::run);
    }

    public static record PayloadHandler<T extends CustomPacketPayload>(Type<T> payload,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec, Consumer<T> handler,
            Set<Dist> registerOn) {
    }
}
