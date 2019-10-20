package subaraki.fashion.network.client;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.client.ClientReferences;
import subaraki.fashion.mod.EnumFashionSlot;

public class PacketSyncFashionToClient {

    public int[] ids = new int[6];
    public boolean isActive;

    public PacketSyncFashionToClient(int[] ids, boolean isActive) {

        this.ids = ids;
        this.isActive = isActive;
    }

    public PacketSyncFashionToClient(PacketBuffer buf) {

        isActive = buf.readBoolean();

        ids = new int[6];
        for (int slot = 0; slot < ids.length; slot++)
            ids[slot] = buf.readInt();
    }

    public void encode(PacketBuffer buf) {

        buf.writeBoolean(isActive);

        for (int i : ids)
            buf.writeInt(i);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {
            for (int slot = 0; slot < 6; slot++)
                FashionData.get(ClientReferences.getClientPlayer()).updatePartIndex(ids[slot], EnumFashionSlot.fromInt(slot));
            FashionData.get(ClientReferences.getClientPlayer()).setRenderFashion(isActive);
        });
        context.get().setPacketHandled(true);
    }
}
