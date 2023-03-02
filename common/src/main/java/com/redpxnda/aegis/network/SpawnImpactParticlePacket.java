package com.redpxnda.aegis.network;

import com.redpxnda.aegis.implementation.RegistryObjects;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public class SpawnImpactParticlePacket {
    private final double x;
    private final double y;
    private final double z;

    public SpawnImpactParticlePacket(FriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    public SpawnImpactParticlePacket(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
    }

    public void apply(Supplier<NetworkManager.PacketContext> supplier) {
        supplier.get().queue(() -> {
            ParticleOptions particleOptions = RegistryObjects.getImpactParticle();
            if (Minecraft.getInstance().player == null) return;
            Player player = Minecraft.getInstance().player;
            player.level.addParticle(particleOptions, x, y, z, 0, 0, 0);
            //player.level.addParticle(new ShieldParticleOptions(RegistryObjects.getShieldParticle(), percent), x, y, z, 0, 0, 0);
            player.level.playSound(player, x, y, z, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1, 1);
        });

    }
}
