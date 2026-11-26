package com.dayofpi.lucys_sloths.event;

import com.dayofpi.lucys_sloths.LucysSloths;
import com.dayofpi.lucys_sloths.client.SlothModel;
import com.dayofpi.lucys_sloths.client.SlothRenderer;
import com.dayofpi.lucys_sloths.registry.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = LucysSloths.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SlothModel.LAYER, SlothModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SLOTH.get(), SlothRenderer::new);
    }
}
