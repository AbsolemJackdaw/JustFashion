package subaraki.fashion.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import subaraki.fashion.network.PacketSyncFashionToClient.PacketSyncFashionToClientHandler;
import subaraki.fashion.network.PacketSyncFashionToTrackedPlayers.PacketSyncFashionToTrackedPlayersHandler;
import subaraki.fashion.network.PacketSyncPlayerFashionToServer.PacketSyncPlayerFashionToServerHandler;

public class NetworkHandler {

	public static final SimpleNetworkWrapper NETWORK = new SimpleNetworkWrapper("FashionNetwork");
	
	public NetworkHandler() {
		NETWORK.registerMessage(PacketSyncPlayerFashionToServerHandler.class, PacketSyncPlayerFashionToServer.class, 0, Side.SERVER);
		NETWORK.registerMessage(PacketSyncFashionToTrackedPlayersHandler.class, PacketSyncFashionToTrackedPlayers.class, 1, Side.CLIENT);
		NETWORK.registerMessage(PacketSyncFashionToClientHandler.class, PacketSyncFashionToClient.class, 2, Side.CLIENT);
	}
}
