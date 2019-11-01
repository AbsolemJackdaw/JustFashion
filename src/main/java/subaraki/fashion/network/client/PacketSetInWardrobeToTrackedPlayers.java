package subaraki.fashion.network.client;

import java.util.UUID;
import java.util.function.Supplier;

import lib.util.ClientReferences;
import lib.util.networking.IPacketBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.network.NetworkHandler;

public class PacketSetInWardrobeToTrackedPlayers implements IPacketBase {

    public UUID sender;
    public boolean isInWardrobe;

    public PacketSetInWardrobeToTrackedPlayers() {

    }

    public PacketSetInWardrobeToTrackedPlayers(UUID otherPlayer, boolean isInWardrobe) {

        this.sender = otherPlayer;
        this.isInWardrobe = isInWardrobe;
    }

    public PacketSetInWardrobeToTrackedPlayers(PacketBuffer buf) {

        decode(buf);

    }

    @Override
    public void decode(PacketBuffer buf) {

        sender = buf.readUniqueId();
        isInWardrobe = buf.readBoolean();
    }

    @Override

    public void encode(PacketBuffer buf) {

        buf.writeUniqueId(sender);
        buf.writeBoolean(isInWardrobe);
    }

    @Override

    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {
            PlayerEntity player = ClientReferences.getClientPlayerByUUID(sender);
            FashionData.get(player).ifPresent(data -> {
                data.setInWardrobe(isInWardrobe);
            });
        });
        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id) {

        NetworkHandler.NETWORK.registerMessage(id, PacketSetInWardrobeToTrackedPlayers.class, PacketSetInWardrobeToTrackedPlayers::encode,
                PacketSetInWardrobeToTrackedPlayers::new, PacketSetInWardrobeToTrackedPlayers::handle);
    }

}
