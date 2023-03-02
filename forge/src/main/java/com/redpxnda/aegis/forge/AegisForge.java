package com.redpxnda.aegis.forge;

import com.redpxnda.aegis.Aegis;
import com.redpxnda.aegis.client.DefenseShieldOverlay;
import com.redpxnda.aegis.forge.client.ForgeDefenseShieldOverlay;
import com.redpxnda.aegis.forge.client.ForgeProtectionBarOverlay;
import com.redpxnda.aegis.forge.shield.EntityShield;
import com.redpxnda.aegis.forge.shield.EntityShieldProvider;
import com.redpxnda.aegis.registry.particle.ImpactParticle;
import dev.architectury.platform.forge.EventBuses;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.redpxnda.aegis.Aegis.MOD_ID;

@Mod(MOD_ID)
public class AegisForge {
    public AegisForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModRegistry.register(modEventBus);

        Aegis.init();
    }

    public static class ModRegistry {
        public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = net.minecraftforge.registries.DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);
        public static final RegistryObject<SimpleParticleType> IMPACT_PARTICLE = PARTICLE_TYPES.register("impact", () -> new SimpleParticleType(true));

        public static void register(IEventBus bus) {
            PARTICLE_TYPES.register(bus);
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class ModEvents {
        @SubscribeEvent
        public static void onAttachCaps(AttachCapabilitiesEvent<Entity> event) {
            if (!(event.getObject() instanceof LivingEntity)) return;
            if (!event.getObject().getCapability(EntityShieldProvider.ENTITY_SHIELD).isPresent()) {
                event.addCapability(new ResourceLocation(MOD_ID, "properties"), new EntityShieldProvider());
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            if (!event.isWasDeath()) return;
            event.getOriginal().getCapability(EntityShieldProvider.ENTITY_SHIELD).ifPresent((old) -> {
                event.getOriginal().getCapability(EntityShieldProvider.ENTITY_SHIELD).ifPresent((current) -> {
                    current.copyFrom(old);
                });
            });
        }

        @SubscribeEvent
        public static void onRegisterCaps(RegisterCapabilitiesEvent event) {
            event.register(EntityShield.class);
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            event.register(ModRegistry.IMPACT_PARTICLE.get(), ImpactParticle.Factory::new);
        }
    }

    public static class ClientEvents {
        @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
        public static class ModBus {
            @SubscribeEvent
            public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
                event.registerAbove(new ResourceLocation("minecraft", "player_health"), "protection", ForgeProtectionBarOverlay.HUD_PROTECTION);
                event.registerBelowAll("defense_shield", ForgeDefenseShieldOverlay.HUD_SHIELD);
            }

            @SubscribeEvent
            public static void onTextureStitch(TextureStitchEvent.Pre event) {
                if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) return;
                event.addSprite(DefenseShieldOverlay.SHIELD_ICON_SPRITE);
                event.addSprite(DefenseShieldOverlay.EMPTY_SHIELD_ICON_SPRITE);
            }
        }
        @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
        public static class ForgeBus {
            @SubscribeEvent
            public static void onArmorBarRender(RenderGuiOverlayEvent.Pre event) {
                if (!event.getOverlay().id().equals(VanillaGuiOverlay.ARMOR_LEVEL.id())) return;
                event.setCanceled(true);
            }
        }
    }
}
