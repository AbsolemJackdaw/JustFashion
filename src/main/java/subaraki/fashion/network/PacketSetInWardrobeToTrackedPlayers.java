package subaraki.fashion.network;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import subaraki.fashion.capability.FashionData;

public class PacketSetInWardrobeToTrackedPlayers {

    public UUID sender;
    public boolean isInWardrobe;

    public PacketSetInWardrobeToTrackedPlayers(UUID otherPlayer, boolean isInWardrobe) {

        this.sender = otherPlayer;
        this.isInWardrobe = isInWardrobe;
    }

    public PacketSetInWardrobeToTrackedPlayers(PacketBuffer buf) {

        sender = UUID.fromString(buf.readString());
        isInWardrobe = buf.readBoolean();
    }

    public void encode(PacketBuffer buf) {

        buf.writeString(sender.toString());
        buf.writeBoolean(isInWardrobe);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {
            PlayerEntity player = Minecraft.getInstance().world.getPlayerByUuid(sender);
            FashionData fashion = FashionData.get(player);
            fashion.setInWardrobe(isInWardrobe);
        });
        context.get().setPacketHandled(true);
    }

}
