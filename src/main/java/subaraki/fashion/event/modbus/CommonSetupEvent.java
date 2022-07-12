package subaraki.fashion.event.modbus;

import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
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
        NetworkHandler.init();
    }

    @SubscribeEvent
    public static void capRegistry(RegisterCapabilitiesEvent event) {
        event.register(FashionData.class);
    }
}
