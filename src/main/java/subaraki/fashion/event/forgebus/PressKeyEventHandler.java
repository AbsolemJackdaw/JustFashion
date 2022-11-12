package subaraki.fashion.event.forgebus;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.server.PacketSetInWardrobeToTrackedPlayers;
import subaraki.fashion.screen.WardrobeScreen;

@Mod.EventBusSubscriber(modid = Fashion.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PressKeyEventHandler {

    @SubscribeEvent
    public static void keyPressed(InputEvent.Key event) {

        if (KeyRegistry.keyWardrobe.consumeClick() && Minecraft.getInstance().player != null) {

            FashionData.get(Minecraft.getInstance().player).ifPresent(data -> {
                data.setInWardrobe(true);
            });

            NetworkHandler.NETWORK.sendToServer(new PacketSetInWardrobeToTrackedPlayers(true));

            Minecraft.getInstance().setScreen(new WardrobeScreen());
        }
    }
}