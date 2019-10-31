package subaraki.fashion.client.event;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyRegistry {

    public static KeyBinding keyWardrobe;

    public void registerKey() {

        keyWardrobe = new KeyBinding("Wardrobe", GLFW.GLFW_KEY_W, "Wardrobe");
        ClientRegistry.registerKeyBinding(keyWardrobe);
    }
}
