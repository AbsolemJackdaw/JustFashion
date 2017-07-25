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

public class PacketSetInWardrobeToTrackedPlayers implements IMessage{

	public UUID otherPlayer;
	public boolean isInWardrobe;
	
	public PacketSetInWardrobeToTrackedPlayers() {
	}
	
	public PacketSetInWardrobeToTrackedPlayers(UUID otherPlayer, boolean isInWardrobe) {
		this.otherPlayer = otherPlayer;
		this.isInWardrobe = isInWardrobe;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		otherPlayer = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		isInWardrobe = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, otherPlayer.toString());
		buf.writeBoolean(isInWardrobe);
	}
	
	public static class PacketSetInWardrobeToTrackedPlayersHandler implements IMessageHandler<PacketSetInWardrobeToTrackedPlayers, IMessage>
	{

		@Override
		public IMessage onMessage(PacketSetInWardrobeToTrackedPlayers message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask( ()->{
				EntityPlayer player = Fashion.proxy.getClientWorld().getPlayerEntityByUUID(message.otherPlayer);
				FashionData fashion = FashionData.get(player);
				fashion.setInWardrobe(message.isInWardrobe);
			});
			return null;
		}
	}
}
