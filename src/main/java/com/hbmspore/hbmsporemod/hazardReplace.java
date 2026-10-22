
package com.hbmspore.hbmsporemod;

import java.util.HashMap;
import java.util.Map;

import com.hbm.handler.radiation.RadiationSystemNT;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = hbmspore.MODID)
public class hazardReplace {

    private static final Map<BlockKey, Integer> EXPOSURE = new HashMap<>();

    private static final ResourceLocation INFESTED_DIRT =
            ResourceLocation.parse("spore:infested_dirt");
    private static final ResourceLocation INFESTED_STONE =
            ResourceLocation.parse("spore:infested_stone");
    private static final ResourceLocation INFESTED_SAND =
            ResourceLocation.parse("spore:infested_sand");

    private static final double RAD_LIMIT = 1.0D;
    private static final int EXPOSURE_LIMIT = 10;

    private static int ticks;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (++ticks < 20) return;
        ticks = 0;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            for (ServerPlayer player : level.players()) {
                BlockPos center = player.blockPosition();

                for (BlockPos pos : BlockPos.betweenClosed(
                        center.getX() - 4, center.getY() - 3, center.getZ() - 4,
                        center.getX() + 4, center.getY() + 3, center.getZ() + 4)) {

                    BlockState state = level.getBlockState(pos);
                    ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());

                    if (!isSporeBlock(id)) {
                        EXPOSURE.remove(new BlockKey(level.dimension(), pos.asLong()));
                        continue;
                    }

                    BlockKey key = new BlockKey(level.dimension(), pos.asLong());
                    double radiation = RadiationSystemNT.getRadForCoord(level, pos);

                    if (radiation < RAD_LIMIT) {
                        EXPOSURE.remove(key);
                        continue;
                    }

                    int exposure = EXPOSURE.getOrDefault(key, 0) + 1;

                    if (exposure >= EXPOSURE_LIMIT) {

                        if (id.equals(INFESTED_DIRT)) {
                            level.setBlock(pos, hbmspore.DEAD_INFESTED_DIRT.get()
                                    .defaultBlockState(), 3);
                        }
                        else if (id.equals(INFESTED_STONE)) {
                            level.setBlock(pos, hbmspore.DEAD_INFESTED_STONE.get()
                                    .defaultBlockState(), 3);
                        }
                        else if (id.equals(INFESTED_SAND)) {
                            level.setBlock(pos, hbmspore.DEAD_INFESTED_SAND.get()
                                    .defaultBlockState(), 3);
                        }

                        EXPOSURE.remove(key);
                    } else {
                        EXPOSURE.put(key, exposure);
                    }
                }
            }
        }
    }

    private static boolean isSporeBlock(ResourceLocation id) {
        return id.equals(INFESTED_DIRT)
                || id.equals(INFESTED_STONE)
                || id.equals(INFESTED_SAND);
    }

    private record BlockKey(ResourceKey<Level> dimension, long pos) {
    }
}
