package com.redpxnda.aegis.network;

import com.redpxnda.aegis.client.ClientShieldData;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class SyncBlockingShieldDataPacket {
    private final double shield;

    public SyncBlockingShieldDataPacket(FriendlyByteBuf buf) {
        this.shield = buf.readDouble();
    }

    public SyncBlockingShieldDataPacket(double amt) {
        this.shield = amt;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(shield);
    }

    public void apply(Supplier<NetworkManager.PacketContext> supplier) {
        supplier.get().queue(() -> {
            ClientShieldData.setBlockingShield(shield);
        });

    }
}
