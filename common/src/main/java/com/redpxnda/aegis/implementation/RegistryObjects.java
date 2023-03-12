package com.redpxnda.aegis.implementation;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;

public class RegistryObjects {
    @ExpectPlatform
    public static ParticleOptions getImpactParticle() {
        return ParticleTypes.CRIT;
    }

    @ExpectPlatform
    public static boolean isItemRegistered(ResourceLocation item) {
        return false;
    }
}
