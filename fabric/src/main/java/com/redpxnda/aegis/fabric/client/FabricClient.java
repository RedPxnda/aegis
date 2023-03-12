package com.redpxnda.aegis.fabric.client;

import com.redpxnda.aegis.client.DefenseShieldOverlay;
import com.redpxnda.aegis.client.ShieldIcons;
import com.redpxnda.aegis.fabric.AegisFabric;
import com.redpxnda.aegis.registry.particle.ImpactParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.renderer.texture.TextureAtlas;

public class FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new FabricProtectionBarOverlay());
        HudRenderCallback.EVENT.register(new FabricDefenseShieldOverlay());
        HudRenderCallback.EVENT.register(new FabricBlockingShieldOverlay());

        ClientSpriteRegistryCallback.event(TextureAtlas.LOCATION_BLOCKS).register(((atlasTexture, registry) -> {
            for (ShieldIcons value : ShieldIcons.values()) {
                registry.register(value.getLocation(true));
                registry.register(value.getEmptyLocation(true));
                registry.register(value.getHitLocation(true));
                registry.register(value.getBreakLocationLeft(true));
                registry.register(value.getBreakLocationRight(true));
            }
        }));

        ParticleFactoryRegistry.getInstance().register(AegisFabric.ModRegistry.IMPACT_PARTICLE, ImpactParticle.Factory::new);
    }
}
