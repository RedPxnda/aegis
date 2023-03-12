package com.redpxnda.aegis.fabric.util;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.world.entity.EquipmentSlot;

public class EnumModification implements Runnable {
    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

        String slotType = 'L' + remapper.mapClassName("intermediary", "net.minecraft.class_1304$class_1305") + ';';
        ClassTinkerers
                .enumBuilder("net.minecraft.world.entity.EquipmentSlot", slotType, int.class, int.class, String.class)
                .addEnum("AEGIS_SHIELD", EquipmentSlot.Type.HAND, 1, 5, "shield")
                .build();
    }
}
