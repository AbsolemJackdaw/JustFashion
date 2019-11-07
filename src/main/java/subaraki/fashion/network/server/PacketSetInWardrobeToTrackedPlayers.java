package subaraki.fashion.network.server;

import java.util.function.Supplier;

import lib.util.networking.IPacketBase;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.client.PacketSetWardrobeToTrackedClientPlayers;

public class PacketSetInWardrobeToTrackedPlayers implements IPacketBase {

    public boolean isInWardrobe;

    public PacketSetInWardrobeToTrackedPlayers() {

    }

    public PacketSetInWardrobeToTrackedPlayers(boolean isInWardrobe) {

        this.isInWardrobe = isInWardrobe;
    }

    public PacketSetInWardrobeToTrackedPlayers(PacketBuffer buf) {

        decode(buf);

    }

    @Override
    public void decode(PacketBuffer buf) {

        isInWardrobe = buf.readBoolean();
    }

    @Override

    public void encode(PacketBuffer buf) {

        buf.writeBoolean(isInWardrobe);
    }

    @Override

    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {
            
            FashionData.get(context.get().getSender()).ifPresent(data -> {
                data.setInWardrobe(isInWardrobe);
            });
            NetworkHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> context.get().getSender()),
                    new PacketSetWardrobeToTrackedClientPlayers(context.get().getSender().getUniqueID(), isInWardrobe));
        });
        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id) {

        NetworkHandler.NETWORK.registerMessage(id, PacketSetInWardrobeToTrackedPlayers.class, PacketSetInWardrobeToTrackedPlayers::encode,
                PacketSetInWardrobeToTrackedPlayers::new, PacketSetInWardrobeToTrackedPlayers::handle);
    }

}
