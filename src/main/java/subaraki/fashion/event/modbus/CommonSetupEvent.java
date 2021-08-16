package subaraki.fashion.event.modbus;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import subaraki.fashion.capability.FashionCapability;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.network.NetworkHandler;

@Mod.EventBusSubscriber(modid = Fashion.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetupEvent {

    public static void startCommonSetup(FMLCommonSetupEvent event) {
        new FashionCapability().register();
        new NetworkHandler();
    }
}
