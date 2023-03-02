package com.redpxnda.aegis.mixin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.redpxnda.aegis.registry.ModRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getAttributeModifiers", at = @At("TAIL"), cancellable = true)
    private void injectGetAttributes(EquipmentSlot equipmentSlot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir) {
        final Map<Attribute, Collection<AttributeModifier>> immutableAttributes = cir.getReturnValue().asMap();
        Map<Attribute, Collection<AttributeModifier>> attributes = new HashMap<>(immutableAttributes);
        Map<Attribute, Collection<AttributeModifier>> newAttributes = new HashMap<>(attributes);
        for (Map.Entry<Attribute, Collection<AttributeModifier>> entry : attributes.entrySet()) {
            Attribute att = entry.getKey();
            if (att.getDescriptionId().equals(Attributes.ARMOR.getDescriptionId())) {
                newAttributes.remove(att, entry.getValue());
                att = ModRegistries.MOD_ATTRIBUTES.get("overall_protection").get();
                newAttributes.put(att, entry.getValue());
            } else if (att.getDescriptionId().equals(Attributes.ARMOR_TOUGHNESS.getDescriptionId())) {
                newAttributes.remove(att, entry.getValue());
                att = ModRegistries.MOD_ATTRIBUTES.get("defense").get();
                newAttributes.put(att, entry.getValue());
            }
        }
        Multimap<Attribute, AttributeModifier> multimap = ArrayListMultimap.create();
        for (Map.Entry<Attribute, Collection<AttributeModifier>> entry : newAttributes.entrySet()) {
            multimap.putAll(entry.getKey(), entry.getValue());
        }
        cir.setReturnValue(multimap);
    }
}
