package subaraki.fashion.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.network.IPacketBase;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.client.PacketSetWardrobeToTrackedClientPlayers;

import java.util.function.Supplier;

public class PacketSetInWardrobeToTrackedPlayers implements IPacketBase {

    public boolean isInWardrobe;

    public PacketSetInWardrobeToTrackedPlayers() {

    }

    public PacketSetInWardrobeToTrackedPlayers(boolean isInWardrobe) {

        this.isInWardrobe = isInWardrobe;
    }

    public PacketSetInWardrobeToTrackedPlayers(FriendlyByteBuf buf) {

        decode(buf);

    }

    @Override
    public void decode(FriendlyByteBuf buf) {

        isInWardrobe = buf.readBoolean();
    }

    @Override

    public void encode(FriendlyByteBuf buf) {

        buf.writeBoolean(isInWardrobe);
    }

    @Override

    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();

            if (player != null) {
                FashionData.get(player).ifPresent(data -> {
                    data.setInWardrobe(isInWardrobe);
                });
                NetworkHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> context.get().getSender()),
                        new PacketSetWardrobeToTrackedClientPlayers(player.getUUID(), isInWardrobe));
            }
        });
        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id) {

        NetworkHandler.NETWORK.registerMessage(id, PacketSetInWardrobeToTrackedPlayers.class, PacketSetInWardrobeToTrackedPlayers::encode,
                PacketSetInWardrobeToTrackedPlayers::new, PacketSetInWardrobeToTrackedPlayers::handle);
    }

}
