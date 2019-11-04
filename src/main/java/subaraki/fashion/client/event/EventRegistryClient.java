package subaraki.fashion.client.event;

import net.minecraftforge.common.MinecraftForge;
import subaraki.fashion.client.eventforge_bus.PressKeyEventHandler;
import subaraki.fashion.client.eventforge_bus.RenderPlayerEventHandler;

public class EventRegistryClient {

    public EventRegistryClient() {

        MinecraftForge.EVENT_BUS.register(new RenderPlayerEventHandler());
        MinecraftForge.EVENT_BUS.register(new PressKeyEventHandler());

    }
}
