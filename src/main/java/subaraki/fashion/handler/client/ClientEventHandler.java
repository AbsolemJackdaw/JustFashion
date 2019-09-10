package subaraki.fashion.handler.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.PacketOpenWardrobe;
import subaraki.fashion.render.layer.LayerWardrobe;

public class ClientEventHandler {

    private static final HandleRenderSwap SWAPPER = new HandleRenderSwap();

    public ClientEventHandler() {

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void registerLayers() {

        String types[] = new String[] { "default", "slim" };

        for (String type : types) {
            PlayerRenderer renderer = ((PlayerRenderer) Minecraft.getInstance().getRenderManager().getSkinMap().get(type));
            renderer.addLayer(new LayerWardrobe(renderer));
        }
    }

    @SubscribeEvent
    public void renderPlayerPre(RenderPlayerEvent.Pre event) {

        SWAPPER.swapRenders(event.getPlayer(), event.getRenderer());
    }

    @SubscribeEvent
    public void renderPlayerPost(RenderPlayerEvent.Post event) {

        SWAPPER.resetRenders(event.getPlayer(), event.getRenderer());
    }

    @SubscribeEvent
    public void keyPressed(KeyInputEvent event) {

        if (KeyRegistry.keyWardrobe.isPressed()) {

            NetworkHandler.NETWORK.sendToServer(new PacketOpenWardrobe());
            FashionData.get(Minecraft.getInstance().player).setInWardrobe(true);
        }
    }
}