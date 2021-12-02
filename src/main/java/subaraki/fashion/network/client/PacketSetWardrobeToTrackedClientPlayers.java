package subaraki.fashion.network.client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.network.IPacketBase;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.util.ClientReferences;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketSetWardrobeToTrackedClientPlayers implements IPacketBase {

    public UUID sender;
    public boolean isInWardrobe;

    public PacketSetWardrobeToTrackedClientPlayers() {

    }

    public PacketSetWardrobeToTrackedClientPlayers(UUID otherPlayer, boolean isInWardrobe) {

        this.sender = otherPlayer;
        this.isInWardrobe = isInWardrobe;
    }

    public PacketSetWardrobeToTrackedClientPlayers(FriendlyByteBuf buf) {

        decode(buf);

    }

    @Override
    public void decode(FriendlyByteBuf buf) {

        sender = buf.readUUID();
        isInWardrobe = buf.readBoolean();
    }

    @Override

    public void encode(FriendlyByteBuf buf) {

        buf.writeUUID(sender);
        buf.writeBoolean(isInWardrobe);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {
            Player player = ClientReferences.getClientPlayerByUUID(sender);
            FashionData.get(player).ifPresent(data -> {
                data.setInWardrobe(isInWardrobe);
            });
        });
        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id) {

        NetworkHandler.NETWORK.registerMessage(id, PacketSetWardrobeToTrackedClientPlayers.class, PacketSetWardrobeToTrackedClientPlayers::encode,
                PacketSetWardrobeToTrackedClientPlayers::new, PacketSetWardrobeToTrackedClientPlayers::handle);
    }

}
