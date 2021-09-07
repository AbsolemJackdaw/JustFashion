package subaraki.fashion.event.modbus;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.network.NetworkHandler;

@Mod.EventBusSubscriber(modid = Fashion.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetupEvent {

    @SubscribeEvent
    public static void startCommonSetup(FMLCommonSetupEvent event) {
        new NetworkHandler();
        //forge 42 will move this to register capability event
        CapabilityManager.INSTANCE.register(FashionData.class);
    }
}
