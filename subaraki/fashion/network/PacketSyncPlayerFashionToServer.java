package subaraki.fashion.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.fashion.capability.FashionCapability;
import subaraki.fashion.capability.FashionData;

public class PacketSyncPlayerFashionToServer implements IMessage{

	public PacketSyncPlayerFashionToServer() {
	}

	public int[] ids;
	public boolean isActive;

	public PacketSyncPlayerFashionToServer(int[] ids, boolean isActive){
		this.ids = ids;
		this.isActive = isActive;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		ids = new int[4];
		for(int slot = 0; slot < ids.length; slot++)
			ids[slot] = buf.readInt();
		isActive = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		for(int i : ids)
			buf.writeInt(i);
		buf.writeBoolean(isActive);
	}

	public static class PacketSyncPlayerFashionToServerHandler implements IMessageHandler<PacketSyncPlayerFashionToServer, IMessage>{

		@Override
		public IMessage onMessage(PacketSyncPlayerFashionToServer message, MessageContext ctx) {
			WorldServer server = ((WorldServer)ctx.getServerHandler().player.world);
			EntityPlayer player = ctx.getServerHandler().player;
			FashionData fashion = FashionData.get(player);

			server.addScheduledTask(()->{
				//update server
				for(int i = 0; i < 4; i++)
					fashion.updatePartIndex(message.ids[i], i);
				fashion.setRenderFashion(message.isActive);

				FashionData.get(player).setInWardrobe(false);
				
				//Send to tracked players
				EntityTracker tracker = server.getEntityTracker();
				for (EntityPlayer entityPlayer : tracker.getTrackingPlayers(player)) {
					IMessage packet = new PacketSyncFashionToTrackedPlayers(message.ids, message.isActive, player.getUniqueID());
					NetworkHandler.NETWORK.sendTo(packet, (EntityPlayerMP)entityPlayer);
					
					packet = new PacketSetInWardrobeToTrackedPlayers(player.getUniqueID(), false);
					NetworkHandler.NETWORK.sendTo(packet, (EntityPlayerMP)entityPlayer);
				}
			});
			return null;
		}
	}
}
