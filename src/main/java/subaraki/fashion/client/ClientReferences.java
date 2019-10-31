package subaraki.fashion.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import subaraki.fashion.client.render.layer.LayerWardrobe;

public class ClientReferences {

    public static void loadLayers() {

        String types[] = new String[] { "default", "slim" };

        for (String type : types) {
            PlayerRenderer renderer = ((PlayerRenderer) Minecraft.getInstance().getRenderManager().getSkinMap().get(type));
            renderer.addLayer(new LayerWardrobe(renderer));
        }
    }

    }
