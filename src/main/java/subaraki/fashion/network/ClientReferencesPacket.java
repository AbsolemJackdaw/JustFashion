package subaraki.fashion.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.client.render.layer.LayerAestheticHeldItem;
import subaraki.fashion.client.render.layer.LayerFashion;
import subaraki.fashion.client.render.layer.LayerWardrobe;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.mod.Fashion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public static void handle(ResourceLocation ids[], boolean isActive, UUID sender, List<String> layers) {

        PlayerEntity distantPlayer = Minecraft.getInstance().level.getPlayerByUUID(sender);
        PlayerEntity player = Minecraft.getInstance().player;

        FashionData.get(distantPlayer).ifPresent(distFashion -> {

            for (EnumFashionSlot slot : EnumFashionSlot.values())
                distFashion.updateFashionSlot(ids[slot.ordinal()], slot);
            distFashion.setRenderFashion(isActive);

            EntityRenderer<? super PlayerEntity> distantPlayerRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(distantPlayer);
            EntityRenderer<? super PlayerEntity> playerRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);

            Field field = null;
            Object ob = null;

            try {
                field = ObfuscationReflectionHelper.findField(LivingRenderer.class, Fashion.obfLayerName);
                ob = field.get(distantPlayerRenderer);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            try {
                if (field == null)
                    field = ObfuscationReflectionHelper.findField(LivingRenderer.class, Fashion.obfLayerName);

                ob = field.get(playerRenderer);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            distFashion.resetKeepLayerForDistantPlayer();
            if (layers != null && !layers.isEmpty() && ob != null) {
                for (Object content : (List<?>) ob) {
                    for (String name : layers)
                        if (content.getClass().getSimpleName().equals(name))
                            distFashion.addLayerToKeep(name);
                }
            }
            distFashion.fashionLayers.clear();
        });
    }
}
