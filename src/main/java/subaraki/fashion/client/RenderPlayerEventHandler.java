package subaraki.fashion.client;

import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderPlayerEventHandler {

    private static final HandleRenderSwap SWAPPER = new HandleRenderSwap();

    @SubscribeEvent
    public void renderPlayerPre(RenderPlayerEvent.Pre event) {

        SWAPPER.swapRenders(event.getPlayer(), event.getRenderer());
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {

        SWAPPER.resetRenders(event.getPlayer(), event.getRenderer());
    }
}
