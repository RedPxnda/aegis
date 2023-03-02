package com.redpxnda.aegis.implementation;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

public class RegistryObjects {
    @ExpectPlatform
    public static ParticleOptions getImpactParticle() {
        return ParticleTypes.CRIT;
    }
}
