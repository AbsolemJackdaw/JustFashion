package subaraki.fashion.network;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lib.util.ClientReferences;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
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
import subaraki.fashion.mod.EnumFashionSlot;

public class ClientReferencesPacket {

    public static List<String> fillLayerList() {

        List<String> list = new ArrayList<>();

        list.add(LayerAestheticHeldItem.class.getSimpleName());
        list.add(ArrowLayer.class.getSimpleName());
        list.add(Deadmau5HeadLayer.class.getSimpleName());
        list.add(CapeLayer.class.getSimpleName());
        list.add(HeadLayer.class.getSimpleName());
        list.add(ElytraLayer.class.getSimpleName());
        list.add(LayerWardrobe.class.getSimpleName());
        list.add(LayerFashion.class.getSimpleName());

        return list;
    }

    public static void handle(int ids[], boolean isActive, UUID sender, List<String> layers) {

        PlayerEntity distantPlayer = ClientReferences.getClientPlayerByUUID(sender);
        PlayerEntity player = ClientReferences.getClientPlayer();

        FashionData.get(distantPlayer).ifPresent(distFashion -> {

            for (int i = 0; i < 6; i++)
                distFashion.updatePartIndex(ids[i], EnumFashionSlot.fromInt(i));
            distFashion.setRenderFashion(isActive);

            EntityRenderer<PlayerEntity> distantPlayerRenderer = ClientReferences.getRenderManager().getRenderer(distantPlayer);
            EntityRenderer<PlayerEntity> playerRenderer = ClientReferences.getRenderManager().getRenderer(player);

            Field field = null;
            Object ob = null;

            try {
                if (field == null)
                    field = ObfuscationReflectionHelper.findField(LivingRenderer.class, "field_177097_h");
                if (ob == null)
                    ob = field.get(distantPlayerRenderer);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            try {
                if (field == null)
                    field = ObfuscationReflectionHelper.findField(LivingRenderer.class, "field_177097_h");

                ob = field.get(playerRenderer);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            distFashion.keepLayers.clear();
            if (layers != null && !layers.isEmpty()) {
                for (Object content : (List<?>) ob) {
                    for (String name : layers)
                        if (content.getClass().getSimpleName().equals(name))
                            distFashion.keepLayers.add((LayerRenderer<?, ?>) content);
                }
            }
            distFashion.fashionLayers.clear();
        });
    }
}
