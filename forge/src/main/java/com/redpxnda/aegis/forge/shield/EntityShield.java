package com.redpxnda.aegis.forge.shield;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class EntityShield {
    private double shield;
    private int lastHurt;

    private double hitX;
    private double hitY;
    private double hitZ;

    public Vec3 getHitPos() {
        return new Vec3(hitX, hitY, hitZ);
    }

    public void setHitPos(Vec3 vec) {
        this.hitX = vec.x;
        this.hitY = vec.y;
        this.hitZ = vec.z;
    }

    public static EntityShield createFake() {
        EntityShield entityShield = new EntityShield();
        entityShield.setShield(-1);
        return entityShield;
    }

    public int getLastHurt() {
        return lastHurt;
    }

    public void resetLastHurt() {
        this.lastHurt = 0;
    }

    public void increaseLastHurt() {
        this.lastHurt++;
    }

    public double getShield() {
        return shield;
    }

    public void setShield(double shield) {
        this.shield = shield;
    }

    public void incrementShield(double amount) {
        this.shield+=amount;
    }

    public void decrementShield(double amount) {
        this.shield-=amount;
    }

    public void copyFrom(EntityShield source) {
        this.shield = source.shield;
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putDouble("Shield", shield);
    }

    public void loadNBTData(CompoundTag tag) {
        this.shield = tag.getDouble("Shield");
    }
}
