package subaraki.fashion.client.event.forge_bus;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.server.PacketSetInWardrobeToTrackedPlayers;
import subaraki.fashion.screen.WardrobeScreen;

public class PressKeyEventHandler {

    @SubscribeEvent
    public void keyPressed(KeyInputEvent event) {

        if (KeyRegistry.keyWardrobe.consumeClick()) {

            FashionData.get(Minecraft.getInstance().player).ifPresent(data -> {
                data.setInWardrobe(true);

            });

            NetworkHandler.NETWORK.sendToServer(new PacketSetInWardrobeToTrackedPlayers(true));

            Minecraft.getInstance().setScreen(new WardrobeScreen());
        }
    }
}
