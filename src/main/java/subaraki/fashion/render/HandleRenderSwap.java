package subaraki.fashion.render;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.event.modbus.BindLayersEvent;
import subaraki.fashion.mod.Fashion;

import java.lang.reflect.Field;
import java.util.List;

public class HandleRenderSwap {

    private Field swap_field_layerrenders;
    private Object swap_list_layerrenders;

    public void swapRenders(Player player, PlayerRenderer renderer) {
        resetAllBeforeResourceReload(player, renderer);

        FashionData.get(player).ifPresent(data -> {
            try {

                // reflection 'swap' fields are volatile and will be set to null after rendering
                // is done
                if (swap_field_layerrenders == null) {
                    swap_field_layerrenders = ObfuscationReflectionHelper.findField(LivingEntityRenderer.class, Fashion.obfLayerName); //f_115291_   layers
                }

                if (swap_list_layerrenders == null) {
                    swap_list_layerrenders = swap_field_layerrenders.get(renderer);
                }

                // save mod list. not volatile !
                if (data.getSavedLayers().isEmpty()) {
                    data.saveOriginalList((List<RenderLayer<?, ?>>) swap_list_layerrenders);
                }

                // if you need fashion rendered
                if (data.shouldRenderFashion()) {
                    // and the cached list (original vanilla list + all exterior mod items) is empty
                    if (data.cachedOriginalRenderList == null) {
                        // copy the vanilla list over
                        data.cachedOriginalRenderList = (List<RenderLayer<?, ?>>) swap_list_layerrenders;

                        // if all cached fashion is empty (previously not wearing any
                        if (data.fashionLayers.isEmpty()) {

                            // add cached layers for fashion : items and armor
                            for (RenderLayer<?, ?> fashionlayer : BindLayersEvent.getMappedfashion().keySet()) {
                                if (BindLayersEvent.getMappedfashion().get(fashionlayer).equals(renderer))
                                    data.fashionLayers.add(fashionlayer);
                            }

                            // if the list of layers to keep is not empty (aka layers are selected)
                            if (!data.hasOtherModLayersToRender()) {
                                // add those layers to our fashion list
                                data.fashionLayers.addAll(data.getLayersToKeep());
                            }

                            // add all vanilla layers back , except for items and armor
                            for (RenderLayer<?, ?> layersFromVanilla : data.getVanillaLayersList()) {
                                if (layersFromVanilla instanceof HumanoidArmorLayer || layersFromVanilla instanceof ItemInHandLayer)
                                    continue;
                                data.fashionLayers.add(layersFromVanilla);
                            }
                        }

                        // swap renderers
                        swap_field_layerrenders.set(renderer, data.fashionLayers);
                    }
                } else {
                    // if fashion does not need to be rendered , we restore the field to the
                    // original list we saved
                    if (data.cachedOriginalRenderList != null) {
                        swap_field_layerrenders.set(renderer, data.cachedOriginalRenderList);
                        data.cachedOriginalRenderList = null;
                    }
                }

            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public void resetRenders(Player player, PlayerRenderer renderer) {
        resetAllBeforeResourceReload(player, renderer);
        FashionData.get(player).ifPresent(data -> {

            try {
                if (swap_field_layerrenders == null)
                    swap_field_layerrenders = ObfuscationReflectionHelper.findField(LivingEntityRenderer.class, Fashion.obfLayerName); // f_115291_  layers
                if (swap_list_layerrenders == null)
                    swap_list_layerrenders = swap_field_layerrenders.get(renderer);

                // reset rendering to the cached list of all layers when the game started
                if (data.cachedOriginalRenderList != null) {
                    swap_field_layerrenders.set(renderer, data.cachedOriginalRenderList);
                    data.cachedOriginalRenderList = null;
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

    }

    public void resetAllBeforeResourceReload(Player player, PlayerRenderer playerRenderer) {
        if (player != null)
            FashionData.get(player).ifPresent(data -> {
                swap_field_layerrenders = null;
                swap_list_layerrenders = null;
                data.checkResourceReloadAndReset(playerRenderer);
            });
    }
}
