package subaraki.fashion.client.event.mod_bus;

import java.util.List;

import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
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
        int size = ResourcePackReader.getWeaponSize();
        Fashion.log.info("MODELS TO LOAD LIST SIZE = " + size);
        List<ResourceLocation> theList = ResourcePackReader.getListForSlot(EnumFashionSlot.WEAPON);
        for (ResourceLocation resLoc : theList) {
            if (ResourcePackReader.isItem(resLoc)) {
                ModelResourceLocation modelres = new ModelResourceLocation(resLoc, "inventory");
                ModelLoader.addSpecialModel(modelres);
                Fashion.log.info(String.format("ITEM MODEL REGISTRY %s", modelres));
            }
        }
    }
}
