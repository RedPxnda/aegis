package com.redpxnda.aegis.implementation.fabric;

import com.redpxnda.aegis.fabric.AegisFabric;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;

public class RegistryObjectsImpl {
    public static ParticleOptions getImpactParticle() {
        return AegisFabric.ModRegistry.IMPACT_PARTICLE;
    }

    public static boolean isItemRegistered(ResourceLocation item) {
        return Registry.ITEM.containsKey(item);
    }
}
