package subaraki.fashion.event.forgebus;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import subaraki.fashion.mod.Fashion;

@Mod.EventBusSubscriber(modid = Fashion.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyRegistry {

    public static KeyMapping keyWardrobe;

    @SubscribeEvent
    public static void launchClientEvent(FMLClientSetupEvent event) {
        new KeyRegistry().registerKey();
    }

    public void registerKey() {

        keyWardrobe = new KeyMapping("Wardrobe", GLFW.GLFW_KEY_SEMICOLON, "Wardrobe");
        ClientRegistry.registerKeyBinding(keyWardrobe);
    }

}
