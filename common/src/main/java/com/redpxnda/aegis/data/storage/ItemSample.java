package com.redpxnda.aegis.data.storage;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemSample {
    private final List<Class<? extends Item>> sampleClasses;

    private ItemSample(List<Class<? extends Item>> sampleClasses) {
        this.sampleClasses = sampleClasses;
    }

    public static ItemSample fromJson(JsonElement element) {
        if (element == null || element.isJsonNull()) throw new JsonParseException("ItemSample cannot be null.");
        if (element.isJsonObject()) throw new JsonParseException("ItemSample cannot be a json object.");
        if (element.isJsonArray()) {
            List<Class<? extends Item>> samples = new ArrayList<>();
            for (JsonElement e : element.getAsJsonArray()) {
                if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString())
                    samples.add(getClassFromStr(e.getAsString()));
            }
            return new ItemSample(samples);
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) throw new JsonParseException("ItemSample must be a string of text.");
        return new ItemSample(List.of(getClassFromStr(element.getAsString())));
    }

    private static Class<? extends Item> getClassFromStr(String str) {
        ResourceLocation loc = ResourceLocation.tryParse(str);
        if (loc == null) throw new JsonParseException("ItemSample must be a valid resource location. (Eg. 'namespace:id')");
        Optional<Item> item = Registry.ITEM.getOptional(loc);
        if (item.isEmpty()) return Item.class;
        return item.get().getClass();
    }

    public boolean test(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        Class<? extends Item> clazz = stack.getItem().getClass();
        return this.getSampleClasses().contains(clazz);
    }



    public List<Class<? extends Item>> getSampleClasses() {
        return sampleClasses;
    }
}
