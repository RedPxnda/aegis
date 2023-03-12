package com.redpxnda.aegis.implementation;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ShieldDataManager {
    @ExpectPlatform
    public static double getShield(LivingEntity player) {
        return 0;
    }

    @ExpectPlatform
    public static void setShield(LivingEntity player, double amount) {

    }

    @ExpectPlatform
    public static void incrementShield(LivingEntity player, double amount) {

    }

    @ExpectPlatform
    public static void decrementShield(LivingEntity player, double amount) {

    }

    @ExpectPlatform
    public static int getLastHurtTime(LivingEntity player) {
        return -1;
    }

    @ExpectPlatform
    public static void resetLastHurtTime(LivingEntity player) {

    }

    @ExpectPlatform
    public static void maximizeLastHurtTime(LivingEntity player) {

    }

    @ExpectPlatform
    public static void increaseLastHurtTime(LivingEntity player) {

    }

    @ExpectPlatform
    public static Vec3 getShieldHitPos(LivingEntity player) {
        return null;
    }

    @ExpectPlatform
    public static void setShieldHitPos(LivingEntity player, Vec3 vec) {

    }

    @ExpectPlatform
    public static double getBlockShield(LivingEntity player) {
        return -1;
    }

    @ExpectPlatform
    public static void setBlockShield(LivingEntity player, double amount) {

    }
}
