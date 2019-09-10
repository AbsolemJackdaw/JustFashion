package subaraki.fashion.handler.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyRegistry {

    public static KeyBinding keyWardrobe;

    public void registerKey() {

        keyWardrobe = new KeyBinding("Wardrobe", InputMappings.getInputByName("key.keyboard.w").getKeyCode(), "Wardrobe");
        ClientRegistry.registerKeyBinding(keyWardrobe);
    }
}
