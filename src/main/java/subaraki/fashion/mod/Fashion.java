package subaraki.fashion.mod;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import subaraki.fashion.render.HandleRenderSwap;
import subaraki.fashion.util.ConfigData;

@Mod(Fashion.MODID)
@EventBusSubscriber(modid = Fashion.MODID, bus = Bus.MOD)
public class Fashion {

    public static final String MODID = "fashion";
    public static final HandleRenderSwap SWAPPER = new HandleRenderSwap();
    public static Logger log = LogManager.getLogger(MODID);

    static boolean isDev = false;
    public static String obfLayerName = isDev ? "layers" : "f_115291_"; //"f_115291_" / "layers"

    public Fashion() {

        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigData.SERVER_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigData.CLIENT_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modConfig);
    }

    public void modConfig(ModConfigEvent event) {

        ModConfig config = event.getConfig();
        if (config.getSpec() == ConfigData.CLIENT_SPEC)
            ConfigData.refreshClient();
        else if (config.getSpec() == ConfigData.SERVER_SPEC)
            ConfigData.refreshServer();
    }
}
