package com.redpxnda.aegis.network;

import com.redpxnda.aegis.client.ClientShieldData;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class SyncShieldDataPacket {
    private final double shield;

    public SyncShieldDataPacket(FriendlyByteBuf buf) {
        this.shield = buf.readDouble();
    }

    public SyncShieldDataPacket(double amt) {
        this.shield = amt;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(shield);
    }

    public void apply(Supplier<NetworkManager.PacketContext> supplier) {
        supplier.get().queue(() -> {
            ClientShieldData.setShield(shield);
        });

    }
}
