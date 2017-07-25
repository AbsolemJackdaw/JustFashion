package subaraki.fashion.handler;

import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import subaraki.fashion.capability.CapabilityInventoryProvider;
import subaraki.fashion.capability.FashionData;
import subaraki.fashion.network.NetworkHandler;
import subaraki.fashion.network.PacketSyncFashionToClient;
import subaraki.fashion.network.PacketSyncFashionToTrackedPlayers;

public class PlayerTracker {

	public PlayerTracker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void playerLogin(PlayerLoggedInEvent event){
		if(!event.player.world.isRemote)
		{
			FashionData fashion = FashionData.get(event.player);
			NetworkHandler.NETWORK.sendTo(new PacketSyncFashionToClient(fashion.getAllParts(), fashion.shouldRenderFashion()), (EntityPlayerMP) event.player);
		}
	}
	@SubscribeEvent
	public void playerDimensionChange(PlayerChangedDimensionEvent event){
		if(!event.player.world.isRemote)
		{
			FashionData fashion = FashionData.get(event.player);
			NetworkHandler.NETWORK.sendTo(new PacketSyncFashionToClient(fashion.getAllParts(), fashion.shouldRenderFashion()), (EntityPlayerMP) event.player);
		}
	}
	@SubscribeEvent
	public void playerTracking(PlayerEvent.StartTracking event){
		if(event.getTarget() instanceof EntityPlayer && event.getEntityPlayer() != null)
			this.sync((EntityPlayer) event.getTarget());
	}

	private void sync(EntityPlayer player){

		if(!player.world.isRemote) {
			EntityTracker tracker = ((WorldServer)player.world).getEntityTracker();
			FashionData fashion = FashionData.get(player);

			for (EntityPlayer entityPlayer : tracker.getTrackingPlayers(player)) {
				NetworkHandler.NETWORK.sendTo(
						new PacketSyncFashionToTrackedPlayers(
								fashion.getAllParts(), 
								fashion.shouldRenderFashion(),
								player.getUniqueID()),
						(EntityPlayerMP)entityPlayer);
			}
		}
	}

	@SubscribeEvent
	public void onAttach(AttachCapabilitiesEvent event){
		final Object entity = event.getObject();

		if (entity instanceof EntityPlayer)
			event.addCapability(CapabilityInventoryProvider.KEY, new CapabilityInventoryProvider((EntityPlayer)entity));
	}
}
