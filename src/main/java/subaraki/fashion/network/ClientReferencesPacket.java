package subaraki.fashion.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.render.EnumFashionSlot;
import subaraki.fashion.render.layer.LayerAestheticHeldItem;
import subaraki.fashion.render.layer.LayerFashion;
import subaraki.fashion.render.layer.LayerWardrobe;
import subaraki.fashion.util.ClientReferences;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientReferencesPacket {

    public static List<String> fillLayerList() {

        List<String> list = new ArrayList<>();

        list.add(LayerAestheticHeldItem.class.getSimpleName());
        list.add(ArrowLayer.class.getSimpleName());
        list.add(Deadmau5EarsLayer.class.getSimpleName());
        list.add(CapeLayer.class.getSimpleName());
        list.add(CustomHeadLayer.class.getSimpleName());
        list.add(ElytraLayer.class.getSimpleName());
        list.add(LayerWardrobe.class.getSimpleName());
        list.add(LayerFashion.class.getSimpleName());

        return list;
    }

    public static void handle(ResourceLocation[] ids, boolean isActive, UUID sender, List<String> layers) {

        Player distantPlayer = ClientReferences.getClientPlayerByUUID(sender);

        FashionData.get(distantPlayer).ifPresent(distFashion -> {

            for (EnumFashionSlot slot : EnumFashionSlot.values())
                distFashion.updateFashionSlot(ids[slot.ordinal()], slot);
            distFashion.setRenderFashion(isActive);

            EntityRenderer<? super Player> distantPlayerRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(distantPlayer);

            Object ob = null;
            try {
                Field field = ObfuscationReflectionHelper.findField(LivingEntityRenderer.class, Fashion.obfLayerName);
                ob = field.get(distantPlayerRenderer);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            distFashion.resetKeepLayerForDistantPlayer();
            if (layers != null && !layers.isEmpty() && ob instanceof List list) {
                for (Object content : list) {
                    for (String name : layers)//compare with
                        if (content.getClass().getSimpleName().equals(name))
                            distFashion.addLayerToKeep(name);
                }
            }
            distFashion.fashionLayers.clear();
        });
    }
}
