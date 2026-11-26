package com.dayofpi.lucys_sloths.registry;

import com.dayofpi.lucys_sloths.LucysSloths;
import com.dayofpi.lucys_sloths.common.SlothEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, LucysSloths.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<SlothEntity>> SLOTH = ENTITY_TYPES.register("sloth",
            () -> EntityType.Builder.of(SlothEntity::new, MobCategory.CREATURE)
                    .sized(1.0F, 0.55F)
                    .clientTrackingRange(8)
                    .build("sloth"));
}
