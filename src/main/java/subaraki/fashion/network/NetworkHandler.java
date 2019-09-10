package subaraki.fashion.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import subaraki.fashion.mod.Fashion;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(Fashion.MODID, "fashion_network"), () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public NetworkHandler() {

        int messageId = 0;
        NETWORK.registerMessage(messageId++, PacketSyncPlayerFashionToServer.class, PacketSyncPlayerFashionToServer::encode,
                PacketSyncPlayerFashionToServer::new, PacketSyncPlayerFashionToServer::handle);

        NETWORK.registerMessage(messageId++, PacketSyncFashionToTrackedPlayers.class, PacketSyncFashionToTrackedPlayers::encode,
                PacketSyncFashionToTrackedPlayers::new, PacketSyncFashionToTrackedPlayers::handle);

        NETWORK.registerMessage(messageId++, PacketSyncFashionToClient.class, PacketSyncFashionToClient::encode, PacketSyncFashionToClient::new,
                PacketSyncFashionToClient::handle);

        NETWORK.registerMessage(messageId++, PacketSetInWardrobeToTrackedPlayers.class, PacketSetInWardrobeToTrackedPlayers::encode,
                PacketSetInWardrobeToTrackedPlayers::new, PacketSetInWardrobeToTrackedPlayers::handle);

        NETWORK.registerMessage(messageId++, PacketOpenWardrobe.class, PacketOpenWardrobe::encode, PacketOpenWardrobe::new, PacketOpenWardrobe::handle);
    }
}