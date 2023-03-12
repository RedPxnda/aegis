package com.redpxnda.aegis.forge.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.common.IExtensibleEnum;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EquipmentSlot.class)
public class EquipmentSlotExtender implements IExtensibleEnum {
    private static final EquipmentSlot AEGIS_SHIELD = create("AEGIS_SHIELD", EquipmentSlot.Type.HAND, 1, 5, "shield");

    private static EquipmentSlot create(String value, EquipmentSlot.Type type, int id, int flags, String name) {
        throw new IllegalStateException("Enum not extended");
    }
}
