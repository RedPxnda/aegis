package com.redpxnda.aegis.implementation.forge;

import com.redpxnda.aegis.forge.AegisForge;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryObjectsImpl {
    public static ParticleOptions getImpactParticle() {
        return AegisForge.ModRegistry.IMPACT_PARTICLE.get();
    }

    public static boolean isItemRegistered(ResourceLocation item) {
        return ForgeRegistries.ITEMS.containsKey(item);
    }
}
