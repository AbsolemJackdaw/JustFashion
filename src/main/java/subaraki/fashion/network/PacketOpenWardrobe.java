package subaraki.fashion.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.screen.WardrobeProvider;

public class PacketOpenWardrobe {

    public PacketOpenWardrobe() {

    }

    public PacketOpenWardrobe(PacketBuffer buf) {

    }

    public void encode(PacketBuffer buf) {

    }

    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {

            ServerPlayerEntity player = context.get().getSender();

            NetworkHooks.openGui(player, new WardrobeProvider());

            FashionData.get(player).setInWardrobe(true);

            Object packet = new PacketSetInWardrobeToTrackedPlayers(player.getUniqueID(), true);

            NetworkHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), packet);

        });
        context.get().setPacketHandled(true);
    }
}
