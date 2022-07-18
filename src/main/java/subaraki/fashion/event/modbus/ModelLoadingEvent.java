package subaraki.fashion.event.modbus;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.util.ResourcePackReader;

@Mod.EventBusSubscriber(modid = Fashion.MODID, bus = Bus.MOD)
public class ModelLoadingEvent {

    @SubscribeEvent
    public static void registerItemModel(ModelEvent.RegisterAdditional event) {
        event.register(new ResourceLocation("fashion:wardrobe"));
        if (!ResourcePackReader.queueModelForRegistry.isEmpty())
            ResourcePackReader.queueModelForRegistry.forEach(event::register);
    }
}
