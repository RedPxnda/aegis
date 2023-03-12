package com.redpxnda.aegis.network;

import dev.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

import static com.redpxnda.aegis.Aegis.MOD_ID;

public class ModPackets {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(MOD_ID, "main"));

    public static void init() {
        CHANNEL.register(ClientShieldImpactPacket.class, ClientShieldImpactPacket::encode, ClientShieldImpactPacket::new, ClientShieldImpactPacket::apply);
        CHANNEL.register(SyncShieldDataPacket.class, SyncShieldDataPacket::encode, SyncShieldDataPacket::new, SyncShieldDataPacket::apply);
        CHANNEL.register(SyncBlockingShieldDataPacket.class, SyncBlockingShieldDataPacket::encode, SyncBlockingShieldDataPacket::new, SyncBlockingShieldDataPacket::apply);
        CHANNEL.register(PlaySoundPacket.class, PlaySoundPacket::encode, PlaySoundPacket::new, PlaySoundPacket::apply);
    }
}
