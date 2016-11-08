package subaraki.fashion.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.Fashion;

public class PacketSyncFashionToClient implements IMessage{

	public PacketSyncFashionToClient() {
	}

	public int[] ids;
	public boolean isActive;

	public PacketSyncFashionToClient(int[] ids, boolean isActive) {
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

	public static class PacketSyncFashionToClientHandler implements IMessageHandler<PacketSyncFashionToClient, IMessage>
	{
		@Override
		public IMessage onMessage(PacketSyncFashionToClient message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask( ()->{
				for(int slot = 0; slot < 4; slot++)
					FashionData.get(Fashion.proxy.getClientPlayer()).updatePartIndex(message.ids[slot], slot);
				FashionData.get(Fashion.proxy.getClientPlayer()).setRenderFashion(message.isActive);
			});
			return null;
		}
	}
}
