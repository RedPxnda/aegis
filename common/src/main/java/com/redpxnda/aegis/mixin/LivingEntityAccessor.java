package com.redpxnda.aegis.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker("hurtArmor")
    void hurtArmor(DamageSource damageSource, float f);

    @Accessor
    NonNullList<ItemStack> getLastHandItemStacks();
}
