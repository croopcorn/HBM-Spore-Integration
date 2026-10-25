package com.hbmspore.hbmsporemod;

import com.hbm.hazard.HazardRegistry;
import com.hbm.hazard.HazardSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(hbmspore.MODID)
public class hbmspore {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "hbmspore";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MODID);

    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MODID);

    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredBlock<Block> DEAD_BIOMASS =
            BLOCKS.registerSimpleBlock("dead_biomass_block", BlockBehaviour.Properties.of().strength(2.0F, 2.0F).sound(SoundType.SLIME_BLOCK));
    public static final DeferredItem<BlockItem> DEAD_BIOMASS_ITEM =
            ITEMS.registerSimpleBlockItem("dead_biomass_block", DEAD_BIOMASS);

    public static final DeferredBlock<Block> DEAD_GASTRIC_BIOMASS =
            BLOCKS.registerSimpleBlock("dead_gastric_biomass_block", BlockBehaviour.Properties.of().strength(4.0F, 4.0F).sound(SoundType.SLIME_BLOCK));
    public static final DeferredItem<BlockItem> DEAD_GASTRIC_BIOMASS_ITEM =
            ITEMS.registerSimpleBlockItem("dead_gastric_biomass_block", DEAD_GASTRIC_BIOMASS);

    public static final DeferredBlock<Block> DEAD_CALCIFIED_BIOMASS =
            BLOCKS.registerSimpleBlock("dead_calcified_biomass_block", BlockBehaviour.Properties.of().strength(8.0F, 20.0F).sound(SoundType.SLIME_BLOCK));
    public static final DeferredItem<BlockItem> DEAD_CALCIFIED_BIOMASS_ITEM =
            ITEMS.registerSimpleBlockItem("dead_calcified_biomass_block", DEAD_CALCIFIED_BIOMASS);

    public static final DeferredBlock<Block> DEAD_SICKEN_BIOMASS =
            BLOCKS.registerSimpleBlock("dead_sicken_biomass_block", BlockBehaviour.Properties.of().strength(4.0F, 4.0F).sound(SoundType.SLIME_BLOCK));
    public static final DeferredItem<BlockItem> DEAD_SICKEN_BIOMASS_ITEM =
            ITEMS.registerSimpleBlockItem("dead_sicken_biomass_block", DEAD_SICKEN_BIOMASS);

    public static final DeferredBlock<Block> DEAD_FREEZE_BURNED_BIOMASS =
            BLOCKS.registerSimpleBlock("dead_freeze_burned_biomass_block", BlockBehaviour.Properties.of().strength(2.0F, 2.0F).sound(SoundType.SLIME_BLOCK).randomTicks());
    public static final DeferredItem<BlockItem> DEAD_FREEZE_BURNED_BIOMASS_ITEM =
            ITEMS.registerSimpleBlockItem("dead_freeze_burned_biomass_block", DEAD_FREEZE_BURNED_BIOMASS);

    public static final DeferredBlock<Block> DEAD_ROOTED_BIOMASS =
            BLOCKS.registerSimpleBlock("dead_rooted_biomass_block", BlockBehaviour.Properties.of().strength(2.0F, 2.0F).sound(SoundType.SLIME_BLOCK));
    public static final DeferredItem<BlockItem> DEAD_ROOTED_BIOMASS_ITEM =
            ITEMS.registerSimpleBlockItem("dead_rooted_biomass_block", DEAD_ROOTED_BIOMASS);

    public static final DeferredBlock<Block> DEAD_CRUSTED_BILE =
            BLOCKS.registerSimpleBlock("dead_crusted_bile_block");
    public static final DeferredItem<BlockItem> DEAD_CRUSTED_BILE_ITEM =
            ITEMS.registerSimpleBlockItem("dead_crusted_bile_block", DEAD_CRUSTED_BILE);


    private static void hazardReg() {
        HazardSystem.register(DEAD_BIOMASS_ITEM.get(), HazardRegistry.makeData(HazardRegistry.RADIATION, 1.0D));
        HazardSystem.register(DEAD_GASTRIC_BIOMASS_ITEM.get(), HazardRegistry.makeData(HazardRegistry.RADIATION, 1.0D));
        HazardSystem.register(DEAD_CALCIFIED_BIOMASS_ITEM.get(), HazardRegistry.makeData(HazardRegistry.RADIATION, 1.0D));
        HazardSystem.register(DEAD_SICKEN_BIOMASS_ITEM.get(), HazardRegistry.makeData(HazardRegistry.RADIATION, 1.0D));
        HazardSystem.register(DEAD_FREEZE_BURNED_BIOMASS_ITEM.get(), HazardRegistry.makeData(HazardRegistry.RADIATION, 1.0D));
        HazardSystem.register(DEAD_ROOTED_BIOMASS_ITEM.get(), HazardRegistry.makeData(HazardRegistry.RADIATION, 1.0D));
        HazardSystem.register(DEAD_CRUSTED_BILE_ITEM.get(), HazardRegistry.makeData(HazardRegistry.RADIATION, 1.0D));
    }


    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB =
            CREATIVE_MODE_TABS.register("example_tab",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.hbmspore"))
                            .withTabsBefore(CreativeModeTabs.COMBAT)
                            .icon(() -> DEAD_BIOMASS_ITEM.get().getDefaultInstance())
                            .displayItems((parameters, output) -> {
                                ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                            })
                            .build()
            );

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public hbmspore(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        //modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want this class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        //modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }


    // Add the example block item to the building blocks tab

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        hazardReg();
        LOGGER.info("HBM Spore Compact server starting");
    }
}