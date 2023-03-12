package com.redpxnda.aegis.fabric.mixin;

import com.redpxnda.aegis.fabric.shield.IEntityDataSaver;
import com.redpxnda.aegis.fabric.shield.ShieldData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntitySaveDataMixin implements IEntityDataSaver {
    private CompoundTag persistentData;

    @Override
    public CompoundTag getPersistentData() {
        if (this.persistentData == null) {
            this.persistentData = new CompoundTag();
            ShieldData.maximizeLastHurtTime(this);
        }

        return persistentData;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void addCustomSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        if (persistentData != null) compoundTag.put("aegis.data", persistentData);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readCustomSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        if (compoundTag.contains("aegis.data")) persistentData = compoundTag.getCompound("aegis.data");
    }

}
