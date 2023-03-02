package com.redpxnda.aegis.mixin;

import com.redpxnda.aegis.implementation.ShieldDataManager;
import com.redpxnda.aegis.network.ModPackets;
import com.redpxnda.aegis.network.SpawnImpactParticlePacket;
import com.redpxnda.aegis.network.SyncShieldDataPacket;
import com.redpxnda.aegis.registry.ModRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void shieldRecharge(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object) this;
        if (ShieldDataManager.getLastHurtTime(entity) < 100) ShieldDataManager.increaseLastHurtTime(entity);
        if (ShieldDataManager.getLastHurtTime(entity) >= 100) ShieldDataManager.incrementShield(entity, 0.1);
        if (entity instanceof ServerPlayer player) {
            ModPackets.CHANNEL.sendToPlayer(player, new SyncShieldDataPacket(ShieldDataManager.getShield(player)));
        }

    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void defenseLogic(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)(Object) this;
        ShieldDataManager.resetLastHurtTime(entity);
        if (damageSource.getDirectEntity() != null && entity.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("defense").get()) > 0 && ShieldDataManager.getShield(entity) > 0) {
            Entity attacker = damageSource.getDirectEntity();
            Vec3 vec = new Vec3(attacker.getX(), attacker.getY(), attacker.getZ()).subtract(entity.getX(), entity.getY(), entity.getZ()).normalize();
            double d = 3;
            Vec3 eyePos = attacker.getEyePosition();
            Vec3 viewVec = attacker.getViewVector(1.0f);
            Vec3 viewVecCalc = eyePos.add(viewVec.x * d, viewVec.y * d, viewVec.z * d);
            AABB aabb = attacker.getBoundingBox().expandTowards(viewVec.scale(d)).inflate(1.0, 1.0, 1.0);
            EntityHitResult result = ProjectileUtil.getEntityHitResult(attacker, eyePos, viewVecCalc, aabb, arg -> !arg.isSpectator() && arg.isPickable(), d*d);
            if (result != null) {
                vec = result.getLocation();
            }
            ServerPlayer aPlayer = null, vPlayer = null;
            if (attacker instanceof ServerPlayer) aPlayer = (ServerPlayer) attacker;
            if (entity instanceof ServerPlayer) vPlayer = (ServerPlayer) entity;
            ShieldDataManager.setShieldHitPos(entity, vec.subtract(entity.getX(), entity.getY(), entity.getZ()));
            if (aPlayer != null && !entity.level.isClientSide()) {
                ModPackets.CHANNEL.sendToPlayer(aPlayer, new SpawnImpactParticlePacket(vec.x, vec.y, vec.z));
            }
            if (vPlayer != null && !entity.level.isClientSide()) {
                ModPackets.CHANNEL.sendToPlayer(vPlayer, new SpawnImpactParticlePacket(vec.x, vec.y, vec.z));
            }
            if (ShieldDataManager.getShield(entity)-damage < 0) {
                ShieldDataManager.setShield(entity, 0);
                entity.hurt(damageSource, (float) Math.abs(ShieldDataManager.getShield(entity)-damage));
            } else ShieldDataManager.decrementShield(entity, damage);
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true)
    private void customProtectionLogic(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity)(Object) this;
        double overallProtectionValue = entity.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("overall_protection").get()); // overall protection attribute
        double frontalProtectionValue = entity.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("frontal_protection").get()); // frontal protection attribute
        double rearwardProtectionValue = entity.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("rearwards_protection").get()); // rearwards protection attribute
        double sidewardProtectionValue = entity.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("sidewards_protection").get()); // sidewards protection attribute
        if (source.isBypassArmor()) cir.setReturnValue(amount);
        Entity directEntity = source.getDirectEntity(); // attacker entity, if present
        float side = -1; // initializing side variable
        if (directEntity != null) { // if there is an attacker entity
            float yLookDE = directEntity.getYRot(); // Y Rot of attacker
            if (yLookDE > 180) yLookDE-=360; // if 270, turn to -90
            else if (yLookDE <= -180) yLookDE+=360; // if -270, turn to 90
            float yLookEntity = entity.getYRot(); // Y Rot of victim
            if (yLookEntity > 180) yLookEntity-=360;
            else if (yLookEntity <= -180) yLookDE+=360;
            side = Math.abs(yLookDE-yLookEntity); // getting the difference between the two - this is how I detect which side the attack is from
            if (side > 180) side = Math.abs(side-360); // if 270, turn to |-90|
            side/=90f; // dividing to get smaller numbers
            side = Math.round(side*1000f)/1000f; // rounding to thousandths
            // 2=front, 0=back, 1=side (and obviously there can be in-betweens)
        }
        double ttlProtection = overallProtectionValue; // initializing ttlProtection variable, which is the total of all protection attributes, if applicable
        if (side >= 0 && side < 1) ttlProtection = overallProtectionValue+(rearwardProtectionValue*(1-side))+(sidewardProtectionValue*side); // if from back or side
        else if (side >= 1 && side <= 2) ttlProtection = overallProtectionValue+(frontalProtectionValue*side)+(sidewardProtectionValue*(2-side)); // if from front or side
        double reductionPercent = ttlProtection/(ttlProtection+20);
        cir.setReturnValue((float) (amount * (1-reductionPercent))); // new damage: this is equivalent to `amount-amount*reductionPercent`
    }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void addCustomAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        ModRegistries.MOD_ATTRIBUTES.forEach((id, att) -> {
            cir.getReturnValue().add(att.get());
        });
    }

    @Inject(method = "getArmorValue", at = @At("HEAD"), cancellable = true)
    private void replaceArmorValue(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }
}
