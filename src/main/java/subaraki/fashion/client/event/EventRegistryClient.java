package subaraki.fashion.client.event;

import net.minecraftforge.common.MinecraftForge;
import subaraki.fashion.client.RenderPlayerEventHandler;

public class EventRegistryClient {

    public EventRegistryClient() {

        MinecraftForge.EVENT_BUS.register(new RenderPlayerEventHandler());
        MinecraftForge.EVENT_BUS.register(new PressKeyEventHandler());

    }
}
