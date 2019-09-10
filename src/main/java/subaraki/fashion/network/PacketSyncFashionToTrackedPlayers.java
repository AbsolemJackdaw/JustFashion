package subaraki.fashion.network;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5HeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.NetworkEvent;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.render.layer.LayerAestheticHeldItem;
import subaraki.fashion.render.layer.LayerFashion;
import subaraki.fashion.render.layer.LayerWardrobe;

public class PacketSyncFashionToTrackedPlayers {

    public int[] ids;
    public boolean isActive;
    public UUID sender;
    public List<String> layers = new ArrayList<String>();

    private static List<String> DEFAULT_LAYERS = new ArrayList<String>();

    public PacketSyncFashionToTrackedPlayers(int[] ids, boolean isActive, UUID sender, List<String> layers) {

        this.ids = ids;
        this.sender = sender;
        this.isActive = isActive;
        this.layers = layers;

        DEFAULT_LAYERS.add(LayerAestheticHeldItem.class.getSimpleName());
        DEFAULT_LAYERS.add(ArrowLayer.class.getSimpleName());
        DEFAULT_LAYERS.add(Deadmau5HeadLayer.class.getSimpleName());
        DEFAULT_LAYERS.add(CapeLayer.class.getSimpleName());
        DEFAULT_LAYERS.add(HeadLayer.class.getSimpleName());
        DEFAULT_LAYERS.add(ElytraLayer.class.getSimpleName());
        DEFAULT_LAYERS.add(LayerWardrobe.class.getSimpleName());
        DEFAULT_LAYERS.add(LayerFashion.class.getSimpleName());

    }

    public PacketSyncFashionToTrackedPlayers(PacketBuffer buf) {

        ids = new int[6];
        for (int slot = 0; slot < ids.length; slot++)
            ids[slot] = buf.readInt();

        isActive = buf.readBoolean();
        sender = buf.readUniqueId();

        int size = buf.readInt();
        if (size > 0) {
            for (int i = 0; i < size; i++)
                layers.add(buf.readString());
        }
    }

    public void encode(PacketBuffer buf) {

        for (int i : ids)
            buf.writeInt(i);

        buf.writeBoolean(isActive);
        buf.writeUniqueId(sender);

        buf.writeInt(layers == null ? 0 : layers.isEmpty() ? 0 : layers.size());

        if (layers != null && !layers.isEmpty()) {
            for (String layer : layers) {
                buf.writeString(layer);
            }
        }
    }

    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {

            PlayerEntity distantPlayer = Minecraft.getInstance().world.getPlayerByUuid(sender);
            PlayerEntity player = Minecraft.getInstance().player;

            FashionData distFashion = FashionData.get(distantPlayer);
            FashionData fashion = FashionData.get(player);

            for (int i = 0; i < 4; i++)
                distFashion.updatePartIndex(ids[i], EnumFashionSlot.fromInt(i));
            distFashion.setRenderFashion(isActive);

            EntityRenderer<PlayerEntity> distantPlayerRenderer = Minecraft.getInstance().getRenderManager().getRenderer(distantPlayer);
            EntityRenderer<PlayerEntity> playerRenderer = Minecraft.getInstance().getRenderManager().getRenderer(player);

            Field field = null;
            Object ob = null;

            try {
                if (field == null)
                    field = ObfuscationReflectionHelper.findField(LivingRenderer.class, "layerRenderers");
                if (ob == null)
                    ob = field.get(distantPlayerRenderer);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            try {
                if (field == null)
                    field = ObfuscationReflectionHelper.findField(LivingRenderer.class, "layerRenderers");

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
        context.get().setPacketHandled(true);
    }

}
