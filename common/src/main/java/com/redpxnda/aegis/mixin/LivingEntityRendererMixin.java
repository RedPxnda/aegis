package com.redpxnda.aegis.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.redpxnda.aegis.client.DefenseShieldOverlay;
import com.redpxnda.aegis.implementation.ShieldDataManager;
import com.redpxnda.aegis.registry.ModRegistries;
import com.redpxnda.aegis.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(
        method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At("HEAD")
    )
    private void renderShieldIcon(LivingEntity livingEntity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int pPackedLight, CallbackInfo ci) {
        //👇 if the entity is the player, don't render (players can't see their own) 👇 if f1, don't render    👇 if they haven't been hurt within 5 seconds, don't render
        if (livingEntity.equals(Minecraft.getInstance().player) || Minecraft.getInstance().options.hideGui || ShieldDataManager.getLastHurtTime(livingEntity) >= 100 || ShieldDataManager.getShield(livingEntity) <= 0) return;
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(DefenseShieldOverlay.SHIELD_ICON_SPRITE); // shield icon sprite
        TextureAtlasSprite emptySprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(DefenseShieldOverlay.EMPTY_SHIELD_ICON_SPRITE); // empty shield icon sprite
        poseStack.pushPose();
        Vec3 translation = ShieldDataManager.getShieldHitPos(livingEntity);
        if (translation != null)
            poseStack.translate(translation.x, translation.y, translation.z-0.01); // moving the render to the last hit location
        //if (livingEntity.shouldShowName()) poseStack.translate(0.0, 0.25, 0.0);
        Quaternion rot = Minecraft.getInstance().gameRenderer.getMainCamera().rotation(); // this line and the next tell the render to always face the player
        poseStack.mulPose(rot);
        if (ShieldDataManager.getLastHurtTime(livingEntity) <= 20)
            poseStack.translate(Math.cos(ShieldDataManager.getLastHurtTime(livingEntity)*1.15f)/20f, 0, 0);
        VertexConsumer buffer = multiBufferSource.getBuffer(Sheets.translucentCullBlockSheet()); // translucentCullBlockSheet render type, cuz idk minecraft is odd
        Matrix4f matrix = poseStack.last().pose();
        float r = 1, g = 1, b = 1; // rgb initialization, now pointless since code was scrapped
        float percent = (float) (ShieldDataManager.getShield(livingEntity)/livingEntity.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("defense").get()));
        float size = 0.25f;
        boolean renderWhite = true;
        //if (ShieldDataManager.getLastHurtTime(livingEntity) <= 10) renderWhite = true;
        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, size, size-0.5f*(1f-percent), 0, sprite.getU0(), sprite.getV(sprite.getHeight()-sprite.getHeight()*percent), pPackedLight, renderWhite);
        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, size, -size, 0, sprite.getU0(), sprite.getV1(), pPackedLight, renderWhite);
        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, -size, -size, 0, sprite.getU1(), sprite.getV1(), pPackedLight, renderWhite);
        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, -size, size-0.5f*(1f-percent), 0, sprite.getU1(), sprite.getV(sprite.getHeight()-sprite.getHeight()*percent), pPackedLight, renderWhite);

        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, size, size, 0, emptySprite.getU0(), emptySprite.getV0(), pPackedLight, renderWhite);
        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, size, -size+0.5f*(percent), 0, emptySprite.getU0(), emptySprite.getV(emptySprite.getHeight()-emptySprite.getHeight()*percent), pPackedLight, renderWhite);
        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, -size, -size+0.5f*(percent), 0, emptySprite.getU1(), emptySprite.getV(emptySprite.getHeight()-emptySprite.getHeight()*percent), pPackedLight, renderWhite);
        RenderUtils.addVertex(matrix, buffer, r, g, b, 1, -size, size, 0, emptySprite.getU1(), emptySprite.getV0(), pPackedLight, renderWhite);
        poseStack.popPose();
    }
}
