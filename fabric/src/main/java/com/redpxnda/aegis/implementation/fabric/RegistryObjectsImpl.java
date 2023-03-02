package com.redpxnda.aegis.implementation.fabric;

import com.redpxnda.aegis.fabric.AegisFabric;
import net.minecraft.core.particles.ParticleOptions;

public class RegistryObjectsImpl {
    public static ParticleOptions getImpactParticle() {
        return AegisFabric.ModRegistry.IMPACT_PARTICLE;
    }
}
