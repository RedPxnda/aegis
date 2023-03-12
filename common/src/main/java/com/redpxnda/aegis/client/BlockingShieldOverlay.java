package com.redpxnda.aegis.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.aegis.registry.ModRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;

import static com.redpxnda.aegis.Aegis.MOD_ID;

public class BlockingShieldOverlay {
    public static final ResourceLocation SHIELD_ICON = new ResourceLocation(MOD_ID, "textures/gui/blocking_shield.png");

    public static boolean render(int width, int height, PoseStack poseStack, Minecraft minecraft) {
        if (
                minecraft.gameMode != null && // doing the checks to make sure the display should be visible
                !minecraft.options.hideGui && // if f1 isnt enabled
                minecraft.gameMode.canHurtPlayer() && // if not creative/spectator
                minecraft.getCameraEntity() instanceof Player &&
                minecraft.player != null &&
                minecraft.player.isUsingItem() &&
                minecraft.player.getUseItem().getItem() instanceof ShieldItem
        ) {
            int x = width/2, y = height/2;
            poseStack.translate(0, 0, -5);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, SHIELD_ICON);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            int offset = (int) ((1 - ClientShieldData.getBlockingShield()/ClientShieldData.getMaxBlockingShield())*9);
            GuiComponent.blit(poseStack, x-4, y-4+offset, 0, offset, 9, 9 - offset, 9, 9);
            RenderSystem.disableBlend();
            return true; // true if successful
        }
        return false; // false if not successful
    }
}
