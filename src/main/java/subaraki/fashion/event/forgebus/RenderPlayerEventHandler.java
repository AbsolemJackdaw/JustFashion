package subaraki.fashion.event.forgebus;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import subaraki.fashion.mod.Fashion;

@Mod.EventBusSubscriber(modid = Fashion.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RenderPlayerEventHandler {

    @SubscribeEvent
    public static void renderPlayerPre(RenderPlayerEvent.Pre event) {

        Fashion.SWAPPER.swapRenders(event.getPlayer(), event.getRenderer());
    }

    @SubscribeEvent
    public static void renderPlayerPost(RenderPlayerEvent.Post event) {

        Fashion.SWAPPER.resetRenders(event.getPlayer(), event.getRenderer());
    }
}
