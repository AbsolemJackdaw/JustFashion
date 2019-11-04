package subaraki.fashion.server.event;

import net.minecraftforge.common.MinecraftForge;
import subaraki.fashion.server.event.forge_bus.AttachEventHandler;
import subaraki.fashion.server.event.forge_bus.PlayerTracker;

public class EventRegistryServer {

    public EventRegistryServer() {

        MinecraftForge.EVENT_BUS.register(new AttachEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerTracker());
    }
}
