package com.redpxnda.aegis.implementation.fabric;

import com.redpxnda.aegis.fabric.shield.IEntityDataSaver;
import com.redpxnda.aegis.fabric.shield.ShieldData;
import com.redpxnda.aegis.implementation.ShieldDataManager;
import com.redpxnda.aegis.registry.ModRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ShieldDataManagerImpl {
    public static double getShield(LivingEntity player) {
        IEntityDataSaver playerData = (IEntityDataSaver) player;
        return ShieldData.getShield(playerData);
    }

    public static void setShield(LivingEntity player, double amount) {
        IEntityDataSaver playerData = (IEntityDataSaver) player;
        ShieldData.setShield(playerData, amount);
    }

    public static void incrementShield(LivingEntity player, double amount) {
        IEntityDataSaver playerData = (IEntityDataSaver) player;
        if (ShieldData.getShield(playerData)+amount <= player.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("defense").get()))
            ShieldData.incrementShield(playerData, amount);
        else ShieldData.setShield(playerData, player.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("defense").get()));
    }

    public static void decrementShield(LivingEntity player, double amount) {
        IEntityDataSaver playerData = (IEntityDataSaver) player;
        if (ShieldData.getShield(playerData)-amount >= 0)
            ShieldData.decrementShield(playerData, amount);
        else ShieldData.setShield(playerData, 0);
    }

    public static int getLastHurtTime(LivingEntity player) {
        IEntityDataSaver playerData = (IEntityDataSaver) player;
        return ShieldData.getHurtTime(playerData);
    }

    public static void resetLastHurtTime(LivingEntity player) {
        IEntityDataSaver playerData = (IEntityDataSaver) player;
        ShieldData.resetLastHurtTime(playerData);
    }

    public static void increaseLastHurtTime(LivingEntity player) {
        IEntityDataSaver playerData = (IEntityDataSaver) player;
        ShieldData.increaseLastHurtTime(playerData);
    }

    public static Vec3 getShieldHitPos(LivingEntity player) {
        IEntityDataSaver playerData = (IEntityDataSaver) player;
        return ShieldData.getHitPos(playerData);
    }

    public static void setShieldHitPos(LivingEntity player, Vec3 vec) {
        IEntityDataSaver playerData = (IEntityDataSaver) player;
        ShieldData.setHitPos(playerData, vec);
    }
}
