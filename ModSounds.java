package com.dayofpi.lucys_sloths.registry;

import com.dayofpi.lucys_sloths.LucysSloths;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, LucysSloths.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SLOTH_AMBIENT = register("entity.sloth.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> SLOTH_EAT = register("entity.sloth.eat");
    public static final DeferredHolder<SoundEvent, SoundEvent> SLOTH_STEP = register("entity.sloth.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> SLOTH_HURT = register("entity.sloth.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SLOTH_DEATH = register("entity.sloth.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> SLOTH_GROW_SEAGRASS = register("entity.sloth.grow_seagrass");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(LucysSloths.MODID, name)));
    }
}
