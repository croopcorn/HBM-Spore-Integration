package com.hbmspore.hbmsporemod;

import com.hbm.handler.radiation.RadiationSystemNT;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@EventBusSubscriber(modid = hbmspore.MODID)
public class hazardReplace {

    private static final Set<ChunkKey> LOADED_CHUNKS = ConcurrentHashMap.newKeySet();

    private static final Queue<ChunkKey> CHUNK_QUEUE = new ConcurrentLinkedQueue<>();

    private static final ResourceLocation AIR = ResourceLocation.parse("minecraft:air");
    private static final ResourceLocation MYCELIUM = ResourceLocation.parse("minecraft:mycelium");

    private static final ResourceLocation BIOMASS = ResourceLocation.parse("spore:biomass_block");
    private static final ResourceLocation GASTRIC_BIOMASS = ResourceLocation.parse("spore:gastric_biomass_block");
    private static final ResourceLocation CALCIFIED_BIOMASS = ResourceLocation.parse("spore:calcified_biomass_block");
    private static final ResourceLocation SICKEN_BIOMASS = ResourceLocation.parse("spore:sicken_biomass_block");
    private static final ResourceLocation FREEZE_BURNED_BIOMASS = ResourceLocation.parse("spore:freeze_burned_biomass");
    private static final ResourceLocation ROOTED_BIOMASS = ResourceLocation.parse("spore:rooted_biomass");
    private static final ResourceLocation CRUSTED_BILE = ResourceLocation.parse("spore:crusted_bile");

    private static final ResourceLocation FUNGAL_STEM_TOP = ResourceLocation.parse("spore:fungal_stem_top");
    private static final ResourceLocation HANGING_FUNGAL_STEM = ResourceLocation.parse("spore:hanging_fungal_stem");
    private static final ResourceLocation FUNGAL_STEM = ResourceLocation.parse("spore:fungal_stem");
    private static final ResourceLocation WALL_GROWTHS_FLESHY = ResourceLocation.parse("spore:wall_growths_fleshy");
    private static final ResourceLocation WALL_GROWTHS_BIG = ResourceLocation.parse("spore:wall_growths_big");
    private static final ResourceLocation WALL_GROWTHS = ResourceLocation.parse("spore:wall_growths");
    private static final ResourceLocation VOCALS = ResourceLocation.parse("spore:vocals");
    private static final ResourceLocation MYCELIUM_VEINS = ResourceLocation.parse("spore:mycelium_veins");
    private static final ResourceLocation FUNGAL_CLAMP = ResourceLocation.parse("spore:fungal_clamp");
    private static final ResourceLocation BLOMFUNG = ResourceLocation.parse("spore:blomfung");
    private static final ResourceLocation BLOOMFUNG2 = ResourceLocation.parse("spore:bloomfung2");
    private static final ResourceLocation FANG_LUMP = ResourceLocation.parse("spore:fang_lump");
    private static final ResourceLocation EXPLODING_LUMP = ResourceLocation.parse("spore:exploding_lump");
    private static final ResourceLocation BIOMASS_BULB = ResourceLocation.parse("spore:biomass_bulb");
    private static final ResourceLocation REMAINS = ResourceLocation.parse("spore:remains");

    private static final Map<ResourceLocation, ResourceLocation> BLOCK_REPLACEMENTS = Map.ofEntries(
            Map.entry(BIOMASS, ResourceLocation.parse("hbmspore:dead_biomass_block")),
            Map.entry(GASTRIC_BIOMASS, ResourceLocation.parse("hbmspore:dead_gastric_biomass_block")),
            Map.entry(CALCIFIED_BIOMASS, ResourceLocation.parse("hbmspore:dead_calcified_biomass_block")),
            Map.entry(SICKEN_BIOMASS, ResourceLocation.parse("hbmspore:dead_sicken_biomass_block")),
            Map.entry(FREEZE_BURNED_BIOMASS, ResourceLocation.parse("hbmspore:dead_freeze_burned_biomass_block")),
            Map.entry(ROOTED_BIOMASS, ResourceLocation.parse("hbmspore:dead_rooted_biomass_block")),
            Map.entry(CRUSTED_BILE, ResourceLocation.parse("hbmspore:dead_crusted_bile_block")),
            Map.entry(MYCELIUM, ResourceLocation.parse("hbm:waste_mycelium")),
            Map.entry(FUNGAL_STEM_TOP, AIR),
            Map.entry(HANGING_FUNGAL_STEM, AIR),
            Map.entry(FUNGAL_STEM, AIR),
            Map.entry(WALL_GROWTHS_FLESHY,AIR),
            Map.entry(WALL_GROWTHS_BIG,AIR),
            Map.entry(WALL_GROWTHS,AIR),
            Map.entry(VOCALS,AIR),
            Map.entry(MYCELIUM_VEINS,AIR),
            Map.entry(FUNGAL_CLAMP,AIR),
            Map.entry(BLOMFUNG,AIR),
            Map.entry(BLOOMFUNG2,AIR),
            Map.entry(FANG_LUMP,AIR),
            Map.entry(EXPLODING_LUMP,AIR),
            Map.entry(BIOMASS_BULB,AIR),
            Map.entry(REMAINS,AIR)
    );

