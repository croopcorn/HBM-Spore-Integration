
package com.hbmspore.hbmsporemod;

import com.hbm.handler.radiation.RadiationSystemNT;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

@EventBusSubscriber(modid = hbmspore.MODID)
public class hazardReplace {

    private static final Set<ChunkKey> LOADED_CHUNKS = new HashSet<>();
    private static final Queue<ChunkKey> CHUNK_QUEUE = new ArrayDeque<>();

    private static final ResourceLocation BIOMASS =
            ResourceLocation.parse("spore:biomass_block");
    private static final ResourceLocation GASTRIC_BIOMASS =
            ResourceLocation.parse("spore:gastric_biomass_block");
    private static final ResourceLocation CALCIFIED_BIOMASS =
            ResourceLocation.parse("spore:calcified_biomass_block");
    private static final ResourceLocation SICKEN_BIOMASS =
            ResourceLocation.parse("spore:sicken_biomass_block");
    private static final ResourceLocation FREEZE_BURNED_BIOMASS =
            ResourceLocation.parse("spore:freeze_burned_biomass");
    private static final ResourceLocation ROOTED_BIOMASS =
            ResourceLocation.parse("spore:rooted_biomass");
    private static final ResourceLocation CRUSTED_BILE =
            ResourceLocation.parse("spore:crusted_bile");

    private static final double RAD_LIMIT = 1.0D;
    private static final int CHECK_INTERVAL = 5;

    private static int ticks;

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ChunkPos pos = event.getChunk().getPos();
        ChunkKey key = new ChunkKey(level.dimension(), pos.x, pos.z);

        if (LOADED_CHUNKS.add(key)) {
            CHUNK_QUEUE.add(key);
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ChunkPos pos = event.getChunk().getPos();
        ChunkKey key = new ChunkKey(level.dimension(), pos.x, pos.z);

        LOADED_CHUNKS.remove(key);
        CHUNK_QUEUE.removeIf(key::equals);
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
            LOADED_CHUNKS.remove(key);
            return;
        }

        LevelChunk chunk = level.getChunkSource().getChunkNow(key.x(), key.z());

        if (chunk == null) {
            LOADED_CHUNKS.remove(key);
            return;
        }

        scanChunk(level, chunk);

        if (LOADED_CHUNKS.contains(key)) {
            CHUNK_QUEUE.add(key);
        }
    }

    private static void scanChunk(ServerLevel level, LevelChunk chunk) {
        LevelChunkSection[] sections = chunk.getSections();
        ChunkPos chunkPos = chunk.getPos();

        int baseX = chunkPos.getMinBlockX();
        int baseZ = chunkPos.getMinBlockZ();

        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            LevelChunkSection section = sections[sectionIndex];

            if (section.hasOnlyAir()) {
                continue;
            }

            int baseY = chunk.getSectionYFromSectionIndex(sectionIndex) * 16;

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

                        if (!isSporeBlock(id)) {
                            continue;
                        }

                        BlockPos pos = new BlockPos(
                                baseX + x,
                                worldY,
                                baseZ + z
                        );

                        double radiation =
                                RadiationSystemNT.getRadForCoord(level, pos);

                        if (radiation < RAD_LIMIT) {
                            continue;
                        }

                        replaceBlock(level, pos, id);
                    }
                }
            }
        }
    }

    private static void replaceBlock(
            ServerLevel level,
            BlockPos pos,
            ResourceLocation id
    ) {
        if (id.equals(BIOMASS)) {
            level.setBlock(pos, hbmspore.DEAD_BIOMASS.get().defaultBlockState(), 3);
        } else if (id.equals(GASTRIC_BIOMASS)) {
            level.setBlock(pos, hbmspore.DEAD_GASTRIC_BIOMASS.get().defaultBlockState(), 3);
        } else if (id.equals(CALCIFIED_BIOMASS)) {
            level.setBlock(pos, hbmspore.DEAD_CALCIFIED_BIOMASS.get().defaultBlockState(), 3);
        } else if (id.equals(SICKEN_BIOMASS)) {
            level.setBlock(pos, hbmspore.DEAD_SICKEN_BIOMASS.get().defaultBlockState(), 3);
        } else if (id.equals(FREEZE_BURNED_BIOMASS)) {
            level.setBlock(pos, hbmspore.DEAD_FREEZE_BURNED_BIOMASS.get().defaultBlockState(), 3);
        } else if (id.equals(ROOTED_BIOMASS)) {
            level.setBlock(pos, hbmspore.DEAD_ROOTED_BIOMASS.get().defaultBlockState(), 3);
        } else if (id.equals(CRUSTED_BILE)) {
            level.setBlock(pos, hbmspore.DEAD_CRUSTED_BILE.get().defaultBlockState(), 3);
        }
    }

    private static boolean isSporeBlock(ResourceLocation id) {
        return id.equals(BIOMASS)
                || id.equals(GASTRIC_BIOMASS)
                || id.equals(CALCIFIED_BIOMASS)
                || id.equals(SICKEN_BIOMASS)
                || id.equals(FREEZE_BURNED_BIOMASS)
                || id.equals(ROOTED_BIOMASS)
                || id.equals(CRUSTED_BILE);
    }

    private record ChunkKey(ResourceKey<Level> dimension, int x, int z) {
    }
}
