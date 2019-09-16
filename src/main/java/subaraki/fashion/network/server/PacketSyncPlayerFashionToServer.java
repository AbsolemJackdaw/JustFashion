package subaraki.fashion.network.server;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.client.PacketSetInWardrobeToTrackedPlayers;
import subaraki.fashion.network.client.PacketSyncFashionToTrackedPlayers;

public class PacketSyncPlayerFashionToServer {

    public int[] ids;
    public boolean isActive;
    public List<String> layers = new ArrayList<String>();

    public PacketSyncPlayerFashionToServer(int[] ids, boolean isActive, List<String> layers) {

        this.ids = ids;
        this.isActive = isActive;
        this.layers = layers;
    }

    public PacketSyncPlayerFashionToServer(PacketBuffer buf) {

        ids = new int[6];
        for (int slot = 0; slot < ids.length; slot++)
            ids[slot] = buf.readInt();

        isActive = buf.readBoolean();

        int size = buf.readInt();

        if (size > 0) {
            for (int i = 0; i < size; i++)
                layers.add(buf.readString(64));
        }
    }

    public void encode(PacketBuffer buf) {

        for (int i : ids)
            buf.writeInt(i);
        
        buf.writeBoolean(isActive);

        buf.writeInt(layers == null ? 0 : layers.isEmpty() ? 0 : layers.size());

        if (layers != null && !layers.isEmpty()) {
            for (String layer : layers) {
                buf.writeString(layer);
            }
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {

        ServerPlayerEntity player = ctx.get().getSender();
        FashionData fashion = FashionData.get(player);

        ctx.get().enqueueWork(() -> {
            for (int i = 0; i < 6; i++)
                fashion.updatePartIndex(ids[i], EnumFashionSlot.fromInt(i));
            fashion.setRenderFashion(isActive);

            FashionData.get(player).setInWardrobe(false);

            // Send to tracked players

            Object packet = new PacketSyncFashionToTrackedPlayers(ids, isActive, player.getUniqueID(), layers);
            NetworkHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), packet);

            packet = new PacketSetInWardrobeToTrackedPlayers(player.getUniqueID(), false);
            NetworkHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), packet);
        });

        ctx.get().setPacketHandled(true);
    }
}
