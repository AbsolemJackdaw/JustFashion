package subaraki.fashion.client.event.forge_bus;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.server.PacketOpenWardrobe;
import subaraki.fashion.screen.WardrobeScreen;

public class PressKeyEventHandler {

    @SubscribeEvent
    public void keyPressed(KeyInputEvent event) {

        if (KeyRegistry.keyWardrobe.isPressed()) {

            NetworkHandler.NETWORK.sendToServer(new PacketOpenWardrobe());

            FashionData.get(Minecraft.getInstance().player).ifPresent(data -> {
                data.setInWardrobe(true);

            });
            Minecraft.getInstance().displayGuiScreen(new WardrobeScreen());
        }
    }
}
