package subaraki.fashion.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.network.client.PacketSetInWardrobeToTrackedPlayers;
import subaraki.fashion.network.client.PacketSyncFashionToClient;
import subaraki.fashion.network.client.PacketSyncFashionToTrackedPlayers;
import subaraki.fashion.network.server.PacketOpenWardrobe;
import subaraki.fashion.network.server.PacketSyncPlayerFashionToServer;
import subaraki.fashion.network.server.PacketSyncSavedListToServer;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(Fashion.MODID, "fashion_network"), () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public NetworkHandler() {

        int messageId = 0;
        new PacketOpenWardrobe().register(messageId++);
        new PacketSyncPlayerFashionToServer().register(messageId++);
        new PacketSetInWardrobeToTrackedPlayers().register(messageId++);
        new PacketSyncFashionToClient().register(messageId++);
        new PacketSyncFashionToTrackedPlayers().register(messageId++);
        new PacketSyncSavedListToServer().register(messageId++);

    }
}