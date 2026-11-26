package com.dayofpi.lucys_sloths;

import com.dayofpi.lucys_sloths.registry.ModEntities;
import com.dayofpi.lucys_sloths.registry.ModItems;
import com.dayofpi.lucys_sloths.registry.ModSounds;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(LucysSloths.MODID)
public class LucysSloths {
    public static final String MODID = "lucys_sloths";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public LucysSloths(IEventBus modEventBus) {
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModSounds.SOUNDS.register(modEventBus);
        modEventBus.addListener(this::addCreative);
        LOGGER.info("Lucy's Sloths initialized");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.SLOTH_SPAWN_EGG.get());
        }
    }
}
