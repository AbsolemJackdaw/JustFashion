package subaraki.fashion.event.modbus;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import subaraki.fashion.mod.Fashion;

@Mod.EventBusSubscriber(modid = Fashion.MODID, bus = Bus.MOD)
public class ModelLoadingEvent {

    @SubscribeEvent
    public static void registerItemModel(ModelRegistryEvent event) {
        ResourceLocation wardrobe = new ResourceLocation("fashion:wardrobe");
        ModelLoader.addSpecialModel(wardrobe);
    }
}
