package subaraki.fashion.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import subaraki.fashion.mod.Fashion;
import subaraki.fashion.network.client.PacketSetWardrobeToTrackedClientPlayers;
import subaraki.fashion.network.client.PacketSyncFashionToClient;
import subaraki.fashion.network.client.PacketSyncFashionToTrackedPlayers;
import subaraki.fashion.network.server.PacketSetInWardrobeToTrackedPlayers;
import subaraki.fashion.network.server.PacketSyncPlayerFashionToServer;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(Fashion.MODID, "fashion_network"), () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public NetworkHandler() {

        int messageId = 0;
        new PacketSyncPlayerFashionToServer().register(messageId++);
        new PacketSetWardrobeToTrackedClientPlayers().register(messageId++);
        new PacketSyncFashionToClient().register(messageId++);
        new PacketSyncFashionToTrackedPlayers().register(messageId++);
        new PacketSetInWardrobeToTrackedPlayers().register(messageId++);

    }
}