
package com.hbmspore.hbmsporemod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.util.RandomSource;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = hbmspore.MODID)
public class physicReplaceLogic extends Block {

    public static final IntegerProperty STEPS =
            IntegerProperty.create("steps", 0, 9);

    private final ResourceLocation replacementId;

    private static final Map<UUID, BlockPos> LAST_POSITIONS =
            new HashMap<>();

    public physicReplaceLogic(
            ResourceLocation replacementId,
            Properties properties
    ) {
        super(properties);
        this.replacementId = replacementId;

        registerDefaultState(
                stateDefinition.any().setValue(STEPS, 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(STEPS);
    }

    @Override
    public void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        if (level.isRainingAt(pos.above())) {
            replaceBlock(level, pos);
        }
    }

    // Count entering a new block position, rather than every tick
    // while the player stands still.
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        UUID uuid = player.getUUID();
        BlockPos current = player.getOnPos().immutable();
        BlockPos previous = LAST_POSITIONS.put(uuid, current);

        if (previous == null || previous.equals(current)) {
            return;
        }

        BlockState state = level.getBlockState(current);

        if (state.getBlock() instanceof physicReplaceLogic block) {
            block.countStep(level, current, state);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(
            PlayerEvent.PlayerLoggedOutEvent event
    ) {
        LAST_POSITIONS.remove(event.getEntity().getUUID());
    }

    private void countStep(
            ServerLevel level,
            BlockPos pos,
            BlockState state
    ) {
        int steps = state.getValue(STEPS);

        if (steps >= 9) {
            replaceBlock(level, pos);
        } else {
            level.setBlock(
                    pos,
                    state.setValue(STEPS, steps + 1),
                    3
            );
        }
    }

    private void replaceBlock(ServerLevel level, BlockPos pos) {
        var replacement =
                BuiltInRegistries.BLOCK.getOptional(replacementId);

        if (replacement.isEmpty()) {
            hbmspore.LOGGER.warn(
                    "Cannot replace dead infested block: unknown block {}",
                    replacementId
            );
            return;
        }

        level.setBlock(
                pos,
                replacement.get().defaultBlockState(),
                3
        );
    }
}
