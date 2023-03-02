package com.redpxnda.aegis.fabric;

import com.redpxnda.aegis.Aegis;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;

import static com.redpxnda.aegis.Aegis.MOD_ID;

public class AegisFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Aegis.init();
        ModRegistry.register();
    }

    public static class ModRegistry {
        public static final SimpleParticleType IMPACT_PARTICLE = FabricParticleTypes.simple();

        public static void register() {
            Registry.register(Registry.PARTICLE_TYPE, new ResourceLocation(MOD_ID, "impact"), IMPACT_PARTICLE);
        }
    }
}
