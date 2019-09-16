package subaraki.fashion.network.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import subaraki.fashion.network.ClientReferencesPacket;

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

           ClientReferencesPacket.handle(ids, isActive, sender, layers);

        });
        context.get().setPacketHandled(true);
    }

}
