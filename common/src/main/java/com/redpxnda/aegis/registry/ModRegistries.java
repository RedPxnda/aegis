package com.redpxnda.aegis.registry;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.redpxnda.aegis.Aegis.MOD_ID;

public class ModRegistries {
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));

    public static Registrar<Attribute> attributes = REGISTRIES.get().get(Registry.ATTRIBUTE_REGISTRY);

    public static Map<String, RegistrySupplier<Attribute>> MOD_ATTRIBUTES = Util.make(new HashMap<>(), map -> {
        registerAttribute("overall_protection", 0, 0, 10240, true, map);
        registerAttribute("frontal_protection", 0, -1024, 1024, true, map);
        registerAttribute("sidewards_protection", 0, -1024, 1024, true, map);
        registerAttribute("rearwards_protection", 0, -1024, 1024, true, map);
        registerAttribute("defense", 0, -1024, 1024, true, map);
    });

    public static void init() {
        Object classLoading = MOD_ATTRIBUTES;
    }

    private static void registerAttribute(String id, double defaultValue, double minValue, double maxValue, boolean syncable, Map<String, RegistrySupplier<Attribute>> map) {
        RegistrySupplier<Attribute> attribute = attributes.register(new ResourceLocation(MOD_ID, id), () ->
                new RangedAttribute(
                        "attribute.aegis." + id,
                        defaultValue,
                        minValue,
                        maxValue)
                        .setSyncable(syncable)
        );
        map.put(id, attribute);
    }
}
