package subaraki.fashion.client.event.mod_bus;

import lib.modelloader.ModelHandle;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import subaraki.fashion.client.ResourcePackReader;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.mod.Fashion;

@Mod.EventBusSubscriber(modid = Fashion.MODID, bus = Bus.MOD)
public class ModelLoadingEvent {

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
}
