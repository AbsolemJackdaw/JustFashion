package subaraki.fashion.server.event;

import net.minecraftforge.common.MinecraftForge;

public class EventRegistryServer {

    public EventRegistryServer() {

        MinecraftForge.EVENT_BUS.register(new AttachEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerTracker());
    }
}
