package subaraki.fashion.network.server;

import java.util.function.Supplier;

import lib.util.networking.IPacketBase;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.client.PacketSetInWardrobeToTrackedPlayers;
import subaraki.fashion.screen.WardrobeProvider;

public class PacketOpenWardrobe implements IPacketBase {

    public PacketOpenWardrobe() {

    }

    public PacketOpenWardrobe(PacketBuffer buf) {

    }

    @Override
    public void encode(PacketBuffer buf) {

    }

    @Override
    public void decode(PacketBuffer buf) {

    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {

        context.get().enqueueWork(() -> {

            ServerPlayerEntity player = context.get().getSender();

            NetworkHooks.openGui(player, new WardrobeProvider());

            FashionData.get(player).ifPresent(data -> {
                data.setInWardrobe(true);
            });

            Object packet = new PacketSetInWardrobeToTrackedPlayers(player.getUniqueID(), true);

            NetworkHandler.NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), packet);

        });
        context.get().setPacketHandled(true);
    }

    @Override
    public void register(int id) {

        NetworkHandler.NETWORK.registerMessage(id, PacketOpenWardrobe.class, PacketOpenWardrobe::encode, PacketOpenWardrobe::new, PacketOpenWardrobe::handle);

    }
}
