package subaraki.fashion.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import subaraki.fashion.network.ClientReferencesPacket;
import subaraki.fashion.network.IPacketBase;
import subaraki.fashion.network.NetworkHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class PacketSyncFashionToTrackedPlayers implements IPacketBase {

    public ResourceLocation[] ids;
    public boolean isActive;
    public UUID sender;
    public List<String> layers = new ArrayList<String>();

    public PacketSyncFashionToTrackedPlayers() {

    }

    public PacketSyncFashionToTrackedPlayers(ResourceLocation[] ids, boolean isActive, UUID sender, List<String> layers) {

        this.ids = ids;
        this.sender = sender;
        this.isActive = isActive;
        this.layers = layers;

    }

    public PacketSyncFashionToTrackedPlayers(FriendlyByteBuf buf) {

        decode(buf);

    }

    @Override
    public void encode(FriendlyByteBuf buf) {

        for (ResourceLocation resLoc : ids)
            if (resLoc != null)
                buf.writeUtf(resLoc.toString());
            else
                buf.writeUtf("missing");

        buf.writeBoolean(isActive);
        buf.writeUUID(sender);

        buf.writeInt(layers == null ? 0 : layers.isEmpty() ? 0 : layers.size());

        if (layers != null && !layers.isEmpty()) {
            for (String layer : layers) {
                buf.writeUtf(layer);
            }
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {

            ClientReferencesPacket.handle(ids, isActive, sender, layers);

        });
        context.get().setPacketHandled(true);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {

        ids = new ResourceLocation[6];
        for (int slot = 0; slot < ids.length; slot++)
            ids[slot] = new ResourceLocation(buf.readUtf(256));

        isActive = buf.readBoolean();

        sender = buf.readUUID();

        int size = buf.readInt();
        if (size > 0) {
            for (int i = 0; i < size; i++)
                layers.add(buf.readUtf(128));
        }
    }

    @Override
    public void register(int id) {

        NetworkHandler.NETWORK.registerMessage(id, PacketSyncFashionToTrackedPlayers.class, PacketSyncFashionToTrackedPlayers::encode,
                PacketSyncFashionToTrackedPlayers::new, PacketSyncFashionToTrackedPlayers::handle);
    }

}
