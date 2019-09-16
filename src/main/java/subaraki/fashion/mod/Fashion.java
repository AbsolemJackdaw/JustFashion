package subaraki.fashion.mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
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
import subaraki.fashion.client.ClientReferences;
import subaraki.fashion.client.ResourcePackReader;
import subaraki.fashion.client.event.EventRegistryClient;
import subaraki.fashion.client.event.KeyRegistry;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.screen.WardrobeContainer;
import subaraki.fashion.screen.WardrobeScreen;
import subaraki.fashion.server.event.EventRegistryServer;

@Mod(Fashion.MODID)
@EventBusSubscriber(modid = Fashion.MODID, bus = Bus.MOD)
public class Fashion {

    public static final String MODID = "fashion";
    public static final String FASHIONPACK = "fashion packs";

    public static Logger log = LogManager.getLogger(MODID);

    public Fashion() {

        // Register doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        // Register commonSetup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        new FashionCapability().register();
        new EventRegistryServer();
        new NetworkHandler();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {

        new EventRegistryClient();
        new ResourcePackReader();
        new KeyRegistry().registerKey();
        ScreenManager.registerFactory(ObjectHolders.WARDROBECONTAINER, WardrobeScreen::new);
        ClientReferences.loadLayers();
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {

        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> new WardrobeContainer(ObjectHolders.WARDROBECONTAINER, windowId))
                .setRegistryName("wardrobe_container"));

    }

    @SubscribeEvent
    public void stitchTextures(TextureStitchEvent.Pre event) {

        Fashion.log.info("stitching weapon textures");

        stitch(ResourcePackReader.partsSize(EnumFashionSlot.WEAPON), EnumFashionSlot.WEAPON, event);
        stitch(ResourcePackReader.partsSize(EnumFashionSlot.SHIELD), EnumFashionSlot.SHIELD, event);

    }

    private void stitch(int size, EnumFashionSlot slot, TextureStitchEvent.Pre event) {

        if (size > 1) {
            for (int partIndex = 1; partIndex < size; partIndex++)
                if (ResourcePackReader.getResourceForPart(slot, partIndex) != null) {
                    ResourceLocation resLoc = ResourcePackReader.getTextureForStitcher(slot, partIndex);
                    if (resLoc != null) {
                        event.addSprite(resLoc);
                    } else {
                        Fashion.log.warn("tried loading a null resourcelocation for weapons.");
                        Fashion.log.warn(ResourcePackReader.getResourceForPart(slot, partIndex));
                    }
                }
        }
    }

    @ObjectHolder(MODID)
    public static class ObjectHolders {

        @ObjectHolder("wardrobe_container")
        public static final ContainerType<WardrobeContainer> WARDROBECONTAINER = null;
    }
}
