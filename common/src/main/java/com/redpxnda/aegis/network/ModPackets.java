package com.redpxnda.aegis.network;

import dev.architectury.networking.NetworkChannel;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static com.redpxnda.aegis.Aegis.MOD_ID;

public class ModPackets {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(MOD_ID, "main"));

    public static void init() {
        CHANNEL.register(SpawnImpactParticlePacket.class, SpawnImpactParticlePacket::encode, SpawnImpactParticlePacket::new, SpawnImpactParticlePacket::apply);
        CHANNEL.register(SyncShieldDataPacket.class, SyncShieldDataPacket::encode, SyncShieldDataPacket::new, SyncShieldDataPacket::apply);
    }
}
