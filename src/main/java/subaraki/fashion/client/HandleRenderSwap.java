package subaraki.fashion.client;

import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.Fashion;

import java.lang.reflect.Field;
import java.util.List;

public class HandleRenderSwap {

    private Field swap_field_layerrenders;
    private Object swap_list_layerrenders;

    public void swapRenders(PlayerEntity player, PlayerRenderer renderer) {
        resetAllBeforeResourceReload(player, renderer);
        FashionData.get(player).ifPresent(data -> {

            try {

                // reflection 'swap' fields are volatile and will be set to null after rendering
                // is done
                if (swap_field_layerrenders == null) {
                    swap_field_layerrenders = ObfuscationReflectionHelper.findField(LivingRenderer.class, Fashion.obfLayerName);
                }

                if (swap_list_layerrenders == null) {
                    swap_list_layerrenders = swap_field_layerrenders.get(renderer);
                }

                // save mod list. not volatile !
                if (data.getSavedLayers().isEmpty()) {
                    data.saveOriginalList((List<LayerRenderer<?, ?>>) swap_list_layerrenders);

                    //this feature got removed with the change to name based keepLayer checking.
                    //it is no longer needed to check these layers against the full list of saved layers
                    // save mod list to server , used for toggling save purposes (only names are allowed on server)
                    // save mod list to server , used for toggling save purposes (only names aare allowed on server)
//                    List<String> names = new ArrayList<String>();
//                    for (LayerRenderer<?, ?> layer : data.getOtherModLayersList())
//                        names.add(layer.getClass().getSimpleName());
//                    // send list of class names to server
//                    NetworkHandler.NETWORK.sendToServer(new PacketSyncSavedListToServer(names));
                }

                // if you need fashion rendered
                if (data.shouldRenderFashion()) {
                    // and the cached list (original vanilla list + all exterior mod items) is empty
                    if (data.cachedOriginalRenderList == null) {
                        // copy the vanilla list over
                        data.cachedOriginalRenderList = (List<LayerRenderer<?, ?>>) swap_list_layerrenders;

                        // if all cached fashion is empty (previously not wearing any
                        if (data.fashionLayers.isEmpty()) {

                            // add cached layers for fashion : items and armor
                            for (LayerRenderer<?, ?> fashionlayer : ClientReferences.getMappedfashion().keySet()) {
                                if (ClientReferences.getMappedfashion().get(fashionlayer).equals(renderer))
                                    data.fashionLayers.add(fashionlayer);
                            }

                            // if the list of layers to keep is not empty (aka layers are selected)
                            if (!data.hasOtherModLayersToRender()) {
                                // add those layers to our fashion list
                                data.fashionLayers.addAll(data.getLayersToKeep());
                            }

                            // add all vanilla layers back , except for items and armor
                            for (LayerRenderer<?, ?> layersFromVanilla : data.getVanillaLayersList()) {
                                if (layersFromVanilla instanceof BipedArmorLayer || layersFromVanilla instanceof HeldItemLayer)
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

    public void resetRenders(PlayerEntity player, PlayerRenderer renderer) {
        resetAllBeforeResourceReload(player, renderer);
        FashionData.get(player).ifPresent(data -> {

            try {
                if (swap_field_layerrenders == null)
                    swap_field_layerrenders = ObfuscationReflectionHelper.findField(LivingRenderer.class, Fashion.obfLayerName);
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

    public void resetAllBeforeResourceReload(PlayerEntity player, PlayerRenderer playerRenderer) {
        if (player != null)
            FashionData.get(player).ifPresent(data -> {
                swap_field_layerrenders = null;
                swap_list_layerrenders = null;
                data.checkResourceReloadAndReset(playerRenderer);
            });
    }
}
