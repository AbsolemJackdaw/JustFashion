package subaraki.fashion.mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import subaraki.fashion.capability.FashionCapability;
import subaraki.fashion.handler.client.ClientEventHandler;
import subaraki.fashion.handler.client.KeyRegistry;
import subaraki.fashion.handler.client.ResourcePackReader;
import subaraki.fashion.handler.common.PlayerTracker;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.screen.WardrobeContainer;
import subaraki.fashion.screen.WardrobeScreen;

@Mod(Fashion.MODID)
@EventBusSubscriber(modid = Fashion.MODID, bus = Bus.MOD)
public class Fashion {

    public static final String MODID = "fashion";
    public static final String NAME = "fashion mod";
    public static final String VERSION = "1.11.2 2.0.0.0";
    public static final String DEPENDENCY = "required-after:subcommonlib";

    public static final String FASHIONPACK = "fashion packs";

    public static Logger log = LogManager.getLogger(MODID);

    private ResourcePackReader rpr = null;

    public Fashion() {

        // Register doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        // Register commonSetup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        new FashionCapability().register();
        new PlayerTracker();
        new NetworkHandler();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

        rpr = new ResourcePackReader();

        rpr.registerReloadListener();
        rpr.loadFashionPacks(); // used to be called in 1.12 upon registry. needs to be triggered manually now
                                // for the first time

        new KeyRegistry().registerKey();

        ScreenManager.registerFactory(ObjectHolders.WARDROBECONTAINER, WardrobeScreen::new);

        new ClientEventHandler().registerLayers();

    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {

        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new WardrobeContainer(ObjectHolders.WARDROBECONTAINER, windowId))
                .setRegistryName("wardrobe_container"));

    }

    @ObjectHolder(MODID)
    public static class ObjectHolders {

        @ObjectHolder("wardrobe_container")
        public static final ContainerType<WardrobeContainer> WARDROBECONTAINER = null;
    }
}
