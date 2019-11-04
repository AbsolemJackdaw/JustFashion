package subaraki.fashion.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import subaraki.fashion.client.render.layer.LayerWardrobe;

public class ClientReferences {

    public static void loadLayers() {

        String types[] = new String[] { "default", "slim" };

        for (String type : types) {
            PlayerRenderer renderer = ((PlayerRenderer) Minecraft.getInstance().getRenderManager().getSkinMap().get(type));
            renderer.addLayer(new LayerWardrobe(renderer));
        }
    }

    /** Used in the packet when logging in to get the list of all layers before the player is actually rendered */
    public static List<LayerRenderer<?, ?>> tryList() throws IllegalArgumentException, IllegalAccessException {

        Field swap_field_layerrenders = null;
        Object swap_list_layerrenders = null;

        List<LayerRenderer<?, ?>> list = new ArrayList<>();

        PlayerRenderer renderer = ((PlayerRenderer) Minecraft.getInstance().getRenderManager().getSkinMap().get("default"));

        if (swap_field_layerrenders == null) {
            swap_field_layerrenders = ObfuscationReflectionHelper.findField(LivingRenderer.class, "field_177097_h");
        }

        if (swap_list_layerrenders == null) {
            swap_list_layerrenders = swap_field_layerrenders.get(renderer);
        }

        if (list.isEmpty()) {
            list = ((List<LayerRenderer<?, ?>>) swap_list_layerrenders);
        }

        return list;
    }
    
    public static boolean isBlockTextureMap(TextureStitchEvent event) {
        return !event.getMap().equals(Minecraft.getInstance().getTextureMap());
    }

}
