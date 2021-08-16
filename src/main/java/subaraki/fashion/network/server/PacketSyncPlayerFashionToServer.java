package subaraki.fashion.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.network.IPacketBase;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.client.PacketSetWardrobeToTrackedClientPlayers;
import subaraki.fashion.network.client.PacketSyncFashionToTrackedPlayers;
import subaraki.fashion.render.EnumFashionSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketSyncPlayerFashionToServer implements IPacketBase {

    public ResourceLocation[] ids;
    public boolean isActive;
    public List<String> layers = new ArrayList<String>();

    public PacketSyncPlayerFashionToServer() {

    }

    public PacketSyncPlayerFashionToServer(ResourceLocation[] ids, boolean isActive, List<String> layers) {

        this.ids = ids;
        this.isActive = isActive;
        this.layers = layers;
    }

    public PacketSyncPlayerFashionToServer(FriendlyByteBuf buf) {

        decode(buf);

    }

    @Override
    public void decode(FriendlyByteBuf buf) {

        ids = new ResourceLocation[6];
        for (int slot = 0; slot < ids.length; slot++)
            ids[slot] = new ResourceLocation(buf.readUtf(256));

        isActive = buf.readBoolean();

        int size = buf.readInt();

        if (size > 0) {
            for (int i = 0; i < size; i++)
                layers.add(buf.readUtf(128));
        }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {

        for (ResourceLocation resLoc : ids)
            if (resLoc != null)
                buf.writeUtf(resLoc.toString());
            else
                buf.writeUtf("missing");

        buf.writeBoolean(isActive);

        buf.writeInt(layers == null ? 0 : layers.isEmpty() ? 0 : layers.size());

        if (layers != null && !layers.isEmpty()) {
            for (String layer : layers) {
                buf.writeUtf(layer);
            }
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {

        ServerPlayer player = ctx.get().getSender();

        ctx.get().enqueueWork(() -> {

            FashionData.get(player).ifPresent(fashion -> {

                for (EnumFashionSlot slot : EnumFashionSlot.values())
                    fashion.updateFashionSlot(ids[slot.ordinal()], slot);

                fashion.setRenderFashion(isActive);

                fashion.setInWardrobe(false);

                //server sided keep layer list
                fashion.getKeepLayerNames().clear();
                fashion.addLayersToKeep(layers);
            });

            // Send to tracked players

            Object packet = new PacketSyncFashionToTrackedPlayers(ids, isActive, player.getUUID(), layers);
            NetworkHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), packet);

            packet = new PacketSetWardrobeToTrackedClientPlayers(player.getUUID(), false);
            NetworkHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), packet);
        });

        ctx.get().setPacketHandled(true);
    }

    @Override
    public void register(int id) {

        NetworkHandler.NETWORK.registerMessage(id, PacketSyncPlayerFashionToServer.class, PacketSyncPlayerFashionToServer::encode,
                PacketSyncPlayerFashionToServer::new, PacketSyncPlayerFashionToServer::handle);
    }
}
