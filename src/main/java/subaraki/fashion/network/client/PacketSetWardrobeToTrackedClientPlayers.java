package subaraki.fashion.network.client;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.network.IPacketBase;
import subaraki.fashion.network.NetworkHandler;

public class PacketSetWardrobeToTrackedClientPlayers implements IPacketBase {

    public UUID sender;
    public boolean isInWardrobe;

    public PacketSetWardrobeToTrackedClientPlayers() {

    }

    public PacketSetWardrobeToTrackedClientPlayers(UUID otherPlayer, boolean isInWardrobe) {

        this.sender = otherPlayer;
        this.isInWardrobe = isInWardrobe;
    }

    public PacketSetWardrobeToTrackedClientPlayers(PacketBuffer buf) {

        decode(buf);

    }

    @Override
    public void decode(PacketBuffer buf) {

        sender = buf.readUUID();
        isInWardrobe = buf.readBoolean();
    }

    @Override

    public void encode(PacketBuffer buf) {

        buf.writeUUID(sender);
        buf.writeBoolean(isInWardrobe);
    }

    @Override

    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().level.getPlayerByUUID(sender);
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
