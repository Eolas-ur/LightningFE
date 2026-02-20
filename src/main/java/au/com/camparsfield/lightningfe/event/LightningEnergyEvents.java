package au.com.camparsfield.lightningfe.event;

import au.com.camparsfield.lightningfe.LightningFE;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.level.GameEventEvent;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = LightningFE.MODID)
public class LightningEnergyEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean DEBUG = false;
    private static final int FE_PER_STRIKE = 20_000_000;
    
    private static long lastStrikeTick = -1;
    private static final Set<BlockPos> strikePositionsThisTick = new HashSet<>();

    @SubscribeEvent
    public static void onGameEvent(GameEventEvent event) {
        if (event.getLevel().isClientSide() || !(event.getLevel() instanceof Level level)) {
            return;
        }

        if (event.getEvent() == GameEvent.LIGHTNING_STRIKE) {
            long currentTick = level.getGameTime();
            
            if (currentTick != lastStrikeTick) {
                lastStrikeTick = currentTick;
                strikePositionsThisTick.clear();
            }

            BlockPos strikePos = BlockPos.containing(event.getPosition());
            
            if (!strikePositionsThisTick.add(strikePos)) {
                return; // Already processed a strike at this position in this tick
            }

            BlockPos rodPos = null;
            if (isLightningRod(level.getBlockState(strikePos))) {
                rodPos = strikePos;
            } else if (isLightningRod(level.getBlockState(strikePos.below()))) {
                rodPos = strikePos.below();
            }

            if (rodPos != null) {
                distributeEnergy(level, rodPos);
            }
        }
    }

    private static boolean isLightningRod(BlockState state) {
        return state.is(Blocks.LIGHTNING_ROD);
    }

    private static void distributeEnergy(Level level, BlockPos rodPos) {
        int remaining = FE_PER_STRIKE;
        int totalAccepted = 0;
        int[] acceptedPerSide = new int[6];

        Direction[] directions = Direction.values();
        for (int i = 0; i < directions.length; i++) {
            Direction dir = directions[i];
            if (remaining <= 0) break;

            BlockPos neighborPos = rodPos.relative(dir);
            BlockEntity be = level.getBlockEntity(neighborPos);
            if (be != null) {
                IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, level.getBlockState(neighborPos), be, dir.getOpposite());
                if (energyStorage != null) {
                    int accepted = energyStorage.receiveEnergy(remaining, false);
                    remaining -= accepted;
                    totalAccepted += accepted;
                    acceptedPerSide[i] = accepted;
                }
            }
        }

        if (DEBUG) {
            LOGGER.info("Lightning strike at {}", rodPos);
            LOGGER.info("Total FE attempted: {}", FE_PER_STRIKE);
            LOGGER.info("Total FE accepted: {}", totalAccepted);
            LOGGER.info("Total FE discarded: {}", remaining);
            for (int i = 0; i < directions.length; i++) {
                LOGGER.info("Side {} accepted: {}", directions[i], acceptedPerSide[i]);
            }
        }
    }
}