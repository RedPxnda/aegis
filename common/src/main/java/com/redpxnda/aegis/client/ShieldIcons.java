package com.redpxnda.aegis.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import static com.redpxnda.aegis.Aegis.MOD_ID;

@Environment(EnvType.CLIENT)
public enum ShieldIcons {
    CLASSIC("gui/shield/classic", null, null, null),
    ROYAL("gui/royal_shield/s", null, null, null);

    private final ResourceLocation location;
    private final ResourceLocation emptyLocation;
    private final ResourceLocation hitLocation;
    private final ResourceLocation breakLocation;

    ShieldIcons(String location, String emptyLocation, String hitLocation, String breakLocation) {
        this(location, emptyLocation, hitLocation, breakLocation, MOD_ID);
    }

    ShieldIcons(String location, String emptyLocation, String hitLocation, String breakLocation, String modID) {
        this.location = new ResourceLocation(MOD_ID, location);
        this.emptyLocation = emptyLocation != null ? new ResourceLocation(MOD_ID, emptyLocation) : new ResourceLocation(MOD_ID, location + "_empty");
        this.hitLocation = hitLocation != null ? new ResourceLocation(MOD_ID, hitLocation) : new ResourceLocation(MOD_ID, location + "_hit");
        this.breakLocation = breakLocation != null ? new ResourceLocation(MOD_ID, breakLocation) : new ResourceLocation(MOD_ID, location + "_fragment");
    }

    public TextureAtlasSprite getSprite() {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(this.location);
    }

    public TextureAtlasSprite getEmptySprite() {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(this.emptyLocation);
    }

    public TextureAtlasSprite getHitSprite() {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(this.hitLocation);
    }

    public TextureAtlasSprite getLeftSprite() {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(this.getBreakLocationLeft(true));
    }

    public TextureAtlasSprite getRightSprite() {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(this.getBreakLocationRight(true));
    }

    public ResourceLocation getLocation(boolean forAtlas) {
        return forAtlas ? location : new ResourceLocation(location.getNamespace(), "textures/" + location.getPath() + ".png");
    }

    public ResourceLocation getEmptyLocation(boolean forAtlas) {
        return forAtlas ? emptyLocation : new ResourceLocation(emptyLocation.getNamespace(), "textures/" + emptyLocation.getPath() + ".png");
    }

    public ResourceLocation getHitLocation(boolean forAtlas) {
        return forAtlas ? hitLocation : new ResourceLocation(hitLocation.getNamespace(), "textures/" + hitLocation.getPath() + ".png");
    }

    public ResourceLocation getBreakLocationLeft(boolean forAtlas) {
        return forAtlas ? new ResourceLocation(breakLocation.getNamespace(), breakLocation.getPath() + "_left") : new ResourceLocation(breakLocation.getNamespace(), "textures/" + breakLocation.getPath() + "_left.png");
    }

    public ResourceLocation getBreakLocationRight(boolean forAtlas) {
        return forAtlas ? new ResourceLocation(breakLocation.getNamespace(), breakLocation.getPath() + "_right") : new ResourceLocation(breakLocation.getNamespace(), "textures/" + breakLocation.getPath() + "_right.png");
    }
}
