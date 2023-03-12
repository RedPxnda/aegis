package com.redpxnda.aegis.mixin;

import com.google.common.collect.Multimap;
import com.redpxnda.aegis.client.ClientShieldData;
import com.redpxnda.aegis.implementation.ShieldDataManager;
import com.redpxnda.aegis.network.*;
import com.redpxnda.aegis.registry.ModRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "startUsingItem", at = @At("HEAD"))
    private void AEGIS_addShieldAttributes(InteractionHand interactionHand, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack stack = entity.getItemInHand(interactionHand);
        if (stack == null || !(stack.getItem() instanceof ShieldItem)) return;
        Multimap<Attribute, AttributeModifier> modifiers = stack.getAttributeModifiers(EquipmentSlot.byName("shield"));
        entity.getAttributes().addTransientAttributeModifiers(modifiers);
        List<AttributeModifier> modifiersList = modifiers.get(ModRegistries.MOD_ATTRIBUTES.get("frontal_protection").get()).stream().toList();
        ShieldDataManager.setBlockShield(entity, modifiersList.size() > 0 ? modifiersList.get(0).getAmount() : 10);
        if (entity.level.isClientSide) {
            double shield = ShieldDataManager.getBlockShield(entity);
            ClientShieldData.setMaxBlockingShield(shield);
            ClientShieldData.setBlockingShield(shield);
        }
    }

    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    private void AEGIS_removeShieldAttributes(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack stack = entity.getUseItem();
        if (stack == null || !(stack.getItem() instanceof ShieldItem)) return;
        Multimap<Attribute, AttributeModifier> modifiers = stack.getAttributeModifiers(EquipmentSlot.byName("shield"));
        entity.getAttributes().removeAttributeModifiers(modifiers);
        if (entity instanceof Player player) player.getCooldowns().addCooldown(stack.getItem(), 100);
        ShieldDataManager.setBlockShield(entity, 0);
        if (entity.level.isClientSide) ClientShieldData.setBlockingShield(ShieldDataManager.getBlockShield(entity));
    }

    @Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
    private void AEGIS_disableShieldBlock(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    private static boolean aegisDamageSourceBlocked(LivingEntity instance, DamageSource damageSource) {
        Vec3 vec3;
        AbstractArrow abstractArrow;
        Entity entity = damageSource.getDirectEntity();
        boolean bl = false;
        if (entity instanceof AbstractArrow && (abstractArrow = (AbstractArrow)entity).getPierceLevel() > 0) {
            bl = true;
        }
        if (!damageSource.isBypassArmor() && instance.isUsingItem() && instance.getUseItem().getItem() instanceof ShieldItem && !bl && (vec3 = damageSource.getSourcePosition()) != null) {
            Vec3 vec32 = instance.getViewVector(1.0f);
            Vec3 vec33 = vec3.vectorTo(instance.position()).normalize();
            vec33 = new Vec3(vec33.x, 0.0, vec33.z);
            if (vec33.dot(vec32) < 0.0) {
                return true;
            }
        }
        return false;
    }

    private static void aegisHurtCurrentlyUsedShield(Player player, float f) {
        if (!player.level.isClientSide) {
            player.awardStat(Stats.ITEM_USED.get(player.getUseItem().getItem()));
        }
        if (f >= 3.0f) {
            int i = 1 + Mth.floor(f);
            InteractionHand interactionHand = player.getUsedItemHand();
            player.getUseItem().hurtAndBreak(i, player, p -> p.broadcastBreakEvent(interactionHand));
            if (player.getUseItem().isEmpty()) {
                if (interactionHand == InteractionHand.MAIN_HAND) {
                    player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
                player.stopUsingItem();
                player.playSound(SoundEvents.SHIELD_BREAK, 0.8f, 0.8f + player.level.random.nextFloat() * 0.4f);
            }
        }
    }

    private static boolean isEntityBlocking(LivingEntity entity) {
        return entity.isUsingItem() && entity.getUseItem().getItem() instanceof ShieldItem;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void AEGIS_shieldRecharge(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object) this;
        if (entity.level.isClientSide) {
            if (ShieldDataManager.getShield(entity) == 0 && ClientShieldData.entityAttackers.getOrDefault(entity, null) != null) {
                int amnt = ClientShieldData.entityTimers.getOrDefault(entity, 0);
                ClientShieldData.entityTimers.put(entity, amnt + 1);
            }
            if (ShieldDataManager.getLastHurtTime(entity) >= 100) ClientShieldData.entityAttackers.remove(entity);
        }
        if (ShieldDataManager.getShield(entity) < 0 && ShieldDataManager.getLastHurtTime(entity) >= 100) ShieldDataManager.setShield(entity, 0);
        if (ShieldDataManager.getLastHurtTime(entity) < 100) ShieldDataManager.increaseLastHurtTime(entity);
        if (ShieldDataManager.getLastHurtTime(entity) >= 100 && !isEntityBlocking(entity)) ShieldDataManager.incrementShield(entity, 0.1);
        if (entity instanceof ServerPlayer player) {
            ModPackets.CHANNEL.sendToPlayer(player, new SyncShieldDataPacket(ShieldDataManager.getShield(player), ShieldDataManager.getLastHurtTime(player)));
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void AEGIS_defenseLogic(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir) {
        boolean cancel = false;
        LivingEntity entity = (LivingEntity)(Object) this;
        if (aegisDamageSourceBlocked(entity, damageSource)) {
            if (!entity.level.isClientSide) {
                if (damageSource.getDirectEntity() instanceof LivingEntity attacker) attacker.knockback(0.25, entity.getX() - attacker.getX(), entity.getZ() - attacker.getZ());
                List<Player> players = entity.level.getNearbyPlayers(
                        TargetingConditions.forNonCombat(),
                        entity,
                        new AABB(entity.getX() - 5, entity.getY() - 5, entity.getZ() - 5, entity.getX() + 5, entity.getY() + 5, entity.getZ() + 5)
                );
                if (entity instanceof Player player) {
                    aegisHurtCurrentlyUsedShield(player, damage);
                    players.add(player);
                }
                List<ServerPlayer> list = new ArrayList<>();
                players.forEach(p -> {
                    if (p instanceof ServerPlayer sp) list.add(sp);
                });
                ModPackets.CHANNEL.sendToPlayers(list, new PlaySoundPacket(
                        entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.SHIELD_BLOCK.getLocation().toString(),
                        "MASTER",
                        1, 1
                ));
            }
            if (ShieldDataManager.getBlockShield(entity)-damage > 0)
                ShieldDataManager.setBlockShield(entity, ShieldDataManager.getBlockShield(entity)-damage);
            else
                entity.stopUsingItem();
            if (!entity.level.isClientSide && entity instanceof ServerPlayer player) ModPackets.CHANNEL.sendToPlayer(player, new SyncBlockingShieldDataPacket(ShieldDataManager.getBlockShield(entity)));
            ShieldDataManager.resetLastHurtTime(entity);
            cir.setReturnValue(false);
            return;
        }
        if (entity.level.isClientSide) {
            if (damageSource.getEntity() != null)
                ClientShieldData.entityAttackers.put(entity, damageSource.getEntity());
            ClientShieldData.entityAttackers.keySet().removeIf(e -> !e.isAlive());
            ClientShieldData.entityAttackers.values().removeIf(e -> !e.isAlive());
        }
        if (!entity.level.isClientSide && !aegisDamageSourceBlocked(entity, damageSource) && damage > 0 && !damageSource.isBypassArmor() && damageSource.getDirectEntity() != null && !(damageSource.getDirectEntity() instanceof AbstractArrow arrow && arrow.getPierceLevel() > 0) && entity.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("defense").get()) > 0 && ShieldDataManager.getShield(entity) > 0) {
            Entity attacker = damageSource.getDirectEntity();
            if (damageSource.getEntity() instanceof LivingEntity livingAttacker) {
                double attackSpeedAttribute = 1;
                if (livingAttacker.getAttributes().hasAttribute(Attributes.ATTACK_SPEED)) attackSpeedAttribute = livingAttacker.getAttributeValue(Attributes.ATTACK_SPEED);
                if (ShieldDataManager.getLastHurtTime(entity) < Math.min(10, 1/attackSpeedAttribute*10)) {
                    cir.setReturnValue(false);
                    return;
                }
            }
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
            if (ShieldDataManager.getShield(entity)-damage < 0) {
                ShieldDataManager.setShield(entity, 0);
                entity.hurt(damageSource, (float) Math.abs(ShieldDataManager.getShield(entity)-damage));
            } else ShieldDataManager.decrementShield(entity, damage);
            ServerPlayer aPlayer = null, vPlayer = null, cPlayer = null; // attacking player, victim player, cause player(if player shoots an arrow, by example)
            if (attacker instanceof ServerPlayer) aPlayer = (ServerPlayer) attacker;
            if (entity instanceof ServerPlayer) vPlayer = (ServerPlayer) entity;
            if (damageSource.getEntity() instanceof ServerPlayer) cPlayer = (ServerPlayer) damageSource.getEntity();
            Vec3 clientHitPos = vec.subtract(entity.getX(), entity.getY(), entity.getZ());
            ShieldDataManager.setShieldHitPos(entity, clientHitPos);
            if (aPlayer != null) {
                ModPackets.CHANNEL.sendToPlayer(aPlayer, new ClientShieldImpactPacket(vec.x, vec.y, vec.z, entity.getId(), ShieldDataManager.getShield(entity), clientHitPos.x, clientHitPos.y, clientHitPos.z));
            } else if (cPlayer != null) {
                ModPackets.CHANNEL.sendToPlayer(cPlayer, new ClientShieldImpactPacket(vec.x, vec.y, vec.z, entity.getId(), ShieldDataManager.getShield(entity), clientHitPos.x, clientHitPos.y, clientHitPos.z));
            }
            if (vPlayer != null) {
                ModPackets.CHANNEL.sendToPlayer(vPlayer, new ClientShieldImpactPacket(vec.x, vec.y, vec.z, entity.getId(), ShieldDataManager.getShield(entity), clientHitPos.x, clientHitPos.y, clientHitPos.z));
            }
            double x = attacker.getX() - entity.getX();
            double z = attacker.getZ() - entity.getZ();
            float i = (entity.getAttributes().hasAttribute(Attributes.ATTACK_KNOCKBACK)) ? (float)entity.getAttributeValue(Attributes.ATTACK_KNOCKBACK) : 0;
            i += (float) EnchantmentHelper.getKnockbackBonus(entity);
            entity.knockback(0.25*(i+1), x, z);
            if (entity instanceof Player player)
                ((LivingEntityAccessor) player).hurtArmor(damageSource, damage);
            cancel = true;
        }
        ShieldDataManager.resetLastHurtTime(entity);
        if (entity.level.isClientSide && Minecraft.getInstance().player != null && entity.is(Minecraft.getInstance().player)) ClientShieldData.setLastHurt(0);
        if (cancel) cir.setReturnValue(false);
    }

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true)
    private void AEGIS_customProtectionLogic(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
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
        else if (side >= 1 && side <= 2) ttlProtection = overallProtectionValue+(frontalProtectionValue*(side-1))+(sidewardProtectionValue*(2-side)); // if from front or side
        double reductionPercent = ttlProtection/(ttlProtection+20);
        if (entity instanceof Player player)
            ((LivingEntityAccessor) player).hurtArmor(source, amount);
        cir.setReturnValue((float) (amount * (1-reductionPercent))); // new damage: this is equivalent to `amount-amount*reductionPercent`
    }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void AEGIS_addCustomAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        ModRegistries.MOD_ATTRIBUTES.forEach((id, att) -> {
            cir.getReturnValue().add(att.get());
        });
    }

    @Inject(method = "getLastHandItem", at = @At("TAIL"), cancellable = true)
    private void AEGIS_getLastShieldHandItem(EquipmentSlot equipmentSlot, CallbackInfoReturnable<ItemStack> cir) {
        if (equipmentSlot.getType() == EquipmentSlot.Type.HAND && equipmentSlot.getIndex() > 1) {
            LivingEntity entity = (LivingEntity) (Object) this;
            cir.setReturnValue(((LivingEntityAccessor) entity).getLastHandItemStacks().get(0));
        }
    }

    @Inject(method = "getArmorValue", at = @At("HEAD"), cancellable = true)
    private void AEGIS_replaceArmorValue(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }
}
