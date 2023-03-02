package com.redpxnda.aegis.fabric.client;

import com.redpxnda.aegis.client.DefenseShieldOverlay;
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

        ClientSpriteRegistryCallback.event(TextureAtlas.LOCATION_BLOCKS).register(((atlasTexture, registry) -> {
            registry.register(DefenseShieldOverlay.SHIELD_ICON_SPRITE);
            registry.register(DefenseShieldOverlay.EMPTY_SHIELD_ICON_SPRITE);
        }));

        ParticleFactoryRegistry.getInstance().register(AegisFabric.ModRegistry.IMPACT_PARTICLE, ImpactParticle.Factory::new);
    }
}
