package au.com.lightningfe.event;

import au.com.lightningfe.LightningFE;
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
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.event.VanillaGameEvent;
import org.slf4j.Logger;
import net.neoforged.neoforge.transfer.Transaction;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = LightningFE.MODID)
public class LightningEnergyEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean DEBUG = false;
    private static final int FE_PER_STRIKE = 20_000_000;
    
    private static long lastStrikeTick = -1;
    private static final Set<String> strikeKeysThisTick = new HashSet<>();

    @SubscribeEvent
    public static void onVanillaGameEvent(VanillaGameEvent event) {
        if (event.getLevel().isClientSide() || !(event.getLevel() instanceof Level level)) {
            return;
        }

        if (event.getVanillaEvent() == GameEvent.LIGHTNING_STRIKE) {
            long currentTick = level.getGameTime();
            
            if (currentTick != lastStrikeTick) {
                lastStrikeTick = currentTick;
                strikeKeysThisTick.clear();
            }

            BlockPos strikePos = BlockPos.containing(event.getEventPosition());
            String key = level.dimension().toString() + ":" + strikePos.asLong();
            
            if (!strikeKeysThisTick.add(key)) {
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
                EnergyHandler handler = level.getCapability(Capabilities.Energy.BLOCK, neighborPos, level.getBlockState(neighborPos), be, dir.getOpposite());
                if (handler != null) {
                    try (var tx = Transaction.openRoot()) {
                        long accepted = handler.insert(remaining, tx);
                        if (accepted > 0) {
                            tx.commit();
                            remaining -= (int) accepted;
                            totalAccepted += (int) accepted;
                            acceptedPerSide[i] = (int) accepted;
                        }
                    }
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