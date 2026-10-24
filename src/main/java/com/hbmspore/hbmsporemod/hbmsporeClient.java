package com.hbmspore.hbmsporemod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = hbmspore.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = hbmspore.MODID, value = Dist.CLIENT)
public class hbmsporeClient {
    public hbmsporeClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> 0x707070,
                hbmspore.DEAD_BIOMASS.get(),
                hbmspore.DEAD_GASTRIC_BIOMASS.get(),
                hbmspore.DEAD_CALCIFIED_BIOMASS.get(),
                hbmspore.DEAD_SICKEN_BIOMASS.get()
,
                hbmspore.DEAD_FREEZE_BURNED_BIOMASS.get()
        );

    }
}
