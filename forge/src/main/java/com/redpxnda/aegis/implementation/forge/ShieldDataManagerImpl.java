package com.redpxnda.aegis.implementation.forge;

import com.redpxnda.aegis.forge.shield.EntityShield;
import com.redpxnda.aegis.forge.shield.EntityShieldProvider;
import com.redpxnda.aegis.registry.ModRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ShieldDataManagerImpl {
    public static double getShield(LivingEntity player) {
        if (player.getCapability(EntityShieldProvider.ENTITY_SHIELD).isPresent())
            return player.getCapability(EntityShieldProvider.ENTITY_SHIELD).orElseGet(EntityShield::createFake).getShield();
        return -1;
    }

    public static void setShield(LivingEntity player, double amount) {
        player.getCapability(EntityShieldProvider.ENTITY_SHIELD).ifPresent(shield -> {
            shield.setShield(amount);
        });
    }

    public static void incrementShield(LivingEntity player, double amount) {
        player.getCapability(EntityShieldProvider.ENTITY_SHIELD).ifPresent(shield -> {
            if (shield.getShield()+amount <= player.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("defense").get()))
                shield.incrementShield(amount);
            else shield.setShield(player.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("defense").get()));
        });
    }

    public static void decrementShield(LivingEntity player, double amount) {
        player.getCapability(EntityShieldProvider.ENTITY_SHIELD).ifPresent(shield -> {
            if (shield.getShield()-amount >= 0)
                shield.decrementShield(amount);
            else shield.setShield(0);
        });
    }

    public static int getLastHurtTime(LivingEntity player) {
        if (player.getCapability(EntityShieldProvider.ENTITY_SHIELD).isPresent())
            return player.getCapability(EntityShieldProvider.ENTITY_SHIELD).orElseGet(EntityShield::createFake).getLastHurt();
        return -1;
    }

    public static void resetLastHurtTime(LivingEntity player) {
        player.getCapability(EntityShieldProvider.ENTITY_SHIELD).ifPresent(EntityShield::resetLastHurt);
    }

    public static void increaseLastHurtTime(LivingEntity player) {
        player.getCapability(EntityShieldProvider.ENTITY_SHIELD).ifPresent(EntityShield::increaseLastHurt);
    }

    public static Vec3 getShieldHitPos(LivingEntity player) {
        if (player.getCapability(EntityShieldProvider.ENTITY_SHIELD).isPresent())
            return player.getCapability(EntityShieldProvider.ENTITY_SHIELD).orElseGet(EntityShield::createFake).getHitPos();
        return new Vec3(0, 0, 0);
    }

    public static void setShieldHitPos(LivingEntity player, Vec3 vec) {
        player.getCapability(EntityShieldProvider.ENTITY_SHIELD).ifPresent((shield) -> shield.setHitPos(vec));
    }
}
