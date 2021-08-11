package subaraki.fashion.client.event;

import net.minecraftforge.common.MinecraftForge;
import subaraki.fashion.client.event.forge_bus.HandRenderEvent;
import subaraki.fashion.client.event.forge_bus.PressKeyEventHandler;
import subaraki.fashion.client.event.forge_bus.RenderPlayerEventHandler;

public class EventRegistryClient {

    public EventRegistryClient() {

        MinecraftForge.EVENT_BUS.register(new RenderPlayerEventHandler());
        MinecraftForge.EVENT_BUS.register(new PressKeyEventHandler());
        MinecraftForge.EVENT_BUS.register(new HandRenderEvent());

    }
}
