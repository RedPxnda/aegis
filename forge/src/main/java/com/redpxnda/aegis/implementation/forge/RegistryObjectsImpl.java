package com.redpxnda.aegis.implementation.forge;

import com.redpxnda.aegis.forge.AegisForge;
import net.minecraft.core.particles.ParticleOptions;

public class RegistryObjectsImpl {
    public static ParticleOptions getImpactParticle() {
        return AegisForge.ModRegistry.IMPACT_PARTICLE.get();
    }
}
