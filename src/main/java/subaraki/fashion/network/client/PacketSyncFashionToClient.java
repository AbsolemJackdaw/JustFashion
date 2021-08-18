package subaraki.fashion.network.client;

import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.network.IPacketBase;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.render.EnumFashionSlot;
import subaraki.fashion.util.ClientReferences;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketSyncFashionToClient implements IPacketBase {

    public ResourceLocation[] ids = new ResourceLocation[6];
    public boolean isActive;
    public List<String> toKeep = new ArrayList<String>();

    public PacketSyncFashionToClient() {

    }

    public PacketSyncFashionToClient(ResourceLocation[] ids, List<String> keepLayers, boolean isActive) {

        this.ids = ids;
        this.isActive = isActive;
        this.toKeep = keepLayers;
    }

    public PacketSyncFashionToClient(FriendlyByteBuf buf) {

        decode(buf);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {

        isActive = buf.readBoolean();

        ids = new ResourceLocation[6];
        for (int slot = 0; slot < ids.length; slot++)
            ids[slot] = new ResourceLocation(buf.readUtf(256));

        int size = buf.readInt();

        if (size > 0)
            for (int i = 0; i < size; i++)
                toKeep.add(buf.readUtf(128));
    }

    @Override
    public void encode(FriendlyByteBuf buf) {

        buf.writeBoolean(isActive);

        for (ResourceLocation resLoc : ids)
            if (resLoc != null)
                buf.writeUtf(resLoc.toString());
            else
                buf.writeUtf("missing");

        buf.writeInt(toKeep.size());

        if (!toKeep.isEmpty())
            for (String s : toKeep)
                buf.writeUtf(s);

    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {

            FashionData.get(ClientReferences.getClientPlayer()).ifPresent(data -> {

                for (EnumFashionSlot slot : EnumFashionSlot.values())
                    data.updateFashionSlot(ids[slot.ordinal()], slot);

                data.setRenderFashion(isActive);

                try {
                    List<RenderLayer<?, ?>> list = ClientReferences.tryList();

                    if (!list.isEmpty())
                        for (RenderLayer<?, ?> layer : list)
                            for (String classname : toKeep)
                                if (layer.getClass().getSimpleName().equals(classname))
                                    data.addLayerToKeep(classname);

                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        });
        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id) {

        NetworkHandler.NETWORK.registerMessage(id, PacketSyncFashionToClient.class, PacketSyncFashionToClient::encode, PacketSyncFashionToClient::new,
                PacketSyncFashionToClient::handle);
    }
}
