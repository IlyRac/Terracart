package com.ilyrac.terracart.network;

import com.ilyrac.terracart.Terracart;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record TerracartInputPayload(float forward, float strafe) implements CustomPacketPayload {

    public static final Type<TerracartInputPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Terracart.MOD_ID, "driver_input"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TerracartInputPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {
                        buf.writeFloat(pkt.forward());
                        buf.writeFloat(pkt.strafe());
                    },
                    buf -> new TerracartInputPayload(
                            buf.readFloat(),
                            buf.readFloat()
                    )
            );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(
                TerracartInputPayload.TYPE,
                TerracartInputPayload.STREAM_CODEC
        );

        ServerPlayNetworking.registerGlobalReceiver(
                TerracartInputPayload.TYPE,
                (payload, context) -> context.server().execute(() -> {
                    var player = context.player();

                    if (player.getVehicle() instanceof com.ilyrac.terracart.entity.TerracartEntity cart) {
                        cart.setDriverInput(payload.forward(), payload.strafe());
                    }
                })
        );
    }
}
