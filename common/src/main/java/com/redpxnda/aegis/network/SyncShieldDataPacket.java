package com.redpxnda.aegis.network;

import com.redpxnda.aegis.client.ClientShieldData;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class SyncShieldDataPacket {
    private final double shield;
    private final int lastHurt;

    public SyncShieldDataPacket(FriendlyByteBuf buf) {
        this.shield = buf.readDouble();
        this.lastHurt = buf.readInt();
    }

    public SyncShieldDataPacket(double amt, int lastHurt) {
        this.shield = amt;
        this.lastHurt = lastHurt;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(shield);
        buf.writeInt(lastHurt);
    }

    public void apply(Supplier<NetworkManager.PacketContext> supplier) {
        supplier.get().queue(() -> {
            ClientShieldData.setPersonalShield(shield);
            ClientShieldData.setLastHurt(lastHurt);
        });

    }
}
