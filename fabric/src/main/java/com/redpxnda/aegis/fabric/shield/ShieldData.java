package com.redpxnda.aegis.fabric.shield;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class ShieldData {
    public static void setHitPos(IEntityDataSaver entity, Vec3 vec) {
        CompoundTag tag = entity.getPersistentData();
        tag.putLongArray("ShieldHitPos", new long[] {(long) vec.x, (long) vec.y, (long) vec.z});
    }

    public static Vec3 getHitPos(IEntityDataSaver entity) {
        CompoundTag tag = entity.getPersistentData();
        long[] longs = tag.getLongArray("ShieldHitPos");
        return new Vec3(longs[0], longs[1], longs[2]);
    }

    public static int increaseLastHurtTime(IEntityDataSaver entity) {
        CompoundTag tag = entity.getPersistentData();
        int time = tag.getInt("LastHurt");

        time++;

        tag.putInt("LastHurt", time);

        return time;
    }

    public static void resetLastHurtTime(IEntityDataSaver entity) {
        CompoundTag tag = entity.getPersistentData();
        tag.putInt("LastHurt", 0);
    }

    public static int getHurtTime(IEntityDataSaver entity) {
        CompoundTag tag = entity.getPersistentData();
        return tag.getInt("LastHurt");
    }

    public static double incrementShield(IEntityDataSaver entity, double amount) {
        CompoundTag tag = entity.getPersistentData();
        double shield = tag.getDouble("Shield");

        shield += amount;

        tag.putDouble("Shield", shield);

        return shield;
    }

    public static double decrementShield(IEntityDataSaver entity, double amount) {
        CompoundTag tag = entity.getPersistentData();
        double shield = tag.getDouble("Shield");

        shield -= amount;

        tag.putDouble("Shield", shield);

        return shield;
    }

    public static double setShield(IEntityDataSaver entity, double amount) {
        CompoundTag tag = entity.getPersistentData();
        tag.putDouble("Shield", amount);
        return amount;
    }

    public static double getShield(IEntityDataSaver entity) {
        CompoundTag tag = entity.getPersistentData();
        return tag.getDouble("Shield");
    }

}
