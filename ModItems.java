package com.dayofpi.lucys_sloths.registry;

import com.dayofpi.lucys_sloths.LucysSloths;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LucysSloths.MODID);

    public static final DeferredItem<DeferredSpawnEggItem> SLOTH_SPAWN_EGG = ITEMS.register("sloth_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.SLOTH, 0x6B702D, 0x70502B, new Item.Properties()));
}
