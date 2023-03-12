package com.redpxnda.aegis.client;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class ClientShieldData {
    public static Map<LivingEntity, Integer> entityTimers = new HashMap<>();
    public static Map<LivingEntity, Entity> entityAttackers = new HashMap<>();
    private static double shield;
    private static double lastHurt;
    private static double blockingShield;
    private static double maxBlockingShield;

    public static void setPersonalShield(double amt) {
        shield = amt;
    }

    public static double getPersonalShield() {
        return shield;
    }

    public static void setLastHurt(double amt) {
        lastHurt = amt;
    }

    public static double getLastHurt() {
        return lastHurt;
    }

    public static void setBlockingShield(double amt) {
        blockingShield = amt;
    }

    public static double getMaxBlockingShield() {
        return maxBlockingShield;
    }

    public static void setMaxBlockingShield(double amt) {
        maxBlockingShield = amt;
    }

    public static double getBlockingShield() {
        return blockingShield;
    }
}
