package subaraki.fashion.network.server;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import subaraki.fashion.network.IPacketBase;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.EnumFashionSlot;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.client.PacketSetWardrobeToTrackedClientPlayers;
import subaraki.fashion.network.client.PacketSyncFashionToTrackedPlayers;

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

    public PacketSyncPlayerFashionToServer(PacketBuffer buf) {

        decode(buf);

    }

    @Override
    public void decode(PacketBuffer buf) {

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
    public void encode(PacketBuffer buf) {

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

        ServerPlayerEntity player = ctx.get().getSender();

        ctx.get().enqueueWork(() -> {

            FashionData.get(player).ifPresent(fashion -> {

                for (EnumFashionSlot slot : EnumFashionSlot.values())
                    fashion.updateFashionSlot(ids[slot.ordinal()], slot);

                fashion.setRenderFashion(isActive);

                fashion.setInWardrobe(false);

                fashion.keepLayersNamesForServer.clear();

                for (String layer : fashion.getSavedOriginalListNamesForServerSidePurposes())
                    for (String classname : layers)
                        if (layer.equals(classname))
                            fashion.keepLayersNamesForServer.add(layer);
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
