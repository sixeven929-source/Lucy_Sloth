package com.dayofpi.lucys_sloths.event;

import com.dayofpi.lucys_sloths.LucysSloths;
import com.dayofpi.lucys_sloths.common.SlothEntity;
import com.dayofpi.lucys_sloths.registry.ModEntities;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

public class CommonEvents {

    @EventBusSubscriber(modid = LucysSloths.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent
        public static void attributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.SLOTH.get(), SlothEntity.createSlothAttributes().build());
        }

        @SubscribeEvent
        public static void spawnPlacements(RegisterSpawnPlacementsEvent event) {
            event.register(ModEntities.SLOTH.get(), SpawnPlacementTypes.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SlothEntity::canSpawn,
                    RegisterSpawnPlacementsEvent.Operation.OR);
        }
    }

    @EventBusSubscriber(modid = LucysSloths.MODID)
    public static class GameBus {
        /** Sustituye al mixin original: los ocelotes cazan perezosos. */
        @SubscribeEvent
        public static void ocelotsHuntSloths(EntityJoinLevelEvent event) {
            if (!event.getLevel().isClientSide() && event.getEntity() instanceof Ocelot ocelot) {
                ocelot.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(ocelot, SlothEntity.class, false));
            }
        }
    }
}
