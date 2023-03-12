package com.redpxnda.aegis.data.listener;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.redpxnda.aegis.data.storage.ItemSample;
import com.redpxnda.aegis.implementation.RegistryObjects;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AttributeData extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Map<Either<Ingredient, ItemSample>, Map<?, ?>> data = new HashMap<>();

    public static Gson GSON = new Gson();

    public AttributeData() {
        super(GSON, "attribute_overrides");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        data.clear();
        object.forEach((key, value) -> {
            if (value.isJsonObject())
                fireApplyCode(key, value);
            else if (value.isJsonArray()) {
                value.getAsJsonArray().forEach(v -> {
                    if (value.isJsonObject()) fireApplyCode(key, v);
                    else LOGGER.warn("Aegis: Error has occurred whilst attempting to parse attribute override \"" + key + "\". (Array contains non-json-object)");
                });
            } else LOGGER.warn("Aegis: Error has occurred whilst attempting to parse attribute override \"" + key + "\". (Not json object nor json array)");
        });
    }

    private static void fireApplyCode(ResourceLocation key, JsonElement value) {
        JsonObject json = value.getAsJsonObject();
        if (json.has("overrides")) {
            if (json.has("items")) {
                Ingredient items = Ingredient.fromJson(json.get("items"));
                if (!items.isEmpty()) addItemToData(Either.left(items), json);
                else LOGGER.warn("Aegis: Attribute override \"" + key + "\" contains an invalid item.");
            } else if (json.has("samples")) {
                ItemSample sample = ItemSample.fromJson(json.get("samples"));
                if (!sample.getSampleClasses().isEmpty()) addItemToData(Either.right(sample), json);
            }
        } else LOGGER.warn("Aegis: Attribute override \"" + key + "\" has no 'item' or 'overrides' section.");
    }

    private static void addItemToData(Either<Ingredient, ItemSample> item, JsonObject json) {
        if (!data.containsKey(item))
            data.put(item, GSON.fromJson(json.get("overrides"), Map.class));
        else {
            Map<?, ?> itemData = data.get(item);
            Map<?, ?> toMerge = GSON.fromJson(json.get("overrides"), Map.class);
            Map<?, ?> merged = Stream.concat(itemData.entrySet().stream(), toMerge.entrySet().stream())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (value1, value2) -> value2
                    ));
            data.put(item, merged);
        }
    }
}
