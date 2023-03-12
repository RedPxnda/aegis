package com.redpxnda.aegis.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.redpxnda.aegis.client.ClientShieldData;
import com.redpxnda.aegis.client.ShieldIcons;
import com.redpxnda.aegis.implementation.ShieldDataManager;
import com.redpxnda.aegis.registry.ModRegistries;
import com.redpxnda.aegis.util.PlayerShieldIcon;
import com.redpxnda.aegis.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.redpxnda.aegis.client.ShieldIcons.*;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(
        method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("HEAD")
    )
    private void AEGIS_renderShieldIcon(LivingEntity livingEntity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int pPackedLight, CallbackInfo ci) {
        //👇 a bunch of checks: making sure the player is the attacker, making sure the entity has been recently hit, making sure the entity has shield, etc.
        if (
                Minecraft.getInstance().player == null ||
                !(Minecraft.getInstance().player.is(ClientShieldData.entityAttackers.getOrDefault(livingEntity, null))) ||
                livingEntity.equals(Minecraft.getInstance().player) ||
                ShieldDataManager.getLastHurtTime(livingEntity) >= 100 ||
                ShieldDataManager.getShield(livingEntity) < 0 ||
                livingEntity.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("defense").get()) <= 0
        ) return;
        double lastHurt = ShieldDataManager.getLastHurtTime(livingEntity);
        ShieldIcons icon = CLASSIC;
        if (livingEntity instanceof Player player)
            icon = PlayerShieldIcon.getPlayersIcon(player);
        TextureAtlasSprite sprite = icon.getSprite(); // shield icon sprite
        TextureAtlasSprite emptySprite = icon.getEmptySprite(); // empty shield icon sprite
        TextureAtlasSprite hitSprite = icon.getHitSprite(); // hit shield icon sprite
        TextureAtlasSprite leftSprite = icon.getLeftSprite(); // left broken shield icon sprite
        TextureAtlasSprite rightSprite = icon.getRightSprite(); // right broken shield icon sprite
        poseStack.pushPose();
        Vec3 translation = ShieldDataManager.getShieldHitPos(livingEntity);
        if (translation != null)
            poseStack.translate(translation.x, translation.y, translation.z); // moving the render to the last hit location
        //if (livingEntity.shouldShowName()) poseStack.translate(0.0, 0.25, 0.0);
        Quaternion rot = Minecraft.getInstance().gameRenderer.getMainCamera().rotation(); // this line and the next tell the render to always face the player
        poseStack.mulPose(rot);
        if (lastHurt <= 20 && ShieldDataManager.getShield(livingEntity) > 0)
            poseStack.translate(Math.cos(lastHurt*1.15f)/(2f*(lastHurt+2)), 0, 0);
        VertexConsumer buffer = multiBufferSource.getBuffer(Sheets.translucentCullBlockSheet()); // translucentCullBlockSheet render type, cuz idk minecraft is odd
        Matrix4f matrix = poseStack.last().pose();
        float r = 1, g = 1, b = 1; // rgb initialization, now pointless since code was scrapped
        float percent = (float) (ShieldDataManager.getShield(livingEntity)/livingEntity.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("defense").get())); // how full the shield should be
        float size = 0.25f;
        if (lastHurt <= 5) { // for the first 10 ticks, render the white hit shield icon
            emptySprite = hitSprite;
            sprite = hitSprite;
        }
        if (ShieldDataManager.getShield(livingEntity) == 0) {
            int tick = ClientShieldData.entityTimers.getOrDefault(livingEntity, 0);
            double time = tick*6.5/250d;
            double yPosLeft = -10 * Math.pow(time - 0.1, 2) + 0.1;
            double yPosRight = -10 * Math.pow(-time + 0.1, 2) + 0.1;
            poseStack.translate(time, yPosLeft, 0);
            float alpha = 1f;
            if (tick >= 15) alpha-=(tick-15)/5f;
            RenderUtils.addVertex(matrix, buffer, r, g, b, alpha, size, size, 0, leftSprite.getU0(), leftSprite.getV0(), pPackedLight);
            RenderUtils.addVertex(matrix, buffer, r, g, b, alpha, size, -size, 0, leftSprite.getU0(), leftSprite.getV1(), pPackedLight);
            RenderUtils.addVertex(matrix, buffer, r, g, b, alpha, -size, -size, 0, leftSprite.getU1(), leftSprite.getV1(), pPackedLight);
            RenderUtils.addVertex(matrix, buffer, r, g, b, alpha, -size, size, 0, leftSprite.getU1(), leftSprite.getV0(), pPackedLight);
            poseStack.translate(-time, -yPosLeft, 0);
            poseStack.translate(-time, yPosRight, 0);
            RenderUtils.addVertex(matrix, buffer, r, g, b, alpha, size, size, 0, rightSprite.getU0(), rightSprite.getV0(), pPackedLight);
            RenderUtils.addVertex(matrix, buffer, r, g, b, alpha, size, -size, 0, rightSprite.getU0(), rightSprite.getV1(), pPackedLight);
            RenderUtils.addVertex(matrix, buffer, r, g, b, alpha, -size, -size, 0, rightSprite.getU1(), rightSprite.getV1(), pPackedLight);
            RenderUtils.addVertex(matrix, buffer, r, g, b, alpha, -size, size, 0, rightSprite.getU1(), rightSprite.getV0(), pPackedLight);

            poseStack.popPose();
            if (tick >= 20) {
                ClientShieldData.entityTimers.remove(livingEntity);
                ClientShieldData.entityAttackers.remove(livingEntity);
                ShieldDataManager.setShield(livingEntity, -1);
            }
            return;
        }
        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, size, size-0.5f*(1f-percent), 0, sprite.getU0(), sprite.getV(sprite.getHeight()-sprite.getHeight()*percent), pPackedLight);
        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, size, -size, 0, sprite.getU0(), sprite.getV1(), pPackedLight);
        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, -size, -size, 0, sprite.getU1(), sprite.getV1(), pPackedLight);
        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, -size, size-0.5f*(1f-percent), 0, sprite.getU1(), sprite.getV(sprite.getHeight()-sprite.getHeight()*percent), pPackedLight);

        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, size, size, 0, emptySprite.getU0(), emptySprite.getV0(), pPackedLight);
        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, size, -size+0.5f*(percent), 0, emptySprite.getU0(), emptySprite.getV(emptySprite.getHeight()-emptySprite.getHeight()*percent), pPackedLight);
        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, -size, -size+0.5f*(percent), 0, emptySprite.getU1(), emptySprite.getV(emptySprite.getHeight()-emptySprite.getHeight()*percent), pPackedLight);
        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, -size, size, 0, emptySprite.getU1(), emptySprite.getV0(), pPackedLight);
        poseStack.popPose();
    }
}
