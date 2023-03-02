package com.redpxnda.aegis.forge.shield;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityShieldProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<EntityShield> ENTITY_SHIELD = CapabilityManager.get(new CapabilityToken<EntityShield>() {});

    private EntityShield shield = null;
    private final LazyOptional<EntityShield> optional = LazyOptional.of(this::createEntityShield);

    private EntityShield createEntityShield() {
        if (this.shield == null) this.shield = new EntityShield();
        return this.shield;
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        if (capability == ENTITY_SHIELD) return optional.cast();

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createEntityShield().saveNBTData(tag);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createEntityShield().loadNBTData(tag);
    }
}
