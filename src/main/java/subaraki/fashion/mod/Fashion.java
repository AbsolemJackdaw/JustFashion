package subaraki.fashion.mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import subaraki.fashion.capability.FashionCapability;
import subaraki.fashion.client.ClientReferences;
import subaraki.fashion.client.event.EventRegistryClient;
import subaraki.fashion.client.event.forge_bus.KeyRegistry;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.server.event.EventRegistryServer;

@Mod(Fashion.MODID)
@EventBusSubscriber(modid = Fashion.MODID, bus = Bus.MOD)
public class Fashion {

    public static final String MODID = "fashion";
    public static final String FASHIONPACK = "fashion packs";

    public static Logger log = LogManager.getLogger(MODID);

    static boolean isDev = false;
    public static String obfLayerName = isDev ? "layers" : "field_177097_h"; //"f_115291_" / "layers"


    public Fashion() {

        // Register doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        // Register commonSetup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigData.SERVER_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigData.CLIENT_SPEC);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modConfig);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

        new FashionCapability().register();
        new EventRegistryServer();
        new NetworkHandler();
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {

        new EventRegistryClient();
        new KeyRegistry().registerKey();
        ClientReferences.loadLayers();
    }

    public void modConfig(ModConfig.ModConfigEvent event)
    {

        ModConfig config = event.getConfig();
        if (config.getSpec() == ConfigData.CLIENT_SPEC)
            ConfigData.refreshClient();
        else
            if (config.getSpec() == ConfigData.SERVER_SPEC)
                ConfigData.refreshServer();
    }
}
