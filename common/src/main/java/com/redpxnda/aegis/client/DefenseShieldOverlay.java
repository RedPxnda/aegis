package com.redpxnda.aegis.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.redpxnda.aegis.registry.ModRegistries;
import com.redpxnda.aegis.util.PlayerShieldIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

import static com.redpxnda.aegis.client.ShieldIcons.*;

public class DefenseShieldOverlay {

    public static boolean render(int width, int height, PoseStack poseStack, Minecraft minecraft) {
        if (
                minecraft.gameMode != null && // doing the checks to make sure the display should be visible
                !minecraft.options.hideGui && // if f1 isnt enabled
                minecraft.gameMode.canHurtPlayer() && // if not creative/spectator
                minecraft.getCameraEntity() instanceof Player &&
                minecraft.player != null &&
                minecraft.player.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("defense").get()) > 0
        ) {
            ShieldIcons icon = PlayerShieldIcon.getPlayersIcon(minecraft.player);
            poseStack.translate(0, 0, -5);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f); // set color to white
            RenderSystem.setShader(GameRenderer::getPositionTexShader); // setting the shader
            double shield = ClientShieldData.getPersonalShield();
            double maxShield = minecraft.player.getAttributeValue(ModRegistries.MOD_ATTRIBUTES.get("defense").get());
            int x = (minecraft.options.mainHand().get().equals(HumanoidArm.LEFT)) ? width/2 - 108 : width/2 + 93; // x position is opposite of main arm, to prevent interference with offhand display
            RenderSystem.setShaderTexture(0, icon.getLocation(false));
            if (ClientShieldData.getLastHurt() <= 10 && shield > 0) {
                RenderSystem.setShaderTexture(0, icon.getHitLocation(false));
                x+=Math.cos(ClientShieldData.getLastHurt()*1.15f)/(2f*(ClientShieldData.getLastHurt()+2));
            }
            GuiComponent.blit(poseStack, x, height-20, 0, 0, 16, 16, 16, 16); // rendering filled icon

            poseStack.translate(0, 0, 0.5);
            RenderSystem.setShaderTexture(0, icon.getEmptyLocation(false));
            if (ClientShieldData.getLastHurt() <= 10 && shield > 0) {
                RenderSystem.setShaderTexture(0, icon.getHitLocation(false));
                x+=Math.cos(ClientShieldData.getLastHurt()*1.15f)/(2f*(ClientShieldData.getLastHurt()+2));
            }
            GuiComponent.blit(poseStack, x, height-20, 0, 0, 16, 16 - (int) ((shield/maxShield)*16), 16, 16); // rendering empty icon
            return true; // true if successful
        }
        return false; // false if not successful
    }
}
