package com.redpxnda.aegis.network;

import com.redpxnda.aegis.implementation.RegistryObjects;
import com.redpxnda.aegis.implementation.ShieldDataManager;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class ClientShieldImpactPacket {
    private final double x;
    private final double y;
    private final double z;
    private final int target;
    private final double targetShield;
    private final double hitX;
    private final double hitY;
    private final double hitZ;

    public ClientShieldImpactPacket(FriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.target = buf.readInt();
        this.targetShield = buf.readDouble();
        this.hitX = buf.readDouble();
        this.hitY = buf.readDouble();
        this.hitZ = buf.readDouble();
    }

    public ClientShieldImpactPacket(double x, double y, double z, int targetId, double targetShield, double hitX, double hitY, double hitZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.target = targetId;
        this.targetShield = targetShield;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(target);
        buf.writeDouble(targetShield);
        buf.writeDouble(hitX);
        buf.writeDouble(hitY);
        buf.writeDouble(hitZ);
    }

    public void apply(Supplier<NetworkManager.PacketContext> supplier) {
        supplier.get().queue(() -> {
            ParticleOptions particleOptions = RegistryObjects.getImpactParticle();
            if (Minecraft.getInstance().player == null) return;
            Player player = Minecraft.getInstance().player;
            player.level.addParticle(particleOptions, x, y, z, 0, 0, 0);
            LivingEntity entity = (LivingEntity) player.level.getEntity(target);
            if (entity != null) {
                ShieldDataManager.setShield(entity, targetShield);
                ShieldDataManager.setShieldHitPos(entity, new Vec3(hitX, hitY, hitZ));
            }
            if (targetShield <= 0) player.level.playSound(player, x, y, z, SoundEvents.SHIELD_BREAK, SoundSource.BLOCKS, 1, 1);
            else player.level.playSound(player, x, y, z, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1, 1);
        });

    }
}
