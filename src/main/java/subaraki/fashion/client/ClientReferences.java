package subaraki.fashion.client;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import subaraki.fashion.client.render.layer.LayerWardrobe;

public class ClientReferences {

    public static PlayerEntity getClientPlayer() {

        return Minecraft.getInstance().player;
    }

    public static World getClientWorld() {

        return Minecraft.getInstance().world;
    }

    public static PlayerEntity getClientPlayerByUUID(UUID uuid) {

        return Minecraft.getInstance().world.getPlayerByUuid(uuid);
    }

    public static void loadLayers() {

        String types[] = new String[] { "default", "slim" };

        for (String type : types) {
            PlayerRenderer renderer = ((PlayerRenderer) Minecraft.getInstance().getRenderManager().getSkinMap().get(type));
            renderer.addLayer(new LayerWardrobe(renderer));
        }
    }

    public static EntityRendererManager getRenderManager() {

        return Minecraft.getInstance().getRenderManager();
    }
}
