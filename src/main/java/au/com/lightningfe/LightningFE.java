package au.com.lightningfe;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(LightningFE.MODID)
public class LightningFE {
    public static final String MODID = "lightningfe";
    private static final Logger LOGGER = LogUtils.getLogger();

    public LightningFE(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP - LightningFE Loaded!");
    }
}