    private static final double RAD_LIMIT = 10.0D;
    private static final int CHECK_INTERVAL = 1;

    private static int ticks = 0;

    private static final Set<ChunkKey> QUEUED_CHUNKS =
            ConcurrentHashMap.newKeySet();


    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ChunkPos pos = event.getChunk().getPos();
        ChunkKey key = new ChunkKey(
                level.dimension(), pos.x, pos.z
        );

        LOADED_CHUNKS.add(key);
        enqueueChunk(key);
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ChunkPos pos = event.getChunk().getPos();
        ChunkKey key = new ChunkKey(
                level.dimension(), pos.x, pos.z
        );

        LOADED_CHUNKS.remove(key);
        CHUNK_QUEUE.removeIf(key::equals);
        QUEUED_CHUNKS.remove(key);
    }

    private static void enqueueChunk(ChunkKey key) {
        if (LOADED_CHUNKS.contains(key)
                && QUEUED_CHUNKS.add(key)) {
            CHUNK_QUEUE.add(key);
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (++ticks < CHECK_INTERVAL) {
            return;
        }

        ticks = 0;

        ChunkKey key = CHUNK_QUEUE.poll();

        if (key == null) {
            return;
        }

        QUEUED_CHUNKS.remove(key);

        if (!LOADED_CHUNKS.contains(key)) {
            return;
        }

        ServerLevel level = null;

        for (ServerLevel candidate : event.getServer().getAllLevels()) {
            if (candidate.dimension().equals(key.dimension())) {
                level = candidate;
                break;
            }
        }

        if (level == null) {
            return;
        }

        try {
            LevelChunk chunk = level.getChunkSource()
                    .getChunkNow(key.x(), key.z());

            if (chunk != null) {
                scanChunk(level, chunk);
            }
        } finally {
            enqueueChunk(key);
        }
    }

    private static void scanChunk(ServerLevel level, LevelChunk chunk) {
        LevelChunkSection[] sections = chunk.getSections();

        boolean radiationChecked = false;
        double chunkRadiation = 0.0D;

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            LevelChunkSection section = sections[sectionIndex];

            if (section == null || section.hasOnlyAir()) {
                continue;
            }

            int baseY = level.getMinBuildHeight() + (sectionIndex << 4);
            int baseX = chunk.getPos().getMinBlockX();
            int baseZ = chunk.getPos().getMinBlockZ();

            for (int y = 0; y < 16; y++) {
                int worldY = baseY + y;

                if (worldY < level.getMinBuildHeight()
                        || worldY >= level.getMaxBuildHeight()) {
                    continue;
                }

                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        BlockState state = section.getBlockState(x, y, z);
                        ResourceLocation id =
                                BuiltInRegistries.BLOCK.getKey(state.getBlock());

                        if (!BLOCK_REPLACEMENTS.containsKey(id)) {
                            continue;
                        }

                        pos.set(baseX + x, worldY, baseZ + z);

                        if (!radiationChecked) {
                            chunkRadiation =
                                    RadiationSystemNT.getRadForCoord(level, pos);
                            radiationChecked = true;

                            if (chunkRadiation < RAD_LIMIT) {
                                return;
                            }
                        }

                        replaceBlock(level, pos, id);
                    }
                }
            }
        }
    }

    private static void replaceBlock(ServerLevel level, BlockPos pos, ResourceLocation id) {
        ResourceLocation replacementId = BLOCK_REPLACEMENTS.get(id);

        if (replacementId == null) {
            return;
        }

        Block replacement = BuiltInRegistries.BLOCK.get(replacementId);
        level.setBlock(pos, replacement.defaultBlockState(), 3);
    }

    private record ChunkKey(ResourceKey<Level> dimension, int x, int z) {
    }
}