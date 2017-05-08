package subaraki.fashion.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.Fashion;

public class PacketSyncFashionToTrackedPlayers implements IMessage{

	public int[] ids;
	public boolean isActive;
	public UUID otherPlayer;

	public PacketSyncFashionToTrackedPlayers(){}

	public PacketSyncFashionToTrackedPlayers(int[] ids, boolean isActive, UUID otherPlayer) {
		this.ids = ids;
		this.otherPlayer = otherPlayer;
		this.isActive = isActive;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		ids = new int[4];
		for(int slot = 0; slot < ids.length; slot++)
			ids[slot] = buf.readInt();
		isActive = buf.readBoolean();
		otherPlayer = UUID.fromString(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		for(int i : ids)
			buf.writeInt(i);
		buf.writeBoolean(isActive);
		ByteBufUtils.writeUTF8String(buf, otherPlayer.toString());
	}

	public static class PacketSyncFashionToTrackedPlayersHandler implements IMessageHandler<PacketSyncFashionToTrackedPlayers, IMessage>{
		@Override
		public IMessage onMessage(PacketSyncFashionToTrackedPlayers message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask( ()->{

				EntityPlayer player = Fashion.proxy.getClientWorld().getPlayerEntityByUUID(message.otherPlayer);
				FashionData fashion = FashionData.get(player);

				for(int i = 0; i < 4; i++)
					fashion.updatePartIndex(message.ids[i], i);
				fashion.setRenderFashion(message.isActive);
			});
			return null;
		}
	}
}
