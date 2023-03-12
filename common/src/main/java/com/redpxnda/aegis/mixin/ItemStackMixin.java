package com.redpxnda.aegis.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.datafixers.util.Either;
import com.redpxnda.aegis.data.listener.AttributeData;
import com.redpxnda.aegis.data.storage.ItemSample;
import com.redpxnda.aegis.registry.ModRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

//TODO: find solution for this bs
@Debug(export = true)
@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(
            method = "getAttributeModifiers",
            at = @At("RETURN"),
            cancellable = true
    )
    private void AEGIS_attributeOverrideModifiers(EquipmentSlot equipmentSlot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        Multimap<Attribute, AttributeModifier> attributes = HashMultimap.create(cir.getReturnValue());
        boolean firstDifferences = Multimaps.filterEntries(attributes, e -> !stack.getItem().getDefaultAttributeModifiers(equipmentSlot).containsEntry(e.getKey(), e.getValue())).isEmpty();
        boolean secondDifferences = Multimaps.filterEntries(stack.getItem().getDefaultAttributeModifiers(equipmentSlot), e -> !attributes.containsEntry(e.getKey(), e.getValue())).isEmpty();
        Either<Ingredient, ItemSample> either = null;
        for (Map.Entry<Either<Ingredient, ItemSample>, Map<?, ?>> entry : AttributeData.data.entrySet()) {
            if (entry.getKey().left().isPresent()) {
                Ingredient ing = entry.getKey().left().get();
                if (ing.test(stack)) {
                    either = entry.getKey();
                    break;
                }
            } else if (entry.getKey().right().isPresent()) {
                ItemSample samp = entry.getKey().right().get();
                if (samp.test(stack)) {
                    either = entry.getKey();
                    break;
                }
            }
        }
        if (either != null) {
            Map<?, ?> itemData = AttributeData.data.get(either);
            itemData.forEach((key, value) -> {
                ResourceLocation attribute;
                if (key instanceof String str && (attribute = ResourceLocation.tryParse(str)) != null && Registry.ATTRIBUTE.containsKey(attribute)) {
                    if (
                        value instanceof Map<?, ?> map &&
                        map.containsKey("operation") &&
                        map.containsKey("slot") &&
                        map.get("slot").equals(equipmentSlot.getName()) &&
                        ((firstDifferences && secondDifferences) || (map.containsKey("force") && map.get("force").equals(true)))
                    ) {
                        String operation = map.get("operation") instanceof String operationStr ? operationStr : null;
                        if (operation == null) return;
                        switch (operation) {
                            case "add", "addition", "plus" -> {
                                if (!map.containsKey("amount") && !map.containsKey("uuid")) return;
                                double amount = (double) map.get("amount");
                                attributes.put(
                                        Registry.ATTRIBUTE.get(attribute),
                                        new AttributeModifier(UUID.fromString((String) map.get("uuid")), "aegis_attribute_override", amount, AttributeModifier.Operation.ADDITION)
                                );
                            }
                            case "multiply", "multiply_base", "times", "times_base" -> {
                                if (!map.containsKey("amount") && !map.containsKey("uuid")) return;
                                double amount = (double) map.get("amount");
                                attributes.put(
                                        Registry.ATTRIBUTE.get(attribute),
                                        new AttributeModifier(UUID.fromString((String) map.get("uuid")), "aegis_attribute_override", amount, AttributeModifier.Operation.MULTIPLY_BASE)
                                );
                            }
                            case "multiply_total", "times_total" -> {
                                if (!map.containsKey("amount") && !map.containsKey("uuid")) return;
                                double amount = (double) map.get("amount");
                                attributes.put(
                                        Registry.ATTRIBUTE.get(attribute),
                                        new AttributeModifier(UUID.fromString((String) map.get("uuid")), "aegis_attribute_override", amount, AttributeModifier.Operation.MULTIPLY_TOTAL)
                                );
                            }
                            case "replace", "replacement" -> {
                                if (!map.containsKey("original") || !(map.get("original") instanceof String ogStr)) return;
                                ResourceLocation ogID = ResourceLocation.tryParse(ogStr);
                                if (ogID == null) return;
                                Attribute original = Registry.ATTRIBUTE.containsKey(ogID) ? Registry.ATTRIBUTE.get(ogID) : null;
                                if (original == null) return;
                                Multimap<Attribute, AttributeModifier> copiedAttributes = HashMultimap.create(attributes);
                                Attribute replacement = Registry.ATTRIBUTE.get(attribute);
                                for (AttributeModifier entry : copiedAttributes.get(original)) {
                                    attributes.remove(original, entry);
                                    if (map.containsKey("multiplier")) entry = new AttributeModifier(entry.getId(), entry.getName(), entry.getAmount()*(double)map.get("multiplier"), entry.getOperation());
                                    attributes.put(replacement, entry);
                                }
                            }
                        }
                    }
                }
            });
        }
        cir.setReturnValue(AEGIS_attributeModifierReplacement(attributes));
    }

    private Multimap<Attribute, AttributeModifier> AEGIS_attributeModifierReplacement(Multimap<Attribute, AttributeModifier> attributes) {
        Multimap<Attribute, AttributeModifier> copiedAttributes = HashMultimap.create(attributes);
        for (Map.Entry<Attribute, AttributeModifier> entry : attributes.entries()) {
            Attribute att = entry.getKey();
            if (att.getDescriptionId().equals(Attributes.ARMOR.getDescriptionId())) {
                copiedAttributes.remove(att, entry.getValue());
                att = ModRegistries.MOD_ATTRIBUTES.get("overall_protection").get();
                copiedAttributes.put(att, entry.getValue());
            } else if (att.getDescriptionId().equals(Attributes.ARMOR_TOUGHNESS.getDescriptionId())) {
                copiedAttributes.remove(att, entry.getValue());
                att = ModRegistries.MOD_ATTRIBUTES.get("defense").get();
                copiedAttributes.put(att, entry.getValue());
            }
        }
        return copiedAttributes;
    }
}
