package subaraki.fashion.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.mod.Fashion;

public class PacketOpenWardrobe implements IMessage{
	public PacketOpenWardrobe() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}
	
	public static class PacketOpenWardrobeHandler implements IMessageHandler<PacketOpenWardrobe, IMessage>
	{
		@Override
		public IMessage onMessage(PacketOpenWardrobe message, MessageContext ctx) {
			EntityPlayer player = ctx.getServerHandler().player;
			WorldServer server = (WorldServer) player.world;
			server.addScheduledTask( ()->{
				
				FMLNetworkHandler.openGui(ctx.getServerHandler().player, Fashion.INSTANCE, 0, ctx.getServerHandler().player.world, 0, 0, 0);
				
				FashionData.get(player).setInWardrobe(true);
				
				EntityTracker tracker = server.getEntityTracker();
				for (EntityPlayer entityPlayer : tracker.getTrackingPlayers(player)) {
					IMessage packet = new PacketSetInWardrobeToTrackedPlayers(player.getUniqueID(), true);
					NetworkHandler.NETWORK.sendTo(packet, (EntityPlayerMP)entityPlayer);
				}
			});
			return null;
		}
	}
}
