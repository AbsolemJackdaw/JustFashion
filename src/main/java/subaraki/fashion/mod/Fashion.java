package subaraki.fashion.mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lib.modelloader.ModelHandle;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
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
    public static void registerItemModel(ModelRegistryEvent event) {

        Fashion.log.info("FIRING MODEL LOADER REGISTRY");
        int size = ResourcePackReader.partsSize(EnumFashionSlot.WEAPON);
        Fashion.log.info("MODELS TO LOAD LIST SIZE = " + size);
        for (int i = 0; i < size; i++) {
            if (ResourcePackReader.isItem(i)) {
                ModelHandle handle = ResourcePackReader.getAestheticWeapon(i);
                ModelResourceLocation modelres = new ModelResourceLocation(handle.getModel(), "inventory");
                ModelLoader.addSpecialModel(modelres);
                Fashion.log.info(String.format("ITEM MODEL REGISTRY %s", modelres));
            }
        }
    }

    @SubscribeEvent
    public static void stitchTextures(TextureStitchEvent.Pre event) {

        if (ClientReferences.isBlockTextureMap(event))
            return;

        Fashion.log.debug("stitching weapon textures");

        stitch(EnumFashionSlot.WEAPON, event);
        stitch(EnumFashionSlot.SHIELD, event);

    }

    private static void stitch(EnumFashionSlot slot, TextureStitchEvent.Pre event) {

        for (ResourceLocation resLoc : ResourcePackReader.getTexturesForStitcher(slot))
            if (resLoc != null) {
                event.addSprite(resLoc);
                Fashion.log.info("stitched " + resLoc.toString());
            } else {
                Fashion.log.warn(String.format(
                        "%n SKIPPED STITCHING A NULL %s RESOURCE %n This is normal only once, and for shields. %n Check your fashion resourcepack json for any path errors if it happens more then once ! ",
                        slot.name()));
            }
    }

    @ObjectHolder(MODID)
    public static class ObjectHolders {

        @ObjectHolder("wardrobe_container")
        public static final ContainerType<WardrobeContainer> WARDROBECONTAINER = null;
    }
}
