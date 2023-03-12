package com.redpxnda.aegis.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.function.Supplier;

public class PlaySoundPacket {
    private final double x;
    private final double y;
    private final double z;
    private final String sound;
    private final String source;
    private final float pitch;
    private final float volume;

    public PlaySoundPacket(FriendlyByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        sound = buf.readUtf();
        source = buf.readUtf();
        pitch = buf.readFloat();
        volume = buf.readFloat();
    }

    public PlaySoundPacket(double x, double y, double z, String sound, String source, float pitch, float volume) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.sound = sound;
        this.source = source;
        this.pitch = pitch;
        this.volume = volume;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeUtf(sound);
        buf.writeUtf(source);
        buf.writeFloat(pitch);
        buf.writeFloat(volume);
    }

    public void apply(Supplier<NetworkManager.PacketContext> supplier) {
        supplier.get().queue(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            ResourceLocation location = ResourceLocation.tryParse(sound);
            if (level != null && location != null) {
                SoundEvent soundEvent = Registry.SOUND_EVENT.get(location);
                if (soundEvent != null)
                    level.playLocalSound(x, y, z, soundEvent, SoundSource.valueOf(source), pitch, volume, false);
            }
        });
    }
}
