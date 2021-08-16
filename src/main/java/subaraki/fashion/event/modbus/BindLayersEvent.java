package subaraki.fashion.event.modbus;

import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.render.layer.LayerAestheticHeldItem;
import subaraki.fashion.render.layer.LayerFashion;
import subaraki.fashion.render.layer.LayerWardrobe;

import java.util.HashMap;

@Mod.EventBusSubscriber(modid = Fashion.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BindLayersEvent {

    private static HashMap<RenderLayer<?, ?>, PlayerRenderer> mappedfashion = new HashMap<>();

    @SubscribeEvent
    public static void layers(EntityRenderersEvent.AddLayers event) {

        //layers get reloaded when resources are reloaded.
        //clear map or you'll get duplicates
        mappedfashion.clear();

        String[] types = new String[]{"default", "slim"};
        for (String type : types)
            if (event.getSkin(type) instanceof PlayerRenderer renderer) {
                renderer.addLayer(new LayerWardrobe(renderer));
                mappedfashion.put(new LayerAestheticHeldItem(renderer), renderer);
                mappedfashion.put(new LayerFashion(renderer), renderer);
            }
    }

    public static HashMap<RenderLayer<?, ?>, PlayerRenderer> getMappedfashion() {

        return mappedfashion;
    }

}