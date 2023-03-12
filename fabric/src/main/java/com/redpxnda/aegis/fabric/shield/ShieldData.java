package com.redpxnda.aegis.fabric.shield;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class ShieldData {
    public static void setHitPos(IEntityDataSaver entity, Vec3 vec) {
        CompoundTag tag = entity.getPersistentData();
        tag.putDouble("ShieldHitPosX", vec.x);
        tag.putDouble("ShieldHitPosY", vec.y);
        tag.putDouble("ShieldHitPosZ", vec.z);
    }

    public static Vec3 getHitPos(IEntityDataSaver entity) {
        CompoundTag tag = entity.getPersistentData();
        double x = tag.getDouble("ShieldHitPosX");
        double y = tag.getDouble("ShieldHitPosY");
        double z = tag.getDouble("ShieldHitPosZ");
        return new Vec3(x, y, z);
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

    public static void maximizeLastHurtTime(IEntityDataSaver entity) {
        CompoundTag tag = entity.getPersistentData();
        tag.putInt("LastHurt", 100);
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

    public static double setBlockShield(IEntityDataSaver entity, double amount) {
        CompoundTag tag = entity.getPersistentData();
        tag.putDouble("BlockShield", amount);
        return amount;
    }

    public static double getBlockShield(IEntityDataSaver entity) {
        CompoundTag tag = entity.getPersistentData();
        return tag.getDouble("BlockShield");
    }

}
