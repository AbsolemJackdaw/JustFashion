package subaraki.fashion.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5HeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.client.render.layer.LayerAestheticHeldItem;
import subaraki.fashion.client.render.layer.LayerFashion;
import subaraki.fashion.client.render.layer.LayerWardrobe;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.server.PacketSyncSavedListToServer;

public class HandleRenderSwap {

    private Field swap_field_layerrenders;
    private Object swap_list_layerrenders;

    public void swapRenders(PlayerEntity player, PlayerRenderer renderer) {

        FashionData.get(player).ifPresent(data -> {

            try {

                // reflection 'swap' fields are volatile and will be set to null after rendering
                // is done
                if (swap_field_layerrenders == null) {
                    swap_field_layerrenders = ObfuscationReflectionHelper.findField(LivingRenderer.class, "field_177097_h");
                }

                if (swap_list_layerrenders == null) {
                    swap_list_layerrenders = swap_field_layerrenders.get(renderer);
                }

                // save original list. not volatile !
                if (data.getSavedOriginalList().isEmpty()) {
                    data.saveVanillaList((List<LayerRenderer<?, ?>>) swap_list_layerrenders);

                    // save original list to server ,so only the class names will be allowed
                    List<String> names = new ArrayList<String>();
                    for (LayerRenderer<?, ?> layer : data.getSavedOriginalList())
                        names.add(layer.getClass().getSimpleName());
                    // send list of class names to server
                    NetworkHandler.NETWORK.sendToServer(new PacketSyncSavedListToServer(names));
                }

                // if you need fashion rendered
                if (data.shouldRenderFashion()) {
                    // and the cached list (original vanilla list + all exterior mod items) is empty
                    if (data.cachedOriginalRenderList == null) {
                        // copy the vanilla list over
                        data.cachedOriginalRenderList = (List<LayerRenderer<?, ?>>) swap_list_layerrenders;
                        // if all cached fashion is empty (previously not wearing any
                        if (data.fashionLayers.isEmpty()) {
                            data.fashionLayers.clear();

                            // add fashion layer and add wardrobe layer to be displayed when changing
                            data.fashionLayers.add(new LayerFashion(renderer));
                            data.fashionLayers.add(new LayerWardrobe(renderer));

                            // if the list of layers to keep is not empty (aka layers are selected)
                            if (!data.keepLayers.isEmpty()) {
                                // add those layers to our fashion list
                                for (LayerRenderer<?, ?> layer : data.keepLayers) {
                                    data.fashionLayers.add(layer);
                                    Fashion.log.debug("Fashion had a render layer Injected.");
                                    Fashion.log.debug(layer.getClass().getName() + " got added.");
                                }
                            }

                            // also add back all most important layers
                            data.fashionLayers.add(new LayerAestheticHeldItem(renderer));
                            data.fashionLayers.add(new ArrowLayer<>(renderer));
                            data.fashionLayers.add(new Deadmau5HeadLayer(renderer));
                            data.fashionLayers.add(new CapeLayer(renderer));
                            data.fashionLayers.add(new HeadLayer<>(renderer));
                            data.fashionLayers.add(new ElytraLayer<>(renderer));

                        }

                        // define the list we are going to swap with the vanilla one
                        swap_field_layerrenders.set(renderer, data.fashionLayers);
                        // System.out.println(data.fashionLayers.size() + " " +
                        // player.getDisplayName().getFormattedText());
                    }
                } else {
                    if (data.cachedOriginalRenderList != null) {
                        swap_field_layerrenders.set(renderer, data.cachedOriginalRenderList);
                        data.cachedOriginalRenderList = null;
                        data.resetSavedOriginalList();
                    }
                }

            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public void resetRenders(PlayerEntity player, PlayerRenderer renderer) {

        FashionData.get(player).ifPresent(data -> {

            try {
                if (swap_field_layerrenders == null)
                    swap_field_layerrenders = ObfuscationReflectionHelper.findField(LivingRenderer.class, "field_177097_h");
                if (swap_list_layerrenders == null)
                    swap_list_layerrenders = swap_field_layerrenders.get(renderer);

                if (data.cachedOriginalRenderList != null) {
                    swap_field_layerrenders.set(renderer, data.cachedOriginalRenderList);
                    data.cachedOriginalRenderList = null;
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }
}
